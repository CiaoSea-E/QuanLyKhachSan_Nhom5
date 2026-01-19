package VIEW;

import Helper.ModernUI;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Giao diện Panel Quản Lý Danh Sách Phòng.
 * Chức năng: Hiển thị bảng phòng, Form nhập liệu, Tìm kiếm & Lọc.
 */
public class QuanLyPhongPanel extends JPanel {

    // ============================================================
    // PHẦN 1: KHAI BÁO CÁC THÀNH PHẦN GIAO DIỆN (COMPONENTS)
    // ============================================================

    // --- Nhóm 1: Nhập liệu (Form) ---
    private ModernUI.RoundedTextField txtSoPhong;
    private JComboBox<String> cboLoaiPhong; // Chọn loại phòng
    private JComboBox<String> cboTrangThai; // Chọn trạng thái (Trống, Đang ở...)

    // --- Nhóm 2: Tìm kiếm & Lọc (Toolbar) ---
    private ModernUI.RoundedTextField txtTimKiem;
    private JButton btnTimKiem;
    private JComboBox<String> cboLocLoai;      // Bộ lọc theo Loại phòng
    private JComboBox<String> cboLocTrangThai; // Bộ lọc theo Trạng thái

    // --- Nhóm 3: Bảng dữ liệu (Table) ---
    private JTable tblPhong;
    private DefaultTableModel modelPhong;

    // --- Nhóm 4: Các nút chức năng (Actions) ---
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnXuatExcel;

    // ============================================================
    // PHẦN 2: KHỞI TẠO (CONSTRUCTOR & INIT)
    // ============================================================

    public QuanLyPhongPanel() {
        initComponents();
        // Lưu ý: Dữ liệu sẽ được Controller nạp vào sau khi view hiện lên
    }

    /**
     * Hàm khởi tạo chính: Sắp xếp toàn bộ giao diện
     */
    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setBackground(ModernUI.BG_COLOR);

        // B1. Khởi tạo các đối tượng (Biến, Nút, Bảng...)
        initObjects();

