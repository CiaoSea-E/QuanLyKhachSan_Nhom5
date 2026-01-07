package Controller;

import DAO.PhongDAO;
import Model.Phong;
import VIEW.QuanLyPhongPanel;
import Helper.ExcelHelper; // <--- 1. IMPORT CÁI NÀY

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PhongController {

    private QuanLyPhongPanel view;
    private PhongDAO dao;

    public PhongController(QuanLyPhongPanel view) {
        this.view = view;
        this.dao = new PhongDAO();

        // 1. Gắn các sự kiện cho nút bấm
        initEvents();

        // 2. Load dữ liệu ban đầu
        loadDataToTable(dao.getAllPhong());
    }

    private void initEvents() {
        // --- NÚT THÊM (ĐÃ BỔ SUNG CHECK TRÙNG) ---
        view.getBtnThem().addActionListener(e -> {
            // 1. Lấy dữ liệu từ ô nhập
            Phong p = getPhongFromInput();
            
            if (p != null) {
                // 2. CHECK TRÙNG: Gọi hàm DAO vừa viết
                if (dao.checkTonTaiSoPhong(p.getSoPhong())) {
                    view.showMessage("Lỗi: Số phòng '" + p.getSoPhong() + "' đã tồn tại! Vui lòng chọn số khác.");
                    return; // Dừng lại, không lưu
                }

                // 3. Nếu không trùng thì Thêm
                if (dao.insertPhong(p)) {
                    view.showMessage("Thêm thành công!");
                    reloadAll();
                } else {
                    view.showMessage("Thêm thất bại!");
                }
            }
        });

        // --- NÚT SỬA (ĐÃ BỔ SUNG CHECK TRÙNG THÔNG MINH) ---
        view.getBtnSua().addActionListener(e -> {
            int row = view.getTblPhong().getSelectedRow();
            if (row == -1) {
                view.showMessage("Vui lòng chọn phòng cần sửa trên bảng!");
                return;
            }
            
            Phong p = getPhongFromInput(); // Lấy dữ liệu mới từ ô nhập
            if (p != null) {
                // Lấy Mã và Số phòng cũ từ bảng để so sánh
                int maPhongCu = Integer.parseInt(view.getTblPhong().getValueAt(row, 0).toString());
                String soPhongCu = view.getTblPhong().getValueAt(row, 1).toString();
                
                p.setMaPhong(maPhongCu); 
                
                // CHECK TRÙNG KHI SỬA:
                // Chỉ kiểm tra trùng nếu người dùng CỐ TÌNH thay đổi số phòng sang số khác
                // (Nếu họ giữ nguyên số phòng cũ thì không báo lỗi)
                if (!p.getSoPhong().equals(soPhongCu)) {
                    if (dao.checkTonTaiSoPhong(p.getSoPhong())) {
                         view.showMessage("Lỗi: Số phòng '" + p.getSoPhong() + "' đã tồn tại! Vui lòng chọn số khác.");
                         return;
                    }
                }
                
                if (dao.updatePhong(p)) {
                    view.showMessage("Cập nhật thành công!");
                    reloadAll();
                } else {
                    view.showMessage("Cập nhật thất bại!");
                }
            }
        });

        // --- NÚT XÓA ---
        view.getBtnXoa().addActionListener(e -> {
            int row = view.getTblPhong().getSelectedRow();
            if (row == -1) {
                view.showMessage("Vui lòng chọn phòng cần xóa!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view, "Bạn chắc chắn muốn xóa phòng này?", 
                    "Cảnh báo", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                int maPhong = Integer.parseInt(view.getTblPhong().getValueAt(row, 0).toString());
                if (dao.deletePhong(maPhong)) {
                    view.showMessage("Xóa thành công!");
                    reloadAll();
                } else {
                    view.showMessage("Xóa thất bại! (Có thể phòng đang có đơn đặt)");
                }
            }
        });

        // --- NÚT TÌM KIẾM ---
        view.getBtnTimKiem().addActionListener(e -> {
            String tuKhoa = view.getTxtTimKiem().getText().trim();
            if (tuKhoa.isEmpty()) {
                view.showMessage("Vui lòng nhập số phòng để tìm!");
                return;
            }
            List<Phong> ketQua = dao.timKiemPhong(tuKhoa);
            if (ketQua.isEmpty()) {
                view.showMessage("Không tìm thấy phòng nào!");
            }
            loadDataToTable(ketQua); // Chỉ hiện kết quả tìm được
        });

        // --- NÚT LÀM MỚI ---
        view.getBtnLamMoi().addActionListener(e -> {
            reloadAll(); 
        });
        
        // --- NÚT XUẤT EXCEL (Dùng Helper mới) ---
        // <--- 2. CHỈ CẦN 1 DÒNG GỌI HÀM NÀY LÀ XONG
        view.getBtnXuatExcel().addActionListener(e -> {
            ExcelHelper.exportToExcel(view.getTblPhong(), view);
        });

        // --- SỰ KIỆN CLICK BẢNG ---
        view.getTblPhong().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.getTblPhong().getSelectedRow();
                if (row >= 0) {
                    fillForm(row);
                }
            }
        });
    }

    // --- CÁC HÀM BỔ TRỢ (HELPER) ---

    // 1. Đổ danh sách vào bảng
    private void loadDataToTable(List<Phong> list) {
        DefaultTableModel model = view.getModelPhong();
        model.setRowCount(0); // Xóa bảng cũ
        for (Phong p : list) {
            model.addRow(new Object[]{
                p.getMaPhong(),
                p.getSoPhong(),
                convertMaLoaiToTen(p.getMaLoaiPhong()), 
                p.getTrangThai()
            });
        }
    }

    // 2. Lấy dữ liệu từ Form -> đóng gói thành Model
    private Phong getPhongFromInput() {
        String soPhong = view.getTxtSoPhong().getText().trim();
        if (soPhong.isEmpty()) {
            view.showMessage("Số phòng không được để trống!");
            return null;
        }
        
        String tenLoai = view.getCboLoaiPhong().getSelectedItem().toString();
        int maLoai = convertTenLoaiToMa(tenLoai);
        
        String trangThai = view.getCboTrangThai().getSelectedItem().toString();

        return new Phong(0, soPhong, maLoai, trangThai);
    }
    
    // 3. Đổ dữ liệu từ bảng lên ô nhập
    private void fillForm(int row) {
        String soPhong = view.getTblPhong().getValueAt(row, 1).toString();
        String tenLoai = view.getTblPhong().getValueAt(row, 2).toString();
        String trangThai = view.getTblPhong().getValueAt(row, 3).toString();
        
        view.getTxtSoPhong().setText(soPhong);
        view.getCboLoaiPhong().setSelectedItem(tenLoai);
        view.getCboTrangThai().setSelectedItem(trangThai);
    }

    // 4. Hàm làm mới toàn bộ
    private void reloadAll() {
        view.getTxtSoPhong().setText("");
        view.getTxtTimKiem().setText("");
        view.getTblPhong().clearSelection();
        loadDataToTable(dao.getAllPhong()); 
    }

    // --- HÀM XỬ LÝ LOGIC RIÊNG (MAPPING) ---
    
    private int convertTenLoaiToMa(String tenLoai) {
        switch (tenLoai) {
            case "Phòng Đơn": return 1;
            case "Phòng Đôi": return 2;
            case "VIP": return 3;
            default: return 1;
        }
    }
    
    private String convertMaLoaiToTen(int maLoai) {
        switch (maLoai) {
            case 1: return "Phòng Đơn";
            case 2: return "Phòng Đôi";
            case 3: return "VIP";
            default: return "Khác";
        }
    }
    
}