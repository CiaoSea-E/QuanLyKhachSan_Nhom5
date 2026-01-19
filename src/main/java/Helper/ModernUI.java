package Helper;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * Lớp tiện ích Giao diện (UI Helper)
 * Chứa các cài đặt màu sắc, font chữ và các Component tùy chỉnh (Button, TextField, Table...)
 * Giúp giao diện ứng dụng đồng bộ, hiện đại và dễ bảo trì.
 */
public class ModernUI {

    // ============================================================
    // PHẦN 1: CẤU HÌNH CHUNG (CONSTANTS)
    // ============================================================

    // --- BẢNG MÀU CHỦ ĐẠO (COLOR PALETTE) ---
    public static final Color PRIMARY = new Color(30, 60, 114);       // Xanh đậm (Màu chính)
    public static final Color PRIMARY_LIGHT = new Color(42, 82, 152); // Xanh nhạt hơn (Hover)
    public static final Color BG_COLOR = new Color(245, 247, 250);    // Màu nền xám nhạt (Background)
    public static final Color WHITE = Color.WHITE;
    
    // --- MÀU CHỨC NĂNG ---
    public static final Color GREEN_COLOR = new Color(39, 174, 96);   // Nút Thêm / Thành công
    public static final Color RED_COLOR = new Color(192, 57, 43);     // Nút Xóa / Hủy / Lỗi
    public static final Color ACCENT = new Color(255, 215, 0);        // Màu nhấn (Vàng)

