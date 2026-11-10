package schoolmanagement.za;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class leanerDatabase implements LeanerManagementInterface<Leaner>, AutoCloseable {
    private Connection connection;

    public leanerDatabase() {
    }

    @Override
    public synchronized void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ex) { throw DatabaseErrors.failure("Could not close the learner database connection.", ex); }
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

    @Override
    public boolean addLeaner(Leaner learner) {
        String sql = "INSERT INTO Leaner(name, id, contact, grade) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setString(1, learner.getFullname());
            ps.setString(2, learner.getId());
            ps.setString(3, learner.getContact());
            ps.setInt(4, learner.getGrade());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not add learner.", ex);
        }
    }

    @Override
    public boolean delete(Leaner learner) {
        String sql = "DELETE FROM Leaner WHERE id=?";

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setString(1, learner.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not remove learner.", ex);
        }
    }

    @Override
    public ArrayList<Leaner> getAll() {
        String sql = "SELECT name, id, contact, grade FROM Leaner";
        ArrayList<Leaner> learners = new ArrayList<>();

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                learners.add(new Leaner(
                        rs.getString("name"),
                        rs.getInt("grade"),
                        rs.getString("id"),
                        rs.getString("contact")
                ));
            }
        } catch (SQLException ex) {
            throw DatabaseErrors.failure("Could not load learner register.", ex);
        }

        return learners;
    }

    @Override
    public boolean updateLeaner(String id, Leaner learner) {
        String sql = "UPDATE Leaner SET name=?, contact=?, grade=? WHERE id=?";

        try (PreparedStatement ps = ensureConnection().prepareStatement(sql)) {
            ps.setString(1, learner.getFullname());
            ps.setString(2, learner.getContact());
            ps.setInt(3, learner.getGrade());
            ps.setString(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (DatabaseErrors.isConstraintViolation(ex)) return false;
            throw DatabaseErrors.failure("Could not update learner.", ex);
        }
    }

    private void createTableIfMissing() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Leaner ("
                + "id VARCHAR(13) NOT NULL, "
                + "name VARCHAR(100) NOT NULL, "
                + "contact VARCHAR(10) NOT NULL, "
                + "grade INT NOT NULL, "
                + "PRIMARY KEY (id))";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
}
