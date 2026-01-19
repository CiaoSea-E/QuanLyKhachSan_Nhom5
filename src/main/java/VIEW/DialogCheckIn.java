package VIEW;

import Helper.ModernUI;
import Model.DatPhong;
import Model.KhachHang;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Dialog hiển thị thông tin xác nhận khi khách đến Nhận Phòng (Check-In).
 * Mục đích: Để lễ tân đối chiếu lại thông tin khách và phòng lần cuối trước khi giao chìa khóa.
 */
public class DialogCheckIn extends JDialog {

    private boolean confirmed = false; // Biến cờ: True nếu người dùng bấm "Xác nhận"

    public DialogCheckIn(Frame parent, DatPhong dp, KhachHang kh, String tenPhong) {
        super(parent, "Check-In Nhận Phòng", true); // true = Modal (Chặn thao tác cửa sổ cha)
        
        initComponents(dp, kh, tenPhong);
        
        setSize(600, 550); // Kích thước khung cửa sổ
        setLocationRelativeTo(parent); // Căn giữa màn hình so với cửa sổ cha
    }

    /**
     * Trả về kết quả người dùng có bấm xác nhận hay không
     */
    public boolean isConfirmed() { 
        return confirmed; 
    }

    // ============================================================
    // PHẦN KHỞI TẠO GIAO DIỆN
    // ============================================================
    private void initComponents(DatPhong dp, KhachHang kh, String tenPhong) {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- 1. PHẦN TIÊU ĐỀ (HEADER) ---
        add(ModernUI.createHeaderPanel("XÁC NHẬN NHẬN PHÒNG"), BorderLayout.NORTH);

        // --- 2. PHẦN NỘI DUNG (CONTENT) ---
        // Sử dụng GridBagLayout để căn chỉnh các dòng thông tin thẳng hàng đẹp mắt
        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(new EmptyBorder(20, 40, 20, 40)); // Padding xung quanh
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0); // Khoảng cách giữa các dòng (Top, Left, Bottom, Right)
        gbc.fill = GridBagConstraints.HORIZONTAL; // Kéo giãn component ngang hết ô
        gbc.anchor = GridBagConstraints.WEST;     // Căn lề trái

        int y = 0; // Biến đếm dòng (row index)

        // Hiển thị thông tin khách hàng
        addRow(pnlContent, gbc, y++, "Khách Hàng:", kh.getHoTen().toUpperCase());
        addRow(pnlContent, gbc, y++, "CCCD / CMND:", kh.getCccd());
        addRow(pnlContent, gbc, y++, "Số Điện Thoại:", kh.getSdt());
        addRow(pnlContent, gbc, y++, "Địa Chỉ:", kh.getDiaChi());
        
        // Hiển thị thông tin phòng
        addRow(pnlContent, gbc, y++, "Phòng Đặt:", tenPhong);
        
        // Kẻ đường gạch ngang phân cách
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = y++;
        pnlContent.add(sep, gbc);

        // Hiển thị thời gian hiện tại
        String now = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        addRow(pnlContent, gbc, y++, "Thời Gian Check-in:", now);

        add(pnlContent, BorderLayout.CENTER);

        // --- 3. PHẦN NÚT BẤM (FOOTER) ---
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        pnlBtn.setBackground(Color.WHITE);
        
        JButton btnXacNhan = new ModernUI.ModernButton("XÁC NHẬN", ModernUI.GREEN_COLOR, ModernUI.GREEN_COLOR.darker());
        JButton btnThoat = new ModernUI.ModernButton("HỦY BỎ", ModernUI.RED_COLOR, ModernUI.RED_COLOR.darker());

        // Sự kiện nút
        btnXacNhan.addActionListener(e -> { 
            confirmed = true; // Đánh dấu là đồng ý
            dispose();        // Đóng cửa sổ
        });
        
        btnThoat.addActionListener(e -> {
            confirmed = false;
            dispose(); 
        });

        pnlBtn.add(btnXacNhan);
        pnlBtn.add(btnThoat);
        add(pnlBtn, BorderLayout.SOUTH);
    }

    /**
     * Hàm hỗ trợ thêm một dòng thông tin vào Panel (Giúp code ngắn gọn hơn)
     * @param p Panel chứa
     * @param gbc Cấu hình layout
     * @param y Chỉ số dòng (row)
     * @param label Nhãn tiêu đề (VD: "Họ tên:")
     * @param value Giá trị hiển thị (VD: "NGUYỄN VĂN A")
     */
    private void addRow(JPanel p, GridBagConstraints gbc, int y, String label, String value) {
        gbc.gridy = y; // Set vị trí dòng
        
        // Cột 1: Nhãn (Chiếm 35% chiều rộng)
        gbc.gridx = 0; 
        gbc.weightx = 0.35; 
        p.add(ModernUI.createLabel(label), gbc);

        // Cột 2: Giá trị (Chiếm 65% chiều rộng)
        gbc.gridx = 1; 
        gbc.weightx = 0.65;
        
        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Font đậm cho giá trị
        lblVal.setForeground(ModernUI.PRIMARY);              // Màu xanh chủ đạo
        p.add(lblVal, gbc);
    }
}