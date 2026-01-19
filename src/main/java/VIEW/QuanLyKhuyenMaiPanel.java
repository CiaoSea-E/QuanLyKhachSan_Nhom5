package VIEW;

import Helper.ModernUI;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class QuanLyKhuyenMaiPanel extends JPanel {

    // ============================================================
    // PHẦN 1: KHAI BÁO BIẾN (COMPONENTS)
    // ============================================================

    // --- Nhóm 1: Các ô nhập liệu (Form Inputs) ---
    private ModernUI.RoundedTextField txtCode;
    private ModernUI.RoundedTextField txtTenSuKien;
    private ModernUI.RoundedTextField txtGiamGia;
    private ModernUI.RoundedTextField txtSoLuong;
    private JComboBox<String> cboLoaiGiam;   // % hoặc VNĐ
    private JComboBox<String> cboTrangThai;  // Hoạt động / Hết hạn
    private JSpinner spinNgayBatDau;
    private JSpinner spinNgayKetThuc;

    // --- Nhóm 2: Các nút chức năng (Actions) ---
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnXuatExcel;

    // --- Nhóm 3: Tìm kiếm & Lọc ---
    private ModernUI.RoundedTextField txtTimKiem;
    private JComboBox<String> cboLocLoai;
    private JButton btnTimKiem;

    // --- Nhóm 4: Bảng hiển thị dữ liệu (Table) ---
    private JTable tblKhuyenMai;
    private DefaultTableModel model;

    // ============================================================
    // PHẦN 2: KHỞI TẠO GIAO DIỆN (CONSTRUCTOR & INIT)
    // ============================================================

    public QuanLyKhuyenMaiPanel() {
        initComponents();
    }

    /**
     * Hàm khởi tạo chính, sắp xếp toàn bộ giao diện.
     */
    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setBackground(ModernUI.BG_COLOR);

        // B1. Khởi tạo các đối tượng (biến) trước
        initObjects();

        // B2. Xây dựng phần Panel Trên (Header + Form + Toolbar)
        JPanel pnlTop = createTopPanel();

        // B3. Xây dựng phần Panel Giữa (Bảng dữ liệu)
        JPanel pnlCenter = createTablePanel();

        // B4. Thêm vào Panel chính
        this.add(pnlTop, BorderLayout.NORTH);
        this.add(pnlCenter, BorderLayout.CENTER);
    }

    /**
     * Khởi tạo chi tiết các component (New object, gán thuộc tính).
     * Tách ra để code chính gọn gàng hơn.
     */
    private void initObjects() {
        // 1. Inputs
        txtCode = new ModernUI.RoundedTextField();
        txtTenSuKien = new ModernUI.RoundedTextField();
        txtGiamGia = new ModernUI.RoundedTextField();
        txtSoLuong = new ModernUI.RoundedTextField();
        
        cboLoaiGiam = new JComboBox<>(new String[]{"VNĐ", "%"});
        cboLoaiGiam.setBackground(Color.WHITE);
        
        cboTrangThai = new JComboBox<>(new String[]{"Đang hoạt động", "Hết hạn", "Tạm dừng"});
        cboTrangThai.setBackground(Color.WHITE);

        // Setup ngày giờ
        spinNgayBatDau = new JSpinner(new SpinnerDateModel());
        spinNgayBatDau.setEditor(new JSpinner.DateEditor(spinNgayBatDau, "dd/MM/yyyy HH:mm"));
        
        spinNgayKetThuc = new JSpinner(new SpinnerDateModel());
        spinNgayKetThuc.setEditor(new JSpinner.DateEditor(spinNgayKetThuc, "dd/MM/yyyy HH:mm"));

        // 2. Search & Filter
        cboLocLoai = new JComboBox<>(new String[]{"Tất cả", "VNĐ", "%"});
        cboLocLoai.setBackground(Color.WHITE);
        txtTimKiem = new ModernUI.RoundedTextField(15);
        btnTimKiem = ModernUI.createPrimaryButton("Tìm");

        // 3. Buttons (Sử dụng ModernUI để tạo nút đẹp)
        btnThem = ModernUI.createGradientButton("Thêm Mã", ModernUI.GREEN_COLOR, ModernUI.GREEN_COLOR.darker());
        btnSua = ModernUI.createGradientButton("Cập Nhật", ModernUI.ACCENT, ModernUI.ACCENT.darker());
        btnXoa = ModernUI.createGradientButton("Xóa Mã", ModernUI.RED_COLOR, ModernUI.RED_COLOR.darker());
        btnLamMoi = ModernUI.createGradientButton("Làm Mới", Color.GRAY, Color.DARK_GRAY);
        btnXuatExcel = ModernUI.createGradientButton("Xuất Excel", new Color(33, 115, 70), new Color(33, 115, 70).darker());

        // 4. Table
        String[] columns = {"Mã KM", "Code", "Tên Sự Kiện", "Giảm Giá", "Loại", "Số Lượng", "Ngày Bắt Đầu", "Ngày Kết Thúc", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; } // Không cho sửa trực tiếp
        };
        tblKhuyenMai = new JTable(model);
    }

    // ============================================================
    // PHẦN 3: CÁC HÀM BỐ CỤC (LAYOUT HELPERS)
    // ============================================================

    /**
     * Tạo vùng hiển thị phía trên: Header -> Form -> Toolbar
     */
    private JPanel createTopPanel() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(0, 20, 0, 20)); // Margin 2 bên

        // 1. Tiêu đề
        pnl.add(ModernUI.createHeaderPanel("QUẢN LÝ KHUYẾN MÃI"));
        pnl.add(Box.createVerticalStrut(15)); // Khoảng cách

        // 2. Form nhập liệu
        pnl.add(createFormLayout());
        pnl.add(Box.createVerticalStrut(10)); 

        // 3. Thanh công cụ (Nút bấm + Tìm kiếm)
        pnl.add(createToolbarLayout());

        return pnl;
    }

    /**
     * Layout cho Form nhập liệu (Sử dụng GridBagLayout để căn chỉnh đẹp)
     */
    private JPanel createFormLayout() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        // Tạo khung viền có tiêu đề
        pnl.setBorder(BorderFactory.createTitledBorder(
            null, " THÔNG TIN KHUYẾN MÃI ", 
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            ModernUI.FONT_BOLD, ModernUI.PRIMARY
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 20); // Padding giữa các ô
        gbc.fill = GridBagConstraints.HORIZONTAL; 

        // Dòng 1: Code + Tên sự kiện
        addInput(pnl, gbc, 0, 0, "Mã Code:", txtCode);
        addInput(pnl, gbc, 1, 0, "Tên Sự Kiện:", txtTenSuKien);

        // Dòng 2: Mức giảm + Loại giảm
        addInput(pnl, gbc, 0, 1, "Mức Giảm:", txtGiamGia);
        // Cột Label (Loại Giảm)
        gbc.gridx = 2; gbc.gridy = 1; 
        gbc.weightx = 0.0; // Label không giãn
        pnl.add(ModernUI.createLabel("Loại Giảm:"), gbc);
        // Cột Input (ComboBox)
        gbc.gridx = 3; gbc.gridy = 1; 
        gbc.weightx = 1.0; // Input giãn hết mức
        pnl.add(cboLoaiGiam, gbc);

        // Dòng 3: Số lượng + Trạng thái
        addInput(pnl, gbc, 0, 2, "Số Lượng:", txtSoLuong);
        gbc.gridx = 2; gbc.gridy = 2; 
        gbc.weightx = 0.0; 
        pnl.add(ModernUI.createLabel("Trạng Thái:"), gbc);
        
        gbc.gridx = 3; gbc.gridy = 2; 
        gbc.weightx = 1.0; 
        pnl.add(cboTrangThai, gbc);

        // Dòng 4: Ngày bắt đầu + Kết thúc
        gbc.gridx = 0; gbc.gridy = 3; 
        gbc.weightx = 0.0; 
        pnl.add(ModernUI.createLabel("Ngày Bắt Đầu:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; 
        gbc.weightx = 1.0; 
        pnl.add(spinNgayBatDau, gbc);
        
        // Ngày kết thúc
        gbc.gridx = 2; gbc.gridy = 3; 
        gbc.weightx = 0.0; 
        pnl.add(ModernUI.createLabel("Ngày Kết Thúc:"), gbc);
        
        gbc.gridx = 3; gbc.gridy = 3; 
        gbc.weightx = 1.0; 
        pnl.add(spinNgayKetThuc, gbc);

        return pnl;
    }
    
    /**
     * Layout cho Thanh công cụ (Toolbar)
     */
    private JPanel createToolbarLayout() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        
        // Trái: Các nút thao tác (Thêm, Sửa, Xóa, Excel...)
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLeft.setOpaque(false);
        pnlLeft.add(btnThem);
        pnlLeft.add(btnSua);
        pnlLeft.add(btnXoa);
        pnlLeft.add(btnLamMoi);
        pnlLeft.add(btnXuatExcel);
        
        // Phải: Tìm kiếm & Lọc
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setOpaque(false);
        
        pnlRight.add(ModernUI.createLabel("Lọc Loại:"));
        cboLocLoai.setPreferredSize(new Dimension(80, 35));
        pnlRight.add(cboLocLoai);
        
        pnlRight.add(ModernUI.createLabel("Tìm:"));
        txtTimKiem.setPreferredSize(new Dimension(150, 35));
        pnlRight.add(txtTimKiem);
        pnlRight.add(btnTimKiem);
        
        pnl.add(pnlLeft, BorderLayout.WEST);
        pnl.add(pnlRight, BorderLayout.EAST);
        return pnl;
    }

    /**
     * Tạo Panel chứa bảng dữ liệu (Table)
     */
    private JPanel createTablePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(0, 20, 20, 20)); // Cách lề dưới và 2 bên
        
        pnl.add(ModernUI.createTable(tblKhuyenMai));
        return pnl;
    }
    
    /**
     * Hàm phụ để thêm component vào GridBagLayout nhanh hơn
     */
    private void addInput(JPanel pnl, GridBagConstraints gbc, int x, int y, String label, Component comp) {
        // 1. Cấu hình cho LABEL (Cột chẵn)
        gbc.gridx = x * 2;     
        gbc.gridy = y; 
        gbc.weightx = 0.0; 
        pnl.add(ModernUI.createLabel(label), gbc);
        
        // 2. Cấu hình cho INPUT (Cột lẻ)
        gbc.gridx = x * 2 + 1; 
        gbc.gridy = y; 
        gbc.weightx = 1.0; //Input giãn ra lấp đầy khoảng trống còn lại
        pnl.add(comp, gbc);
    }

    // ============================================================
    // PHẦN 4: GETTERS (ĐỂ CONTROLLER TRUY CẬP DỮ LIỆU)
    // ============================================================
    public JTextField getTxtCode() { return txtCode; }
    public JTextField getTxtTenSuKien() { return txtTenSuKien; }
    public JTextField getTxtGiamGia() { return txtGiamGia; }
    public JTextField getTxtSoLuong() { return txtSoLuong; }
    
    public JComboBox<String> getCboLoaiGiam() { return cboLoaiGiam; }
    public JComboBox<String> getCboTrangThai() { return cboTrangThai; }
    public JSpinner getSpinNgayBatDau() { return spinNgayBatDau; }
    public JSpinner getSpinNgayKetThuc() { return spinNgayKetThuc; }
    
    public JTable getTblKhuyenMai() { return tblKhuyenMai; }
    public DefaultTableModel getModel() { return model; }
    
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JButton getBtnXuatExcel() { return btnXuatExcel; }
    
    public JComboBox<String> getCboLocLoai() { return cboLocLoai; }
    public JTextField getTxtTimKiem() { return txtTimKiem; }
    public JButton getBtnTimKiem() { return btnTimKiem; }
}