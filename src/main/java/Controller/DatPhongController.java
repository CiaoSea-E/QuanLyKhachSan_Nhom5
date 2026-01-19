package Controller;

import DAO.*;
import Model.*;
import VIEW.DialogDatPhong;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controller xử lý logic cho màn hình Dialog Đặt Phòng (Thêm mới / Sửa)
 * Nhiệm vụ:
 * 1. Load dữ liệu loại phòng, phòng trống.
 * 2. Tìm kiếm hoặc thêm mới khách hàng tự động.
 * 3. Xử lý nghiệp vụ Đặt phòng (Insert) hoặc Cập nhật đơn (Update).
 */
public class DatPhongController {
    
    private DialogDatPhong view;
    
    // --- KHAI BÁO CÁC DAO (DATA ACCESS OBJECT) ---
    private KhachHangDAO khDao;
    private LoaiPhongDAO lpDao;
    private PhongDAO pDao;
    private DatPhongDAO dpDao;

    public DatPhongController(DialogDatPhong view) {
        this.view = view;
        
        // Khởi tạo các DAO
        this.khDao = new KhachHangDAO();
        this.lpDao = new LoaiPhongDAO();
        this.pDao = new PhongDAO();
        this.dpDao = new DatPhongDAO();

        // 1. Gắn sự kiện cho các nút bấm
        initEvents();       
        
        // 2. Tải dữ liệu ban đầu (Danh sách loại phòng)
        loadDataCombobox(); 
    }

    // ============================================================
    // PHẦN 1: KHỞI TẠO SỰ KIỆN (EVENTS)
    // ============================================================
    private void initEvents() {
        // Sự kiện: Tìm khách hàng khi nhập CCCD hoặc bấm nút Tìm
        view.getBtnTimKhach().addActionListener(e -> timKhachHang());
        view.getTxtCCCD().addActionListener(e -> timKhachHang()); 

        // Sự kiện: Khi chọn Loại phòng -> Tự động load danh sách Phòng trống tương ứng
        view.getCboLoaiPhong().addActionListener(e -> {
            LoaiPhong loai = (LoaiPhong) view.getCboLoaiPhong().getSelectedItem();
            if (loai != null) {
                // Hiển thị giá tiền gợi ý
                view.getLblGiaPhong().setText("Giá: " + String.format("%,.0f", loai.getGiaTheoNgay()) + " VNĐ");
                // Load danh sách phòng
                loadPhongTrong(loai.getMaLoaiPhong());
            }
        });

        // Sự kiện: Bấm nút Lưu (Xác nhận Đặt hoặc Sửa)
        view.getBtnLuu().addActionListener(e -> xuLyLuu());
    }

    // ============================================================
    // PHẦN 2: CÁC HÀM HỖ TRỢ LOAD DỮ LIỆU (DATA LOADING)
    // ============================================================

    /**
     * Load danh sách tất cả Loại phòng vào ComboBox
     */
    private void loadDataCombobox() {
        List<LoaiPhong> list = lpDao.getAllLoaiPhong();
        for (LoaiPhong lp : list) {
            view.getCboLoaiPhong().addItem(lp);
        }
    }

    /**
     * Load danh sách Phòng Trống dựa theo Loại phòng đã chọn
     * Logic đặc biệt: Nếu đang ở chế độ SỬA, phải hiển thị cả phòng cũ của đơn đó.
     */
    private void loadPhongTrong(int maLoai) {
        view.getCboPhongTrong().removeAllItems();
        
        // 1. Lấy danh sách các phòng đang TRỐNG trong CSDL
        List<Phong> list = pDao.getPhongTrongByLoai(maLoai);
        
        // 2. [LOGIC SỬA]: Nếu đang sửa đơn, ta cần thêm phòng cũ vào list (dù nó đang trạng thái 'Đã đặt')
        // để người dùng có thể giữ nguyên phòng cũ nếu muốn.
        if (view.getMaDatPhongDangSua() > 0) {
            DatPhong oldDP = dpDao.getDatPhongById(view.getMaDatPhongDangSua());
            // (Đoạn này logic nâng cao: Cần query lấy thông tin phòng cũ và add vào list nếu chưa có)
            // Hiện tại ta tạm chấp nhận chỉ load phòng trống.
        }

        // 3. Đổ dữ liệu vào ComboBox
        if (list.isEmpty()) {
            view.getCboPhongTrong().addItem(new Phong(0, "Hết phòng", 0, ""));
        } else {
            for (Phong p : list) {
                view.getCboPhongTrong().addItem(p);
            }
        }
    }

