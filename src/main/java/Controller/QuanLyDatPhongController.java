package Controller;

import DAO.DatPhongDAO;
import VIEW.DialogDatPhong;
import VIEW.QuanLyDatPhongPanel;
import Helper.ExcelHelper; 
import Model.DatPhong;
import java.awt.event.ItemEvent;
import javax.swing.*;

public class QuanLyDatPhongController {
    
    private QuanLyDatPhongPanel view;
    private DatPhongDAO dao;

    public QuanLyDatPhongController(QuanLyDatPhongPanel view) {
        this.view = view;
        this.dao = new DatPhongDAO();
        initEvents(); // Gắn sự kiện ngay khi khởi tạo
    }

    private void initEvents() {
        // 1. SỰ KIỆN TẠO MỚI
        view.getBtnTaoMoi().addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
            DialogDatPhong dialog = new DialogDatPhong(parent, true);
            new DatPhongController(dialog); 
            dialog.setVisible(true);
            view.loadData();
        });

        // 2. SỰ KIỆN LÀM MỚI (REFRESH)
        view.getBtnLamMoi().addActionListener(e -> view.loadData());

        // 3. SỰ KIỆN HỦY ĐƠN
        view.getBtnHuyDon().addActionListener(e -> xuLyHuyDon());
        
        // 4. SỰ KIỆN SỬA ĐƠN
        view.getBtnSua().addActionListener(e -> xuLySuaDatPhong());

        // 5. SỰ KIỆN XUẤT EXCEL
        view.getBtnXuatExcel().addActionListener(e -> {
            ExcelHelper.exportToExcel(view.getTblDatPhong(), view);
        });

        // 6. SỰ KIỆN NHẬN PHÒNG (CHECK-IN)
        view.getBtnNhanPhong().addActionListener(e -> xuLyNhanPhong());

        // 7. SỰ KIỆN TRẢ PHÒNG (CHECK-OUT & THANH TOÁN)
        view.getBtnTraPhong().addActionListener(e -> xuLyTraPhong());
        
        // 8. SỰ KIỆN LỌC TRẠNG THÁI
        view.getCboTrangThai().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String trangThai = e.getItem().toString();
                java.util.List<String[]> list = dao.timKiemTheoTrangThai(trangThai);
                
                javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) view.getTblDatPhong().getModel();
                model.setRowCount(0);
                for (String[] row : list) {
                    model.addRow(row);
                }
            }
        });
        //SỰ KIỆN TÌM KIẾM THEO NGÀY 
        view.getBtnTimNgay().addActionListener(e -> {
        // Lấy ngày từ ô chọn
        java.util.Date ngayCanTim = view.getNgayTim();
        
        // Gọi DAO tìm kiếm
        java.util.List<String[]> list = dao.timKiemTheoNgay(ngayCanTim);
        
        // Đổ dữ liệu vào bảng
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) view.getTblDatPhong().getModel();
        model.setRowCount(0);
        
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy đơn nào trong ngày này!");
        } else {
            for (String[] row : list) {
                model.addRow(row);
            }
        }
    });
    }

    // --- LOGIC: HỦY ĐƠN ĐẶT PHÒNG ---
    private void xuLyHuyDon() {
        JTable tbl = view.getTblDatPhong();
        int row = tbl.getSelectedRow();
        
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần hủy!");
            return;
        }

        // Lấy trạng thái (Cột 5)
        String trangThai = tbl.getValueAt(row, 5).toString();
        
        if (!trangThai.equals("Đã đặt")) {
            JOptionPane.showMessageDialog(view, "Chỉ có thể hủy đơn đang ở trạng thái 'Đã đặt'!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, 
            "Bạn có chắc chắn muốn HỦY đơn này?\nPhòng sẽ được trả về trạng thái TRỐNG.",
            "Xác nhận Hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int maDatPhong = Integer.parseInt(tbl.getValueAt(row, 0).toString());
            
            if (dao.huyDatPhong(maDatPhong)) {
                JOptionPane.showMessageDialog(view, "Đã hủy đơn thành công!");
                view.loadData(); 
            } else {
                JOptionPane.showMessageDialog(view, "Hủy thất bại! Có lỗi xảy ra.");
            }
        }
    }

    // --- LOGIC: SỬA ĐƠN ĐẶT PHÒNG ---
    private void xuLySuaDatPhong() {
        JTable tbl = view.getTblDatPhong();
        int row = tbl.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần sửa!");
            return;
        }

        String trangThai = tbl.getValueAt(row, 5).toString();
        // Chặn sửa đơn đã Hủy hoặc đã Trả
        if (trangThai.equals("Đã hủy") || trangThai.equals("Đã trả")) {
             JOptionPane.showMessageDialog(view, "Không thể sửa đơn đã Hủy hoặc đã Trả phòng!");
             return;
        }

        int maDatPhong = Integer.parseInt(tbl.getValueAt(row, 0).toString());

        // 1. Lấy dữ liệu Đặt phòng cũ
        DatPhong dpCu = dao.getDatPhongById(maDatPhong);
        if (dpCu == null) return;

        // 2. Lấy thông tin Khách hàng cũ
        DAO.KhachHangDAO khDao = new DAO.KhachHangDAO();
        Model.KhachHang khachHang = khDao.getKhachHangById(dpCu.getMaKhachHang());

        // 3. Mở Dialog Sửa
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
        DialogDatPhong dialog = new DialogDatPhong(parent, true);
        
        // Truyền cả 2 đối tượng vào Dialog
        dialog.setModel(dpCu, khachHang); 
        
        new DatPhongController(dialog); 
        dialog.setVisible(true);

        // 4. Load lại bảng
        view.loadData();
    }

    // --- LOGIC: NHẬN PHÒNG (CHECK-IN) ---
    private void xuLyNhanPhong() {
        JTable tbl = view.getTblDatPhong();
        int row = tbl.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn khách đến nhận phòng!");
            return;
        }

        String trangThai = tbl.getValueAt(row, 5).toString();
        
        // Chỉ đơn "Đã đặt" mới được Check-in
        if (!trangThai.equals("Đã đặt")) {
            JOptionPane.showMessageDialog(view, "Chỉ có thể Nhận phòng cho đơn ở trạng thái 'Đã đặt'!");
            return;
        }

        int maDatPhong = Integer.parseInt(tbl.getValueAt(row, 0).toString());
        
        if (dao.nhanPhong(maDatPhong)) {
            JOptionPane.showMessageDialog(view, "Check-in thành công! Trạng thái chuyển sang 'Đang ở'.");
            view.loadData();
        }
    }

    // --- LOGIC: TRẢ PHÒNG & TÍNH TIỀN (CHECK-OUT) ---
    // [CẬP NHẬT] Logic chặt chẽ: Phải "Đang ở" mới được Trả phòng
    private void xuLyTraPhong() {
        JTable tbl = view.getTblDatPhong();
        int row = tbl.getSelectedRow();
        
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn khách muốn trả phòng!");
            return;
        }

        String trangThai = tbl.getValueAt(row, 5).toString();

        // 1. Nếu trạng thái là 'Đã đặt' -> Yêu cầu Check-in trước
        if (trangThai.equals("Đã đặt")) {
            JOptionPane.showMessageDialog(view, "Khách chưa nhận phòng (Check-in) nên không thể Trả phòng!\nVui lòng bấm nút 'Nhận Phòng' màu xanh trước.");
            return;
        }

        // 2. Chỉ cho phép trả phòng khi trạng thái là 'Đang ở'
        if (!trangThai.equals("Đang ở")) {
            JOptionPane.showMessageDialog(view, "Chỉ có thể trả phòng cho đơn đang ở trạng thái 'Đang ở'!");
            return;
        }

        int maDatPhong = Integer.parseInt(tbl.getValueAt(row, 0).toString());

        try {

            DatPhong dp = dao.getDatPhongById(maDatPhong);
            double giaPhong = dao.getGiaPhongByMaDatPhong(maDatPhong);
            

            java.util.Date now = new java.util.Date();
            long diff = now.getTime() - dp.getNgayCheckIn().getTime(); 
            long soNgay = java.util.concurrent.TimeUnit.DAYS.convert(diff, java.util.concurrent.TimeUnit.MILLISECONDS);
            
 
            if (soNgay <= 0) soNgay = 1; 

            // 3. Tính tiền
            double tongTienPhong = soNgay * giaPhong;
            double tienCoc = dp.getTienDatCoc();
            double phaiThanhToan = tongTienPhong - tienCoc;
            
            String bill = "=== HÓA ĐƠN THANH TOÁN ===\n\n" +
                          "Mã Đơn: " + maDatPhong + "\n" +
                          "Khách hàng: " + tbl.getValueAt(row, 1).toString() + "\n" +
                          "Phòng: " + tbl.getValueAt(row, 2).toString() + "\n" +
                          "----------------------------------\n" +
                          "Số ngày ở: " + soNgay + " ngày\n" +
                          "Đơn giá: " + String.format("%,.0f", giaPhong) + " VNĐ/ngày\n" +
                          "Thành tiền: " + String.format("%,.0f", tongTienPhong) + " VNĐ\n" +
                          "Đã đặt cọc: -" + String.format("%,.0f", tienCoc) + " VNĐ\n" +
                          "----------------------------------\n" +
                          "CẦN THANH TOÁN: " + String.format("%,.0f", phaiThanhToan) + " VNĐ\n\n" +
                          "Bạn có muốn xác nhận trả phòng không?";

            int confirm = JOptionPane.showConfirmDialog(view, bill, "Xác nhận Trả Phòng", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Gọi DAO để lưu xuống CSDL
                if (dao.traPhong(maDatPhong, tongTienPhong)) {
                    JOptionPane.showMessageDialog(view, "Trả phòng thành công! Phòng đã chuyển về trạng thái TRỐNG.");
                    view.loadData(); 
                } else {
                    JOptionPane.showMessageDialog(view, "Lỗi khi lưu dữ liệu!");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi tính toán: " + ex.getMessage());
        }
    }
    
}