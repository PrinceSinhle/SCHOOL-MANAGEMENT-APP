package schoolmanagement.za;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class BackupService {
    private static final String VERSION = "SCHOOL-MANAGEMENT-BACKUP-1";
    private static final String[] ENTRY_NAMES = {"Teacher.tsv", "Leaner.tsv", "StudentMark.tsv", "Attendance.tsv"};

    private BackupService() {
    }

    public static void createBackup(Path destination) throws IOException {
        ensureTables();
        Path absolute = destination.toAbsolutePath();
        Path temporary = Files.createTempFile(absolute.getParent(), ".school-backup-", ".tmp");
        boolean completed = false;
        try {
            try (Connection connection = DatabaseConfig.openConnection()) {
                connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                connection.setAutoCommit(false);
                try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(temporary), StandardCharsets.UTF_8)) {
                    writeEntry(zip, "backup-version.txt", (VERSION + "\n").getBytes(StandardCharsets.UTF_8));
                    writeTable(zip, connection, ENTRY_NAMES[0],
                            "SELECT teacher_id, full_name, phone_number, qualification, password, teacher_role FROM Teacher");
                    writeTable(zip, connection, ENTRY_NAMES[1],
                            "SELECT name, id, contact, grade FROM Leaner");
                    writeTable(zip, connection, ENTRY_NAMES[2],
                            "SELECT mark_id, learner_id, learner_name, subject, term, mark_obtained, total_mark, teacher_id, teacher_comment FROM StudentMark");
                    writeTable(zip, connection, ENTRY_NAMES[3],
                            "SELECT attendance_id, learner_id, attendance_date, attendance_status, recorded_by, attendance_comment FROM Attendance");
                    connection.commit();
                } catch (SQLException | IOException ex) {
                    connection.rollback();
                    throw ex;
                }
            } catch (SQLException ex) {
                throw new IOException("Could not create the backup. Check the MySQL connection and permissions.", ex);
            }
            try {
                Files.move(temporary, absolute, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporary, absolute, StandardCopyOption.REPLACE_EXISTING);
            }
            completed = true;
        } finally {
            if (!completed) Files.deleteIfExists(temporary);
        }
    }

    public static void restoreBackup(Path source) throws IOException {
        ensureTables();
        Map<String, String> entries = readArchive(source);
        if (!VERSION.equals(entries.get("backup-version.txt"))) {
            throw new IOException("This backup is missing or uses an unsupported format.");
        }

        List<String[]> teachers = parse(entries, ENTRY_NAMES[0], 6);
        List<String[]> learners = parse(entries, ENTRY_NAMES[1], 4);
        List<String[]> marks = parse(entries, ENTRY_NAMES[2], 9);
        List<String[]> attendance = parse(entries, ENTRY_NAMES[3], 6);
        boolean hasAdmin = teachers.stream().anyMatch(row -> "admin".equalsIgnoreCase(row[5]));
        if (!hasAdmin) throw new IOException("The backup has no administrator account and cannot be restored.");

        try (Connection connection = DatabaseConfig.openConnection()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM Attendance");
                statement.executeUpdate("DELETE FROM StudentMark");
                statement.executeUpdate("DELETE FROM Leaner");
                statement.executeUpdate("DELETE FROM Teacher");
                insertTeachers(connection, teachers);
                insertLearners(connection, learners);
                insertMarks(connection, marks);
                insertAttendance(connection, attendance);
                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                if (ex instanceof IOException) throw (IOException) ex;
                throw new IOException("Restore failed. The current database has been kept unchanged.", ex);
            }
        } catch (SQLException ex) {
            throw new IOException("Could not restore the backup. Check the MySQL connection and permissions.", ex);
        }
    }

    private static void ensureTables() {
        try (TeacherDatabase teachers = new TeacherDatabase();
             leanerDatabase learners = new leanerDatabase();
             StudentMarkDatabase marks = new StudentMarkDatabase();
             AttendanceDatabase attendance = new AttendanceDatabase()) {
            teachers.getAll();
            learners.getAll();
            marks.getAll();
            attendance.getAll();
        }
    }

    private static void writeTable(ZipOutputStream zip, Connection connection, String entryName,
                                   String sql) throws SQLException, IOException {
        zip.putNextEntry(new ZipEntry(entryName));
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int columns = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                StringBuilder line = new StringBuilder();
                for (int i = 1; i <= columns; i++) {
                    if (i > 1) line.append('\t');
                    String value = rs.getString(i);
                    line.append(value == null ? "~" : Base64.getEncoder().encodeToString(
                            value.getBytes(StandardCharsets.UTF_8)));
                }
                line.append('\n');
                zip.write(line.toString().getBytes(StandardCharsets.UTF_8));
            }
        }
        zip.closeEntry();
    }

    private static void writeEntry(ZipOutputStream zip, String name, byte[] value) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(value);
        zip.closeEntry();
    }

    private static Map<String, String> readArchive(Path source) throws IOException {
        Map<String, String> entries = new HashMap<>();
        try (InputStream file = Files.newInputStream(source); ZipInputStream zip = new ZipInputStream(file, StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = zip.read(buffer)) != -1) bytes.write(buffer, 0, read);
                    entries.put(entry.getName(), new String(bytes.toByteArray(), StandardCharsets.UTF_8));
                }
                zip.closeEntry();
            }
        }
        return entries;
    }

    private static List<String[]> parse(Map<String, String> entries, String name, int columns) throws IOException {
        String contents = entries.get(name);
        if (contents == null) throw new IOException("Backup is missing " + name + ".");
        List<String[]> rows = new ArrayList<>();
        for (String line : contents.split("\\R")) {
            if (line.isEmpty()) continue;
            String[] encoded = line.split("\\t", -1);
            if (encoded.length != columns) throw new IOException("Backup contains an invalid row in " + name + ".");
            String[] values = new String[columns];
            for (int i = 0; i < columns; i++) {
                try {
                    values[i] = "~".equals(encoded[i]) ? null
                            : new String(Base64.getDecoder().decode(encoded[i]), StandardCharsets.UTF_8);
                } catch (IllegalArgumentException ex) {
                    throw new IOException("Backup contains invalid data in " + name + ".", ex);
                }
            }
            rows.add(values);
        }
        return rows;
    }

    private static void insertTeachers(Connection connection, List<String[]> rows) throws SQLException {
        String sql = "INSERT INTO Teacher (teacher_id, full_name, phone_number, qualification, password, teacher_role) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String[] row : rows) {
                for (int i = 0; i < row.length; i++) ps.setString(i + 1, row[i]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void insertLearners(Connection connection, List<String[]> rows) throws SQLException {
        String sql = "INSERT INTO Leaner (name, id, contact, grade) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String[] row : rows) {
                ps.setString(1, row[0]); ps.setString(2, row[1]); ps.setString(3, row[2]);
                ps.setInt(4, Integer.parseInt(row[3])); ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void insertMarks(Connection connection, List<String[]> rows) throws SQLException {
        String sql = "INSERT INTO StudentMark (mark_id, learner_id, learner_name, subject, term, mark_obtained, total_mark, teacher_id, teacher_comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String[] row : rows) {
                ps.setInt(1, Integer.parseInt(row[0]));
                for (int i = 1; i <= 4; i++) ps.setString(i + 1, row[i]);
                ps.setDouble(6, Double.parseDouble(row[5])); ps.setDouble(7, Double.parseDouble(row[6]));
                ps.setString(8, row[7]); ps.setString(9, row[8]); ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void insertAttendance(Connection connection, List<String[]> rows) throws SQLException {
        String sql = "INSERT INTO Attendance (attendance_id, learner_id, attendance_date, attendance_status, recorded_by, attendance_comment) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String[] row : rows) {
                ps.setInt(1, Integer.parseInt(row[0]));
                ps.setString(2, row[1]); ps.setDate(3, Date.valueOf(row[2]));
                ps.setString(4, row[3]); ps.setString(5, row[4]); ps.setString(6, row[5]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
