package VIEW;

import Model.*;
import java.awt.*;
import java.util.Date;
import java.util.List;
import javax.swing.*;

public class DialogDatPhong extends JDialog {

    // Khai báo public hoặc có Getter để Controller truy cập được
    private JTextField txtCCCD, txtHoTen, txtSDT, txtDiaChi, txtTienCoc;
    private JComboBox<LoaiPhong> cboLoaiPhong;
    private JComboBox<Phong> cboPhongTrong;
    private JLabel lblGiaPhong;
    private JSpinner spinNgayDen, spinNgayDi;
    private JButton btnTimKhach, btnLuu, btnHuy;

    public DialogDatPhong(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
    }

    private void initComponents() {
        this.setTitle("TẠO ĐẶT PHÒNG MỚI");
        this.setSize(700, 550);
        this.setLayout(new BorderLayout());

        // --- 1. Header ---
        JLabel lblHeader = new JLabel("THÔNG TIN ĐẶT PHÒNG", JLabel.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 22));
        lblHeader.setForeground(Color.BLUE);
        this.add(lblHeader, BorderLayout.NORTH);

        // --- 2. Body ---
        JPanel pnlBody = new JPanel(new GridLayout(1, 2, 10, 0));

        // Cột Trái (Khách)
        JPanel pnlTrai = new JPanel(new GridLayout(8, 1));
        pnlTrai.setBorder(BorderFactory.createTitledBorder("1. Khách Hàng"));

        pnlTrai.add(new JLabel("CCCD:"));
        JPanel pnlTim = new JPanel(new BorderLayout());
        txtCCCD = new JTextField();
        btnTimKhach = new JButton("Tìm");
        pnlTim.add(txtCCCD, BorderLayout.CENTER);
        pnlTim.add(btnTimKhach, BorderLayout.EAST);
        pnlTrai.add(pnlTim);

        pnlTrai.add(new JLabel("Họ Tên:"));
        txtHoTen = new JTextField();
        pnlTrai.add(txtHoTen);
        pnlTrai.add(new JLabel("SĐT:"));
        txtSDT = new JTextField();
        pnlTrai.add(txtSDT);
        pnlTrai.add(new JLabel("Địa Chỉ:"));
        txtDiaChi = new JTextField();
        pnlTrai.add(txtDiaChi);
        pnlBody.add(pnlTrai);

        // Cột Phải (Phòng)
        JPanel pnlPhai = new JPanel(new GridLayout(10, 1));
        pnlPhai.setBorder(BorderFactory.createTitledBorder("2. Phòng & Cọc"));

        pnlPhai.add(new JLabel("Loại Phòng:"));
        cboLoaiPhong = new JComboBox<>();
        pnlPhai.add(cboLoaiPhong);
        lblGiaPhong = new JLabel("Giá: 0 VNĐ");
        lblGiaPhong.setForeground(Color.RED);
        pnlPhai.add(lblGiaPhong);

        pnlPhai.add(new JLabel("Chọn Phòng:"));
        cboPhongTrong = new JComboBox<>();
        pnlPhai.add(cboPhongTrong);

