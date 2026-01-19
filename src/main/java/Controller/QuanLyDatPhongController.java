package Controller;

import DAO.DatPhongDAO;
import DAO.KhachHangDAO;
import VIEW.DialogDatPhong;
import VIEW.QuanLyDatPhongPanel;
import Helper.ExcelHelper; 
import Model.DatPhong;
import Model.KhachHang;
import VIEW.DialogCheckIn;
import java.awt.event.ItemEvent;
import javax.swing.*;

/**
 * Controller điều phối chức năng Quản Lý Danh Sách Đặt Phòng
 * Nhiệm vụ:
 * 1. Hiển thị danh sách phiếu đặt.
 * 2. Xử lý các quy trình: Tạo mới -> Check-in -> Check-out -> Hủy.
 * 3. Tìm kiếm và Lọc dữ liệu.
 */
public class QuanLyDatPhongController {
    
    private QuanLyDatPhongPanel view;
    private DatPhongDAO dao;

    public QuanLyDatPhongController(QuanLyDatPhongPanel view) {
        this.view = view;
        this.dao = new DatPhongDAO();
        
        // 1. Gắn sự kiện cho các nút
        initEvents(); 
    }   

    // ============================================================
    // PHẦN 1: KHỞI TẠO SỰ KIỆN (EVENT LISTENER)
    // ============================================================
    private void initEvents() {
        // --- NHÓM 1: CÁC NÚT THAO TÁC CƠ BẢN ---
        view.getBtnTaoMoi().addActionListener(e -> moDialogTaoMoi());
        view.getBtnSua().addActionListener(e -> moDialogSua());
        view.getBtnHuyDon().addActionListener(e -> xuLyHuyDon());
        view.getBtnLamMoi().addActionListener(e -> view.loadData()); // Tải lại bảng
        
        // --- NHÓM 2: QUY TRÌNH CHECK-IN / CHECK-OUT ---
        view.getBtnNhanPhong().addActionListener(e -> xuLyNhanPhong());
        view.getBtnTraPhong().addActionListener(e -> xuLyTraPhong());
        
        // --- NHÓM 3: TIỆN ÍCH ---
        view.getBtnXuatExcel().addActionListener(e -> ExcelHelper.exportToExcel(view.getTblDatPhong(), view));
        
        // --- NHÓM 4: TÌM KIẾM & LỌC ---
        
        // Sự kiện: Lọc theo trạng thái (Combobox)
        view.getCboTrangThai().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String trangThai = e.getItem().toString();
                java.util.List<String[]> list = dao.timKiemTheoTrangThai(trangThai);
                doDuLieuVaoBang(list);
            }
        });

        // Sự kiện: Tìm theo Ngày Check-In (Ngày đến)
        view.getBtnTimNgayIn().addActionListener(e -> {
            java.util.Date ngayCanTim = view.getNgayIn();
            java.util.List<String[]> list = dao.timKiemTheoNgay(ngayCanTim);
            if (list.isEmpty()) JOptionPane.showMessageDialog(view, "Không tìm thấy đơn check-in ngày này!");
            doDuLieuVaoBang(list);
        });
        
        // Sự kiện: Tìm theo Ngày Check-Out (Ngày đi)
        view.getBtnTimNgayOut().addActionListener(e -> {
            java.util.Date ngayCanTim = view.getNgayOut();
            java.util.List<String[]> list = dao.timKiemTheoNgayOut(ngayCanTim);
            if (list.isEmpty()) JOptionPane.showMessageDialog(view, "Không tìm thấy đơn check-out ngày này!");
            doDuLieuVaoBang(list);
        });
    }

    // ============================================================
    // PHẦN 2: CÁC CHỨC NĂNG NGHIỆP VỤ (BUSINESS LOGIC)
    // ============================================================

    /**
     * Mở Dialog để tạo đơn đặt phòng mới
     */
    private void moDialogTaoMoi() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
        DialogDatPhong dialog = new DialogDatPhong(parent, true);
        
        // Gắn Controller riêng cho Dialog
        new DatPhongController(dialog); 
        dialog.setVisible(true);
        
        // Sau khi đóng dialog thì tải lại bảng để thấy đơn mới
        view.loadData();
    }

    /**
     * Mở Dialog để sửa thông tin đơn đặt phòng
     */
    private void moDialogSua() {
        JTable tbl = view.getTblDatPhong();
        int row = tbl.getSelectedRow();
        if (row == -1) { 
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần sửa!"); 
            return; 
        }
        
        // Lấy ID đơn cần sửa
        int maDatPhong = Integer.parseInt(tbl.getValueAt(row, 0).toString());
        
        // Lấy dữ liệu cũ từ DB
        DatPhong dpCu = dao.getDatPhongById(maDatPhong);
        KhachHangDAO khDao = new KhachHangDAO();
        KhachHang kh = khDao.getKhachHangById(dpCu.getMaKhachHang());
        
        // Hiển thị lên Dialog
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
        DialogDatPhong dialog = new DialogDatPhong(parent, true);
        dialog.setModel(dpCu, kh); // Đổ dữ liệu cũ vào form
        
        new DatPhongController(dialog);
        dialog.setVisible(true);
        view.loadData();
    }

    /**
     * Xử lý Hủy Đơn
     * Điều kiện: Chỉ hủy được đơn ở trạng thái "Đã đặt" (Chưa Check-in)
     */
    private void xuLyHuyDon() {
        JTable tbl = view.getTblDatPhong();
        int row = tbl.getSelectedRow();
        if (row == -1) { 
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần hủy!"); 
            return; 
        }
        
        // Lấy trạng thái hiện tại (Cột index 5)
        String trangThai = tbl.getValueAt(row, 5).toString();
        
        if (!trangThai.equals("Đã đặt")) { 
            JOptionPane.showMessageDialog(view, "Chỉ có thể hủy đơn khi khách chưa nhận phòng (Trạng thái: Đã đặt)!"); 
            return; 
        }
        
        if (JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn hủy đơn này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int maDatPhong = Integer.parseInt(tbl.getValueAt(row, 0).toString());
            
            // Gọi DAO hủy đơn (Đổi trạng thái đơn -> Đã hủy, Phòng -> Trống)
            if (dao.huyDatPhong(maDatPhong)) { 
                JOptionPane.showMessageDialog(view, "Đã hủy thành công!"); 
                view.loadData(); 
            }
        }
    }

    /**
     * Xử lý Check-in (Nhận Phòng)
     * Điều kiện: Chỉ nhận được đơn "Đã đặt"
     */
    private void xuLyNhanPhong() {
        JTable tbl = view.getTblDatPhong();
        int row = tbl.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Chọn đơn khách đến nhận phòng!");
            return;
        }
        
        String trangThai = tbl.getValueAt(row, 5).toString();
        if (!trangThai.equals("Đã đặt")) { 
            JOptionPane.showMessageDialog(view, "Chỉ check-in được cho đơn ở trạng thái 'Đã đặt'!"); 
            return; 
        }
        
        // Lấy thông tin cần thiết
        int maDatPhong = Integer.parseInt(tbl.getValueAt(row, 0).toString());
        String tenPhong = tbl.getValueAt(row, 2).toString();
        
        DatPhong dp = dao.getDatPhongById(maDatPhong);
        KhachHangDAO khDao = new KhachHangDAO();
        KhachHang kh = khDao.getKhachHangById(dp.getMaKhachHang());
        
        // Mở Dialog xác nhận Check-in (Có hiện thông tin khách)
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
        DialogCheckIn dialog = new DialogCheckIn(parent, dp, kh, tenPhong);
        dialog.setVisible(true);
        
        // Nếu người dùng bấm Xác nhận trong Dialog
        if (dialog.isConfirmed()) {
            if (dao.nhanPhong(maDatPhong)) { 
                JOptionPane.showMessageDialog(view, "Check-in thành công! Chúc quý khách vui vẻ."); 
                view.loadData(); 
            }
        }
    }

    /**
     * Xử lý Check-out (Trả Phòng)
     * Điều kiện: Chỉ trả được đơn "Đang ở"
     */
    private void xuLyTraPhong() {
        JTable tbl = view.getTblDatPhong();
        int row = tbl.getSelectedRow();
        
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn khách muốn trả phòng!");
            return;
        }

        // Lấy trạng thái (Cột 5)
        String trangThai = tbl.getValueAt(row, 5).toString(); 

        // 1. Kiểm tra logic nghiệp vụ
        if (trangThai.equals("Đã đặt")) {
            JOptionPane.showMessageDialog(view, "Khách này chưa nhận phòng (Check-in) nên không thể trả phòng!");
            return;
        }
        if (trangThai.equals("Đã trả") || trangThai.equals("Đã hủy")) {
            JOptionPane.showMessageDialog(view, "Đơn này đã kết thúc rồi!");
            return;
        }

        // 2. Lấy thông tin hiển thị xác nhận
        String tenKhach = tbl.getValueAt(row, 1).toString();
        String soPhong = tbl.getValueAt(row, 2).toString();
        
        String msg = "Xác nhận khách " + tenKhach.toUpperCase() + " trả phòng " + soPhong + "?\n\n" +
                     "Lưu ý: Phòng sẽ được chuyển ngay về trạng thái TRỐNG.";

        int confirm = JOptionPane.showConfirmDialog(view, msg, "Xác nhận Trả Phòng", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int maDatPhong = Integer.parseInt(tbl.getValueAt(row, 0).toString());
            
            // Gọi hàm trả phòng (Truyền 0 vì không tính tiền tại đây)
            if (dao.traPhong(maDatPhong, 0)) {
                JOptionPane.showMessageDialog(view, "Đã trả phòng thành công!");
                view.loadData(); 
            } else {
                JOptionPane.showMessageDialog(view, "Lỗi kết nối cơ sở dữ liệu!");
            }
        }
    }
    
    // Hàm phụ: Đổ danh sách dữ liệu vào bảng
    private void doDuLieuVaoBang(java.util.List<String[]> list) {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) view.getTblDatPhong().getModel();
        model.setRowCount(0);
        for (String[] row : list) {
            model.addRow(row);
        }
    }
}