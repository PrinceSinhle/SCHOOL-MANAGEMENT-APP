package schoolmanagement.za;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TeacherDatabase implements AutoCloseable {
    private Connection connection;

    public TeacherDatabase() {
    }

    @Override
    public synchronized void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ex) { throw DatabaseErrors.failure("Could not close the staff database connection.", ex); }
            connection = null;
        }
    }

    private synchronized Connection ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConfig.openConnection();
            try {
                createTableIfMissing();
            } catch (SQLException ex) {
                connection.close();
                connection = null;
                throw ex;
            }
        }
        return connection;
    }

    public boolean addTeacher(Teacher teacher) {
        try {
            return insertTeacher(teacher);
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not add staff account.", ex);
        }
    }

    public boolean addFirstAdministrator(Teacher teacher) {
        String lockName = "school_management_first_administrator";
        try {
            try (PreparedStatement ps = ensureConnection().prepareStatement("SELECT GET_LOCK(?, 5)")) {
                ps.setString(1, lockName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) != 1) return false;
                }
            }
            try {
                if (countAdministrators() > 0) return false;
                return insertTeacher(teacher);
            } finally {
                try (PreparedStatement ps = ensureConnection().prepareStatement("SELECT RELEASE_LOCK(?)")) {
                    ps.setString(1, lockName);
                    ps.executeQuery().close();
                }
            }
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not create the first administrator.", ex);
        }
    }

    private boolean insertTeacher(Teacher teacher) throws SQLException {
        String sql = "INSERT INTO Teacher "
                + "(teacher_id, full_name, phone_number, qualification, password, teacher_role) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setString(1, teacher.getTeacherId());
            ps.setString(2, teacher.getFullName());
            ps.setString(3, teacher.getPhoneNumber());
            ps.setString(4, teacher.getQualification());
            ps.setString(5, PasswordHasher.hash(teacher.getPassword()));
            ps.setString(6, teacher.getRole());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateTeacher(String teacherId, Teacher teacher) {
        boolean replacePassword = teacher.getPassword() != null && !teacher.getPassword().isEmpty();
        String sql = replacePassword
                ? "UPDATE Teacher SET full_name=?, phone_number=?, qualification=?, password=?, teacher_role=? WHERE teacher_id=?"
                : "UPDATE Teacher SET full_name=?, phone_number=?, qualification=?, teacher_role=? WHERE teacher_id=?";

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setString(1, teacher.getFullName());
            ps.setString(2, teacher.getPhoneNumber());
            ps.setString(3, teacher.getQualification());
            if (replacePassword) {
                ps.setString(4, PasswordHasher.hash(teacher.getPassword()));
                ps.setString(5, teacher.getRole());
                ps.setString(6, teacherId);
            } else {
                ps.setString(4, teacher.getRole());
                ps.setString(5, teacherId);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not update staff account.", ex);
        }
    }

    public Teacher authenticateTeacher(String teacherId) {
        String sql = "SELECT teacher_id, full_name, phone_number, qualification, password, teacher_role "
                + "FROM Teacher WHERE teacher_id=?";
        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setString(1, teacherId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapTeacher(rs) : null;
            }
        } catch (SQLException ex) {
            throw DatabaseErrors.failure("Could not check staff sign-in.", ex);
        }
    }

    public Teacher getTeacher(String id) {
        String sql = "SELECT teacher_id, full_name, phone_number, qualification, teacher_role "
                + "FROM Teacher WHERE teacher_id=?";

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setString(1, id.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Teacher(
                            rs.getString("teacher_id"),
                            rs.getString("full_name"),
                            rs.getString("phone_number"),
                            rs.getString("qualification"),
                            null,
                            rs.getString("teacher_role")
                    );
                }
            }
        } catch (SQLException ex) {
            throw DatabaseErrors.failure("Could not load staff account.", ex);
        }

        return null;
    }

    public ArrayList<Teacher> getAll() {
        String sql = "SELECT teacher_id, full_name, phone_number, qualification, teacher_role FROM Teacher";
        ArrayList<Teacher> teachers = new ArrayList<>();

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                teachers.add(new Teacher(
                        rs.getString("teacher_id"),
                        rs.getString("full_name"),
                        rs.getString("phone_number"),
                        rs.getString("qualification"),
                        null,
                        rs.getString("teacher_role")
                ));
            }
        } catch (SQLException ex) {
            throw DatabaseErrors.failure("Could not load staff accounts.", ex);
        }

        return teachers;
    }

    public boolean hasAdministrator() {
        return countAdministrators() > 0;
    }

    public int countAdministrators() {
        String sql = "SELECT COUNT(*) FROM Teacher WHERE LOWER(teacher_role)='admin'";
        try (PreparedStatement ps = ensureConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not count administrator accounts.", ex);
        }
    }

    public boolean updatePassword(String teacherId, String passwordHash) {
        String sql = "UPDATE Teacher SET password=? WHERE teacher_id=?";
        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, teacherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw DatabaseErrors.failure("Could not upgrade the staff password.", ex);
        }
    }

    public boolean deleteTeacher(String id) {
        String sql = "DELETE FROM Teacher WHERE teacher_id=?";

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not remove staff account.", ex);
        }
    }

    private void createTableIfMissing() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Teacher ("
                + "teacher_id VARCHAR(13) NOT NULL, "
                + "full_name VARCHAR(100) NOT NULL, "
                + "phone_number VARCHAR(10) NOT NULL, "
                + "qualification VARCHAR(30) NOT NULL, "
                + "password VARCHAR(100) NOT NULL, "
                + "teacher_role VARCHAR(30) NOT NULL, "
                + "PRIMARY KEY (teacher_id))";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private Teacher mapTeacher(ResultSet rs) throws SQLException {
        return new Teacher(
                rs.getString("teacher_id"),
                rs.getString("full_name"),
                rs.getString("phone_number"),
                rs.getString("qualification"),
                rs.getString("password"),
                rs.getString("teacher_role")
        );
    }
}
