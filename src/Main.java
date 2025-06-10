import javax.swing.SwingUtilities;
import javax.swing.JFrame;

/**
 * Entry point for the Student Management Application.
 */
public class Main {

    public static void main(String[] args) {
        // Run GUI creation on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            AppGUI window = new AppGUI();
            window.pack(); // Fit the window size to components
            window.setLocationRelativeTo(null); // Center the window on the screen
            window.setVisible(true);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
