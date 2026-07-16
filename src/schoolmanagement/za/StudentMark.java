package schoolmanagement.za;

public class StudentMark {
    private int markId;
    private String learnerId;
    private String learnerName;
    private String subject;
    private String term;
    private double markObtained;
    private double totalMark;
    private String teacherId;
    private String comment;

    public StudentMark() {
    }

    public StudentMark(int markId, String learnerId, String learnerName, String subject, String term,
                       double markObtained, double totalMark, String teacherId, String comment) {
        this.markId = markId;
        this.learnerId = learnerId;
        this.learnerName = learnerName;
        this.subject = subject;
        this.term = term;
        this.markObtained = markObtained;
        this.totalMark = totalMark;
        this.teacherId = teacherId;
        this.comment = comment;
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public String getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(String learnerId) {
        this.learnerId = learnerId;
    }

    public String getLearnerName() {
        return learnerName;
    }

    public void setLearnerName(String learnerName) {
        this.learnerName = learnerName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public double getMarkObtained() {
        return markObtained;
    }

    public void setMarkObtained(double markObtained) {
        this.markObtained = markObtained;
    }

    public double getTotalMark() {
        return totalMark;
    }

    public void setTotalMark(double totalMark) {
        this.totalMark = totalMark;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getPercentage() {
        if (totalMark <= 0) {
            return 0;
        }
        return (markObtained / totalMark) * 100;
    }

    public String getResult() {
        return getPercentage() >= 50 ? "Pass" : "Fail";
    }
}
