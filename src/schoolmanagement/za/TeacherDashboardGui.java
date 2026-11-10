package schoolmanagement.za;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

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
    private final JTextField markField = new JTextField(18);
    private final JTextField totalField = new JTextField(18);
    private final JTextField commentField = new JTextField(18);
    private final JTextField searchField = new JTextField(22);
    private final JComboBox<Leaner> learnerComboBox = new JComboBox<>();
    private final JComboBox<String> subjectComboBox = new JComboBox<>(SUBJECTS);
    private final JComboBox<String> termComboBox = new JComboBox<>(TERMS);
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
    private final DefaultTableModel learnerTableModel = new DefaultTableModel(
            new String[] {"Learner Name", "Learner ID", "Grade", "Contact"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable learnerTable = new JTable(learnerTableModel);
    private final TableRowSorter<DefaultTableModel> learnerSorter = new TableRowSorter<>(learnerTableModel);
    private final leanerManagement learnerManagement = new leanerManagement();
    private final StudentMarkManagement markManagement = new StudentMarkManagement();

    public TeacherDashboardGui(Teacher currentTeacher) {
        if (currentTeacher == null) {
            throw new SecurityException("Sign in before opening the dashboard.");
        }
        this.currentTeacher = currentTeacher;
        configureWindow();
        configureTable();
        add(buildMainPanel());
        registerEvents();
        registerSearch();
        UiTasks.closeOnDispose(this, learnerManagement, markManagement);
        loadLearnerChoices(this::loadMarks);
    }

    private void configureWindow() {
        setTitle("Teacher Dashboard");
        setSize(940, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void configureTable() {
        DesktopTheme.styleTable(table);
        table.setRowSorter(sorter);
        table.setRowHeight(27);
        table.setFillsViewportHeight(true);
        table.removeColumn(table.getColumnModel().getColumn(0));
        DesktopTheme.styleTable(learnerTable);
        learnerTable.setRowSorter(learnerSorter);
    }

    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        mainPanel.add(buildHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(buildTablePanel(), BorderLayout.CENTER);
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

        addWideFormRow(formPanel, gbc, 0, "Learner", learnerComboBox);
        addFormRow(formPanel, gbc, 1, 0, "Subject", subjectComboBox);
        addFormRow(formPanel, gbc, 1, 2, "Term", termComboBox);
        addFormRow(formPanel, gbc, 2, 0, "Mark Obtained", markField);
        addFormRow(formPanel, gbc, 2, 2, "Total Mark", totalField);
        addWideFormRow(formPanel, gbc, 3, "Comment", commentField);

        return formPanel;
    }

    private JPanel buildButtonPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));

        JPanel actionsPanel = new JPanel(new GridLayout(1, 6, 8, 0));
        JButton addButton = new JButton("Add Mark");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton resetButton = new JButton("Reset");
        JButton loadButton = new JButton("Refresh");
        JButton exportButton = new JButton("Export CSV");
        DesktopTheme.stylePrimary(addButton);

        addButton.addActionListener(event -> addMark());
        updateButton.addActionListener(event -> updateMark());
        deleteButton.addActionListener(event -> deleteMark());
        resetButton.addActionListener(event -> resetForm());
        loadButton.addActionListener(event -> loadLearnerChoices(this::loadMarks));
        exportButton.addActionListener(event -> exportMarks());

        actionsPanel.add(addButton);
        actionsPanel.add(updateButton);
        actionsPanel.add(deleteButton);
        actionsPanel.add(resetButton);
        actionsPanel.add(loadButton);
        actionsPanel.add(exportButton);

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton attendanceButton = new JButton("Attendance");
        attendanceButton.addActionListener(event -> openDashboardPage(new AttendanceGui(currentTeacher)));
        navigationPanel.add(attendanceButton);
        JButton learnerButton = new JButton("Manage Learners");
        learnerButton.setVisible(TeacherManagement.canManageLearners(currentTeacher));
        JButton logoutButton = new JButton("Logout");

        learnerButton.addActionListener(event -> openDashboardPage(new leanerGUI(currentTeacher)));
        logoutButton.addActionListener(event -> logout());

        navigationPanel.add(learnerButton);
        if (TeacherManagement.isAdministrator(currentTeacher)) {
            JButton staffButton = new JButton("Manage Staff");
            staffButton.addActionListener(event -> openDashboardPage(new TeacherGui(currentTeacher, false)));
            navigationPanel.add(staffButton);
            JButton backupButton = new JButton("Backup");
            backupButton.setToolTipText("Save a copy of school records and staff password hashes.");
            backupButton.addActionListener(event -> createBackup());
            JButton restoreButton = new JButton("Restore");
            restoreButton.addActionListener(event -> restoreBackup());
            navigationPanel.add(backupButton);
            navigationPanel.add(restoreButton);
        }
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
        learnerTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                selectLearnerFromStudentRow();
            }
        });
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel heading = new JPanel(new BorderLayout(8, 0));
        JLabel label = new JLabel("School register");
        label.setFont(label.getFont().deriveFont(16f));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchField.putClientProperty("JTextField.placeholderText", "Name or learner ID");
        searchField.setToolTipText("Filters students and marks by learner name or ID as you type.");
        search.add(new JLabel("Search students"));
        search.add(searchField);
        heading.add(label, BorderLayout.WEST);
        heading.add(search, BorderLayout.EAST);
        panel.add(heading, BorderLayout.NORTH);
        JTabbedPane records = new JTabbedPane();
        records.addTab("Students", new JScrollPane(learnerTable));
        records.addTab("Marks register", new JScrollPane(table));
        panel.add(records, BorderLayout.CENTER);
        return panel;
    }

    private void registerSearch() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { applySearch(); }
            @Override public void removeUpdate(DocumentEvent event) { applySearch(); }
            @Override public void changedUpdate(DocumentEvent event) { applySearch(); }
        });
    }

    private void applySearch() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            learnerSorter.setRowFilter(null);
            sorter.setRowFilter(null);
            return;
        }
        String expression = "(?i)" + Pattern.quote(text);
        learnerSorter.setRowFilter(RowFilter.regexFilter(expression, 0, 1));
        sorter.setRowFilter(RowFilter.regexFilter(expression, 1, 2));
    }

    private void addMark() {
        StudentMark mark = buildMarkFromForm();
        if (mark == null) {
            return;
        }

        UiTasks.run(this, "Saving mark", () -> markManagement.addMark(currentTeacher, mark), saved -> {
            if (!saved) { showError("Student mark was not recorded."); return; }
            addMarkToTable(mark);
            resetForm();
            JOptionPane.showMessageDialog(this, "Student mark recorded successfully.");
        });
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

        UiTasks.run(this, "Updating mark", () -> markManagement.updateMark(currentTeacher, markId, mark), saved -> {
            if (!saved) { showError("The mark was not updated. Teachers can edit only their own marks."); return; }
            updateTableRow(modelRow, mark);
            resetForm();
            JOptionPane.showMessageDialog(this, "Student mark updated successfully.");
        });
    }

    private void deleteMark() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a mark to delete.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int markId = (Integer) tableModel.getValueAt(modelRow, 0);

        if (JOptionPane.showConfirmDialog(this, "Delete this mark?", "Confirm delete",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        UiTasks.run(this, "Deleting mark", () -> markManagement.removeMark(currentTeacher, markId), deleted -> {
            if (!deleted) { showError("The mark was not deleted. Teachers can delete only their own marks."); return; }
            tableModel.removeRow(modelRow);
            resetForm();
            JOptionPane.showMessageDialog(this, "Student mark deleted successfully.");
        });
    }

    private void loadMarks() {
        UiTasks.run(this, "Loading marks", () -> markManagement.getAll(currentTeacher), marks -> {
            tableModel.setRowCount(0);
            for (StudentMark mark : marks) addMarkToTable(mark);
        });
    }

    private void loadLearnerChoices(Runnable afterLoad) {
        UiTasks.run(this, "Loading learner register", () -> learnerManagement.getAll(currentTeacher), learners -> {
            learnerComboBox.removeAllItems();
            learnerTableModel.setRowCount(0);
            learnerComboBox.addItem(new Leaner("Select a learner", 0, "", ""));
            for (Leaner learner : learners) {
                learnerComboBox.addItem(learner);
                learnerTableModel.addRow(new Object[] {
                    learner.getFullname(), learner.getId(), learner.getGrade(), learner.getContact()
                });
            }
            afterLoad.run();
        });
    }

    private void selectLearnerFromStudentRow() {
        int selectedRow = learnerTable.getSelectedRow();
        if (selectedRow < 0) return;
        int modelRow = learnerTable.convertRowIndexToModel(selectedRow);
        String learnerId = learnerTableModel.getValueAt(modelRow, 1).toString();
        for (int index = 1; index < learnerComboBox.getItemCount(); index++) {
            if (learnerId.equals(learnerComboBox.getItemAt(index).getId())) {
                learnerComboBox.setSelectedIndex(index);
                break;
            }
        }
    }

    private void exportMarks() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export marks report");
        chooser.setSelectedFile(new File("marks-report.csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV report (*.csv)", "csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = withExtension(chooser.getSelectedFile().toPath(), ".csv");
        UiTasks.run(this, "Exporting marks report", () -> {
            ReportService.exportMarks(path, markManagement.getAll(currentTeacher));
            return path;
        }, saved -> JOptionPane.showMessageDialog(this, "Marks report saved to:\n" + saved));
    }

    private void createBackup() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Create database backup");
        chooser.setSelectedFile(new File("school-backup.schoolbackup"));
        chooser.setFileFilter(new FileNameExtensionFilter("School database backup (*.schoolbackup)", "schoolbackup"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = withExtension(chooser.getSelectedFile().toPath(), ".schoolbackup");
        UiTasks.run(this, "Creating database backup", () -> {
            BackupService.createBackup(path);
            return path;
        }, saved -> JOptionPane.showMessageDialog(this,
                "Backup saved to:\n" + saved + "\nStore it securely; it contains school records and password hashes."));
    }

    private void restoreBackup() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a school database backup");
        chooser.setFileFilter(new FileNameExtensionFilter("School database backup (*.schoolbackup)", "schoolbackup"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = chooser.getSelectedFile().toPath();
        int choice = JOptionPane.showConfirmDialog(this,
                "Restoring replaces all current staff, learner, marks, and attendance records. Continue?",
                "Replace current school data", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        UiTasks.run(this, "Restoring database backup", () -> {
            BackupService.restoreBackup(path);
            return Boolean.TRUE;
        }, restored -> {
            JOptionPane.showMessageDialog(this, "Restore completed. Sign in again using an account from the backup.");
            dispose();
            new Login().setVisible(true);
        });
    }

    private Path withExtension(Path path, String extension) {
        String name = path.getFileName().toString();
        return name.toLowerCase(java.util.Locale.ROOT).endsWith(extension)
                ? path : path.resolveSibling(name + extension);
    }

    private StudentMark buildMarkFromForm() {
        Leaner learner = (Leaner) learnerComboBox.getSelectedItem();
        String subject = subjectComboBox.getSelectedItem().toString();
        String term = termComboBox.getSelectedItem().toString();
        String comment = commentField.getText().trim();
        double markObtained;
        double totalMark;

        if (learner == null || learner.getId() == null || learner.getId().isEmpty()) {
            showError("Please select a learner from the school register.");
            return null;
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
        return new StudentMark(0, learner.getId(), learner.getFullname(), subject, term,
                markObtained, totalMark, teacherId, comment);
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String learnerId = tableModel.getValueAt(modelRow, 1).toString();
        for (int i = 1; i < learnerComboBox.getItemCount(); i++) {
            if (learnerId.equals(learnerComboBox.getItemAt(i).getId())) {
                learnerComboBox.setSelectedIndex(i);
                break;
            }
        }
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
        if (learnerComboBox.getItemCount() > 0) {
            learnerComboBox.setSelectedIndex(0);
        }
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

    private void openDashboardPage(JFrame page) {
        page.setVisible(true);
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

    private void addWideFormRow(JPanel panel, GridBagConstraints gbc, int row,
                                String labelText, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 3;
        panel.add(field, gbc);
        gbc.gridwidth = 1;
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
