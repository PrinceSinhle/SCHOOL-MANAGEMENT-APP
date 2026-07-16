package schoolmanagement.za;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class StudentMarkDatabase {
    private final Connection connection;

    public StudentMarkDatabase() {
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

    public boolean addMark(StudentMark mark) {
        String sql = "INSERT INTO StudentMark "
                + "(learner_id, learner_name, subject, term, mark_obtained, total_mark, teacher_id, teacher_comment) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setMarkValues(ps, mark);
            boolean saved = ps.executeUpdate() > 0;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    mark.setMarkId(rs.getInt(1));
                }
            }

            return saved;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public boolean updateMark(int markId, StudentMark mark) {
        String sql = "UPDATE StudentMark SET learner_id=?, learner_name=?, subject=?, term=?, "
                + "mark_obtained=?, total_mark=?, teacher_id=?, teacher_comment=? WHERE mark_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setMarkValues(ps, mark);
            ps.setInt(9, markId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public boolean deleteMark(int markId) {
        String sql = "DELETE FROM StudentMark WHERE mark_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, markId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public ArrayList<StudentMark> getAll() {
        String sql = "SELECT mark_id, learner_id, learner_name, subject, term, mark_obtained, "
                + "total_mark, teacher_id, teacher_comment FROM StudentMark ORDER BY learner_name, subject, term";
        ArrayList<StudentMark> marks = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                marks.add(mapMark(rs));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return marks;
    }

    private void createTableIfMissing() throws SQLException {
        String sql = "CREATE TABLE StudentMark ("
                + "mark_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
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
        } catch (SQLException ex) {
            if (!"X0Y32".equals(ex.getSQLState())) {
                throw ex;
            }
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
