package schoolmanagement.za;

import java.util.ArrayList;

public class leanerManagement implements AutoCloseable {
    private final leanerDatabase database;

    public leanerManagement() {
        this.database = new leanerDatabase();
    }

    public boolean addLeaner(Teacher actor, Leaner learner) {
        return TeacherManagement.canManageLearners(actor) && database.addLeaner(learner);
    }

    public boolean removeLeaner(Teacher actor, String id) {
        if (!TeacherManagement.canManageLearners(actor)) {
            return false;
        }
        Leaner learner = new Leaner();
        learner.setId(id);
        return database.delete(learner);
    }

    public ArrayList<Leaner> getAll(Teacher actor) {
        return actor != null ? database.getAll() : new ArrayList<>();
    }

    public boolean updateLeaner(Teacher actor, String id, Leaner learner) {
        return TeacherManagement.canManageLearners(actor) && database.updateLeaner(id, learner);
    }

    @Override
    public void close() {
        database.close();
    }
}
