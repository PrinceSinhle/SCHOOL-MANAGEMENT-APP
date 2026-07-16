package schoolmanagement.za;

import java.util.ArrayList;

public class StudentMarkManagement {
    private final StudentMarkDatabase database;

    public StudentMarkManagement() {
        this.database = new StudentMarkDatabase();
    }

    public boolean addMark(StudentMark mark) {
        return database.addMark(mark);
    }

    public boolean updateMark(int markId, StudentMark mark) {
        return database.updateMark(markId, mark);
    }

    public boolean removeMark(int markId) {
        return database.deleteMark(markId);
    }

    public ArrayList<StudentMark> getAll() {
        return database.getAll();
    }
}
