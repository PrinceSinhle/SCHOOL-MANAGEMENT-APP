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

    public Login() {
        configureWindow();
        add(buildMainPanel());
        registerEvents();
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

        addFormRow(formPanel, gbc, 0, "Teacher ID", teacherIdField);
        addFormRow(formPanel, gbc, 1, "Password", passwordField);

        return formPanel;
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JButton registerButton = new JButton("Register Teacher");
        JButton loginButton = new JButton("Login");

        registerButton.addActionListener(event -> openTeacherRegistration());
        loginButton.addActionListener(event -> login());

        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);
        return buttonPanel;
    }

    private void registerEvents() {
        passwordField.addActionListener(event -> login());
    }

    private void login() {
        String teacherId = teacherIdField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (teacherId.isEmpty() || password.isEmpty()) {
            showError("Please enter your teacher ID and password.");
            return;
        }

        if (teacherManagement.login(teacherId, password)) {
            JOptionPane.showMessageDialog(this, "Login successful.");
            new TeacherDashboardGui(teacherManagement.getTeacher(teacherId)).setVisible(true);
            dispose();
        } else {
            showError("Invalid teacher ID or password.");
        }
    }

    private void openTeacherRegistration() {
        new TeacherGui().setVisible(true);
        dispose();
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
