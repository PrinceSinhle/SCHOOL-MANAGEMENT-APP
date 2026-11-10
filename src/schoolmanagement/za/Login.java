package schoolmanagement.za;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {
    private final JTextField teacherIdField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final TeacherManagement teacherManagement = new TeacherManagement();
    private final JButton setupButton = new JButton("Set Up First Administrator");
    private final JButton loginButton = new JButton("Login");

    public Login() {
        configureWindow();
        add(buildMainPanel());
        registerEvents();
        UiTasks.closeOnDispose(this, teacherManagement);
        checkFirstAdministrator();
    }

    private void configureWindow() {
        setTitle("School Management Login");
        setSize(420, 260);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel heading = new JLabel("Login", JLabel.CENTER);
        heading.setFont(heading.getFont().deriveFont(22f));

        mainPanel.add(heading, BorderLayout.NORTH);
        mainPanel.add(buildFormPanel(), BorderLayout.CENTER);
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formPanel, gbc, 0, " ID", teacherIdField);
        addFormRow(formPanel, gbc, 1, "Password", passwordField);

        return formPanel;
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel();
        setupButton.setVisible(false);
        loginButton.setEnabled(false);
        DesktopTheme.stylePrimary(loginButton);
        DesktopTheme.stylePrimary(setupButton);

        setupButton.addActionListener(event -> openTeacherRegistration());
        loginButton.addActionListener(event -> login());

        buttonPanel.add(setupButton);
        buttonPanel.add(loginButton);
        return buttonPanel;
    }

    private void registerEvents() {
        passwordField.addActionListener(event -> login());
    }

    private void login() {
        String teacherId = teacherIdField.getText().trim();
        char[] password = passwordField.getPassword();

        if (teacherId.isEmpty() || password.length == 0) {
            java.util.Arrays.fill(password, '\0');
            showError("Please enter your teacher ID and password.");
            return;
        }
        passwordField.setText("");
        UiTasks.run(this, "Signing in", () -> {
            try {
                boolean authenticated = teacherManagement.login(teacherId, password);
                return new LoginResult(authenticated,
                        authenticated ? teacherManagement.getTeacher(teacherId) : null);
            } finally {
                java.util.Arrays.fill(password, '\0');
            }
        }, result -> {
            if (result.authenticated) {
                new TeacherDashboardGui(result.teacher).setVisible(true);
                dispose();
            } else {
                showError("Invalid teacher ID or password.");
            }
        });
    }

    private void openTeacherRegistration() {
        UiTasks.run(this, "Checking first-run setup", () -> teacherManagement.hasAdministrator(), hasAdmin -> {
            if (hasAdmin) {
                showError("The first administrator is already set up. Sign in to manage staff.");
                setupButton.setVisible(false);
            } else {
                new TeacherGui(null, true).setVisible(true);
                dispose();
            }
        });
    }

    private void checkFirstAdministrator() {
        UiTasks.run(this, "Connecting to the school database", () -> teacherManagement.hasAdministrator(), hasAdmin -> {
            setupButton.setVisible(!hasAdmin);
            loginButton.setEnabled(true);
        });
    }

    private static final class LoginResult {
        private final boolean authenticated;
        private final Teacher teacher;

        private LoginResult(boolean authenticated, Teacher teacher) {
            this.authenticated = authenticated;
            this.teacher = teacher;
        }
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}
