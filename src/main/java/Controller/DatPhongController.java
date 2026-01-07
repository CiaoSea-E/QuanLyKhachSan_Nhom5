package Controller;

import DAO.*;
import Model.*;
import VIEW.DialogDatPhong;
import java.util.List;
import javax.swing.JOptionPane;

public class DatPhongController {
    
    private DialogDatPhong view;
    
    // Khai báo các DAO
    private KhachHangDAO khDao = new KhachHangDAO();
    private LoaiPhongDAO lpDao = new LoaiPhongDAO();
    private PhongDAO pDao = new PhongDAO();
    private DatPhongDAO dpDao = new DatPhongDAO();

    public DatPhongController(DialogDatPhong view) {
        this.view = view;
        initEvents();       // Gắn sự kiện
        loadDataCombobox(); // Load dữ liệu ban đầu
    }

    private void initEvents() {
        // 1. Sự kiện Tìm khách
        view.getBtnTimKhach().addActionListener(e -> timKhachHang());
        view.getTxtCCCD().addActionListener(e -> timKhachHang()); 

        // 2. Sự kiện Chọn Loại Phòng
        view.getCboLoaiPhong().addActionListener(e -> {
            LoaiPhong loai = (LoaiPhong) view.getCboLoaiPhong().getSelectedItem();
            if (loai != null) {
                view.getLblGiaPhong().setText("Giá: " + String.format("%,.0f", loai.getGiaTheoNgay()) + " VNĐ");
                loadPhongTrong(loai.getMaLoaiPhong());
            }
        });

        // 3. Sự kiện Lưu -> Phân chia trường hợp Thêm/Sửa
        view.getBtnLuu().addActionListener(e -> xuLyLuu());
    }

    // --- CÁC HÀM HỖ TRỢ ---

    private void loadDataCombobox() {
        List<LoaiPhong> list = lpDao.getAllLoaiPhong();
        for (LoaiPhong lp : list) {
            view.getCboLoaiPhong().addItem(lp);
        }
    }

    private void loadPhongTrong(int maLoai) {
        view.getCboPhongTrong().removeAllItems();
        
        // Logic bổ sung: Khi Sửa, ta cần load cả phòng đang ở hiện tại vào list phòng trống
        // Tuy nhiên để đơn giản, ta cứ load phòng trống trước.
        List<Phong> list = pDao.getPhongTrongByLoai(maLoai);
        
        // Nếu đang sửa, ta cần add thêm cái phòng cũ vào list này để người dùng chọn lại nó được
        if (view.getMaDatPhongDangSua() > 0) {
            // Lấy thông tin đơn cũ
            DatPhong oldDP = dpDao.getDatPhongById(view.getMaDatPhongDangSua());
            if (oldDP != null && oldDP.getMaPhong() != 0) {
                // Giả lập tạo đối tượng phòng cũ (Chỉ cần Mã và Số phòng để hiển thị)
                // Lưu ý: Đúng ra phải query lấy tên phòng, ở đây tôi ví dụ
                 // Bạn có thể cần hàm pDao.getPhongById(id) để lấy chính xác tên phòng
                 // Tạm thời add vào list một phòng "Phòng Hiện Tại"
                 // list.add(0, new Phong(oldDP.getMaPhong(), "Phòng cũ (Đang chọn)", 0, ""));
            }
        }

        if (list.isEmpty()) {
            view.getCboPhongTrong().addItem(new Phong(0, "Hết phòng", 0, ""));
            // Nếu không phải admin thì disable, tùy logic
        } else {
            for (Phong p : list) view.getCboPhongTrong().addItem(p);
        }
    }

    private void timKhachHang() {
        String cccd = view.getTxtCCCD().getText().trim();
        if (cccd.isEmpty()) return;

        KhachHang kh = khDao.getKhachHangByCCCD(cccd);
        if (kh != null) {
            view.getTxtHoTen().setText(kh.getHoTen());
            view.getTxtSDT().setText(kh.getSdt());
            view.getTxtDiaChi().setText(kh.getDiaChi());
            view.getTxtHoTen().setEditable(false);
            JOptionPane.showMessageDialog(view, "Tìm thấy khách cũ!");
        } else {
            view.getTxtHoTen().setText("");
            view.getTxtSDT().setText("");
            view.getTxtDiaChi().setText("");
            view.getTxtHoTen().setEditable(true); // Cho phép nhập mới
            JOptionPane.showMessageDialog(view, "Khách mới. Vui lòng nhập thông tin.");
        }
    }

