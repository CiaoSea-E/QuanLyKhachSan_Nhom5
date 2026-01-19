package VIEW;

import Helper.ModernUI;
import Model.*;
import java.awt.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Màn hình Dialog (Cửa sổ nổi) nhập thông tin Đặt Phòng.
 * Sử dụng chung cho 2 mục đích:
 * 1. Tạo đơn mới (Form trống).
 * 2. Cập nhật đơn cũ (Form có sẵn dữ liệu).
 */
public class DialogDatPhong extends JDialog {

    // --- 1. KHAI BÁO CÁC COMPONENT GIAO DIỆN ---
    
    // Phần thông tin khách hàng
    private ModernUI.RoundedTextField txtCCCD, txtHoTen, txtSDT, txtDiaChi;
    private JButton btnTimKhach; // Nút tìm khách theo CCCD
    
    // Phần thông tin phòng & thanh toán
    private JComboBox<LoaiPhong> cboLoaiPhong;
    private JComboBox<Phong> cboPhongTrong;
    private JSpinner spinNgayDen, spinNgayDi;
    private ModernUI.RoundedTextField txtTienCoc;
    private JLabel lblGiaPhong;
    
    // Nút xác nhận
    private JButton btnLuu, btnHuy;
    
    // Biến trạng thái: Lưu ID đơn hàng nếu đang ở chế độ Sửa (0 = Thêm mới)
    private int maDatPhongDangSua = 0;

