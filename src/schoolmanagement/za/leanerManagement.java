package schoolmanagement.za;

import java.util.ArrayList;

public class leanerManagement {
    private final leanerDatabase database;

    public leanerManagement() {
        this.database = new leanerDatabase();
    }

    public boolean addLeaner(Leaner learner) {
        return database.addLeaner(learner);
    }

    public boolean removeLeaner(String id) {
        Leaner learner = new Leaner();
        learner.setId(id);
        return database.delete(learner);
    }

    public ArrayList<Leaner> getAll() {
        return database.getAll();
    }

    public boolean updateLeaner(String id, Leaner learner) {
        return database.updateLeaner(id, learner);
    }
}