    // ==========================================================
    // === HÀM XỬ LÝ LƯU (QUAN TRỌNG NHẤT) - ĐÃ SỬA LOGIC ===
    // ==========================================================
    private void xuLyLuu() {
        // 1. Validate dữ liệu trống (Kiểm tra cơ bản)
        if (view.getTxtHoTen().getText().trim().isEmpty() || 
            view.getTxtCCCD().getText().trim().isEmpty() ||
            view.getTxtSDT().getText().trim().isEmpty() ||
            view.getTxtTienCoc().getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ: Họ tên, CCCD, SĐT và Tiền cọc!");
            return;
        }
        
        // --- 2. VALIDATE ĐỊNH DẠNG (CODE MỚI THÊM) ---
        String sdt = view.getTxtSDT().getText().trim();
        String cccd = view.getTxtCCCD().getText().trim();
        
        // Gọi hàm checkValidate ở cuối file để kiểm tra
        if (!checkValidate("SDT", sdt)) {
            JOptionPane.showMessageDialog(view, "Lỗi: Số điện thoại không hợp lệ!\n(Phải có 10 số, bắt đầu bằng số 0)");
            view.getTxtSDT().requestFocus(); // Đưa chuột về ô lỗi
            return; // Dừng lại, không lưu
        }
        
        if (!checkValidate("CCCD", cccd)) {
            JOptionPane.showMessageDialog(view, "Lỗi: CCCD không hợp lệ!\n(Phải chứa đúng 12 chữ số)");
            view.getTxtCCCD().requestFocus();
            return;
        }
        // ---------------------------------------------
        
        Phong phongMoi = (Phong) view.getCboPhongTrong().getSelectedItem();
        if (phongMoi == null || phongMoi.getMaPhong() == 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một phòng hợp lệ!");
            return;
        }

        try {
            double tienCoc = Double.parseDouble(view.getTxtTienCoc().getText());
            int maDatPhong = view.getMaDatPhongDangSua(); 

            // --- TRƯỜNG HỢP 1: THÊM MỚI (INSERT) ---
            if (maDatPhong == 0) {
                int maKH = xuLyKhachHang(); 
                
                DatPhong dp = new DatPhong();
                dp.setMaPhong(phongMoi.getMaPhong());
                dp.setMaKhachHang(maKH);
                dp.setMaNhanVien(1); 
                dp.setTienDatCoc(tienCoc);
                dp.setNgayCheckIn(view.getNgayDen());
                dp.setNgayCheckOut(view.getNgayDi());
                dp.setTrangThai("Đã đặt");

                if (dpDao.insertDatPhong(dp)) {
                    pDao.updateTrangThaiPhong(phongMoi.getMaPhong(), "Đã đặt");
                    JOptionPane.showMessageDialog(view, "Thêm mới thành công!");
                    view.dispose();
                }

            // --- TRƯỜNG HỢP 2: CẬP NHẬT (UPDATE) ---
            } else {
                int confirm = JOptionPane.showConfirmDialog(view, 
                        "Bạn có chắc chắn muốn cập nhật thông tin đơn này?", 
                        "Xác nhận sửa", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                DatPhong dpCu = dpDao.getDatPhongById(maDatPhong);
                int maPhongCu = dpCu.getMaPhong();
                int maPhongMoi = phongMoi.getMaPhong();

                DatPhong dpMoi = new DatPhong();
                dpMoi.setMaDatPhong(maDatPhong);
                dpMoi.setMaPhong(maPhongMoi);
                dpMoi.setTienDatCoc(tienCoc);
                dpMoi.setNgayCheckIn(view.getNgayDen());
                dpMoi.setNgayCheckOut(view.getNgayDi());
                
                if (dpDao.updateDatPhong(dpMoi)) {
                    if (maPhongCu != maPhongMoi) {
                        pDao.updateTrangThaiPhong(maPhongCu, "Trống");   
                        pDao.updateTrangThaiPhong(maPhongMoi, "Đã đặt"); 
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

    // Hàm phụ: Xử lý tìm hoặc thêm khách hàng
    private int xuLyKhachHang() {
        KhachHang khCheck = khDao.getKhachHangByCCCD(view.getTxtCCCD().getText());
        if (khCheck != null) {
            return khCheck.getMaKhachHang();
        } else {
            KhachHang newKH = new KhachHang(0, view.getTxtHoTen().getText(), 
                    view.getTxtCCCD().getText(), view.getTxtSDT().getText(), view.getTxtDiaChi().getText());
            return khDao.addKhachHang(newKH);
        }
    }
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