    // --- 2. CONSTRUCTOR ---
    public DialogDatPhong(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent); // Căn giữa màn hình
    }

    // --- 3. KHỞI TẠO GIAO DIỆN CHÍNH ---
    private void initComponents() {
        this.setTitle("Đặt Phòng Mới");
        this.setSize(1000, 650);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(ModernUI.BG_COLOR);

        // A. Header (Tiêu đề trên cùng)
        this.add(ModernUI.createHeaderPanel("THÔNG TIN ĐẶT PHÒNG"), BorderLayout.NORTH);

        // B. Body (Phần nội dung chính - Chia làm 2 cột)
        JPanel pnlBody = new JPanel(new GridLayout(1, 2, 25, 0)); // 1 dòng, 2 cột, khoảng cách 25px
        pnlBody.setOpaque(false);
        pnlBody.setBorder(new EmptyBorder(25, 25, 25, 25)); // Căn lề các bên

        // Cột Trái: Thông tin Khách Hàng
        JPanel pnlKhach = createSectionPanel("THÔNG TIN KHÁCH HÀNG");
        layoutKhachHang(pnlKhach);
        pnlBody.add(pnlKhach);

        // Cột Phải: Thông tin Phòng
        JPanel pnlPhong = createSectionPanel("CHI TIẾT PHÒNG & CỌC");
        layoutPhong(pnlPhong);
        pnlBody.add(pnlPhong);

        this.add(pnlBody, BorderLayout.CENTER);

        // C. Footer (Các nút bấm dưới cùng)
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlFooter.setBackground(Color.WHITE);
        
        btnHuy = new ModernUI.ModernButton("THOÁT", ModernUI.RED_COLOR, ModernUI.RED_COLOR.darker());
        btnLuu = new ModernUI.ModernButton("XÁC NHẬN", ModernUI.GREEN_COLOR, ModernUI.GREEN_COLOR.darker());

        pnlFooter.add(btnHuy);
        pnlFooter.add(btnLuu);
        this.add(pnlFooter, BorderLayout.SOUTH);

        // Sự kiện mặc định nút Thoát
        btnHuy.addActionListener(e -> this.dispose());
    }

    // --- 4. CÁC HÀM HỖ TRỢ LAYOUT (SẮP XẾP GIAO DIỆN) ---

    // Tạo khung viền có tiêu đề cho từng vùng
    private JPanel createSectionPanel(String title) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        Border line = BorderFactory.createLineBorder(new Color(230, 230, 230));
        Border titled = BorderFactory.createTitledBorder(line, title, 
                0, 0, ModernUI.FONT_BOLD, ModernUI.PRIMARY);
        p.setBorder(BorderFactory.createCompoundBorder(titled, new EmptyBorder(10, 20, 10, 20)));
        return p;
    }

    // Sắp xếp các ô nhập liệu Khách Hàng
    private void layoutKhachHang(JPanel p) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0); // Khoảng cách giữa các dòng
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0; 
        int y = 0; // Biến đếm dòng

        // Dòng 1: CCCD + Nút Tìm
        gbc.gridy = y++; p.add(ModernUI.createLabel("CCCD / CMND:"), gbc);
        JPanel pnlTim = new JPanel(new BorderLayout(10, 0));
        pnlTim.setOpaque(false);
        
        txtCCCD = new ModernUI.RoundedTextField(15);
        btnTimKhach = new ModernUI.ModernButton("TÌM", ModernUI.PRIMARY, ModernUI.PRIMARY_LIGHT);
        btnTimKhach.setPreferredSize(new Dimension(70, 35));
        
        pnlTim.add(txtCCCD, BorderLayout.CENTER);
        pnlTim.add(btnTimKhach, BorderLayout.EAST);
        gbc.gridy = y++; p.add(pnlTim, gbc);

        // Dòng 2: Họ Tên
        gbc.gridy = y++; p.add(ModernUI.createLabel("Họ Tên Khách:"), gbc);
        gbc.gridy = y++; txtHoTen = new ModernUI.RoundedTextField(15); p.add(txtHoTen, gbc);

        // Dòng 3: Số điện thoại
        gbc.gridy = y++; p.add(ModernUI.createLabel("Số Điện Thoại:"), gbc);
        gbc.gridy = y++; txtSDT = new ModernUI.RoundedTextField(15); p.add(txtSDT, gbc);

        // Dòng 4: Địa chỉ
        gbc.gridy = y++; p.add(ModernUI.createLabel("Địa Chỉ:"), gbc);
        gbc.gridy = y++; 
        gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.NORTH; // Đẩy lên trên
        txtDiaChi = new ModernUI.RoundedTextField(15); p.add(txtDiaChi, gbc);
    }

    // Sắp xếp các ô nhập liệu Phòng
    private void layoutPhong(JPanel p) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0; 
        int y = 0;

        // Dòng 1: Chọn Loại Phòng
        gbc.gridy = y++; p.add(ModernUI.createLabel("Loại Phòng:"), gbc);
        gbc.gridy = y++; 
        cboLoaiPhong = new JComboBox<>(); 
        cboLoaiPhong.setPreferredSize(new Dimension(200, 35)); 
        cboLoaiPhong.setBackground(Color.WHITE); 
        p.add(cboLoaiPhong, gbc);

        // Dòng 2: Hiển thị giá tiền gợi ý
        gbc.gridy = y++;
        lblGiaPhong = new JLabel("Giá: 0 VNĐ");
        lblGiaPhong.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
        lblGiaPhong.setForeground(ModernUI.RED_COLOR);
        p.add(lblGiaPhong, gbc);

        // Dòng 3: Chọn Phòng Trống
        gbc.gridy = y++; p.add(ModernUI.createLabel("Chọn Phòng Trống:"), gbc);
        gbc.gridy = y++; 
        cboPhongTrong = new JComboBox<>(); 
        cboPhongTrong.setPreferredSize(new Dimension(200, 35)); 
        cboPhongTrong.setBackground(Color.WHITE); 
        p.add(cboPhongTrong, gbc);

        // Dòng 4: Chọn Ngày Đến - Ngày Đi
        gbc.gridy = y++; p.add(ModernUI.createLabel("Thời Gian Lưu Trú:"), gbc);
        gbc.gridy = y++;
        JPanel pnlDate = new JPanel(new GridLayout(1, 2, 10, 0)); 
        pnlDate.setOpaque(false);
        
        spinNgayDen = createSpinner();
        spinNgayDi = createSpinner();
        
        // Mặc định ngày đi là ngày mai
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, 1);
        spinNgayDi.setValue(cal.getTime());

        pnlDate.add(spinNgayDen); 
        pnlDate.add(spinNgayDi);
        p.add(pnlDate, gbc);

        // Dòng 5: Tiền cọc
        gbc.gridy = y++; p.add(ModernUI.createLabel("Tiền Đặt Cọc (VNĐ):"), gbc);
        gbc.gridy = y++; 
        gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.NORTH;
        txtTienCoc = new ModernUI.RoundedTextField(15); p.add(txtTienCoc, gbc);
    }

    // Tạo Spinner chọn ngày giờ
    private JSpinner createSpinner() {
        JSpinner spin = new JSpinner(new SpinnerDateModel());
        spin.setEditor(new JSpinner.DateEditor(spin, "dd/MM/yyyy HH:mm"));
        spin.setPreferredSize(new Dimension(100, 35));
        spin.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return spin;
    }

    // --- 5. HÀM ĐẶC BIỆT: CHUYỂN CHẾ ĐỘ SỬA ---
    /**
     * Đổ dữ liệu từ một đơn đặt phòng có sẵn vào Dialog.
     * Hàm này được gọi khi người dùng bấm nút "Sửa" ở màn hình quản lý.
     */
    public void setModel(DatPhong dp, KhachHang kh) {
        this.maDatPhongDangSua = dp.getMaDatPhong(); // Lưu lại ID để biết là đang sửa
        
        // 1. Đổ thông tin khách
        if (kh != null) {
            txtCCCD.setText(kh.getCccd());
            txtHoTen.setText(kh.getHoTen());
            txtSDT.setText(kh.getSdt());
            txtDiaChi.setText(kh.getDiaChi());
        }
        
        // 2. Đổ thông tin phòng & cọc
        txtTienCoc.setText(String.valueOf((long)dp.getTienDatCoc()));
        spinNgayDen.setValue(dp.getNgayCheckIn());
        spinNgayDi.setValue(dp.getNgayCheckOut());
        
        // 3. Cập nhật giao diện
        this.setTitle("CẬP NHẬT ĐƠN - MÃ: " + dp.getMaDatPhong());
        btnLuu.setText("LƯU CẬP NHẬT");
        txtCCCD.setEditable(false); // Không cho sửa CCCD để tránh lỗi logic khách hàng
        
        // 4. Chọn lại đúng phòng cũ trong ComboBox
        // (Lưu ý: Logic này chỉ chọn được nếu phòng cũ có trong danh sách phòng trống
        //  Controller sẽ xử lý việc load thêm phòng cũ vào list nếu cần)
        ComboBoxModel<Phong> model = cboPhongTrong.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Phong p = model.getElementAt(i);
            if (p.getMaPhong() == dp.getMaPhong()) {
                cboPhongTrong.setSelectedIndex(i);
                break;
            }
        }
    }

    // --- 6. GETTERS (ĐỂ CONTROLLER LẤY DỮ LIỆU) ---
    public JButton getBtnTimKhach() { return btnTimKhach; }
    public JButton getBtnLuu() { return btnLuu; }
    public JComboBox<LoaiPhong> getCboLoaiPhong() { return cboLoaiPhong; }
    public JComboBox<Phong> getCboPhongTrong() { return cboPhongTrong; }
    public JTextField getTxtCCCD() { return txtCCCD; }
    public JTextField getTxtHoTen() { return txtHoTen; }
    public JTextField getTxtSDT() { return txtSDT; }
    public JTextField getTxtDiaChi() { return txtDiaChi; }
    public JTextField getTxtTienCoc() { return txtTienCoc; }
    public JLabel getLblGiaPhong() { return lblGiaPhong; }
    public Date getNgayDen() { return (Date) spinNgayDen.getValue(); }
    public Date getNgayDi() { return (Date) spinNgayDi.getValue(); }
    
    // Getter/Setter cho biến trạng thái
    public int getMaDatPhongDangSua() { return maDatPhongDangSua; }
    public void setMaDatPhongDangSua(int maDatPhongDangSua) { this.maDatPhongDangSua = maDatPhongDangSua; }
}