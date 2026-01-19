package VIEW;

import Helper.MainFrameUI;
import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private JPanel pnlContent;
    private CardLayout cardLayout;

    // Menu Buttons
    private MainFrameUI.MenuButton btnTrangChu, btnPhong, btnDatPhong, btnKhuyenMai, 
                                   btnKhachHang, btnThongKe, btnDangXuat;

    // View Panels (Khai báo các màn hình con)
    private QuanLyPhongPanel pnlPhong;
    private QuanLyDatPhongPanel pnlDatPhong;
    private QuanLyKhuyenMaiPanel pnlKhuyenMai; // <--- 1. KHAI BÁO THÊM Ở ĐÂY

    public MainFrame() {
        initGUI();
    }

    private void initGUI() {
        setTitle("LUXURY HOTEL MANAGER - TEAM 5");
        setSize(1350, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createContent(), BorderLayout.CENTER);
        
        // Mặc định chọn tab Phòng
        MainFrameUI.setSelected(btnPhong);
    }

    private JPanel createSidebar() {
        MainFrameUI.SidebarPanel pnl = new MainFrameUI.SidebarPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Logo
        gbc.gridy = 0; gbc.weighty = 0; gbc.anchor = GridBagConstraints.NORTH;
        pnl.add(MainFrameUI.createLogo(), gbc);

        // Menu List
        JPanel pnlList = new JPanel(new GridLayout(0, 1, 0, 8));
        pnlList.setOpaque(false);

        btnTrangChu  = new MainFrameUI.MenuButton("🏠", "Trang Chủ");
        btnPhong     = new MainFrameUI.MenuButton("🛏", "Quản Lý Phòng");
        btnDatPhong  = new MainFrameUI.MenuButton("📅", "Đặt Phòng");
        btnKhuyenMai = new MainFrameUI.MenuButton("🎁", "Khuyến Mãi");
        btnKhachHang = new MainFrameUI.MenuButton("👥", "Khách Hàng");
        btnThongKe   = new MainFrameUI.MenuButton("📊", "Thống Kê");

        // Gắn sự kiện chuyển trang nội bộ (để View tự xử lý hiển thị tab)
        setupEvent(btnTrangChu, "CARD_HOME");
        setupEvent(btnPhong, "CARD_PHONG");
        setupEvent(btnDatPhong, "CARD_DATPHONG");
        setupEvent(btnKhuyenMai, "CARD_KHUYENMAI");
        setupEvent(btnKhachHang, "CARD_KHACHHANG");
        setupEvent(btnThongKe, "CARD_THONGKE");

        pnlList.add(btnTrangChu);
        pnlList.add(btnPhong);
        pnlList.add(btnDatPhong);
        pnlList.add(btnKhuyenMai);
        pnlList.add(btnKhachHang);
        pnlList.add(btnThongKe);

        gbc.gridy = 1; gbc.weighty = 1.0; 
        pnl.add(pnlList, gbc);

        // Logout
        btnDangXuat = new MainFrameUI.MenuButton("🚪", "Đăng Xuất");
        btnDangXuat.setForeground(new Color(255, 100, 100));
        btnDangXuat.addActionListener(e -> System.exit(0));
        
        gbc.gridy = 2; gbc.weighty = 0; gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(0, 0, 30, 0);
        pnl.add(btnDangXuat, gbc);

        return pnl;
    }

    private JPanel createContent() {
        pnlContent = new JPanel(new CardLayout());
        cardLayout = (CardLayout) pnlContent.getLayout();
        pnlContent.setBackground(new Color(245, 247, 250));

        // Khởi tạo các Panel con
        pnlPhong = new QuanLyPhongPanel();
        pnlDatPhong = new QuanLyDatPhongPanel();
        pnlKhuyenMai = new QuanLyKhuyenMaiPanel(); // <--- 2. KHỞI TẠO PANEL KHUYẾN MÃI

        // Thêm vào CardLayout
        pnlContent.add(pnlPhong, "CARD_PHONG");
        pnlContent.add(pnlDatPhong, "CARD_DATPHONG");
        pnlContent.add(pnlKhuyenMai, "CARD_KHUYENMAI"); // <--- 3. THÊM VÀO CARDLAYOUT (Thay thế new JPanel cũ)
        
        pnlContent.add(new JPanel(), "CARD_HOME");
        pnlContent.add(new JPanel(), "CARD_KHACHHANG");
        pnlContent.add(new JPanel(), "CARD_THONGKE");

        return pnlContent;
    }

    private void setupEvent(JButton btn, String cardName) {
        btn.addActionListener(e -> {
            MainFrameUI.setSelected(btn);
            cardLayout.show(pnlContent, cardName);
            
            // Reload dữ liệu khi chuyển tab (nếu cần)
            if(cardName.equals("CARD_DATPHONG")) pnlDatPhong.loadData();
            // if(cardName.equals("CARD_PHONG")) pnlPhong.loadData();
        });
    }

    // --- GETTER ---
    public CardLayout getCardLayout() { return cardLayout; }
    public JPanel getPnlContent() { return pnlContent; }

    public JButton getBtnQuanLyPhong() { return btnPhong; }
    public JButton getBtnQuanLyDatPhong() { return btnDatPhong; }
    public JButton getBtnKhuyenMai() { return btnKhuyenMai; }
    public JButton getBtnDangXuat() { return btnDangXuat; }
    
    public QuanLyPhongPanel getPnlPhong() { return pnlPhong; }
    public QuanLyDatPhongPanel getPnlDatPhong() { return pnlDatPhong; }
    public QuanLyKhuyenMaiPanel getPnlKhuyenMai() { return pnlKhuyenMai; } // <--- 4. THÊM GETTER NÀY
}