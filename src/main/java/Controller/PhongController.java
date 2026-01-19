package Controller;

import DAO.LoaiPhongDAO;
import DAO.PhongDAO;
import Model.LoaiPhong;
import Model.Phong;
import VIEW.QuanLyPhongPanel;
import Helper.ExcelHelper;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PhongController {

    private QuanLyPhongPanel view;
    private PhongDAO dao;
    private LoaiPhongDAO loaiPhongDAO;

    public PhongController(QuanLyPhongPanel view) {
        this.view = view;
        this.dao = new PhongDAO();
        this.loaiPhongDAO = new LoaiPhongDAO();
        
        loadComboBoxLoaiPhong(); // Nạp dữ liệu vào ComboBox nhập liệu (Thêm/Sửa)
        loadComboBoxBoLoc();     // Nạp dữ liệu vào ComboBox thanh tìm kiếm
        
        // Cấu hình bảng (Ẩn cột ID)
        // Cột ID nằm ở index 0, ta chỉnh độ rộng về 0 để người dùng không thấy
        // nhưng code vẫn có thể lấy được giá trị để thực hiện Sửa/Xóa.
        view.getTblPhong().getColumnModel().getColumn(0).setMinWidth(0);
        view.getTblPhong().getColumnModel().getColumn(0).setMaxWidth(0);
        view.getTblPhong().getColumnModel().getColumn(0).setWidth(0);
        
        // Kích hoạt sự kiện và Tải dữ liệu lần đầu
        initEvents();
        refreshTable(); 
    }
    // PHẦN 1: LOGIC TÌM KIẾM VÀ LẤY DỮ LIỆU (DATA RETRIEVAL)

    private List<Phong> layDanhSachTheoBoLoc() {
        // 1. Lấy từ khóa nhập tay
        String tuKhoa = view.getTxtTimKiem().getText().trim();
        
        // 2. Lấy trạng thái lọc
        String trangThai = view.getCboLocTrangThai().getSelectedItem().toString();
        // Nếu chọn "Tất cả TT" (trên giao diện) thì đổi thành "Tất cả" để khớp với logic DAO
        if(trangThai.contains("Tất cả")) trangThai = "Tất cả"; 
        
        // 3. Lấy mã loại phòng lọc
        int maLoai = -1; // Mặc định -1 là lấy tất cả
        Object selected = view.getCboLocLoai().getSelectedItem();
        if (selected instanceof LoaiPhong) {
             maLoai = ((LoaiPhong) selected).getMaLoaiPhong();
        }

        // 4. Gọi xuống DAO để truy vấn SQL
        return dao.timKiemVaLoc(tuKhoa, maLoai, trangThai);
    }
    // PHẦN 2: LOGIC HIỂN THỊ DỮ LIỆU (DATA RENDERING)
    private void hienThiLenBang(List<Phong> list) {
        DefaultTableModel model = view.getModelPhong();
        model.setRowCount(0); // Xóa sạch dữ liệu cũ trên bảng
        
        // Lấy toàn bộ danh sách Loại phòng 1 lần duy nhất để tra cứu
        // (Tránh việc gọi DB nhiều lần trong vòng lặp -> Tối ưu hiệu năng)
        List<LoaiPhong> listLoai = loaiPhongDAO.getAllLoaiPhong();

        for (Phong p : list) {
            // Logic: Mapping từ ID Loại phòng -> Tên Loại và Giá Tiền
            String tenLoai = "Không xác định";
            double giaTien = 0;
            
            for(LoaiPhong lp : listLoai) {
                if(lp.getMaLoaiPhong() == p.getMaLoaiPhong()) {
                    tenLoai = lp.getTenLoai();
                    giaTien = lp.getGiaTheoNgay();
                    break; 
                }
            }
            
            // Thêm dòng mới vào bảng
            model.addRow(new Object[]{
                p.getMaPhong(),                  // Cột 0: ID (Đang bị ẩn)
                p.getSoPhong(),                  // Cột 1: Số phòng
                tenLoai,                         // Cột 2: Tên loại (Đã map từ ID)
                String.format("%,.0f", giaTien), // Cột 3: Giá tiền (Format có dấu phẩy: 500,000)
                p.getTrangThai()                 // Cột 4: Trạng thái
            });
        }
    }

    /**
     * Hàm điều phối chính: Làm mới bảng dữ liệu
     * Quy trình: Lấy dữ liệu theo bộ lọc -> Hiển thị lên bảng
     */
    private void refreshTable() {
        List<Phong> data = layDanhSachTheoBoLoc();
        hienThiLenBang(data);
    }

    // ============================================================
    // PHẦN 3: XỬ LÝ SỰ KIỆN (EVENT HANDLING)
    // ============================================================

    private void initEvents() {
        // Nhóm nút chức năng CRUD (Thêm, Sửa, Xóa)
        view.getBtnThem().addActionListener(e -> themPhong());
        view.getBtnSua().addActionListener(e -> suaPhong());
        view.getBtnXoa().addActionListener(e -> xoaPhong());
        
        // Nút Làm mới: Reset form và tải lại bảng gốc
        view.getBtnLamMoi().addActionListener(e -> {
            clearForm();
            refreshTable();
        });
        
        // Nút Xuất Excel
        view.getBtnXuatExcel().addActionListener(e -> xuatExcel());

        // Nhóm Tìm kiếm & Lọc: Tự động tải lại bảng khi thao tác
        view.getBtnTimKiem().addActionListener(e -> refreshTable());
        view.getCboLocLoai().addActionListener(e -> refreshTable());
        view.getCboLocTrangThai().addActionListener(e -> refreshTable());
        
        // Sự kiện Click chuột vào bảng -> Đổ dữ liệu lên Form
        view.getTblPhong().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillForm();
            }
        });
    }
    
    // ============================================================
    // PHẦN 4: CÁC HÀM HỖ TRỢ (HELPER METHODS)
    // ============================================================

    // Load danh sách loại phòng vào ComboBox nhập liệu (Form Thêm/Sửa)
    private void loadComboBoxLoaiPhong() {
        List<LoaiPhong> list = loaiPhongDAO.getAllLoaiPhong();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (LoaiPhong lp : list) { model.addElement(lp); }
        view.getCboLoaiPhong().setModel(model);
    }

    // Load danh sách loại phòng vào ComboBox Lọc (Thanh Toolbar)
    private void loadComboBoxBoLoc() {
        List<LoaiPhong> list = loaiPhongDAO.getAllLoaiPhong();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        
        // Thêm mục "Tất cả" vào đầu danh sách (dùng ID giả là -1)
        LoaiPhong tatCa = new LoaiPhong(-1, "Tất cả Loại", 0);
        model.addElement(tatCa);
        
        for (LoaiPhong lp : list) { model.addElement(lp); }
        view.getCboLocLoai().setModel(model);
    }

    // Gọi Helper để xuất file Excel
    private void xuatExcel() {
        ExcelHelper.exportToExcel(view.getTblPhong(), view);
    }

    // Đóng gói dữ liệu từ Form nhập liệu vào Object Phong
    private Phong getModelFromForm() {
        Phong p = new Phong();
        p.setSoPhong(view.getTxtSoPhong().getText().trim());
        
        // Lấy ID thật từ object LoaiPhong trong ComboBox
        LoaiPhong selectedLoai = (LoaiPhong) view.getCboLoaiPhong().getSelectedItem();
        if (selectedLoai != null) {
            p.setMaLoaiPhong(selectedLoai.getMaLoaiPhong());
        }
        
        p.setTrangThai(view.getCboTrangThai().getSelectedItem().toString());
        return p;
    }

    // ============================================================
    // PHẦN 5: CHỨC NĂNG CRUD (THÊM - SỬA - XÓA)
    // ============================================================

    private void themPhong() {
        Phong p = getModelFromForm();
        
        // Validate 1: Không được để trống
        if (p.getSoPhong().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập Số phòng!");
            return;
        }
        // Validate 2: Kiểm tra trùng lặp
        if (dao.checkTonTaiSoPhong(p.getSoPhong())) {
            JOptionPane.showMessageDialog(view, "Số phòng " + p.getSoPhong() + " đã tồn tại!");
            return;
        }
        
        // Gọi DAO thêm mới
        if (dao.insertPhong(p)) {
            JOptionPane.showMessageDialog(view, "Thêm thành công!");
            refreshTable(); // Tải lại bảng ngay để thấy kết quả
            clearForm();    // Xóa trắng form để nhập tiếp
        } else {
            JOptionPane.showMessageDialog(view, "Thêm thất bại!");
        }
    }

    private void suaPhong() {
        int row = view.getTblPhong().getSelectedRow();
        if (row == -1) { 
            JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng cần sửa trên bảng!"); 
            return; 
        }
        
        // Lấy ID thật từ cột ẩn (cột 0)
        int maPhong = Integer.parseInt(view.getTblPhong().getValueAt(row, 0).toString());
        
        Phong p = getModelFromForm();
        p.setMaPhong(maPhong); // Gán ID để DAO biết sửa dòng nào
        
        if(dao.updatePhong(p)) {
            JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thất bại!");
        }
    }

    private void xoaPhong() {
        int row = view.getTblPhong().getSelectedRow();
        if (row == -1) { 
            JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng cần xóa!"); 
            return; 
        }
        
        // Hỏi xác nhận trước khi xóa
        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa phòng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            // Lấy ID thật từ cột ẩn
            int maPhong = Integer.parseInt(view.getTblPhong().getValueAt(row, 0).toString());
            
            if(dao.deletePhong(maPhong)) {
                JOptionPane.showMessageDialog(view, "Xóa thành công!");
                refreshTable();
                clearForm();
            }
        }
    }

    // Đổ dữ liệu từ Bảng lên Form khi click chuột
    private void fillForm() {
        int row = view.getTblPhong().getSelectedRow();
        if (row == -1) return;

        // 1. Đổ Số phòng
        view.getTxtSoPhong().setText(view.getTblPhong().getValueAt(row, 1).toString());
        
        // 2. Chọn lại Loại phòng trên ComboBox (Dựa vào Tên hiển thị trên bảng)
        String tenLoaiTrenBang = view.getTblPhong().getValueAt(row, 2).toString();
        DefaultComboBoxModel model = (DefaultComboBoxModel) view.getCboLoaiPhong().getModel();
        for (int i = 0; i < model.getSize(); i++) {
            LoaiPhong lp = (LoaiPhong) model.getElementAt(i);
            if (lp.getTenLoai().equals(tenLoaiTrenBang)) {
                view.getCboLoaiPhong().setSelectedIndex(i);
                break;
            }
        }
        
        // 3. Đổ Trạng thái
        view.getCboTrangThai().setSelectedItem(view.getTblPhong().getValueAt(row, 4).toString());
    }

    // Xóa trắng Form và Reset bộ lọc
    private void clearForm() {
        view.getTxtSoPhong().setText("");
        view.getTxtTimKiem().setText("");
        
        if(view.getCboLoaiPhong().getItemCount() > 0) view.getCboLoaiPhong().setSelectedIndex(0);
        view.getCboTrangThai().setSelectedIndex(0);
        
        // Reset bộ lọc về mặc định
        view.getCboLocLoai().setSelectedIndex(0);
        view.getCboLocTrangThai().setSelectedIndex(0);
        
        view.getTblPhong().clearSelection();
    }
}