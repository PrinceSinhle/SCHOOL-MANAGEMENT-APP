package schoolmanagement.za;

public class Leaner {
    private String fullname;
    private int grade;
    private String id;
    private String contact;

    public Leaner() {
    }

    public Leaner(String fullname, int grade, String id, String contact) {
        this.fullname = fullname;
        this.grade = grade;
        this.id = id;
        this.contact = contact;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "Leaner{"
                + "fullname='" + fullname + '\''
                + ", grade=" + grade
                + ", id='" + id + '\''
                + ", contact='" + contact + '\''
                + '}';
    }
}
