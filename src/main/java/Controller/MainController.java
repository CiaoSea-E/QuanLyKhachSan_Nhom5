package Controller;

import VIEW.MainFrame;
import VIEW.QuanLyDatPhongPanel;
import VIEW.QuanLyKhuyenMaiPanel;
import VIEW.QuanLyPhongPanel;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class MainController {

    private MainFrame mainFrame;

    // Khai báo các Controller con
    private PhongController phongController;
    private QuanLyDatPhongController datPhongController;
    private KhuyenMaiController khuyenMaiController; // <--- 1. KHAI BÁO

    public MainController(MainFrame frame) {
        this.mainFrame = frame;
        initSubControllers(); 
        initEvents();         
    }

    // Kích hoạt các Controller con
    private void initSubControllers() {
        // Lấy các Panel từ MainFrame ra
        QuanLyPhongPanel pnlPhong = mainFrame.getPnlPhong();
        QuanLyDatPhongPanel pnlDatPhong = mainFrame.getPnlDatPhong();
        QuanLyKhuyenMaiPanel pnlKhuyenMai = mainFrame.getPnlKhuyenMai(); // <--- 2. LẤY PANEL RA
        
        // Gắn logic (Controller) vào các Panel đó
        this.phongController = new PhongController(pnlPhong);
        this.datPhongController = new QuanLyDatPhongController(pnlDatPhong);
        this.khuyenMaiController = new KhuyenMaiController(pnlKhuyenMai); // <--- 3. KÍCH HOẠT CONTROLLER
    }

    private void initEvents() {
        // Nút Quản Lý Phòng
        mainFrame.getBtnQuanLyPhong().addActionListener(e -> switchPanel("CARD_PHONG"));

        // Nút Quản Lý Đặt Phòng
        mainFrame.getBtnQuanLyDatPhong().addActionListener(e -> {
            switchPanel("CARD_DATPHONG");
            mainFrame.getPnlDatPhong().loadData();
        });
        
        // Nút Khuyến Mãi
        mainFrame.getBtnKhuyenMai().addActionListener(e -> {
            switchPanel("CARD_KHUYENMAI");
            // Reload data nếu cần (Hiện tại Controller khuyến mãi đã load lúc khởi tạo rồi)
        });

        // Nút Đăng Xuất
        mainFrame.getBtnDangXuat().addActionListener(e -> {
            int chon = JOptionPane.showConfirmDialog(mainFrame,
                    "Bạn có chắc chắn muốn thoát?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (chon == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private void switchPanel(String cardName) {
        CardLayout cl = mainFrame.getCardLayout();
        cl.show(mainFrame.getPnlContent(), cardName);
    }
}