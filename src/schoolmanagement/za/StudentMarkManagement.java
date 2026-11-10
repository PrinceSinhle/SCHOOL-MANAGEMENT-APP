package schoolmanagement.za;

import java.util.ArrayList;

public class StudentMarkManagement implements AutoCloseable {
    private final StudentMarkDatabase database;

    public StudentMarkManagement() {
        this.database = new StudentMarkDatabase();
    }

    public boolean addMark(Teacher actor, StudentMark mark) {
        if (actor == null) {
            return false;
        }
        mark.setTeacherId(actor.getTeacherId());
        return database.addMark(mark);
    }

    public boolean updateMark(Teacher actor, int markId, StudentMark mark) {
        if (actor == null) {
            return false;
        }
        if (TeacherManagement.canManageLearners(actor)) {
            return database.updateMark(markId, mark);
        }
        mark.setTeacherId(actor.getTeacherId());
        return database.updateMarkForTeacher(markId, mark, actor.getTeacherId());
    }

    public boolean removeMark(Teacher actor, int markId) {
        if (actor == null) {
            return false;
        }
        return TeacherManagement.canManageLearners(actor)
                ? database.deleteMark(markId)
                : database.deleteMarkForTeacher(markId, actor.getTeacherId());
    }

    public ArrayList<StudentMark> getAll(Teacher actor) {
        if (actor == null) {
            return new ArrayList<>();
        }
        return TeacherManagement.canManageLearners(actor)
                ? database.getAll()
                : database.getAllForTeacher(actor.getTeacherId());
    }

    @Override
    public void close() {
        database.close();
    }
}
