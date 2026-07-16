package schoolmanagement.za;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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

public class leanerGUI extends JFrame {
    private static final String[] GRADES = {"Select grade", "1", "2", "3", "4", "5", "6", "7"};
    private static final String[] COLUMNS = {"Full Name", "ID", "Grade", "Contact"};

    private final JTextField nameField = new JTextField(20);
    private final JTextField idField = new JTextField(20);
    private final JTextField contactField = new JTextField(20);
    private final JComboBox<String> gradeComboBox = new JComboBox<>(GRADES);
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final leanerManagement learnerManagement = new leanerManagement();

    public leanerGUI() {
        configureWindow();
        add(buildMainPanel());
        registerEvents();
    }

    private void configureWindow() {
        setTitle("Learner Management");
        setSize(720, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel heading = new JLabel("Sabatha Primary School", JLabel.CENTER);
        heading.setFont(heading.getFont().deriveFont(22f));

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(buildFormPanel(), BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Learner Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formPanel, gbc, 0, "Full Name", nameField);
        addFormRow(formPanel, gbc, 1, "ID", idField);
        addFormRow(formPanel, gbc, 2, "Contact", contactField);
        addFormRow(formPanel, gbc, 3, "Grade", gradeComboBox);

        return formPanel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 8, 0));

        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        JButton loadButton = new JButton("Load");
        JButton updateButton = new JButton("Update");
        JButton exitButton = new JButton("Exit");

        addButton.addActionListener(event -> addLearner());
        removeButton.addActionListener(event -> removeLearner());
        loadButton.addActionListener(event -> loadLearners());
        updateButton.addActionListener(event -> updateLearner());
        exitButton.addActionListener(event -> dispose());

        panel.add(addButton);
        panel.add(removeButton);
        panel.add(loadButton);
        panel.add(updateButton);
        panel.add(exitButton);

        return panel;
    }

    private void registerEvents() {
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                populateFormFromSelectedRow();
            }
        });
    }

    private void addLearner() {
        Leaner learner = buildLearnerFromForm(true);
        if (learner == null) {
            return;
        }

        if (isLearnerInTable(learner.getId())) {
            showError("Learner with this ID already exists in the table.");
            return;
        }

        if (learnerManagement.addLeaner(learner)) {
            addLearnerToTable(learner);
            resetForm();
            JOptionPane.showMessageDialog(this, "Learner added successfully.");
        } else {
            showError("Learner was not added. Check whether the ID already exists.");
        }
    }

    private void removeLearner() {
        String learnerId = idField.getText().trim();
        if (learnerId.isEmpty()) {
            learnerId = JOptionPane.showInputDialog(this, "Enter learner ID to remove:");
        }

        if (learnerId == null || learnerId.trim().isEmpty()) {
            return;
        }

        if (learnerManagement.removeLeaner(learnerId.trim())) {
            removeLearnerFromTable(learnerId.trim());
            resetForm();
            JOptionPane.showMessageDialog(this, "Learner removed successfully.");
        } else {
            showError("Learner was not removed.");
        }
    }

    private void loadLearners() {
        tableModel.setRowCount(0);
        ArrayList<Leaner> learners = learnerManagement.getAll();
        for (Leaner learner : learners) {
            addLearnerToTable(learner);
        }
    }

    private void updateLearner() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a learner to update.");
            return;
        }

        Leaner learner = buildLearnerFromForm(false);
        if (learner == null) {
            return;
        }

        String originalId = tableModel.getValueAt(selectedRow, 1).toString();
        if (learnerManagement.updateLeaner(originalId, learner)) {
            tableModel.setValueAt(learner.getFullname(), selectedRow, 0);
            tableModel.setValueAt(learner.getGrade(), selectedRow, 2);
            tableModel.setValueAt(learner.getContact(), selectedRow, 3);
            resetForm();
            JOptionPane.showMessageDialog(this, "Learner updated successfully.");
        } else {
            showError("Learner was not updated.");
        }
    }

    private Leaner buildLearnerFromForm(boolean validateId) {
        String fullName = nameField.getText().trim();
        String learnerId = idField.getText().trim();
        String contact = contactField.getText().trim();
        int grade = gradeComboBox.getSelectedIndex();

        if (fullName.isEmpty() || learnerId.isEmpty() || contact.isEmpty() || grade == 0) {
            showError("Please fill in all fields.");
            return null;
        }

        if (!fullName.matches("[a-zA-Z\\s]+")) {
            return showValidationError("Name can only contain letters and spaces.", nameField);
        }

        if (validateId && !learnerId.matches("\\d{13}")) {
            return showValidationError("ID must be exactly 13 digits.", idField);
        }

        if (!contact.matches("0\\d{9}")) {
            return showValidationError("Contact must start with 0 and be exactly 10 digits.", contactField);
        }

        return new Leaner(fullName, grade, learnerId, contact);
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        nameField.setText(tableModel.getValueAt(selectedRow, 0).toString());
        idField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        gradeComboBox.setSelectedIndex((Integer) tableModel.getValueAt(selectedRow, 2));
        contactField.setText(tableModel.getValueAt(selectedRow, 3).toString());
    }

    private void addLearnerToTable(Leaner learner) {
        tableModel.addRow(new Object[] {
            learner.getFullname(),
            learner.getId(),
            learner.getGrade(),
            learner.getContact()
        });
    }

    private boolean isLearnerInTable(String learnerId) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (learnerId.equals(tableModel.getValueAt(row, 1))) {
                return true;
            }
        }
        return false;
    }

    private void removeLearnerFromTable(String learnerId) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (learnerId.equals(tableModel.getValueAt(row, 1))) {
                tableModel.removeRow(row);
                return;
            }
        }
    }

    private void resetForm() {
        nameField.setText("");
        idField.setText("");
        contactField.setText("");
        gradeComboBox.setSelectedIndex(0);
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

    private Leaner showValidationError(String message, java.awt.Component field) {
        showError(message);
        field.requestFocus();
        return null;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}
