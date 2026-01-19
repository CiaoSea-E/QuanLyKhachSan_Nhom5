package VIEW;

import DAO.DatPhongDAO;
import Helper.ModernUI; 
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class QuanLyDatPhongPanel extends JPanel {

    private JTable tblDatPhong;
    private DefaultTableModel model;
    private DatPhongDAO dao;
    
    // Nút chức năng
    private JButton btnTaoMoi, btnSua, btnHuyDon, btnXuatExcel, btnLamMoi;
    private JButton btnNhanPhong, btnTraPhong;
    
    // Tìm kiếm
    private JButton btnTimNgayIn, btnTimNgayOut;
    private JComboBox<String> cboTrangThai; 
    private JSpinner spinNgayIn, spinNgayOut;

    public QuanLyDatPhongPanel() {
        dao = new DatPhongDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setBackground(ModernUI.BG_COLOR);

        initObjects();

        // PHẦN TRÊN: HEADER + TOOLBAR
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setOpaque(false);
        pnlTop.setBorder(new EmptyBorder(0, 20, 0, 20));

        pnlTop.add(ModernUI.createHeaderPanel("QUẢN LÝ DANH SÁCH ĐẶT PHÒNG"));
        pnlTop.add(Box.createVerticalStrut(15));
        
        // Add Toolbar đã được sắp xếp lại
        pnlTop.add(createToolbarPanel());

        // PHẦN GIỮA: BẢNG DỮ LIỆU
        JPanel pnlTableContainer = new JPanel(new BorderLayout());
        pnlTableContainer.setOpaque(false);
        pnlTableContainer.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        JScrollPane scroll = ModernUI.createTable(tblDatPhong);
        pnlTableContainer.add(scroll);

        this.add(pnlTop, BorderLayout.NORTH);
        this.add(pnlTableContainer, BorderLayout.CENTER);
    }

    private void initObjects() {
        // --- NHÓM 1: QUẢN LÝ ĐƠN (Thêm, Sửa, Hủy) ---
        btnTaoMoi = ModernUI.createGradientButton("+ Tạo Mới", ModernUI.GREEN_COLOR, ModernUI.GREEN_COLOR.darker());
        btnSua = ModernUI.createGradientButton("Sửa Đơn", ModernUI.ACCENT, ModernUI.ACCENT.darker());
        btnHuyDon = ModernUI.createGradientButton("Hủy Đơn", ModernUI.RED_COLOR, ModernUI.RED_COLOR.darker());
        
        // --- NHÓM 2: QUY TRÌNH (Nhận, Trả) ---
        // Chỉnh màu sắc riêng cho dễ nhận diện
        btnNhanPhong = new ModernUI.ModernButton("Check-In", new Color(41, 128, 185), new Color(41, 128, 185).darker());
        btnTraPhong = new ModernUI.ModernButton("Check-Out", new Color(142, 68, 173), new Color(142, 68, 173).darker());
        btnNhanPhong.setPreferredSize(new Dimension(100, 35));
        btnTraPhong.setPreferredSize(new Dimension(100, 35));

        // --- NHÓM 3: TIỆN ÍCH ---
        btnXuatExcel = ModernUI.createGradientButton("Xuất Excel", new Color(33, 115, 70), new Color(33, 115, 70).darker());
        
        // --- BỘ LỌC & TÌM KIẾM ---
        String[] trangThai = {"Tất cả", "Đã đặt", "Đang ở", "Đã trả", "Đã hủy"};
        cboTrangThai = new JComboBox<>(trangThai);
        cboTrangThai.setBackground(Color.WHITE);
        cboTrangThai.setPreferredSize(new Dimension(110, 35));

        spinNgayIn = new JSpinner(new SpinnerDateModel());
        spinNgayIn.setEditor(new JSpinner.DateEditor(spinNgayIn, "dd/MM/yyyy"));
        spinNgayIn.setPreferredSize(new Dimension(110, 35));
        btnTimNgayIn = ModernUI.createPrimaryButton("Tìm");
        btnTimNgayIn.setPreferredSize(new Dimension(60, 35)); // Nút tìm nhỏ gọn
        
        spinNgayOut = new JSpinner(new SpinnerDateModel());
        spinNgayOut.setEditor(new JSpinner.DateEditor(spinNgayOut, "dd/MM/yyyy"));
        spinNgayOut.setPreferredSize(new Dimension(110, 35));
        btnTimNgayOut = ModernUI.createPrimaryButton("Tìm");
        btnTimNgayOut.setPreferredSize(new Dimension(60, 35));

        // [FIX] Tăng kích thước nút Refresh để không bị mất chữ
        btnLamMoi = new ModernUI.ModernButton("Làm Mới", Color.GRAY, Color.DARK_GRAY);
        btnLamMoi.setPreferredSize(new Dimension(100, 35)); 

        // --- BẢNG ---
        String[] columns = {"Mã Phiếu", "Khách Hàng", "Phòng", "Ngày Check-In", "Ngày Check-Out", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblDatPhong = new JTable(model);
        
        // [FIX] Set độ rộng cột để hiển thị đủ ngày giờ
        tblDatPhong.getColumnModel().getColumn(0).setPreferredWidth(60);  // Mã
        tblDatPhong.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên
        tblDatPhong.getColumnModel().getColumn(2).setPreferredWidth(60);  // Phòng
        tblDatPhong.getColumnModel().getColumn(3).setPreferredWidth(140); // Ngày In
        tblDatPhong.getColumnModel().getColumn(4).setPreferredWidth(140); // Ngày Out
    }

    private JPanel createToolbarPanel() {
        // Sử dụng GridBagLayout hoặc BoxLayout để kiểm soát tốt hơn
        // Ở đây dùng JPanel lồng nhau để chia khu vực
        JPanel pnlMain = new JPanel(new BorderLayout(10, 10)); 
        pnlMain.setOpaque(false);
        pnlMain.setBorder(new EmptyBorder(0, 0, 15, 0));

        // --- DÒNG 1: CÁC NÚT THAO TÁC (Action Buttons) ---
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlActions.setOpaque(false);
        
        // Nhóm CRUD
        pnlActions.add(btnTaoMoi);
        pnlActions.add(btnSua);
        pnlActions.add(btnHuyDon);
        
        // Vách ngăn
        pnlActions.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Nhóm Quy trình
        pnlActions.add(btnNhanPhong);
        pnlActions.add(btnTraPhong);
        
        // Nút Excel dạt sang phải (Dùng panel riêng nếu muốn, hoặc để chung)
        pnlActions.add(Box.createHorizontalStrut(20));
        pnlActions.add(btnXuatExcel);

        // --- DÒNG 2: BỘ LỌC (Filters) ---
        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlFilters.setOpaque(false);
        
        pnlFilters.add(ModernUI.createLabel("Trạng thái:"));
        pnlFilters.add(cboTrangThai);
        
        pnlFilters.add(Box.createHorizontalStrut(15));
        pnlFilters.add(ModernUI.createLabel("Ngày Đến:"));
        pnlFilters.add(spinNgayIn);
        pnlFilters.add(btnTimNgayIn);
        
        pnlFilters.add(Box.createHorizontalStrut(15));
        pnlFilters.add(ModernUI.createLabel("Ngày Đi:"));
        pnlFilters.add(spinNgayOut);
        pnlFilters.add(btnTimNgayOut);
        
        pnlFilters.add(Box.createHorizontalStrut(20));
        pnlFilters.add(btnLamMoi);

        // Add vào Panel chính
        pnlMain.add(pnlActions, BorderLayout.NORTH);
        pnlMain.add(pnlFilters, BorderLayout.CENTER);
        
        return pnlMain;
    }

    public void loadData() {
        model.setRowCount(0);
        List<String[]> list = dao.getAllDatPhong();
        for (String[] row : list) {
            model.addRow(row);
        }
    }

    // Getters
    public JButton getBtnTaoMoi() { return btnTaoMoi; }
    public JButton getBtnHuyDon() { return btnHuyDon; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JTable getTblDatPhong() { return tblDatPhong; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXuatExcel() { return btnXuatExcel; }
    public JComboBox<String> getCboTrangThai() { return cboTrangThai; }
    public JButton getBtnNhanPhong() { return btnNhanPhong; } 
    public JButton getBtnTraPhong() { return btnTraPhong; } 
    
    public JButton getBtnTimNgayIn() { return btnTimNgayIn; }
    public java.util.Date getNgayIn() { return (java.util.Date) spinNgayIn.getValue(); }
    
    public JButton getBtnTimNgayOut() { return btnTimNgayOut; } 
    public java.util.Date getNgayOut() { return (java.util.Date) spinNgayOut.getValue(); }
}