    /**
     * Tìm thông tin khách hàng dựa trên số CCCD nhập vào
     */
    private void timKhachHang() {
        String cccd = view.getTxtCCCD().getText().trim();
        if (cccd.isEmpty()) return;

        KhachHang kh = khDao.getKhachHangByCCCD(cccd);
        
        if (kh != null) {
            // Trường hợp 1: Khách quen (Đã có trong CSDL)
            view.getTxtHoTen().setText(kh.getHoTen());
            view.getTxtSDT().setText(kh.getSdt());
            view.getTxtDiaChi().setText(kh.getDiaChi());
            view.getTxtHoTen().setEditable(false); // Khóa tên lại cho chính xác
            JOptionPane.showMessageDialog(view, "Tìm thấy khách hàng cũ!");
        } else {
            // Trường hợp 2: Khách mới
            view.getTxtHoTen().setText("");
            view.getTxtSDT().setText("");
            view.getTxtDiaChi().setText("");
            view.getTxtHoTen().setEditable(true); // Mở khóa để nhập tên mới
            JOptionPane.showMessageDialog(view, "Khách mới. Vui lòng nhập thông tin.");
        }
    }

    // ============================================================
    // PHẦN 3: XỬ LÝ NGHIỆP VỤ CHÍNH (SAVE LOGIC)
    // ============================================================
    