        pnlPhai.add(new JLabel("Ngày Đến - Ngày Đi:"));
        JPanel pnlNgay = new JPanel(new GridLayout(1, 2));
        spinNgayDen = new JSpinner(new SpinnerDateModel());
        spinNgayDen.setEditor(new JSpinner.DateEditor(spinNgayDen, "dd/MM/yyyy HH:mm"));
        spinNgayDi = new JSpinner(new SpinnerDateModel());
        spinNgayDi.setEditor(new JSpinner.DateEditor(spinNgayDi, "dd/MM/yyyy HH:mm"));
        // Set mặc định ngày đi là ngày mai
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, 1);
        spinNgayDi.setValue(cal.getTime());
        pnlNgay.add(spinNgayDen);
        pnlNgay.add(spinNgayDi);
        pnlPhai.add(pnlNgay);

        pnlPhai.add(new JLabel("Tiền Cọc:"));
        txtTienCoc = new JTextField();
        pnlPhai.add(txtTienCoc);
        pnlBody.add(pnlPhai);
        this.add(pnlBody, BorderLayout.CENTER);

        // --- 3. Footer ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnHuy = new JButton("Hủy");
        btnLuu = new JButton("Lưu & Thanh Toán");
        btnLuu.setBackground(Color.GREEN);
        pnlFooter.add(btnHuy);
        pnlFooter.add(btnLuu);
        this.add(pnlFooter, BorderLayout.SOUTH);

        // --- SỰ KIỆN HỦY (Logic đơn giản để luôn ở View cũng được) ---
        btnHuy.addActionListener(e -> this.dispose());
    }

    // === CÁC HÀM GETTER ĐỂ CONTROLLER GỌI ===
    public JButton getBtnTimKhach() {
        return btnTimKhach;
    }

    public JButton getBtnLuu() {
        return btnLuu;
    }

    public JComboBox<LoaiPhong> getCboLoaiPhong() {
        return cboLoaiPhong;
    }

    public JComboBox<Phong> getCboPhongTrong() {
        return cboPhongTrong;
    }

    public JTextField getTxtCCCD() {
        return txtCCCD;
    }

    public JTextField getTxtHoTen() {
        return txtHoTen;
    }

    public JTextField getTxtSDT() {
        return txtSDT;
    }

    public JTextField getTxtDiaChi() {
        return txtDiaChi;
    }

    public JTextField getTxtTienCoc() {
        return txtTienCoc;
    }

    public JLabel getLblGiaPhong() {
        return lblGiaPhong;
    }

    public Date getNgayDen() {
        return (Date) spinNgayDen.getValue();
    }

    public Date getNgayDi() {
        return (Date) spinNgayDi.getValue();
    }

// --- PHẦN BỔ SUNG CHO CHỨC NĂNG SỬA ---
    // Biến lưu mã đặt phòng đang sửa (Mặc định là 0 hoặc -1 nghĩa là đang Tạo Mới)
    private int maDatPhongDangSua = 0;

    public int getMaDatPhongDangSua() {
        return maDatPhongDangSua;
    }

    public void setMaDatPhongDangSua(int maDatPhongDangSua) {
        this.maDatPhongDangSua = maDatPhongDangSua;
    }

    /**
     * Hàm này dùng để đổ dữ liệu cũ vào Form khi bấm nút Sửa
     */
    public void setModel(DatPhong dp, KhachHang kh) {
        // 1. Gán mã để Controller biết đây là sửa
        this.maDatPhongDangSua = dp.getMaDatPhong(); 
        
        // 2. Điền thông tin khách hàng (CODE MỚI THÊM)
        if (kh != null) {
            txtCCCD.setText(kh.getCccd());
            txtHoTen.setText(kh.getHoTen());
            txtSDT.setText(kh.getSdt());
            txtDiaChi.setText(kh.getDiaChi());
        }
        
        // 3. Set tiền cọc và ngày (Giữ nguyên)
        txtTienCoc.setText(String.valueOf((long)dp.getTienDatCoc()));
        spinNgayDen.setValue(dp.getNgayCheckIn());
        spinNgayDi.setValue(dp.getNgayCheckOut());
        
        // 4. Setup giao diện (Giữ nguyên)
        this.setTitle("CẬP NHẬT ĐƠN ĐẶT PHÒNG - MÃ: " + dp.getMaDatPhong());
        btnLuu.setText("Lưu Cập Nhật");
        btnLuu.setBackground(Color.ORANGE);
        
        // Khóa không cho sửa thông tin khách để tránh rối logic (hoặc mở nếu bạn muốn)
        txtCCCD.setEditable(false); 
        
        // 5. Chọn lại Phòng cũ trong Combobox (Giữ nguyên)
        ComboBoxModel<Phong> model = cboPhongTrong.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Phong p = model.getElementAt(i);
            if (p.getMaPhong() == dp.getMaPhong()) {
                cboPhongTrong.setSelectedIndex(i);
                break;
            }
        }
    }
}
