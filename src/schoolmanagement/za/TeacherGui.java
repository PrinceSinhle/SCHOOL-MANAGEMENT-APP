package schoolmanagement.za;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class TeacherGui extends JFrame {
    private static final String[] ROLES = {"Select role", "admin", "principal", "teacher"};
    private static final String[] COLUMNS = {
        "Full Name", "Teacher ID", "Phone Number", "Role", "Qualification"
    };

    private final JTextField nameField = new JTextField(20);
    private final JTextField idField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JComboBox<String> roleComboBox = new JComboBox<>(ROLES);
    private final ButtonGroup qualificationGroup = new ButtonGroup();
    private final JRadioButton phdButton = new JRadioButton("PHD");
    private final JRadioButton medButton = new JRadioButton("M.Ed");
    private final JRadioButton bedButton = new JRadioButton("B.Ed");
    private final JRadioButton ledButton = new JRadioButton("L.Ed");
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final TeacherManagement teacherManagement = new TeacherManagement();

    public TeacherGui() {
        configureWindow();
        configureQualificationButtons();
        add(buildMainPanel());
        registerEvents();
    }

    private void configureWindow() {
        setTitle("Teacher Management");
        setSize(760, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void configureQualificationButtons() {
        qualificationGroup.add(phdButton);
        qualificationGroup.add(medButton);
        qualificationGroup.add(bedButton);
        qualificationGroup.add(ledButton);
    }

    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        mainPanel.add(buildInputPanel(), BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel buildInputPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(12, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Teacher Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formPanel, gbc, 0, "Full Name", nameField);
        addFormRow(formPanel, gbc, 1, "Teacher ID", idField);
        addFormRow(formPanel, gbc, 2, "Phone Number", phoneField);
        addFormRow(formPanel, gbc, 3, "Password", passwordField);
        addFormRow(formPanel, gbc, 4, "Role", roleComboBox);

        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.add(buildQualificationPanel(), BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel buildQualificationPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 8, 0));
        panel.setBorder(new TitledBorder("Qualification"));
        panel.add(phdButton);
        panel.add(medButton);
        panel.add(bedButton);
        panel.add(ledButton);
        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 8, 0));

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton resetButton = new JButton("Reset");
        JButton loadButton = new JButton("Load");

        addButton.addActionListener(event -> addTeacher());
        updateButton.addActionListener(event -> updateTeacher());
        deleteButton.addActionListener(event -> deleteTeacher());
        resetButton.addActionListener(event -> resetForm());
        loadButton.addActionListener(event -> loadTeachers());

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(resetButton);
        panel.add(loadButton);

        return panel;
    }

    private void registerEvents() {
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                populateFormFromSelectedRow();
            }
        });
    }

    private void addTeacher() {
        Teacher teacher = buildTeacherFromForm();
        if (teacher == null) {
            return;
        }

        if (teacherManagement.addTeacher(teacher)) {
            addTeacherToTable(teacher);
            resetForm();
            JOptionPane.showMessageDialog(this, "Teacher added successfully.");
        } else {
            showError("Teacher was not added. Check whether the ID already exists.");
        }
    }

    private void updateTeacher() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a teacher to update.");
            return;
        }

        Teacher teacher = buildTeacherFromForm();
        if (teacher == null) {
            return;
        }

        String originalId = tableModel.getValueAt(selectedRow, 1).toString();
        if (teacherManagement.updateTeacher(originalId, teacher)) {
            tableModel.setValueAt(teacher.getFullName(), selectedRow, 0);
            tableModel.setValueAt(teacher.getTeacherId(), selectedRow, 1);
            tableModel.setValueAt(teacher.getPhoneNumber(), selectedRow, 2);
            tableModel.setValueAt(teacher.getRole(), selectedRow, 3);
            tableModel.setValueAt(teacher.getQualification(), selectedRow, 4);
            resetForm();
            JOptionPane.showMessageDialog(this, "Teacher updated successfully.");
        } else {
            showError("Teacher was not updated.");
        }
    }

    private void deleteTeacher() {
        String teacherId = idField.getText().trim();
        if (teacherId.isEmpty()) {
            teacherId = JOptionPane.showInputDialog(this, "Enter teacher ID to remove:");
        }

        if (teacherId == null || teacherId.trim().isEmpty()) {
            return;
        }

        if (teacherManagement.remove(teacherId.trim())) {
            removeTeacherFromTable(teacherId.trim());
            resetForm();
            JOptionPane.showMessageDialog(this, "Teacher removed successfully.");
        } else {
            showError("Teacher was not removed.");
        }
    }

    private void loadTeachers() {
        tableModel.setRowCount(0);
        ArrayList<Teacher> teachers = teacherManagement.getAll();
        for (Teacher teacher : teachers) {
            addTeacherToTable(teacher);
        }
    }

    private Teacher buildTeacherFromForm() {
        String fullName = nameField.getText().trim();
        String teacherId = idField.getText().trim();
        String phoneNumber = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = roleComboBox.getSelectedItem().toString();
        String qualification = getSelectedQualification();

        if (fullName.isEmpty()) {
            return showValidationError("Full name cannot be empty.", nameField);
        }

        if (!teacherId.matches("\\d{13}")) {
            return showValidationError("Teacher ID must be exactly 13 digits.", idField);
        }

        if (!phoneNumber.matches("0\\d{9}")) {
            return showValidationError("Phone number must start with 0 and be exactly 10 digits.", phoneField);
        }

        if (password.length() < 10 || !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?\\d].*")) {
            return showValidationError("Password must be at least 10 characters and contain a number or special character.", passwordField);
        }

        if (roleComboBox.getSelectedIndex() == 0) {
            return showValidationError("Please select a role.", roleComboBox);
        }

        if (qualification.isEmpty()) {
            showError("Please select a qualification.");
            return null;
        }

        return new Teacher(teacherId, fullName, phoneNumber, qualification, password, role);
    }

    private String getSelectedQualification() {
        if (phdButton.isSelected()) {
            return "PHD";
        }
        if (medButton.isSelected()) {
            return "M.Ed";
        }
        if (bedButton.isSelected()) {
            return "B.Ed";
        }
        if (ledButton.isSelected()) {
            return "L.Ed";
        }
        return "";
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        nameField.setText(tableModel.getValueAt(selectedRow, 0).toString());
        idField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        phoneField.setText(tableModel.getValueAt(selectedRow, 2).toString());
        roleComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 3).toString());
        selectQualification(tableModel.getValueAt(selectedRow, 4).toString());
    }

    private void selectQualification(String qualification) {
        qualificationGroup.clearSelection();
        if ("PHD".equalsIgnoreCase(qualification)) {
            phdButton.setSelected(true);
        } else if ("M.Ed".equalsIgnoreCase(qualification)) {
            medButton.setSelected(true);
        } else if ("B.Ed".equalsIgnoreCase(qualification)) {
            bedButton.setSelected(true);
        } else if ("L.Ed".equalsIgnoreCase(qualification)) {
            ledButton.setSelected(true);
        }
    }

    private void addTeacherToTable(Teacher teacher) {
        tableModel.addRow(new Object[] {
            teacher.getFullName(),
            teacher.getTeacherId(),
            teacher.getPhoneNumber(),
            teacher.getRole(),
            teacher.getQualification()
        });
    }

    private void removeTeacherFromTable(String teacherId) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (teacherId.equals(tableModel.getValueAt(row, 1))) {
                tableModel.removeRow(row);
                return;
            }
        }
    }

    private void resetForm() {
        nameField.setText("");
        idField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
        qualificationGroup.clearSelection();
        table.clearSelection();
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private Teacher showValidationError(String message, java.awt.Component field) {
        showError(message);
        field.requestFocus();
        return null;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}
