package schoolmanagement.za;

import java.util.ArrayList;

public class AttendanceManagement implements AutoCloseable {
    private final AttendanceDatabase database = new AttendanceDatabase();

    public ArrayList<AttendanceRecord> getAll(Teacher actor) {
        if (actor == null) return new ArrayList<>();
        return TeacherManagement.canManageLearners(actor)
                ? database.getAll() : database.getAllForTeacher(actor.getTeacherId());
    }

    public boolean add(Teacher actor, AttendanceRecord record) {
        if (actor == null) return false;
        record.setRecordedBy(actor.getTeacherId());
        return database.add(record);
    }

    public boolean update(Teacher actor, AttendanceRecord record) {
        if (actor == null) return false;
        return TeacherManagement.canManageLearners(actor)
                ? database.update(record) : database.updateForTeacher(record, actor.getTeacherId());
    }

    public boolean delete(Teacher actor, int attendanceId) {
        if (actor == null) return false;
        return TeacherManagement.canManageLearners(actor)
                ? database.delete(attendanceId) : database.deleteForTeacher(attendanceId, actor.getTeacherId());
    }

    @Override
    public void close() {
        database.close();
    }
}
