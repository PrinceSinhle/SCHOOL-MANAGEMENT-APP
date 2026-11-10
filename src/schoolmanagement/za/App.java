package schoolmanagement.za;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DesktopTheme.apply();
            new Login().setVisible(true);
        });
    }
}
