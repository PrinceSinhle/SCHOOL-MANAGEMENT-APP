package schoolmanagement.za;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

final class UiTasks {
    private UiTasks() {
    }

    static <T> void run(JFrame frame, String message, Callable<T> task, Consumer<T> onSuccess) {
        Component previousGlass = frame.getGlassPane();
        boolean previousVisible = previousGlass.isVisible();
        JPanel busyGlass = new JPanel(new GridBagLayout());
        busyGlass.setOpaque(false);
        busyGlass.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JLabel status = new JLabel("  " + message + "  ");
        status.setOpaque(true);
        status.setBackground(new Color(21, 32, 52));
        status.setForeground(new Color(241, 245, 249));
        busyGlass.add(status);
        frame.setGlassPane(busyGlass);
        busyGlass.setVisible(true);

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                busyGlass.setVisible(false);
                frame.setGlassPane(previousGlass);
                previousGlass.setVisible(previousVisible);
                try {
                    onSuccess.accept(get());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showError(frame, "The operation was interrupted.");
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    String detail = cause.getMessage();
                    showError(frame, detail == null || detail.trim().isEmpty()
                            ? "The operation could not be completed." : detail);
                } catch (RuntimeException ex) {
                    showError(frame, ex.getMessage() == null
                            ? "The operation could not be completed." : ex.getMessage());
                }
            }
        }.execute();
    }

    static void showError(Window parent, String message) {
        javax.swing.JOptionPane.showMessageDialog(parent, message, "Operation Failed",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    static void closeOnDispose(JFrame frame, AutoCloseable... resources) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                for (AutoCloseable resource : resources) {
                    try { resource.close(); } catch (Exception ignored) { }
                }
            }
        });
    }
}
