package Controller;

import VIEW.MainFrame;
import VIEW.QuanLyDatPhongPanel;
import VIEW.QuanLyPhongPanel;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import Controller.KhuyenMaiController;

public class MainController {

    private MainFrame mainFrame;

    // Khai báo các Controller con
    private PhongController phongController;
    private QuanLyDatPhongController datPhongController;
    private KhuyenMaiController khuyenMaiController;

    public MainController(MainFrame frame) {
        this.mainFrame = frame;
        initSubControllers(); // Khởi tạo logic cho các panel con
        initEvents();         // Bắt sự kiện menu
    }

    // 1. Kích hoạt các Controller con
    private void initSubControllers() {
        // Lấy các Panel từ MainFrame ra
        QuanLyPhongPanel pnlPhong = mainFrame.getPnlPhong();
        QuanLyDatPhongPanel pnlDatPhong = mainFrame.getPnlDatPhong();
        VIEW.QuanLyKhuyenMaiPanel pnlKM = mainFrame.getPnlKhuyenMai();
        // Gắn logic (Controller) vào các Panel đó
        // (Lưu ý: Bạn phải đảm bảo đã có PhongController trong package Controller nhé)
        this.phongController = new PhongController(pnlPhong);

        this.datPhongController = new QuanLyDatPhongController(pnlDatPhong);
        this.khuyenMaiController = new KhuyenMaiController(pnlKM);  
    }

    // 2. Bắt sự kiện chuyển Tab trên Menu
    private void initEvents() {
        // Nút Quản Lý Phòng
        mainFrame.getBtnQuanLyPhong().addActionListener(e -> {
            switchPanel("CARD_PHONG");
            // Reload lại dữ liệu phòng cho mới
            // mainFrame.getPnlPhong().loadData(); (Nếu cần)
        });

        // Nút Quản Lý Đặt Phòng
        mainFrame.getBtnQuanLyDatPhong().addActionListener(e -> {
            switchPanel("CARD_DATPHONG");
            // Reload lại dữ liệu đặt phòng
            mainFrame.getPnlDatPhong().loadData();
        });
        mainFrame.getBtnKhuyenMai().addActionListener(e -> {
            switchPanel("CARD_KHUYENMAI");});

        // Nút Đăng Xuất
        mainFrame.getBtnDangXuat().addActionListener(e -> {
            int chon = JOptionPane.showConfirmDialog(mainFrame,
                    "Bạn có chắc chắn muốn thoát?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (chon == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
    }

    // Hàm chuyển màn hình (Helper)
    private void switchPanel(String cardName) {
        CardLayout cl = mainFrame.getCardLayout();
        cl.show(mainFrame.getPnlContent(), cardName);
    }
}