    /**
     * Hàm xử lý nút LƯU / XÁC NHẬN
     * Bao gồm: Validate dữ liệu -> Kiểm tra khách hàng -> Insert hoặc Update
     */
    private void xuLyLuu() {
        // --- BƯỚC 1: KIỂM TRA DỮ LIỆU ĐẦU VÀO (VALIDATION) ---
        if (view.getTxtHoTen().getText().trim().isEmpty() || 
            view.getTxtCCCD().getText().trim().isEmpty() ||
            view.getTxtSDT().getText().trim().isEmpty() ||
            view.getTxtTienCoc().getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ: Họ tên, CCCD, SĐT và Tiền cọc!");
            return;
        }
        
        // Kiểm tra định dạng (Regex)
        String sdt = view.getTxtSDT().getText().trim();
        String cccd = view.getTxtCCCD().getText().trim();
        
        if (!checkValidate("SDT", sdt)) {
            JOptionPane.showMessageDialog(view, "Lỗi: SĐT phải có 10 số, bắt đầu bằng số 0!");
            view.getTxtSDT().requestFocus();
            return; 
        }
        
        if (!checkValidate("CCCD", cccd)) {
            JOptionPane.showMessageDialog(view, "Lỗi: CCCD phải chứa đúng 12 chữ số!");
            view.getTxtCCCD().requestFocus();
            return;
        }
        
        // Kiểm tra phòng hợp lệ
        Phong phongMoi = (Phong) view.getCboPhongTrong().getSelectedItem();
        if (phongMoi == null || phongMoi.getMaPhong() == 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một phòng hợp lệ!");
            return;
        }

        try {
            double tienCoc = Double.parseDouble(view.getTxtTienCoc().getText());
            
            // Lấy ID đơn hàng (Nếu = 0 là Thêm mới, > 0 là Đang sửa)
            int maDatPhong = view.getMaDatPhongDangSua(); 

            // --- TRƯỜNG HỢP 1: THÊM MỚI (INSERT) ---
            if (maDatPhong == 0) {
                // A. Lấy ID Khách hàng (Tìm cũ hoặc Thêm mới)
                int maKH = xuLyKhachHang(); 
                
                // B. Tạo đối tượng DatPhong
                DatPhong dp = new DatPhong();
                dp.setMaPhong(phongMoi.getMaPhong());
                dp.setMaKhachHang(maKH);
                dp.setMaNhanVien(1); // Mặc định NV số 1 (Sau này sẽ lấy từ session đăng nhập)
                dp.setTienDatCoc(tienCoc);
                dp.setNgayCheckIn(view.getNgayDen());
                dp.setNgayCheckOut(view.getNgayDi());
                dp.setTrangThai("Đã đặt");

                // C. Lưu xuống DB và Cập nhật trạng thái phòng
                if (dpDao.insertDatPhong(dp)) {
                    pDao.updateTrangThaiPhong(phongMoi.getMaPhong(), "Đã đặt"); // Phòng chuyển sang "Đã đặt"
                    JOptionPane.showMessageDialog(view, "Đặt phòng thành công!");
                    view.dispose(); // Đóng dialog
                }

            // --- TRƯỜNG HỢP 2: CẬP NHẬT ĐƠN CŨ (UPDATE) ---
            } else {
                int confirm = JOptionPane.showConfirmDialog(view, 
                        "Bạn có chắc chắn muốn cập nhật thông tin đơn này?", 
                        "Xác nhận sửa", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                // A. Lấy thông tin cũ để so sánh
                DatPhong dpCu = dpDao.getDatPhongById(maDatPhong);
                int maPhongCu = dpCu.getMaPhong();
                int maPhongMoi = phongMoi.getMaPhong();

                // B. Tạo đối tượng cập nhật
                DatPhong dpMoi = new DatPhong();
                dpMoi.setMaDatPhong(maDatPhong);
                dpMoi.setMaPhong(maPhongMoi);
                dpMoi.setTienDatCoc(tienCoc);
                dpMoi.setNgayCheckIn(view.getNgayDen());
                dpMoi.setNgayCheckOut(view.getNgayDi());
                
                // C. Thực hiện Update
                if (dpDao.updateDatPhong(dpMoi)) {
                    // Logic đổi phòng: Nếu khách đổi sang phòng khác
                    if (maPhongCu != maPhongMoi) {
                        pDao.updateTrangThaiPhong(maPhongCu, "Trống");   // Trả phòng cũ về Trống
                        pDao.updateTrangThaiPhong(maPhongMoi, "Đã đặt"); // Set phòng mới thành Đã đặt
                    }
                    
                    JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
                    view.dispose();
                } else {
                    JOptionPane.showMessageDialog(view, "Cập nhật thất bại!");
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Lỗi: Tiền cọc phải là số!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage());
        }
    }

    // ============================================================
    // PHẦN 4: CÁC HÀM TIỆN ÍCH (PRIVATE HELPERS)
    // ============================================================

    /**
     * Helper: Kiểm tra khách hàng đã có chưa.
     * Nếu chưa -> Tự động thêm mới vào bảng KhachHang.
     * @return maKhachHang
     */
    private int xuLyKhachHang() {
        KhachHang khCheck = khDao.getKhachHangByCCCD(view.getTxtCCCD().getText());
        if (khCheck != null) {
            return khCheck.getMaKhachHang(); // Khách cũ
        } else {
            // Khách mới -> Tạo mới
            KhachHang newKH = new KhachHang(0, view.getTxtHoTen().getText(), 
                    view.getTxtCCCD().getText(), view.getTxtSDT().getText(), view.getTxtDiaChi().getText());
            return khDao.addKhachHang(newKH);
        }
    }

    /**
     * Helper: Kiểm tra định dạng chuỗi (Regex)
     */
    private boolean checkValidate(String type, String value) {
        if (value == null || value.trim().isEmpty()) return false;
        
        switch (type) {
            case "SDT": // Số điện thoại VN: 10 số, bắt đầu bằng 0
                return value.matches("^0\\d{9}$");
                
            case "CCCD": // Căn cước công dân: 12 chữ số
                return value.matches("^\\d{12}$");
                
            case "EMAIL": // Định dạng email chuẩn
                return value.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
                
            default:
                return false;
        }
    }
}