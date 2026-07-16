package schoolmanagement.za;

public class currentSession {
    private final Teacher currentTeacher;
    private final Login currentLogin;

    public currentSession(Teacher currentTeacher, Login currentLogin) {
        this.currentTeacher = currentTeacher;
        this.currentLogin = currentLogin;
    }

    public Teacher getCurrentTeacher() {
        return currentTeacher;
    }

    public Login getCurrentLogin() {
        return currentLogin;
    }
}
