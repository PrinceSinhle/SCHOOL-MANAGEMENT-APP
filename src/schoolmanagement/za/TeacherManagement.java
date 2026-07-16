package schoolmanagement.za;

import java.util.ArrayList;

public class TeacherManagement {
    private final TeacherDatabase database;

    public TeacherManagement() {
        this.database = new TeacherDatabase();
    }

    public boolean addTeacher(Teacher teacher) {
        return database.addTeacher(teacher);
    }

    public boolean updateTeacher(String teacherId, Teacher teacher) {
        return database.updateTeacher(teacherId, teacher);
    }

    public boolean remove(String id) {
        return database.deleteTeacher(id);
    }

    public ArrayList<Teacher> getAll() {
        return database.getAll();
    }

    public boolean login(String teacherId, String password) {
        Teacher teacher = database.authenticateTeacher(teacherId);
        return teacher != null && teacher.getPassword().equals(password);
    }

    public Teacher getTeacher(String teacherId) {
        return database.getTeacher(teacherId);
    }

    public boolean canManageLearners(String teacherId) {
        Teacher teacher = database.getTeacher(teacherId);
        if (teacher == null) {
            return false;
        }

        String role = teacher.getRole();
        return "admin".equalsIgnoreCase(role) || "principal".equalsIgnoreCase(role);
    }
}
