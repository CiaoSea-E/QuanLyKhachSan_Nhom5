package Helper;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrameUI {

    // --- MÀU SẮC RIÊNG CHO MAINFRAME (Theme Tối Sang Trọng) ---
    public static final Color SIDEBAR_COLOR_1 = new Color(20, 30, 48); // Xanh đen
    public static final Color SIDEBAR_COLOR_2 = new Color(36, 59, 85); // Xanh Navy
    public static final Color TEXT_IDLE       = new Color(170, 170, 170); // Chữ xám
    public static final Color TEXT_ACTIVE     = Color.WHITE;              // Chữ trắng
    public static final Color ACCENT_COLOR    = new Color(255, 215, 0);   // Vàng Gold

    // Font
    private static final Font FONT_MENU = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 26);

    // Danh sách quản lý các nút menu (để xử lý active/inactive)
    private static List<MenuButton> buttonsList = new ArrayList<>();

    // =========================================================================
    // 1. PANEL SIDEBAR (Nền Gradient)
    // =========================================================================
    public static class SidebarPanel extends JPanel {
        public SidebarPanel() {
            setLayout(new GridBagLayout()); // Dùng GridBag để căn chỉnh Logo/Menu/Logout
            setPreferredSize(new Dimension(280, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Vẽ Gradient dọc
            GradientPaint gp = new GradientPaint(0, 0, SIDEBAR_COLOR_1, 0, getHeight(), SIDEBAR_COLOR_2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // =========================================================================
    // 2. MENU BUTTON (Nút Menu có hiệu ứng Kính + Vạch vàng)
    // =========================================================================
    public static class MenuButton extends JButton {
        private boolean isActive = false;
        private boolean isHover = false;

        public MenuButton(String icon, String text) {
            super(icon + "    " + text.toUpperCase());
            setFont(FONT_MENU);
            setForeground(TEXT_IDLE);
            setBackground(new Color(0,0,0,0)); // Trong suốt
            setBorder(new EmptyBorder(12, 30, 12, 10)); // Padding
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Thêm vào danh sách quản lý
            buttonsList.add(this);

            // Sự kiện chuột
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHover = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHover = false; repaint(); }
            });
        }

        // Hàm set trạng thái
        public void setActive(boolean val) {
            this.isActive = val;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                // Đang chọn: Nền kính mờ + Vạch vàng
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(10, 0, getWidth()-10, getHeight(), 0, 20);
                
                g2.setColor(ACCENT_COLOR);
                g2.fillRect(0, 5, 5, getHeight()-10); // Thanh active
                
                setForeground(TEXT_ACTIVE);
            } else if (isHover) {
                // Hover: Sáng nhẹ
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillRoundRect(10, 0, getWidth()-20, getHeight(), 10, 10);
                setForeground(TEXT_ACTIVE);
            } else {
                setForeground(TEXT_IDLE);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // 3. LOGIC & HELPER METHODS
    // =========================================================================
    
    // Tạo Logo
    public static JLabel createLogo() {
        JLabel lbl = new JLabel("HOTEL MANAGER", JLabel.CENTER);
        lbl.setFont(FONT_LOGO);
        lbl.setForeground(ACCENT_COLOR);
        lbl.setBorder(new EmptyBorder(30, 0, 30, 0));
        return lbl;
    }

    // Hàm đổi màu nút (Gọi khi bấm menu)
    public static void setSelected(JButton selectedBtn) {
        for (MenuButton btn : buttonsList) {
            btn.setActive(btn == selectedBtn);
        }
    }
}