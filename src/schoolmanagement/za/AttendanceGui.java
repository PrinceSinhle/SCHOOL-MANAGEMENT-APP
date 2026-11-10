package schoolmanagement.za;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
import javax.swing.filechooser.FileNameExtensionFilter;

public class AttendanceGui extends JFrame {
    private static final String[] COLUMNS = {
        "Record ID", "Learner ID", "Learner", "Date", "Status", "Recorded By", "Comment"
    };
    private static final String[] STATUSES = {"Select status", "Present", "Absent", "Late"};

    private final Teacher actor;
    private final AttendanceManagement attendanceManagement = new AttendanceManagement();
    private final leanerManagement learnerManagement = new leanerManagement();
    private final JComboBox<Leaner> learnerBox = new JComboBox<>();
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 12);
    private final JComboBox<String> statusBox = new JComboBox<>(STATUSES);
    private final JTextField commentField = new JTextField(24);
    private final DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    public AttendanceGui(Teacher actor) {
        if (actor == null) throw new SecurityException("Sign in before recording attendance.");
        this.actor = actor;
        configureWindow();
        DesktopNavigation.installBackNavigation(this, this::backToDashboard);
        DesktopTheme.styleTable(table);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(26);
        table.removeColumn(table.getColumnModel().getColumn(0));
        add(buildMainPanel());
        registerEvents();
        UiTasks.closeOnDispose(this, attendanceManagement, learnerManagement);
        loadLearners(this::loadAttendance);
    }

    private void configureWindow() {
        setTitle("Attendance");
        setSize(920, 560);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBorder(new EmptyBorder(16, 18, 16, 18));
        JLabel title = new JLabel("Attendance Register", JLabel.LEFT);
        title.setFont(title.getFont().deriveFont(22f));
        JPanel header = new JPanel(new BorderLayout(8, 10));
        header.add(title, BorderLayout.NORTH);
        header.add(buildForm(), BorderLayout.CENTER);
        main.add(header, BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(buildActions(), BorderLayout.SOUTH);
        return main;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Daily attendance"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 7, 6, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addFormRow(form, gbc, 0, 0, "Learner", learnerBox);
        addFormRow(form, gbc, 0, 2, "Date (YYYY-MM-DD)", dateField);
        addFormRow(form, gbc, 1, 0, "Status", statusBox);
        addFormRow(form, gbc, 1, 2, "Comment", commentField);
        return form;
    }

    private JPanel buildActions() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        JPanel actions = new JPanel(new GridLayout(1, 5, 8, 0));
        JButton add = new JButton("Record");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");
        JButton export = new JButton("Export CSV");
        DesktopTheme.stylePrimary(add);
        add.addActionListener(event -> addRecord());
        update.addActionListener(event -> updateRecord());
        delete.addActionListener(event -> deleteRecord());
        refresh.addActionListener(event -> loadAttendance());
        export.addActionListener(event -> exportAttendance());
        actions.add(add); actions.add(update); actions.add(delete); actions.add(refresh); actions.add(export);
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("Back to Dashboard");
        back.addActionListener(event -> backToDashboard());
        closePanel.add(back);
        wrapper.add(actions, BorderLayout.CENTER);
        wrapper.add(closePanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private void registerEvents() {
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) populateSelectedRecord();
        });
    }

    private void backToDashboard() {
        DesktopNavigation.returnToDashboard(this, actor);
    }

    private void loadLearners(Runnable afterLoad) {
        UiTasks.run(this, "Loading learner register", () -> learnerManagement.getAll(actor), learners -> {
            learnerBox.removeAllItems();
            learnerBox.addItem(new Leaner("Select a learner", 0, "", ""));
            for (Leaner learner : learners) learnerBox.addItem(learner);
            afterLoad.run();
        });
    }

    private void loadAttendance() {
        UiTasks.run(this, "Loading attendance", () -> attendanceManagement.getAll(actor), records -> {
            model.setRowCount(0);
            for (AttendanceRecord record : records) addRow(record);
        });
    }

    private void addRecord() {
        AttendanceRecord record = buildRecord(0);
        if (record == null) return;
        UiTasks.run(this, "Saving attendance", () -> attendanceManagement.add(actor, record), saved -> {
            if (!saved) {
                showError("Could not record attendance. A record may already exist for this learner and date.");
                return;
            }
            addRow(record);
            resetForm();
        });
    }

    private void updateRecord() {
        int selected = table.getSelectedRow();
        if (selected < 0) { showError("Select an attendance record to update."); return; }
        int row = table.convertRowIndexToModel(selected);
        int id = (Integer) model.getValueAt(row, 0);
        AttendanceRecord record = buildRecord(id);
        if (record == null) return;
        UiTasks.run(this, "Updating attendance", () -> attendanceManagement.update(actor, record), saved -> {
            if (!saved) { showError("You can update only records you entered."); return; }
            model.removeRow(row);
            addRow(record);
            resetForm();
        });
    }

    private void deleteRecord() {
        int selected = table.getSelectedRow();
        if (selected < 0) { showError("Select an attendance record to delete."); return; }
        int row = table.convertRowIndexToModel(selected);
        int id = (Integer) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this attendance record?", "Confirm delete",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        UiTasks.run(this, "Deleting attendance", () -> attendanceManagement.delete(actor, id), deleted -> {
            if (!deleted) { showError("You can delete only records you entered."); return; }
            model.removeRow(row);
            resetForm();
        });
    }

    private void exportAttendance() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export attendance report");
        chooser.setSelectedFile(new File("attendance-report.csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV report (*.csv)", "csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path selected = chooser.getSelectedFile().toPath();
        String name = selected.getFileName().toString();
        Path destination = name.toLowerCase(java.util.Locale.ROOT).endsWith(".csv")
                ? selected : selected.resolveSibling(name + ".csv");
        UiTasks.run(this, "Exporting attendance report", () -> {
            ReportService.exportAttendance(destination, attendanceManagement.getAll(actor));
            return destination;
        }, saved -> JOptionPane.showMessageDialog(this, "Attendance report saved to:\n" + saved));
    }

    private AttendanceRecord buildRecord(int id) {
        Leaner learner = (Leaner) learnerBox.getSelectedItem();
        if (learner == null || learner.getId() == null || learner.getId().isEmpty()) {
            showError("Select a learner from the register."); return null;
        }
        if (statusBox.getSelectedIndex() == 0) { showError("Select a status."); return null; }
        try {
            return new AttendanceRecord(id, learner.getId(), learner.getFullname(),
                    LocalDate.parse(dateField.getText().trim()), statusBox.getSelectedItem().toString(),
                    actor.getTeacherId(), commentField.getText().trim());
        } catch (DateTimeParseException ex) {
            showError("Enter the date as YYYY-MM-DD.");
            dateField.requestFocus();
            return null;
        }
    }

    private void populateSelectedRecord() {
        int selected = table.getSelectedRow();
        if (selected < 0) return;
        int row = table.convertRowIndexToModel(selected);
        String learnerId = model.getValueAt(row, 1).toString();
        for (int i = 1; i < learnerBox.getItemCount(); i++) {
            if (learnerId.equals(learnerBox.getItemAt(i).getId())) {
                learnerBox.setSelectedIndex(i); break;
            }
        }
        dateField.setText(model.getValueAt(row, 3).toString());
        statusBox.setSelectedItem(model.getValueAt(row, 4).toString());
        commentField.setText(model.getValueAt(row, 6) == null ? "" : model.getValueAt(row, 6).toString());
    }

    private void addRow(AttendanceRecord record) {
        model.addRow(new Object[] {record.getAttendanceId(), record.getLearnerId(), record.getLearnerName(),
                record.getDate(), record.getStatus(), record.getRecordedBy(), record.getComment()});
    }

    private void resetForm() {
        learnerBox.setSelectedIndex(0);
        dateField.setText(LocalDate.now().toString());
        statusBox.setSelectedIndex(0);
        commentField.setText("");
        table.clearSelection();
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, int column,
                            String labelText, java.awt.Component field) {
        gbc.gridx = column; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = column + 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Attendance", JOptionPane.ERROR_MESSAGE);
    }
}
