package schoolmanagement.za;

import java.util.ArrayList;

public class TeacherManagement implements AutoCloseable {
    private final TeacherDatabase database;

    public TeacherManagement() {
        this.database = new TeacherDatabase();
    }

    public boolean addTeacher(Teacher actor, Teacher teacher) {
        return isAdministrator(actor) && database.addTeacher(teacher);
    }

    public boolean addFirstAdministrator(Teacher teacher) {
        if (teacher == null || !isAdministrator(teacher)) {
            return false;
        }
        return database.addFirstAdministrator(teacher);
    }

    public boolean hasAdministrator() {
        return database.hasAdministrator();
    }

    public boolean updateTeacher(Teacher actor, String teacherId, Teacher teacher) {
        if (!isAdministrator(actor)) {
            return false;
        }
        Teacher existing = database.getTeacher(teacherId);
        if (existing == null) {
            return false;
        }
        if (isAdministrator(existing) && !isAdministrator(teacher)
                && database.countAdministrators() <= 1) {
            return false;
        }
        return database.updateTeacher(teacherId, teacher);
    }

    public boolean remove(Teacher actor, String id) {
        if (!isAdministrator(actor) || actor.getTeacherId().equals(id)) {
            return false;
        }
        Teacher target = database.getTeacher(id);
        if (target == null || (isAdministrator(target) && database.countAdministrators() <= 1)) {
            return false;
        }
        return database.deleteTeacher(id);
    }

    public ArrayList<Teacher> getAll(Teacher actor) {
        if (!isAdministrator(actor)) {
            return new ArrayList<>();
        }
        return database.getAll();
    }

    public boolean login(String teacherId, char[] password) {
        Teacher teacher = database.authenticateTeacher(teacherId);
        if (teacher == null || !PasswordHasher.verify(new String(password), teacher.getPassword())) {
            return false;
        }
        if (!PasswordHasher.isEncoded(teacher.getPassword())) {
            return database.updatePassword(teacher.getTeacherId(), PasswordHasher.hash(new String(password)));
        }
        return true;
    }

    public Teacher getTeacher(String teacherId) {
        return database.getTeacher(teacherId);
    }

    public static boolean isAdministrator(Teacher teacher) {
        return teacher != null && "admin".equalsIgnoreCase(teacher.getRole());
    }

    public static boolean canManageLearners(Teacher teacher) {
        return isAdministrator(teacher)
                || (teacher != null && "principal".equalsIgnoreCase(teacher.getRole()));
    }

    @Override
    public void close() {
        database.close();
    }
}
