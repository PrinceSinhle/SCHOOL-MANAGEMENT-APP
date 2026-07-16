package schoolmanagement.za;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TeacherDatabase {
    private final Connection connection;

    public TeacherDatabase() {
        try {
            connection = DriverManager.getConnection(
                    DatabaseConfig.URL,
                    DatabaseConfig.USER,
                    DatabaseConfig.PASSWORD
            );
            createTableIfMissing();
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not connect to database", ex);
        }
    }

    public boolean addTeacher(Teacher teacher) {
        String sql = "INSERT INTO Teacher "
                + "(teacher_id, full_name, phone_number, qualification, password, role) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, teacher.getTeacherId());
            ps.setString(2, teacher.getFullName());
            ps.setString(3, teacher.getPhoneNumber());
            ps.setString(4, teacher.getQualification());
            ps.setString(5, teacher.getPassword());
            ps.setString(6, teacher.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public boolean updateTeacher(String teacherId, Teacher teacher) {
        String sql = "UPDATE Teacher SET full_name=?, phone_number=?, qualification=?, password=?, role=? "
                + "WHERE teacher_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, teacher.getFullName());
            ps.setString(2, teacher.getPhoneNumber());
            ps.setString(3, teacher.getQualification());
            ps.setString(4, teacher.getPassword());
            ps.setString(5, teacher.getRole());
            ps.setString(6, teacherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public Teacher authenticateTeacher(String teacherId) {
        return getTeacher(teacherId);
    }

    public Teacher getTeacher(String id) {
        String sql = "SELECT teacher_id, full_name, phone_number, qualification, password, role "
                + "FROM Teacher WHERE teacher_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTeacher(rs);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    public ArrayList<Teacher> getAll() {
        String sql = "SELECT teacher_id, full_name, phone_number, qualification, password, role FROM Teacher";
        ArrayList<Teacher> teachers = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                teachers.add(mapTeacher(rs));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return teachers;
    }

    public boolean deleteTeacher(String id) {
        String sql = "DELETE FROM Teacher WHERE teacher_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    private void createTableIfMissing() throws SQLException {
        String sql = "CREATE TABLE Teacher ("
                + "teacher_id VARCHAR(13) NOT NULL, "
                + "full_name VARCHAR(100) NOT NULL, "
                + "phone_number VARCHAR(10) NOT NULL, "
                + "qualification VARCHAR(30) NOT NULL, "
                + "password VARCHAR(100) NOT NULL, "
                + "role VARCHAR(30) NOT NULL, "
                + "PRIMARY KEY (teacher_id))";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            if (!"X0Y32".equals(ex.getSQLState())) {
                throw ex;
            }
        }
    }

    private Teacher mapTeacher(ResultSet rs) throws SQLException {
        return new Teacher(
                rs.getString("teacher_id"),
                rs.getString("full_name"),
                rs.getString("phone_number"),
                rs.getString("qualification"),
                rs.getString("password"),
                rs.getString("role")
        );
    }
}
