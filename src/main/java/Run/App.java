package Run;

import Controller.MainController;
import VIEW.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        // Làm đẹp giao diện (Optional)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            // 1. Tạo View (Khung chính)
            MainFrame mainFrame = new MainFrame();
            
            // 2. Tạo Controller (Điều khiển khung chính & các panel con)
            new MainController(mainFrame);
            
            // 3. Hiện lên
            mainFrame.setVisible(true);
        });
    }
}