        // B2. Xây dựng Vùng Trên (Tiêu đề + Form + Thanh công cụ)
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS)); // Xếp dọc
        pnlTop.setOpaque(false);
        pnlTop.setBorder(new EmptyBorder(0, 20, 0, 20)); // Căn lề 2 bên

        // 2.1 Tiêu đề
        pnlTop.add(ModernUI.createHeaderPanel("QUẢN LÝ DANH SÁCH PHÒNG"));
        pnlTop.add(Box.createVerticalStrut(15)); // Khoảng cách

        // 2.2 Form nhập liệu
        pnlTop.add(createFormLayout());
        
        // 2.3 Thanh công cụ (Nút bấm + Tìm kiếm)
        pnlTop.add(createToolbarLayout());

        // B3. Xây dựng Vùng Giữa (Bảng danh sách)
        JPanel pnlTableContainer = new JPanel(new BorderLayout());
        pnlTableContainer.setOpaque(false);
        pnlTableContainer.setBorder(new EmptyBorder(0, 20, 20, 20)); // Căn lề dưới và 2 bên
        
        // Dùng Helper tạo bảng đẹp
        pnlTableContainer.add(ModernUI.createTable(tblPhong));

        // B4. Lắp ráp vào Panel chính
        this.add(pnlTop, BorderLayout.NORTH);
        this.add(pnlTableContainer, BorderLayout.CENTER);
    }

    /**
     * Hàm khởi tạo chi tiết các đối tượng
     */
    private void initObjects() {
        // --- 1. Khởi tạo Inputs ---
        txtSoPhong = new ModernUI.RoundedTextField(15);
        
        cboLoaiPhong = new JComboBox<>(); // Dữ liệu sẽ nạp từ DB
        cboLoaiPhong.setBackground(Color.WHITE);
        
        cboTrangThai = new JComboBox<>(new String[]{"Trống", "Đang ở", "Đã đặt", "Đang dọn"});
        cboTrangThai.setBackground(Color.WHITE);

        // --- 2. Khởi tạo Bộ Lọc ---
        txtTimKiem = new ModernUI.RoundedTextField(15);
        btnTimKiem = ModernUI.createPrimaryButton("Tìm");
        
        cboLocLoai = new JComboBox<>(); // Dữ liệu nạp từ DB (kèm dòng "Tất cả")
        cboLocLoai.setBackground(Color.WHITE);

        cboLocTrangThai = new JComboBox<>(new String[]{"Tất cả", "Trống", "Đang ở", "Đã đặt", "Đang dọn"});
        cboLocTrangThai.setBackground(Color.WHITE);

        // --- 3. Khởi tạo Nút bấm ---
        btnThem = ModernUI.createGradientButton("Thêm Mới", ModernUI.GREEN_COLOR, ModernUI.GREEN_COLOR.darker());
        btnSua  = ModernUI.createGradientButton("Cập Nhật", ModernUI.ACCENT, ModernUI.ACCENT.darker());
        btnXoa  = ModernUI.createGradientButton("Xóa Phòng", ModernUI.RED_COLOR, ModernUI.RED_COLOR.darker());
        btnLamMoi = ModernUI.createGradientButton("Làm Mới", Color.GRAY, Color.DARK_GRAY);
        btnXuatExcel = ModernUI.createGradientButton("Xuất Excel", new Color(33, 115, 70), new Color(33, 115, 70).darker());

        // --- 4. Khởi tạo Bảng ---
        // Cấu trúc cột: ID (Ẩn) | Số Phòng | Loại Phòng | Giá Tiền | Trạng Thái
        String[] columns = {"ID Ẩn", "Số Phòng", "Loại Phòng", "Giá Tiền (VNĐ)", "Trạng Thái"};
        
        modelPhong = new DefaultTableModel(columns, 0) {
            @Override // Chặn không cho người dùng sửa trực tiếp trên ô
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tblPhong = new JTable(modelPhong);
    }

    // ============================================================
    // PHẦN 3: CÁC HÀM BỐ CỤC (LAYOUT HELPERS)
    // ============================================================

    /**
     * Tạo giao diện Form nhập liệu (GridBagLayout)
     */
    private JPanel createFormLayout() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        
        // Tạo khung viền có tiêu đề
        pnl.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            " THÔNG TIN PHÒNG ",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            ModernUI.FONT_BOLD,
            ModernUI.PRIMARY
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15); // Khoảng cách giữa các ô
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Số Phòng + Loại Phòng
        gbc.gridx = 0; gbc.gridy = 0; pnl.add(ModernUI.createLabel("Số Phòng:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; pnl.add(txtSoPhong, gbc);

        gbc.gridx = 2; gbc.gridy = 0; pnl.add(ModernUI.createLabel("Loại Phòng:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; 
        cboLoaiPhong.setPreferredSize(new Dimension(200, 35));
        pnl.add(cboLoaiPhong, gbc);

        // Dòng 2: Trạng Thái
        gbc.gridx = 0; gbc.gridy = 1; pnl.add(ModernUI.createLabel("Trạng Thái:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; 
        cboTrangThai.setPreferredSize(new Dimension(200, 35));
        pnl.add(cboTrangThai, gbc);

        return pnl;
    }

    /**
     * Tạo giao diện Thanh công cụ (Toolbar)
     */
    private JPanel createToolbarLayout() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(15, 0, 15, 0));

        // Phần Trái: Các nút chức năng (Thêm, Sửa, Xóa...)
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLeft.setOpaque(false);
        pnlLeft.add(btnThem);
        pnlLeft.add(btnSua);
        pnlLeft.add(btnXoa);
        pnlLeft.add(btnLamMoi);
        pnlLeft.add(btnXuatExcel);

        // Phần Phải: Bộ Lọc & Tìm kiếm
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setOpaque(false);
        
        // ComboBox Lọc
        cboLocLoai.setPreferredSize(new Dimension(110, 35));
        pnlRight.add(cboLocLoai);
        
        cboLocTrangThai.setPreferredSize(new Dimension(100, 35));
        pnlRight.add(cboLocTrangThai);

        // Ô Tìm kiếm
        pnlRight.add(ModernUI.createLabel("Tìm số:"));
        txtTimKiem.setPreferredSize(new Dimension(100, 35));
        pnlRight.add(txtTimKiem);
        pnlRight.add(btnTimKiem);

        pnl.add(pnlLeft, BorderLayout.CENTER);
        pnl.add(pnlRight, BorderLayout.EAST);
        return pnl;
    }

    // ============================================================
    // PHẦN 4: GETTERS (ĐỂ CONTROLLER TRUY CẬP DỮ LIỆU)
    // ============================================================
    
    public JTextField getTxtSoPhong() { return txtSoPhong; }
    public JTextField getTxtTimKiem() { return txtTimKiem; }
    
    // ComboBox
    public JComboBox<String> getCboLoaiPhong() { return cboLoaiPhong; }
    public JComboBox<String> getCboTrangThai() { return cboTrangThai; }
    public JComboBox<String> getCboLocLoai() { return cboLocLoai; }
    public JComboBox<String> getCboLocTrangThai() { return cboLocTrangThai; }
    
    // Buttons
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnTimKiem() { return btnTimKiem; }
    public JButton getBtnXuatExcel() { return btnXuatExcel; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    
    // Table
    public JTable getTblPhong() { return tblPhong; }
    public DefaultTableModel getModelPhong() { return modelPhong; }
}