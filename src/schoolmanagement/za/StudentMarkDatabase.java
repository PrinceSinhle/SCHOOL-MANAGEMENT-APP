package schoolmanagement.za;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class StudentMarkDatabase implements AutoCloseable {
    private Connection connection;

    public StudentMarkDatabase() {
    }

    @Override
    public synchronized void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ex) { throw DatabaseErrors.failure("Could not close the marks database connection.", ex); }
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

    public boolean addMark(StudentMark mark) {
        String sql = "INSERT INTO StudentMark "
                + "(learner_id, learner_name, subject, term, mark_obtained, total_mark, teacher_id, teacher_comment) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setMarkValues(ps, mark);
            boolean saved = ps.executeUpdate() > 0;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    mark.setMarkId(rs.getInt(1));
                }
            }

            return saved;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not save mark.", ex);
        }
    }

    public boolean updateMark(int markId, StudentMark mark) {
        String sql = "UPDATE StudentMark SET learner_id=?, learner_name=?, subject=?, term=?, "
                + "mark_obtained=?, total_mark=?, teacher_id=?, teacher_comment=? WHERE mark_id=?";

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            setMarkValues(ps, mark);
            ps.setInt(9, markId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not update mark.", ex);
        }
    }

    public boolean updateMarkForTeacher(int markId, StudentMark mark, String teacherId) {
        String sql = "UPDATE StudentMark SET learner_id=?, learner_name=?, subject=?, term=?, "
                + "mark_obtained=?, total_mark=?, teacher_id=?, teacher_comment=? "
                + "WHERE mark_id=? AND teacher_id=?";

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            setMarkValues(ps, mark);
            ps.setInt(9, markId);
            ps.setString(10, teacherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not update mark.", ex);
        }
    }

    public boolean deleteMark(int markId) {
        String sql = "DELETE FROM StudentMark WHERE mark_id=?";

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setInt(1, markId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not delete mark.", ex);
        }
    }

    public boolean deleteMarkForTeacher(int markId, String teacherId) {
        String sql = "DELETE FROM StudentMark WHERE mark_id=? AND teacher_id=?";
        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setInt(1, markId);
            ps.setString(2, teacherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not delete mark.", ex);
        }
    }

    public ArrayList<StudentMark> getAll() {
        String sql = "SELECT m.mark_id, m.learner_id, COALESCE(l.name, m.learner_name) AS learner_name, "
                + "m.subject, m.term, m.mark_obtained, m.total_mark, m.teacher_id, m.teacher_comment "
                + "FROM StudentMark m LEFT JOIN Leaner l ON l.id=m.learner_id "
                + "ORDER BY learner_name, m.subject, m.term";
        ArrayList<StudentMark> marks = new ArrayList<>();

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                marks.add(mapMark(rs));
            }
        } catch (SQLException ex) {
            throw DatabaseErrors.failure("Could not load marks.", ex);
        }

        return marks;
    }

    public ArrayList<StudentMark> getAllForTeacher(String teacherId) {
        String sql = "SELECT m.mark_id, m.learner_id, COALESCE(l.name, m.learner_name) AS learner_name, "
                + "m.subject, m.term, m.mark_obtained, m.total_mark, m.teacher_id, m.teacher_comment "
                + "FROM StudentMark m LEFT JOIN Leaner l ON l.id=m.learner_id "
                + "WHERE m.teacher_id=? ORDER BY learner_name, m.subject, m.term";
        ArrayList<StudentMark> marks = new ArrayList<>();
        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setString(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    marks.add(mapMark(rs));
                }
            }
        } catch (SQLException ex) {
            throw DatabaseErrors.failure("Could not load marks.", ex);
        }
        return marks;
    }

    private void createTableIfMissing() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS StudentMark ("
                + "mark_id INTEGER NOT NULL AUTO_INCREMENT, "
                + "learner_id VARCHAR(13) NOT NULL, "
                + "learner_name VARCHAR(100) NOT NULL, "
                + "subject VARCHAR(60) NOT NULL, "
                + "term VARCHAR(20) NOT NULL, "
                + "mark_obtained DOUBLE NOT NULL, "
                + "total_mark DOUBLE NOT NULL, "
                + "teacher_id VARCHAR(13), "
                + "teacher_comment VARCHAR(255), "
                + "PRIMARY KEY (mark_id))";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
        ensureLearnerForeignKey();
    }

    private void ensureLearnerForeignKey() throws SQLException {
        String existingConstraint = "SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE "
                + "WHERE CONSTRAINT_SCHEMA=DATABASE() AND TABLE_NAME='StudentMark' "
                + "AND COLUMN_NAME='learner_id' AND REFERENCED_TABLE_NAME='Leaner' "
                + "AND REFERENCED_COLUMN_NAME='id'";
        try (PreparedStatement ps = connection.prepareStatement(existingConstraint);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        String orphanCount = "SELECT COUNT(*) FROM StudentMark m LEFT JOIN Leaner l "
                + "ON l.id=m.learner_id WHERE l.id IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(orphanCount);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                System.err.println("Some marks refer to missing learners. Resolve them before the learner foreign key can be added.");
                return;
            }
        }

        String addConstraint = "ALTER TABLE StudentMark ADD CONSTRAINT fk_studentmark_learner "
                + "FOREIGN KEY (learner_id) REFERENCES Leaner(id) ON UPDATE CASCADE ON DELETE RESTRICT";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(addConstraint);
        }
    }

    private void setMarkValues(PreparedStatement ps, StudentMark mark) throws SQLException {
        ps.setString(1, mark.getLearnerId());
        ps.setString(2, mark.getLearnerName());
        ps.setString(3, mark.getSubject());
        ps.setString(4, mark.getTerm());
        ps.setDouble(5, mark.getMarkObtained());
        ps.setDouble(6, mark.getTotalMark());
        ps.setString(7, mark.getTeacherId());
        ps.setString(8, mark.getComment());
    }

    private StudentMark mapMark(ResultSet rs) throws SQLException {
        return new StudentMark(
                rs.getInt("mark_id"),
                rs.getString("learner_id"),
                rs.getString("learner_name"),
                rs.getString("subject"),
                rs.getString("term"),
                rs.getDouble("mark_obtained"),
                rs.getDouble("total_mark"),
                rs.getString("teacher_id"),
                rs.getString("teacher_comment")
        );
    }
}
