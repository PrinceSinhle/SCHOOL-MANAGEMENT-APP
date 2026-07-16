package schoolmanagement.za;

import java.util.ArrayList;

public interface LeanerManagementInterface<T> {
    boolean addLeaner(T learner);

    ArrayList<T> getAll();

    boolean delete(T learner);

    boolean updateLeaner(String id, T learner);
}
