package schoolmanagement.za;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class TeacherDashboardGui extends JFrame {
    private static final String[] TERMS = {"Select term", "Term 1", "Term 2", "Term 3", "Term 4"};
    private static final String[] SUBJECTS = {
        "Select subject", "Mathematics", "English", "Life Skills", "Natural Sciences",
        "Social Sciences", "Technology", "Creative Arts"
    };
    private static final String[] COLUMNS = {
        "Mark ID", "Learner ID", "Learner Name", "Subject", "Term", "Mark", "Total", "Percent", "Result", "Comment"
    };
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00");

    private final Teacher currentTeacher;
    private final JTextField learnerIdField = new JTextField(18);
    private final JTextField learnerNameField = new JTextField(18);
    private final JTextField markField = new JTextField(18);
    private final JTextField totalField = new JTextField(18);
    private final JTextField commentField = new JTextField(18);
    private final JComboBox<String> subjectComboBox = new JComboBox<>(SUBJECTS);
    private final JComboBox<String> termComboBox = new JComboBox<>(TERMS);
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final StudentMarkManagement markManagement = new StudentMarkManagement();

    public TeacherDashboardGui(Teacher currentTeacher) {
        this.currentTeacher = currentTeacher;
        configureWindow();
        configureTable();
        add(buildMainPanel());
        registerEvents();
        loadMarks();
    }

    private void configureWindow() {
        setTitle("Teacher Dashboard");
        setSize(940, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void configureTable() {
        table.removeColumn(table.getColumnModel().getColumn(0));
    }

    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        mainPanel.add(buildHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel buildHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));

        JLabel heading = new JLabel("Teacher Dashboard", JLabel.CENTER);
        heading.setFont(heading.getFont().deriveFont(22f));

        String teacherName = currentTeacher == null ? "Teacher" : currentTeacher.getFullName();
        JLabel teacherLabel = new JLabel("Logged in as: " + teacherName, JLabel.CENTER);

        wrapper.add(heading, BorderLayout.NORTH);
        wrapper.add(teacherLabel, BorderLayout.CENTER);
        wrapper.add(buildFormPanel(), BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Record Student Marks"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formPanel, gbc, 0, 0, "Learner ID", learnerIdField);
        addFormRow(formPanel, gbc, 0, 2, "Learner Name", learnerNameField);
        addFormRow(formPanel, gbc, 1, 0, "Subject", subjectComboBox);
        addFormRow(formPanel, gbc, 1, 2, "Term", termComboBox);
        addFormRow(formPanel, gbc, 2, 0, "Mark Obtained", markField);
        addFormRow(formPanel, gbc, 2, 2, "Total Mark", totalField);
        addFormRow(formPanel, gbc, 3, 0, "Comment", commentField);

        return formPanel;
    }

    private JPanel buildButtonPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));

        JPanel actionsPanel = new JPanel(new GridLayout(1, 5, 8, 0));
        JButton addButton = new JButton("Add Mark");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton resetButton = new JButton("Reset");
        JButton loadButton = new JButton("Load");

        addButton.addActionListener(event -> addMark());
        updateButton.addActionListener(event -> updateMark());
        deleteButton.addActionListener(event -> deleteMark());
        resetButton.addActionListener(event -> resetForm());
        loadButton.addActionListener(event -> loadMarks());

        actionsPanel.add(addButton);
        actionsPanel.add(updateButton);
        actionsPanel.add(deleteButton);
        actionsPanel.add(resetButton);
        actionsPanel.add(loadButton);

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton learnerButton = new JButton("Manage Learners");
        JButton logoutButton = new JButton("Logout");

        learnerButton.addActionListener(event -> new leanerGUI().setVisible(true));
        logoutButton.addActionListener(event -> logout());

        navigationPanel.add(learnerButton);
        navigationPanel.add(logoutButton);

        wrapper.add(actionsPanel, BorderLayout.CENTER);
        wrapper.add(navigationPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private void registerEvents() {
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                populateFormFromSelectedRow();
            }
        });
    }

    private void addMark() {
        StudentMark mark = buildMarkFromForm();
        if (mark == null) {
            return;
        }

        if (markManagement.addMark(mark)) {
            addMarkToTable(mark);
            resetForm();
            JOptionPane.showMessageDialog(this, "Student mark recorded successfully.");
        } else {
            showError("Student mark was not recorded.");
        }
    }

    private void updateMark() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a mark to update.");
            return;
        }

        StudentMark mark = buildMarkFromForm();
        if (mark == null) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int markId = (Integer) tableModel.getValueAt(modelRow, 0);
        mark.setMarkId(markId);

        if (markManagement.updateMark(markId, mark)) {
            updateTableRow(modelRow, mark);
            resetForm();
            JOptionPane.showMessageDialog(this, "Student mark updated successfully.");
        } else {
            showError("Student mark was not updated.");
        }
    }

    private void deleteMark() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a mark to delete.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int markId = (Integer) tableModel.getValueAt(modelRow, 0);

        if (markManagement.removeMark(markId)) {
            tableModel.removeRow(modelRow);
            resetForm();
            JOptionPane.showMessageDialog(this, "Student mark deleted successfully.");
        } else {
            showError("Student mark was not deleted.");
        }
    }

    private void loadMarks() {
        tableModel.setRowCount(0);
        ArrayList<StudentMark> marks = markManagement.getAll();
        for (StudentMark mark : marks) {
            addMarkToTable(mark);
        }
    }

    private StudentMark buildMarkFromForm() {
        String learnerId = learnerIdField.getText().trim();
        String learnerName = learnerNameField.getText().trim();
        String subject = subjectComboBox.getSelectedItem().toString();
        String term = termComboBox.getSelectedItem().toString();
        String comment = commentField.getText().trim();
        double markObtained;
        double totalMark;

        if (learnerId.isEmpty() || learnerName.isEmpty()) {
            showError("Please enter the learner ID and learner name.");
            return null;
        }

        if (!learnerId.matches("\\d{13}")) {
            return showValidationError("Learner ID must be exactly 13 digits.", learnerIdField);
        }

        if (!learnerName.matches("[a-zA-Z\\s]+")) {
            return showValidationError("Learner name can only contain letters and spaces.", learnerNameField);
        }

        if (subjectComboBox.getSelectedIndex() == 0) {
            showError("Please select a subject.");
            return null;
        }

        if (termComboBox.getSelectedIndex() == 0) {
            showError("Please select a term.");
            return null;
        }

        try {
            markObtained = Double.parseDouble(markField.getText().trim());
            totalMark = Double.parseDouble(totalField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Mark and total must be valid numbers.");
            return null;
        }

        if (totalMark <= 0) {
            return showValidationError("Total mark must be greater than zero.", totalField);
        }

        if (markObtained < 0 || markObtained > totalMark) {
            return showValidationError("Mark obtained must be between 0 and the total mark.", markField);
        }

        String teacherId = currentTeacher == null ? "" : currentTeacher.getTeacherId();
        return new StudentMark(0, learnerId, learnerName, subject, term, markObtained, totalMark, teacherId, comment);
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        learnerIdField.setText(tableModel.getValueAt(modelRow, 1).toString());
        learnerNameField.setText(tableModel.getValueAt(modelRow, 2).toString());
        subjectComboBox.setSelectedItem(tableModel.getValueAt(modelRow, 3).toString());
        termComboBox.setSelectedItem(tableModel.getValueAt(modelRow, 4).toString());
        markField.setText(tableModel.getValueAt(modelRow, 5).toString());
        totalField.setText(tableModel.getValueAt(modelRow, 6).toString());
        commentField.setText(tableModel.getValueAt(modelRow, 9).toString());
    }

    private void addMarkToTable(StudentMark mark) {
        tableModel.addRow(buildRow(mark));
    }

    private void updateTableRow(int row, StudentMark mark) {
        Object[] values = buildRow(mark);
        for (int column = 0; column < values.length; column++) {
            tableModel.setValueAt(values[column], row, column);
        }
    }

    private Object[] buildRow(StudentMark mark) {
        return new Object[] {
            mark.getMarkId(),
            mark.getLearnerId(),
            mark.getLearnerName(),
            mark.getSubject(),
            mark.getTerm(),
            mark.getMarkObtained(),
            mark.getTotalMark(),
            PERCENT_FORMAT.format(mark.getPercentage()) + "%",
            mark.getResult(),
            mark.getComment()
        };
    }

    private void resetForm() {
        learnerIdField.setText("");
        learnerNameField.setText("");
        markField.setText("");
        totalField.setText("");
        commentField.setText("");
        subjectComboBox.setSelectedIndex(0);
        termComboBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void logout() {
        new Login().setVisible(true);
        dispose();
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, int column,
                            String labelText, java.awt.Component field) {
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = column + 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private StudentMark showValidationError(String message, java.awt.Component field) {
        showError(message);
        field.requestFocus();
        return null;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}
