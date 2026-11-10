package schoolmanagement.za;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AttendanceDatabase implements AutoCloseable {
    private Connection connection;

    public AttendanceDatabase() {
    }

    @Override
    public synchronized void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ex) { throw DatabaseErrors.failure("Could not close the attendance database connection.", ex); }
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

    public boolean add(AttendanceRecord record) {
        String sql = "INSERT INTO Attendance (learner_id, attendance_date, attendance_status, recorded_by, attendance_comment) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = ensureConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setValues(ps, record);
            if (ps.executeUpdate() == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) record.setAttendanceId(keys.getInt(1));
            }
            return true;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not save attendance.", ex);
        }
    }

    public boolean update(AttendanceRecord record) {
        String sql = "UPDATE Attendance SET learner_id=?, attendance_date=?, attendance_status=?, attendance_comment=? "
                + "WHERE attendance_id=?";
        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            setUpdateValues(ps, record);
            ps.setInt(5, record.getAttendanceId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not update attendance.", ex);
        }
    }

    public boolean updateForTeacher(AttendanceRecord record, String teacherId) {
        String sql = "UPDATE Attendance SET learner_id=?, attendance_date=?, attendance_status=?, attendance_comment=? "
                + "WHERE attendance_id=? AND recorded_by=?";
        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            setUpdateValues(ps, record);
            ps.setInt(5, record.getAttendanceId());
            ps.setString(6, teacherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not update attendance.", ex);
        }
    }

    public boolean delete(int attendanceId) {
        return deleteWhere("DELETE FROM Attendance WHERE attendance_id=?", attendanceId, null);
    }

    public boolean deleteForTeacher(int attendanceId, String teacherId) {
        return deleteWhere("DELETE FROM Attendance WHERE attendance_id=? AND recorded_by=?", attendanceId, teacherId);
    }

    private boolean deleteWhere(String sql, int attendanceId, String teacherId) {
        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setInt(1, attendanceId);
            if (teacherId != null) ps.setString(2, teacherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not delete attendance.", ex);
        }
    }

    public ArrayList<AttendanceRecord> getAll() {
        return query("SELECT a.attendance_id, a.learner_id, l.name AS learner_name, a.attendance_date, "
                + "a.attendance_status, a.recorded_by, a.attendance_comment FROM Attendance a "
                + "JOIN Leaner l ON l.id=a.learner_id ORDER BY a.attendance_date DESC, l.name", null);
    }

    public ArrayList<AttendanceRecord> getAllForTeacher(String teacherId) {
        return query("SELECT a.attendance_id, a.learner_id, l.name AS learner_name, a.attendance_date, "
                + "a.attendance_status, a.recorded_by, a.attendance_comment FROM Attendance a "
                + "JOIN Leaner l ON l.id=a.learner_id WHERE a.recorded_by=? "
                + "ORDER BY a.attendance_date DESC, l.name", teacherId);
    }

    private ArrayList<AttendanceRecord> query(String sql, String teacherId) {
        ArrayList<AttendanceRecord> records = new ArrayList<>();
        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            if (teacherId != null) ps.setString(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(new AttendanceRecord(
                            rs.getInt("attendance_id"), rs.getString("learner_id"),
                            rs.getString("learner_name"), rs.getDate("attendance_date").toLocalDate(),
                            rs.getString("attendance_status"), rs.getString("recorded_by"),
                            rs.getString("attendance_comment")));
                }
            }
        } catch (SQLException ex) {
            throw DatabaseErrors.failure("Could not load attendance.", ex);
        }
        return records;
    }

    private void createTableIfMissing() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Attendance ("
                + "attendance_id INTEGER NOT NULL AUTO_INCREMENT, "
                + "learner_id VARCHAR(13) NOT NULL, "
                + "attendance_date DATE NOT NULL, "
                + "attendance_status VARCHAR(10) NOT NULL, "
                + "recorded_by VARCHAR(13) NOT NULL, "
                + "attendance_comment VARCHAR(255), "
                + "PRIMARY KEY (attendance_id), "
                + "UNIQUE KEY uq_attendance_learner_date (learner_id, attendance_date), "
                + "CONSTRAINT fk_attendance_learner FOREIGN KEY (learner_id) REFERENCES Leaner(id) "
                + "ON UPDATE CASCADE ON DELETE RESTRICT, "
                + "CONSTRAINT fk_attendance_teacher FOREIGN KEY (recorded_by) REFERENCES Teacher(teacher_id) "
                + "ON UPDATE CASCADE ON DELETE RESTRICT)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private void setValues(PreparedStatement ps, AttendanceRecord record) throws SQLException {
        ps.setString(1, record.getLearnerId());
        ps.setDate(2, Date.valueOf(record.getDate()));
        ps.setString(3, record.getStatus());
        ps.setString(4, record.getRecordedBy());
        ps.setString(5, record.getComment());
    }

    private void setUpdateValues(PreparedStatement ps, AttendanceRecord record) throws SQLException {
        ps.setString(1, record.getLearnerId());
        ps.setDate(2, Date.valueOf(record.getDate()));
        ps.setString(3, record.getStatus());
        ps.setString(4, record.getComment());
    }
}
