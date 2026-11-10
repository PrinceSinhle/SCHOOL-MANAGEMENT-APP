package schoolmanagement.za;

import java.sql.SQLException;

final class DatabaseErrors {
    private DatabaseErrors() {
    }

    static boolean isConstraintViolation(SQLException error) {
        String state = error.getSQLState();
        return state != null && state.startsWith("23");
    }

    static IllegalStateException failure(String action, SQLException error) {
        return new IllegalStateException(action + " Please check that MySQL Server is running and the app account has access.", error);
    }
}
