package schoolmanagement.za;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ReportService {
    private ReportService() {
    }

    public static void exportMarks(Path destination, List<StudentMark> marks) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(destination, StandardCharsets.UTF_8)) {
            writer.write("Learner ID,Learner Name,Subject,Term,Mark,Total,Percent,Result,Comment\r\n");
            for (StudentMark mark : marks) {
                row(writer, mark.getLearnerId(), mark.getLearnerName(), mark.getSubject(), mark.getTerm(),
                        Double.toString(mark.getMarkObtained()), Double.toString(mark.getTotalMark()),
                        String.format(java.util.Locale.ROOT, "%.2f%%", mark.getPercentage()),
                        mark.getResult(), mark.getComment());
            }
        }
    }

    public static void exportAttendance(Path destination, List<AttendanceRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(destination, StandardCharsets.UTF_8)) {
            writer.write("Learner ID,Learner Name,Date,Status,Recorded By,Comment\r\n");
            for (AttendanceRecord record : records) {
                row(writer, record.getLearnerId(), record.getLearnerName(), record.getDate().toString(),
                        record.getStatus(), record.getRecordedBy(), record.getComment());
            }
        }
    }

    private static void row(BufferedWriter writer, String... values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) writer.write(',');
            String value = values[i] == null ? "" : values[i];
            writer.write('"');
            writer.write(value.replace("\"", "\"\""));
            writer.write('"');
        }
        writer.write("\r\n");
    }
}
