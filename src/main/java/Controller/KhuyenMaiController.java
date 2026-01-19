package Controller;

import DAO.KhuyenMaiDAO;
import Model.KhuyenMai;
import VIEW.QuanLyKhuyenMaiPanel;
import Helper.ExcelHelper;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controller điều phối chức năng Quản Lý Khuyến Mãi
 * Nhiệm vụ:
 * 1. Lắng nghe sự kiện từ View (Nút bấm, Click bảng).
 * 2. Gọi DAO để xử lý dữ liệu xuống Database.
 * 3. Cập nhật lại giao diện (Bảng, Form) cho người dùng.
 */
public class KhuyenMaiController {

    private QuanLyKhuyenMaiPanel view;
    private KhuyenMaiDAO dao;

    public KhuyenMaiController(QuanLyKhuyenMaiPanel view) {
        this.view = view;
        this.dao = new KhuyenMaiDAO();
        
        // 1. Gắn sự kiện cho các nút bấm
        initEvents(); 
        
        // 2. Tải dữ liệu ban đầu lên bảng
        loadDataToTable(); 
    }

    // ============================================================
    // PHẦN 1: KHỞI TẠO SỰ KIỆN (EVENT LISTENER)
    // ============================================================
    private void initEvents() {
        // Nhóm nút thao tác chính (CRUD)
        view.getBtnThem().addActionListener(e -> themKhuyenMai());
        view.getBtnSua().addActionListener(e -> capNhatKhuyenMai());
        view.getBtnXoa().addActionListener(e -> xoaKhuyenMai());
        view.getBtnLamMoi().addActionListener(e -> clearForm());
        
        // Nút tiện ích
        view.getBtnXuatExcel().addActionListener(e -> xuatExcel());
        
        // Nhóm Tìm kiếm & Lọc
        view.getBtnTimKiem().addActionListener(e -> xuLyTimKiem());
        view.getCboLocLoai().addActionListener(e -> xuLyTimKiem()); // Tự động tìm khi chọn ComboBox
        
        // Sự kiện Click chuột vào bảng -> Đổ dữ liệu lên Form
        view.getTblKhuyenMai().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });
    }
    
    // ============================================================
    // PHẦN 2: CÁC CHỨC NĂNG NGHIỆP VỤ (BUSINESS LOGIC)
    // ============================================================

    /**
     * Lấy dữ liệu mới nhất từ CSDL và hiển thị lên bảng
     */
    private void loadDataToTable() {
        view.getModel().setRowCount(0); // Xóa trắng bảng cũ
        List<KhuyenMai> list = dao.getAll();
        
        for (KhuyenMai km : list) {
            view.getModel().addRow(new Object[]{
                km.getMaGiamGia(),
                km.getCode(),
                km.getTenSuKien(),
                km.getGiamGia(),
                (km.getLoaiGiam() == 1 ? "%" : "VNĐ"), // Chuyển đổi số thành chữ cho dễ đọc
                km.getSoLuong(),
                km.getNgayBatDau(),
                km.getNgayKetthuc(),
                km.getTrangThai()
            });
        }
    }

    private void themKhuyenMai() {
        // B1. Kiểm tra dữ liệu đầu vào
        if (!validateForm()) return;
        
        // B2. Lấy dữ liệu từ Form đóng gói thành Object
        KhuyenMai km = getModelFromView();
        
        // B3. Kiểm tra trùng lặp mã Code
        if (dao.checkTrung(km.getCode())) {
            JOptionPane.showMessageDialog(view, "Mã Code này đã tồn tại! Vui lòng chọn mã khác.");
            return;
        }
        
        // B4. Gọi DAO để thêm vào DB
        if (dao.insert(km)) {
            JOptionPane.showMessageDialog(view, "Thêm thành công!");
            loadDataToTable(); // Tải lại bảng để thấy dòng mới
            clearForm();       // Xóa trắng form để nhập tiếp
        } else {
            JOptionPane.showMessageDialog(view, "Thêm thất bại! Vui lòng kiểm tra lại.");
        }
    }
    
    private void capNhatKhuyenMai() {
        // Kiểm tra xem người dùng đã chọn dòng nào chưa
        if (view.getTxtCode().getText().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn mã cần sửa trên bảng!");
            return;
        }
        if (!validateForm()) return;
        
        KhuyenMai km = getModelFromView();
        
        // Gọi DAO cập nhật
        if (dao.update(km)) {
            JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
            loadDataToTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thất bại!");
        }
    }
    
    private void xoaKhuyenMai() {
        int row = view.getTblKhuyenMai().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dòng cần xóa!");
            return;
        }
        
        // Hỏi xác nhận trước khi xóa (An toàn)
        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa mã này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String code = view.getTblKhuyenMai().getValueAt(row, 1).toString();
            
            if (dao.delete(code)) {
                JOptionPane.showMessageDialog(view, "Đã xóa thành công!");
                loadDataToTable();
                clearForm();
            }
        }
    }
    
    /**
     * Xử lý tìm kiếm kết hợp lọc
     */
    private void xuLyTimKiem() {
        // Lấy từ khóa
        String tuKhoa = view.getTxtTimKiem().getText().trim();
        
        // Lấy tiêu chí lọc (Map từ index của ComboBox sang giá trị trong DB)
        int index = view.getCboLocLoai().getSelectedIndex();
        int loaiGiam = -1; // -1: Tất cả
        if (index == 1) loaiGiam = 0; // VNĐ
        if (index == 2) loaiGiam = 1; // %
        
        // Gọi DAO tìm kiếm
        List<KhuyenMai> list = dao.timKiemVaLoc(tuKhoa, loaiGiam);
        
        // Đổ dữ liệu tìm được lên bảng
        view.getModel().setRowCount(0);
        for (KhuyenMai km : list) {
            view.getModel().addRow(new Object[]{
                km.getMaGiamGia(),
                km.getCode(),
                km.getTenSuKien(),
                km.getGiamGia(),
                (km.getLoaiGiam() == 1 ? "%" : "VNĐ"),
                km.getSoLuong(),
                km.getNgayBatDau(),
                km.getNgayKetthuc(),
                km.getTrangThai()
            });
        }
    }

    private void xuatExcel() {
        ExcelHelper.exportToExcel(view.getTblKhuyenMai(), view);
    }

    // ============================================================
    // PHẦN 3: CÁC HÀM TIỆN ÍCH (HELPER METHODS)
    // ============================================================

    /**
     * Reset Form về trạng thái ban đầu (Trắng trơn)
     */
    private void clearForm() {
        view.getTxtCode().setText("");
        view.getTxtTenSuKien().setText("");
        view.getTxtGiamGia().setText("");
        view.getTxtSoLuong().setText("");
        
        // Reset ComboBox
        view.getCboLoaiGiam().setSelectedIndex(0);
        view.getCboTrangThai().setSelectedIndex(0);
        
        // Reset Ngày giờ về hiện tại
        view.getSpinNgayBatDau().setValue(new Date());
        view.getSpinNgayKetThuc().setValue(new Date());
        
        // Reset Tìm kiếm và Bảng
        view.getTxtTimKiem().setText("");
        view.getCboLocLoai().setSelectedIndex(0);
        view.getTblKhuyenMai().clearSelection();
        view.getTxtCode().setEditable(true); // Mở khóa ô Code để nhập mới
        
        loadDataToTable(); // Tải lại toàn bộ dữ liệu gốc
    }
    
    /**
     * Validate dữ liệu nhập vào từ Form
     * @return true nếu hợp lệ, false nếu có lỗi
     */
    private boolean validateForm() {
        if (view.getTxtCode().getText().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập Mã Code!");
            return false;
        }
        try {
            Double.parseDouble(view.getTxtGiamGia().getText());
            Integer.parseInt(view.getTxtSoLuong().getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Lỗi: 'Giảm giá' và 'Số lượng' phải là số!");
            return false;
        }
        return true;
    }

    /**
     * Lấy dữ liệu từ các ô nhập liệu trên Form và đóng gói vào Object KhuyenMai
     */
    private KhuyenMai getModelFromView() {
        KhuyenMai km = new KhuyenMai();
        km.setCode(view.getTxtCode().getText().trim());
        km.setTenSuKien(view.getTxtTenSuKien().getText().trim());
        km.setGiamGia(Double.parseDouble(view.getTxtGiamGia().getText()));
        km.setLoaiGiam(view.getCboLoaiGiam().getSelectedIndex());
        km.setSoLuong(Integer.parseInt(view.getTxtSoLuong().getText()));
        
        // Ép kiểu từ Spinner sang Date
        km.setNgayBatDau((Date) view.getSpinNgayBatDau().getValue());
        km.setNgayKetthuc((Date) view.getSpinNgayKetThuc().getValue());
        
        km.setTrangThai(view.getCboTrangThai().getSelectedItem().toString());
        return km;
    }
    
    /**
     * Đổ dữ liệu từ dòng được chọn trên Bảng ngược lại lên Form
     */
    private void fillFormFromTable() {
        int row = view.getTblKhuyenMai().getSelectedRow();
        if (row == -1) return;
        
        // Lấy dữ liệu từ bảng (Cẩn thận null)
        Object code = view.getTblKhuyenMai().getValueAt(row, 1);
        view.getTxtCode().setText(code != null ? code.toString() : "");
        view.getTxtCode().setEditable(false); // Khóa ô Code không cho sửa
        
        Object ten = view.getTblKhuyenMai().getValueAt(row, 2);
        view.getTxtTenSuKien().setText(ten != null ? ten.toString() : "");
        
        Object giamGia = view.getTblKhuyenMai().getValueAt(row, 3);
        view.getTxtGiamGia().setText(giamGia != null ? giamGia.toString() : "0");
        
        // Xử lý loại giảm (% hay VNĐ)
        String loai = view.getTblKhuyenMai().getValueAt(row, 4).toString();
        view.getCboLoaiGiam().setSelectedIndex(loai.equals("%") ? 1 : 0);
        
        Object sl = view.getTblKhuyenMai().getValueAt(row, 5);
        view.getTxtSoLuong().setText(sl != null ? sl.toString() : "0");
        
        // Xử lý ngày tháng (Cần try-catch vì ép kiểu Date có thể lỗi)
        try {
            Object ngayBD = view.getTblKhuyenMai().getValueAt(row, 6);
            Object ngayKT = view.getTblKhuyenMai().getValueAt(row, 7);
            if(ngayBD instanceof Date) view.getSpinNgayBatDau().setValue((Date) ngayBD);
            if(ngayKT instanceof Date) view.getSpinNgayKetThuc().setValue((Date) ngayKT);
        } catch (Exception e) {}
        
        view.getCboTrangThai().setSelectedItem(view.getTblKhuyenMai().getValueAt(row, 8).toString());
    }
}