    // --- FONT CHỮ CHUẨN (TYPOGRAPHY) ---
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);

    // ============================================================
    // PHẦN 2: CÁC COMPONENT CƠ BẢN (PANEL, LABEL)
    // ============================================================

    /**
     * Tạo Panel tiêu đề (Header) màu xanh ở trên cùng mỗi màn hình.
     * @param title Nội dung tiêu đề
     */
    public static JPanel createHeaderPanel(String title) {
        JPanel pnl = new JPanel(new GridBagLayout()); // Dùng GridBag để căn giữa text
        pnl.setBackground(PRIMARY);
        pnl.setPreferredSize(new Dimension(0, 60)); // Chiều cao cố định 60px
        
        JLabel lbl = new JLabel(title.toUpperCase());
        lbl.setFont(FONT_HEADER);
        lbl.setForeground(WHITE);
        
        pnl.add(lbl);
        return pnl;
    }

    /**
     * Tạo Label (Nhãn) với font chuẩn
     */
    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }

    // ============================================================
    // PHẦN 3: Ô NHẬP LIỆU (TEXT FIELD) BO TRÒN
    // ============================================================

    /**
     * Lớp con của JTextField với viền bo tròn và hiệu ứng focus.
     */
    public static class RoundedTextField extends JTextField {
        private int radius;
        private Color borderColor = new Color(200, 200, 200); // Màu viền mặc định

        public RoundedTextField(int radius) {
            this.radius = radius;
            setOpaque(false); // Để vẽ nền trong suốt (custom paint)
            setFont(FONT_PLAIN);
            setBorder(new EmptyBorder(5, 10, 5, 10)); // Padding nội dung bên trong
            setPreferredSize(new Dimension(200, 35));
            
            // Hiệu ứng đổi màu viền khi click vào
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { borderColor = PRIMARY; repaint(); }
                public void focusLost(FocusEvent e) { borderColor = new Color(200, 200, 200); repaint(); }
            });
        }
        
        // Constructor mặc định (radius 15)
        public RoundedTextField() { this(15); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            // Khử răng cưa cho nét vẽ mượt mà
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Vẽ nền trắng
            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            
            // Vẽ viền
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            
            g2.dispose();
            super.paintComponent(g); // Vẽ text lên trên
        }
    }
    
    // Class viết tắt cho nhanh
    public static class ModernTextField extends RoundedTextField {
        public ModernTextField() { super(15); }
    }

    // ============================================================
    // PHẦN 4: NÚT BẤM (MODERN BUTTON)
    // ============================================================

    /**
     * Lớp con của JButton hỗ trợ màu Gradient và hiệu ứng Hover.
     */
    public static class ModernButton extends JButton {
        private Color c1, c2; // Màu bắt đầu và kết thúc của Gradient

        public ModernButton(String text, Color c1, Color c2) {
            super(text);
            this.c1 = c1;
            this.c2 = c2;
            
            setFont(FONT_BOLD);
            setForeground(WHITE);
            
            // Cấu hình để vẽ nút thủ công (Custom Paint)
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false); 
            setBorderPainted(false);
            
            setCursor(new Cursor(Cursor.HAND_CURSOR)); // Con trỏ tay khi hover
            setPreferredSize(new Dimension(120, 35));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Vẽ nền (Gradient hoặc Đơn sắc)
            if (c1.equals(c2)) {
                g2.setColor(c1); 
            } else {
                g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2)); 
            }
            
            // Vẽ hình chữ nhật bo góc
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            
            super.paintComponent(g); // Vẽ chữ lên trên nền
        }
    }

    // --- CÁC HÀM FACTORY TẠO NÚT NHANH ---

    public static JButton createGradientButton(String text, Color c1, Color c2) {
        return new ModernButton(text, c1, c2);
    }

    public static JButton createPrimaryButton(String text) {
        JButton btn = new ModernButton(text, PRIMARY, PRIMARY); // Màu đơn sắc
        btn.setPreferredSize(new Dimension(80, 35)); // Nút tìm kiếm thường nhỏ hơn
        return btn;
    }

    // ============================================================
    // PHẦN 5: BẢNG DỮ LIỆU (TABLE & SCROLLPANE)
    // ============================================================

    /**
     * Tạo JScrollPane bao quanh JTable đã được style đẹp.
     */
    public static JScrollPane createTable(JTable table) {
        // 1. Style Header (Tiêu đề cột)
        table.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
        
        // 2. Style các dòng (Rows)
        table.setRowHeight(35);
        table.setFont(FONT_PLAIN);
        table.setSelectionBackground(new Color(230, 240, 255)); // Màu nền khi chọn dòng
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false); // Ẩn đường kẻ dọc cho thoáng

        // 3. Xử lý hiển thị nội dung cột (Renderer)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DateRenderer dateRenderer = new DateRenderer(); // Renderer chuyên dụng cho ngày tháng

        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            String colName = table.getColumnName(i).toLowerCase();
            
            // Tự động nhận diện cột Ngày tháng để format đẹp
            if (colName.contains("ngày") || colName.contains("date") || colName.contains("time")) {
                column.setCellRenderer(dateRenderer);
            } else {
                column.setCellRenderer(centerRenderer);
            }
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(WHITE); // Nền trắng cho phần trống
        
        return scroll;
    }

    // --- RENDERER CHO HEADER BẢNG ---
    static class CustomHeaderRenderer extends DefaultTableCellRenderer {
        public CustomHeaderRenderer() {
            setOpaque(true);
            setHorizontalAlignment(JLabel.CENTER);
            setBackground(PRIMARY); // Nền xanh
            setForeground(WHITE);   // Chữ trắng
            setFont(FONT_BOLD);
            setPreferredSize(new Dimension(0, 40));
        }
    }

    // --- RENDERER CHO CỘT NGÀY THÁNG (QUAN TRỌNG) ---
    /**
     * Tự động format ngày tháng từ chuỗi SQL thô (yyyy-MM-dd HH:mm:ss)
     * sang định dạng người Việt (dd/MM/yyyy HH:mm:ss)
     */
    static class DateRenderer extends DefaultTableCellRenderer {
        java.text.SimpleDateFormat fmtOut = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        java.text.SimpleDateFormat fmtIn = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        public DateRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public void setValue(Object value) {
            try {
                if (value != null) {
                    // TH1: Dữ liệu là Date object
                    if (value instanceof java.util.Date) {
                        value = fmtOut.format(value);
                    } 
                    // TH2: Dữ liệu là String từ SQL
                    else if (value instanceof String) {
                        String str = (String) value;
                        // Cắt bỏ phần mili giây lẻ (.0) nếu có
                        if (str.contains(".")) {
                            str = str.substring(0, str.indexOf("."));
                        }
                        // Parse và Format lại
                        try {
                            java.util.Date d = fmtIn.parse(str);
                            value = fmtOut.format(d);
                        } catch (Exception e) {
                            // Nếu lỗi thì giữ nguyên text gốc
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore error
            }
            super.setValue(value);
        }
    }
}