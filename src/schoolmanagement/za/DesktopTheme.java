package schoolmanagement.za;

import java.awt.Color;
import java.awt.Font;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;

final class DesktopTheme {
    private static final Color BASE = new Color(15, 23, 42);
    private static final Color SURFACE = new Color(21, 32, 52);
    private static final Color INPUT = new Color(11, 19, 35);
    private static final Color TEXT = new Color(241, 245, 249);
    private static final Color MUTED = new Color(166, 181, 202);
    private static final Color BORDER = new Color(48, 65, 91);
    private static final Color BLUE = new Color(59, 130, 246);
    private static final Color SELECTED = new Color(30, 64, 112);

    private DesktopTheme() {
    }

    static void apply() {
        try {
            FlatDarkLaf.setup();
        } catch (Exception ignored) {
            // Use the Java default theme when the platform theme is unavailable.
        }
        Font body = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("defaultFont", body);
        UIManager.put("Panel.background", BASE);
        UIManager.put("Panel.foreground", TEXT);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("TitledBorder.titleColor", MUTED);
        UIManager.put("Separator.foreground", BORDER);
        UIManager.put("Component.borderColor", BORDER);
        UIManager.put("Component.focusColor", BLUE);
        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 8);
        UIManager.put("Button.background", SURFACE);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.hoverBackground", new Color(36, 52, 78));
        UIManager.put("TextField.background", INPUT);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.caretForeground", TEXT);
        UIManager.put("TextField.selectionBackground", SELECTED);
        UIManager.put("PasswordField.background", INPUT);
        UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("PasswordField.caretForeground", TEXT);
        UIManager.put("ComboBox.background", INPUT);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.buttonBackground", SURFACE);
        UIManager.put("Table.background", SURFACE);
        UIManager.put("Table.alternateRowColor", new Color(25, 38, 60));
        UIManager.put("TableHeader.background", new Color(27, 41, 64));
        UIManager.put("TableHeader.foreground", TEXT);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("ScrollPane.background", BASE);
        UIManager.put("Viewport.background", SURFACE);
        UIManager.put("OptionPane.background", BASE);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Label.font", body);
        UIManager.put("Button.font", body);
        UIManager.put("TextField.font", body);
        UIManager.put("PasswordField.font", body);
        UIManager.put("ComboBox.font", body);
        UIManager.put("Table.font", body);
        UIManager.put("Table.rowHeight", 31);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.selectionBackground", SELECTED);
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("Table.showGrid", false);
        UIManager.put("TitledBorder.font", body.deriveFont(Font.BOLD));
        UIManager.put("OptionPane.messageFont", body);
    }

    static void styleTable(JTable table) {
        table.setRowHeight(31);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));
        table.setBackground(SURFACE);
        table.setForeground(TEXT);
        table.setSelectionBackground(SELECTED);
        table.setSelectionForeground(TEXT);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(27, 41, 64));
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
    }

    static void stylePrimary(JButton button) {
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
    }
}
