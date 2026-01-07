package VIEW;

import VIEW.QuanLyDatPhongPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QuanLyPhongPanel extends JPanel {

    // Khai báo các linh kiện (Components)
    private JTextField txtSoPhong;
    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiPhong;
    private JComboBox<String> cboTrangThai;
    private JTable tblPhong;
    private DefaultTableModel modelPhong;

    // Các nút bấm
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnTimKiem;
    private JButton btnXuatExcel;
    private JButton btnLamMoi;

    public QuanLyPhongPanel() {
        initComponents();
    }

    // Hàm khởi tạo giao diện
    private void initComponents() {
        // --- 1. PHẦN TIÊU ĐỀ ---
        JLabel lblTitle = new JLabel("QUẢN LÝ PHÒNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.BLUE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // --- 2. PHẦN NHẬP LIỆU ---
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin phòng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Số phòng & Loại phòng
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlInput.add(new JLabel("Số Phòng:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        txtSoPhong = new JTextField(15);
        pnlInput.add(txtSoPhong, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        pnlInput.add(new JLabel("Loại Phòng:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        cboLoaiPhong = new JComboBox<>(new String[]{"Phòng Đơn", "Phòng Đôi", "VIP"});
        pnlInput.add(cboLoaiPhong, gbc);

        // Dòng 2: Trạng thái
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlInput.add(new JLabel("Trạng Thái:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        cboTrangThai = new JComboBox<>(new String[]{"Trống", "Đang ở", "Đã đặt", "Đang dọn"});
        pnlInput.add(cboTrangThai, gbc);

        // --- 3. PHẦN NÚT BẤM CHỨC NĂNG (Dùng hàm createStyledButton) ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Gọi hàm tạo nút ở đây
        btnThem = createStyledButton("Thêm Mới", new Color(46, 204, 113), Color.WHITE);
        btnSua = createStyledButton("Cập Nhật", new Color(255, 193, 7), Color.BLACK);
        btnXoa = createStyledButton("Xóa", new Color(231, 76, 60), Color.WHITE);
        btnLamMoi = createStyledButton("Làm Mới", new Color(52, 152, 219), Color.WHITE);
        btnXuatExcel = createStyledButton("Xuất Excel", new Color(149, 165, 166), Color.WHITE);

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnXuatExcel);

        // --- 4. PHẦN TÌM KIẾM ---
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlSearch.add(new JLabel("Tìm kiếm (Số phòng): "));
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm"); // Nút tìm kiếm để mặc định hoặc style tùy bạn
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);

        // --- GOM NHÓM PHẦN ĐẦU (Header) ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.add(lblTitle);     // Tiêu đề trên cùng
        pnlHeader.add(pnlInput);     // Form nhập
        pnlHeader.add(pnlButtons);   // Nút bấm
        pnlHeader.add(pnlSearch);    // Thanh tìm kiếm

        // --- 5. PHẦN BẢNG DANH SÁCH ---
        String[] columns = {"Mã Phòng", "Số Phòng", "Mã Loại", "Trạng Thái"};
        modelPhong = new DefaultTableModel(columns, 0);
        tblPhong = new JTable(modelPhong);
        tblPhong.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tblPhong);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách phòng"));

        // --- LẮP RÁP VÀO PANEL CHÍNH (Sửa lại Layout cho đẹp) ---
        this.setLayout(new BorderLayout());
        this.add(pnlHeader, BorderLayout.NORTH);  // Đẩy hết phần nhập liệu lên trên
        this.add(scrollPane, BorderLayout.CENTER); // Bảng nằm giữa, tự giãn full màn hình
    }

    // =================================================================
    // KHU VỰC HÀM PHỤ (HELPER METHODS)
    // =================================================================
    // Đây là hàm bị thiếu khiến bạn gặp lỗi "createStyledButton can't found"
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Arial", Font.BOLD, 12));

        // Quan trọng để hiện màu trên nền tảng Java Swing cũ/mới khác nhau
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    // =================================================================
    // KHU VỰC GETTER
    // =================================================================
    public JTextField getTxtSoPhong() {
        return txtSoPhong;
    }

    public JTextField getTxtTimKiem() {
        return txtTimKiem;
    }

    public JComboBox<String> getCboLoaiPhong() {
        return cboLoaiPhong;
    }

    public JComboBox<String> getCboTrangThai() {
        return cboTrangThai;
    }

    public JButton getBtnThem() {
        return btnThem;
    }

    public JButton getBtnSua() {
        return btnSua;
    }

    public JButton getBtnXoa() {
        return btnXoa;
    }

    public JButton getBtnTimKiem() {
        return btnTimKiem;
    }

    public JButton getBtnXuatExcel() {
        return btnXuatExcel;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    public JTable getTblPhong() {
        return tblPhong;
    }

    public DefaultTableModel getModelPhong() {
        return modelPhong;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
