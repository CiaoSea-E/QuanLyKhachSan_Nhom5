package VIEW;

import DAO.DatPhongDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class QuanLyDatPhongPanel extends JPanel {

    private JTable tblDatPhong;
    private DefaultTableModel model;
    private DatPhongDAO dao;
    
    // Khai báo các nút chức năng
    private JButton btnTaoMoi, btnSua, btnHuyDon, btnXuatExcel, btnLamMoi;
    private JButton btnNhanPhong, btnTraPhong;
    private JButton btnTimNgay; // <--- NÚT TÌM MỚI
    
    private JComboBox<String> cboTrangThai; 
    private JSpinner spinNgayTim; // <--- Ô CHỌN NGÀY MỚI

    public QuanLyDatPhongPanel() {
        dao = new DatPhongDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());

        // 1. TIÊU ĐỀ
        JLabel lblTitle = new JLabel("QUẢN LÝ DANH SÁCH ĐẶT PHÒNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(41, 128, 185));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        this.add(lblTitle, BorderLayout.NORTH);

        // 2. THANH CÔNG CỤ (CHIA LÀM 2 DÒNG)
        JPanel pnlMainToolbar = new JPanel(new GridLayout(2, 1, 0, 0)); 
        
        // --- DÒNG 1: CÁC NÚT CHỨC NĂNG CHÍNH ---
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlActions.setBackground(new Color(245, 245, 245));

        btnTaoMoi = new JButton(" + Tạo Mới ");
        setupButton(btnTaoMoi, new Color(39, 174, 96));

        btnSua = new JButton(" Sửa Đơn ");
        setupButton(btnSua, new Color(243, 156, 18)); 

        btnHuyDon = new JButton(" Hủy Đơn ");
        setupButton(btnHuyDon, new Color(231, 76, 60));
        
        btnNhanPhong = new JButton(" Nhận Phòng ");
        setupButton(btnNhanPhong, new Color(41, 128, 185));

        btnTraPhong = new JButton(" Trả Phòng ");
        setupButton(btnTraPhong, new Color(155, 89, 182)); 

        btnXuatExcel = new JButton(" Xuất Excel ");
        setupButton(btnXuatExcel, new Color(46, 204, 113));

        pnlActions.add(btnTaoMoi);
        pnlActions.add(btnSua);
        pnlActions.add(btnHuyDon);
        pnlActions.add(Box.createHorizontalStrut(10)); 
        pnlActions.add(btnNhanPhong);
        pnlActions.add(btnTraPhong);
        pnlActions.add(Box.createHorizontalStrut(10));
        pnlActions.add(btnXuatExcel);

        // --- DÒNG 2: BỘ LỌC VÀ TÌM KIẾM NGÀY ---
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFilter.setBackground(new Color(245, 245, 245));
        
        // [CŨ] Lọc trạng thái
        JLabel lblLoc = new JLabel("Lọc trạng thái:");
        lblLoc.setFont(new Font("Arial", Font.BOLD, 14));
        String[] trangThai = {"Tất cả", "Đã đặt", "Đang ở", "Đã trả", "Đã hủy"};
        cboTrangThai = new JComboBox<>(trangThai);
        cboTrangThai.setPreferredSize(new Dimension(120, 30));
        cboTrangThai.setFont(new Font("Arial", Font.PLAIN, 13));

        // [MỚI] Tìm theo ngày
        JLabel lblNgay = new JLabel(" |  Ngày đến:");
        lblNgay.setFont(new Font("Arial", Font.BOLD, 14));
        
        spinNgayTim = new JSpinner(new SpinnerDateModel());
        spinNgayTim.setEditor(new JSpinner.DateEditor(spinNgayTim, "dd/MM/yyyy")); // Định dạng ngày Việt Nam
        spinNgayTim.setPreferredSize(new Dimension(120, 30));
        spinNgayTim.setFont(new Font("Arial", Font.PLAIN, 13));
        
        btnTimNgay = new JButton("Tìm");
        setupButton(btnTimNgay, new Color(52, 152, 219)); // Màu xanh dương
        btnTimNgay.setPreferredSize(new Dimension(70, 30));

        btnLamMoi = new JButton("Refresh");
        setupButton(btnLamMoi, Color.WHITE);
        btnLamMoi.setForeground(Color.BLACK);

        // Add vào dòng 2
        pnlFilter.add(lblLoc);
        pnlFilter.add(cboTrangThai);
        
        pnlFilter.add(lblNgay);      // Label Ngày
        pnlFilter.add(spinNgayTim);  // Ô chọn ngày
        pnlFilter.add(btnTimNgay);   // Nút tìm
        
        pnlFilter.add(Box.createHorizontalStrut(20));
        pnlFilter.add(btnLamMoi);
        
        pnlMainToolbar.add(pnlActions);
        pnlMainToolbar.add(pnlFilter);

        // 3. BẢNG DANH SÁCH
        String[] columns = {"Mã Phiếu", "Khách Hàng", "Phòng", "Ngày Check-In", "Ngày Check-Out", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDatPhong = new JTable(model);
        tblDatPhong.setRowHeight(30);
        tblDatPhong.setFont(new Font("Arial", Font.PLAIN, 14));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(52, 152, 219));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Arial", Font.BOLD, 14));
        
        for (int i = 0; i < tblDatPhong.getColumnModel().getColumnCount(); i++) {
            tblDatPhong.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tblDatPhong);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.add(pnlMainToolbar, BorderLayout.NORTH);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);

        this.add(pnlCenter, BorderLayout.CENTER);
    }

    public void loadData() {
        model.setRowCount(0);
        List<String[]> list = dao.getAllDatPhong();
        for (String[] row : list) {
            model.addRow(row);
        }
    }

    private void setupButton(JButton btn, Color bg) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(5, 10, 5, 10)); 
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
    public JButton getBtnTimNgay() { return btnTimNgay; }
    public java.util.Date getNgayTim() { return (java.util.Date) spinNgayTim.getValue(); }
}