package VIEW;

import java.awt.*;
import javax.swing.*;
import VIEW.QuanLyKhuyenMaiPanel;
public class MainFrame extends JFrame {

    // Khai báo các thành phần giao diện
    private JPanel pnlMenu;
    private JPanel pnlContent;
    private CardLayout cardLayout;

    // Các nút trên Menu
    private JButton btnTrangChu; // (Optional)
    private JButton btnQuanLyPhong;
    private JButton btnQuanLyDatPhong;
    private JButton btnQuanLyKhachHang; // (Chờ phát triển sau)
    private JButton btnThongKe;         // (Chờ phát triển sau)
    private JButton btnDangXuat;
    private JButton btnKhuyenMai;

    // Các Panel chức năng (View con)
    private QuanLyPhongPanel pnlPhong;
    private QuanLyDatPhongPanel pnlDatPhong;
    private QuanLyKhuyenMaiPanel pnlKhuyenMai;
    public MainFrame() {
        initGUI();
    }

    private void initGUI() {
        // 1. Cấu hình cửa sổ chính
        this.setTitle("PHẦN MỀM QUẢN LÝ KHÁCH SẠN - NHÓM 5");
        this.setSize(1200, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // 2. TẠO MENU BÊN TRÁI (SIDEBAR)
        pnlMenu = new JPanel();
        pnlMenu.setBackground(new Color(44, 62, 80)); // Màu xanh đen
        pnlMenu.setLayout(new GridLayout(10, 1, 0, 10)); // 10 dòng, cách nhau 10px
        pnlMenu.setPreferredSize(new Dimension(250, 0));
        pnlMenu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Header Menu
        JLabel lblHeader = new JLabel("MENU CHÍNH", JLabel.CENTER);
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 22));
        pnlMenu.add(lblHeader);

        // Tạo các nút
        btnQuanLyPhong = createMenuButton("Quản Lý Phòng");
        btnQuanLyDatPhong = createMenuButton("Quản Lý Đặt Phòng");
        btnQuanLyKhachHang = createMenuButton("Quản Lý Khách Hàng");
        btnThongKe = createMenuButton("Báo Cáo Thống Kê");
        btnKhuyenMai = createMenuButton("Quản Lý Khuyến Mãi");
        btnDangXuat = createMenuButton("Đăng Xuất");
        btnDangXuat.setBackground(new Color(192, 57, 43));

        // Add nút vào Menu
        pnlMenu.add(btnQuanLyPhong);
        pnlMenu.add(btnQuanLyDatPhong);
        pnlMenu.add(btnQuanLyKhachHang);
        pnlMenu.add(btnThongKe);
        pnlMenu.add(Box.createVerticalGlue()); // Khoảng trắng đệm
        pnlMenu.add(btnDangXuat);
        pnlMenu.add(btnKhuyenMai);

        this.add(pnlMenu, BorderLayout.WEST);

        // 3. TẠO KHUNG HIỂN THỊ CHÍNH (CONTENT - CARD LAYOUT)
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);

        // Khởi tạo các View con (Chưa gắn Controller, để MainController làm việc đó)
        pnlPhong = new QuanLyPhongPanel();
        pnlDatPhong = new QuanLyDatPhongPanel();
        pnlKhuyenMai = new QuanLyKhuyenMaiPanel();

        // Thêm vào CardLayout với tên định danh
        pnlContent.add(pnlPhong, "CARD_PHONG");
        pnlContent.add(pnlDatPhong, "CARD_DATPHONG");
        pnlContent.add(pnlKhuyenMai, "CARD_KHUYENMAI");
        
        // Mặc định hiện cái nào trước? -> Phòng
        cardLayout.show(pnlContent, "CARD_PHONG");

        this.add(pnlContent, BorderLayout.CENTER);
    }

    // Hàm tạo nút Menu cho đẹp
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(new Color(52, 152, 219)); // Màu xanh dương
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // === CÁC GETTER ĐỂ MAIN CONTROLLER SỬ DỤNG ===
    
    public JButton getBtnQuanLyPhong() { return btnQuanLyPhong; }
    public JButton getBtnQuanLyDatPhong() { return btnQuanLyDatPhong; }
    public JButton getBtnDangXuat() { return btnDangXuat; }
    
    public JPanel getPnlContent() { return pnlContent; }
    public CardLayout getCardLayout() { return cardLayout; }
    
    public QuanLyPhongPanel getPnlPhong() { return pnlPhong; }
    public QuanLyDatPhongPanel getPnlDatPhong() { return pnlDatPhong; }
    public JButton getBtnKhuyenMai() { return btnKhuyenMai; }
    public QuanLyKhuyenMaiPanel getPnlKhuyenMai() { return pnlKhuyenMai; }
}