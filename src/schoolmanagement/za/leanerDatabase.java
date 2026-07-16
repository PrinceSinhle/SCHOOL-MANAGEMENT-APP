package schoolmanagement.za;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class leanerDatabase implements LeanerManagementInterface<Leaner> {
    private final Connection connection;

    public leanerDatabase() {
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

    @Override
    public boolean addLeaner(Leaner learner) {
        String sql = "INSERT INTO Leaner(name, id, contact, grade) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, learner.getFullname());
            ps.setString(2, learner.getId());
            ps.setString(3, learner.getContact());
            ps.setInt(4, learner.getGrade());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Leaner learner) {
        String sql = "DELETE FROM Leaner WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, learner.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    @Override
    public ArrayList<Leaner> getAll() {
        String sql = "SELECT name, id, contact, grade FROM Leaner";
        ArrayList<Leaner> learners = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
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
            System.out.println(ex.getMessage());
        }

        return learners;
    }

    @Override
    public boolean updateLeaner(String id, Leaner learner) {
        String sql = "UPDATE Leaner SET name=?, contact=?, grade=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, learner.getFullname());
            ps.setString(2, learner.getContact());
            ps.setInt(3, learner.getGrade());
            ps.setString(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    private void createTableIfMissing() throws SQLException {
        String sql = "CREATE TABLE Leaner ("
                + "id VARCHAR(13) NOT NULL, "
                + "name VARCHAR(100) NOT NULL, "
                + "contact VARCHAR(10) NOT NULL, "
                + "grade INT NOT NULL, "
                + "PRIMARY KEY (id))";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            if (!"X0Y32".equals(ex.getSQLState())) {
                throw ex;
            }
        }
    }
}
