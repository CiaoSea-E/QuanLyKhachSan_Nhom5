package VIEW;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class QuanLyKhuyenMaiPanel extends JPanel {

    // Khai báo các linh kiện
    private JTextField txtMaKM, txtTenKM, txtGiamGia, txtTimKiem;
    private JSpinner spinNgayBatDau, spinNgayKetThuc;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem, btnXuatExcel;
    private JTable tblKhuyenMai;
    private DefaultTableModel model;

    public QuanLyKhuyenMaiPanel() {
        initComponents();
    }

    private void initComponents() {
        // Sử dụng BorderLayout giống mẫu QuanLyDatPhongPanel
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // --- 1. TIÊU ĐỀ ---
        JLabel lblTitle = new JLabel("QUẢN LÝ CHƯƠNG TRÌNH KHUYẾN MÃI", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(41, 128, 185)); // Màu xanh chủ đạo
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        this.add(lblTitle, BorderLayout.NORTH);

        // --- 2. KHU VỰC NHẬP LIỆU & CHỨC NĂNG (CENTER - NORTH) ---
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setBackground(Color.WHITE);

        // A. Form Nhập Liệu
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                " Thông tin khuyến mãi ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(41, 128, 185)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Font chữ
        Font fontLabel = new Font("Arial", Font.BOLD, 13);
        Font fontInput = new Font("Arial", Font.PLAIN, 13);

        // Dòng 1: Mã & Tên
        gbc.gridx = 0; gbc.gridy = 0; pnlInput.add(createLabel("Mã KM:", fontLabel), gbc);
        gbc.gridx = 1; txtMaKM = new JTextField(18); txtMaKM.setFont(fontInput); pnlInput.add(txtMaKM, gbc);

        gbc.gridx = 2; pnlInput.add(createLabel("Tên Chương Trình:", fontLabel), gbc);
        gbc.gridx = 3; txtTenKM = new JTextField(18); txtTenKM.setFont(fontInput); pnlInput.add(txtTenKM, gbc);

        // Dòng 2: Giảm giá & Ngày bắt đầu
        gbc.gridx = 0; gbc.gridy = 1; pnlInput.add(createLabel("Số Tiền Giảm:", fontLabel), gbc);
        gbc.gridx = 1; txtGiamGia = new JTextField(18); txtGiamGia.setFont(fontInput); pnlInput.add(txtGiamGia, gbc);

        gbc.gridx = 2; pnlInput.add(createLabel("Ngày Bắt Đầu:", fontLabel), gbc);
        gbc.gridx = 3;
        spinNgayBatDau = new JSpinner(new SpinnerDateModel());
        spinNgayBatDau.setEditor(new JSpinner.DateEditor(spinNgayBatDau, "dd/MM/yyyy HH:mm"));
        spinNgayBatDau.setFont(fontInput);
        pnlInput.add(spinNgayBatDau, gbc);

        // Dòng 3: Ngày kết thúc
        gbc.gridx = 2; gbc.gridy = 2; pnlInput.add(createLabel("Ngày Kết Thúc:", fontLabel), gbc);
        gbc.gridx = 3;
        spinNgayKetThuc = new JSpinner(new SpinnerDateModel());
        spinNgayKetThuc.setEditor(new JSpinner.DateEditor(spinNgayKetThuc, "dd/MM/yyyy HH:mm"));
        spinNgayKetThuc.setFont(fontInput);
        pnlInput.add(spinNgayKetThuc, gbc);

        pnlTop.add(pnlInput);

        // B. Thanh Công Cụ (Buttons) - Giống style QuanLyDatPhong
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        pnlActions.setBackground(new Color(245, 245, 245));

        btnThem = new JButton(" Thêm ");
        setupButton(btnThem, new Color(46, 204, 113)); // Xanh lá

        btnSua = new JButton(" Sửa ");
        setupButton(btnSua, new Color(243, 156, 18)); // Cam

        btnXoa = new JButton(" Xóa ");
        setupButton(btnXoa, new Color(231, 76, 60)); // Đỏ

        btnLamMoi = new JButton(" Làm Mới ");
        setupButton(btnLamMoi, new Color(149, 165, 166)); // Xám

        btnXuatExcel = new JButton(" Xuất Excel ");
        setupButton(btnXuatExcel, new Color(39, 174, 96)); // Xanh đậm

        pnlActions.add(btnThem);
        pnlActions.add(btnSua);
        pnlActions.add(btnXoa);
        pnlActions.add(btnLamMoi);
        pnlActions.add(Box.createHorizontalStrut(10)); // Khoảng cách
        pnlActions.add(btnXuatExcel);

        // Phần tìm kiếm
        pnlActions.add(Box.createHorizontalStrut(20));
        JLabel lblTim = new JLabel("Tìm kiếm: ");
        lblTim.setFont(fontLabel);
        pnlActions.add(lblTim);
        
        txtTimKiem = new JTextField(12);
        txtTimKiem.setFont(fontInput);
        txtTimKiem.setPreferredSize(new Dimension(120, 30));
        pnlActions.add(txtTimKiem);

        btnTimKiem = new JButton("Tìm");
        setupButton(btnTimKiem, new Color(52, 152, 219)); // Xanh dương
        btnTimKiem.setPreferredSize(new Dimension(70, 30));
        pnlActions.add(btnTimKiem);

        pnlTop.add(pnlActions);
        
        // Thêm pnlTop vào vùng giữa (nhưng đẩy lên trên)
        JPanel pnlCenterContainer = new JPanel(new BorderLayout());
        pnlCenterContainer.add(pnlTop, BorderLayout.NORTH);

        // --- 3. BẢNG DỮ LIỆU ---
        String[] columns = {"Mã KM", "Tên Chương Trình", "Giảm Giá", "Ngày Bắt Đầu", "Ngày Kết Thúc", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblKhuyenMai = new JTable(model);
        
        // Style bảng giống QuanLyDatPhong
        tblKhuyenMai.setRowHeight(30);
        tblKhuyenMai.setFont(new Font("Arial", Font.PLAIN, 14));
        tblKhuyenMai.setSelectionBackground(new Color(236, 240, 241));
        tblKhuyenMai.setSelectionForeground(Color.BLACK);

        // Header bảng
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(52, 152, 219)); // Màu header xanh
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Arial", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < tblKhuyenMai.getColumnModel().getColumnCount(); i++) {
            tblKhuyenMai.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        JScrollPane scroll = new JScrollPane(tblKhuyenMai);
        pnlCenterContainer.add(scroll, BorderLayout.CENTER);

        this.add(pnlCenterContainer, BorderLayout.CENTER);
    }

    // --- HÀM HỖ TRỢ STYLE GIỐNG QUANLYDATPHONG ---
    
    private void setupButton(JButton btn, Color bg) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Margin giống mẫu
        btn.setMargin(new Insets(8, 15, 8, 15)); 
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        return lbl;
    }
    
    // --- GETTER (Giữ nguyên) ---
    public JTextField getTxtMaKM() { return txtMaKM; }
    public JTextField getTxtTenKM() { return txtTenKM; }
    public JTextField getTxtGiamGia() { return txtGiamGia; }
    public JTextField getTxtTimKiem() { return txtTimKiem; }
    public JSpinner getSpinNgayBatDau() { return spinNgayBatDau; }
    public JSpinner getSpinNgayKetThuc() { return spinNgayKetThuc; }
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JButton getBtnTimKiem() { return btnTimKiem; }
    public JButton getBtnXuatExcel() { return btnXuatExcel; }
    public JTable getTblKhuyenMai() { return tblKhuyenMai; }
    public DefaultTableModel getModel() { return model; }
}