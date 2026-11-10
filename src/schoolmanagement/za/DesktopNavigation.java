package schoolmanagement.za;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

final class DesktopNavigation {
    private DesktopNavigation() { }

    static void installBackNavigation(JFrame window, Runnable backAction) {
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                backAction.run();
            }
        });
    }

    static void returnToDashboard(JFrame currentWindow, Teacher teacher) {
        currentWindow.dispose();
        new TeacherDashboardGui(teacher).setVisible(true);
    }

    static void returnToLogin(JFrame currentWindow) {
        currentWindow.dispose();
        new Login().setVisible(true);
    }
}
