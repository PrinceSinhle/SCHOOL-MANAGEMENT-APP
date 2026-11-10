package schoolmanagement.za;

import java.time.LocalDate;

public class AttendanceRecord {
    private int attendanceId;
    private String learnerId;
    private String learnerName;
    private LocalDate date;
    private String status;
    private String recordedBy;
    private String comment;

    public AttendanceRecord(int attendanceId, String learnerId, String learnerName,
                            LocalDate date, String status, String recordedBy, String comment) {
        this.attendanceId = attendanceId;
        this.learnerId = learnerId;
        this.learnerName = learnerName;
        this.date = date;
        this.status = status;
        this.recordedBy = recordedBy;
        this.comment = comment;
    }

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }
    public String getLearnerId() { return learnerId; }
    public String getLearnerName() { return learnerName; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }
    public String getComment() { return comment; }
}
