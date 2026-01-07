package Controller;

import DAO.KhuyenMaiDAO;
import Model.KhuyenMai;
import VIEW.QuanLyKhuyenMaiPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import Helper.ExcelHelper;

public class KhuyenMaiController {

    private QuanLyKhuyenMaiPanel view;
    private KhuyenMaiDAO dao;

    public KhuyenMaiController(QuanLyKhuyenMaiPanel view) {
        this.view = view;
        this.dao = new KhuyenMaiDAO();
        
        initEvents();
    }
//su kien
    private void initEvents() {
        view.getBtnThem().addActionListener(e -> xuLyThem());
        view.getBtnSua().addActionListener(e -> xuLySua());
        view.getBtnXoa().addActionListener(e -> xuLyXoa());
        
        view.getBtnLamMoi().addActionListener(e -> {
            clearForm();
            loadDataToTable();
        });
        
        view.getBtnTimKiem().addActionListener(e -> {
            String keyword = view.getTxtTimKiem().getText().trim();
            List<KhuyenMai> list = dao.timKiem(keyword);
            fillTable(list);
        });

        view.getTblKhuyenMai().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.getTblKhuyenMai().getSelectedRow();
                if (row >= 0) fillForm(row);
            }
        });
        view.getBtnXuatExcel().addActionListener(e -> {
            try {
                // Gọi hàm static bên ExcelHelper
                // Tham số 1: Cái bảng dữ liệu (JTable) lấy từ View
                // Tham số 2: Cái View (để hiện thông báo ở giữa màn hình)
                ExcelHelper.exportToExcel(view.getTblKhuyenMai(), view);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void xuLyThem() {
        KhuyenMai km = getModelFromForm(); 
        if (km == null) return;

        if (dao.checkTonTai(km.getMaKM())) {
            JOptionPane.showMessageDialog(view, "Mã khuyến mãi '" + km.getMaKM() + "' đã tồn tại!");
            return;
        }

        if (dao.insertKhuyenMai(km)) {
            JOptionPane.showMessageDialog(view, "Thêm thành công!");
            loadDataToTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(view, "Thêm thất bại!");
        }
    }

    private void xuLySua() {
        KhuyenMai km = getModelFromForm();
        if (km == null) return;

        if (dao.updateKhuyenMai(km)) {
            JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
            loadDataToTable(); 
            clearForm();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thất bại!");
        }
    }

    private void xuLyXoa() {
        String maKM = view.getTxtMaKM().getText();
        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khuyến mãi cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteKhuyenMai(maKM)) {
                JOptionPane.showMessageDialog(view, "Đã xóa thành công!");
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thất bại! (Có thể mã đang được sử dụng)");
            }
        }
    }

    private KhuyenMai getModelFromForm() {
        String ma = view.getTxtMaKM().getText().trim();
        String ten = view.getTxtTenKM().getText().trim();
        String giaStr = view.getTxtGiamGia().getText().trim();
        Date start = (Date) view.getSpinNgayBatDau().getValue();
        Date end = (Date) view.getSpinNgayKetThuc().getValue();

        if (ma.isEmpty() || ten.isEmpty() || giaStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin!");
            return null;
        }

        if (end.before(start)) {
            JOptionPane.showMessageDialog(view, "Lỗi: Ngày kết thúc phải sau ngày bắt đầu!");
            return null;
        }

        double giamGia = 0;
        try {
            giamGia = Double.parseDouble(giaStr);
            if (giamGia < 0) {
                JOptionPane.showMessageDialog(view, "Tiền giảm giá không được âm!");
                return null;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Tiền giảm giá phải là số!");
            return null;
        }

        return new KhuyenMai(ma, ten, giamGia, start, end, "Đang hoạt động");
    }

    private void loadDataToTable() {
        List<KhuyenMai> list = dao.getAllKhuyenMai();
        fillTable(list);
    }

    private void fillTable(List<KhuyenMai> list) {
        view.getModel().setRowCount(0);
        for (KhuyenMai km : list) {
            view.getModel().addRow(new Object[]{
                km.getMaKM(),
                km.getTenKM(),
                String.format("%,.0f", km.getGiamGia()), 
                km.getNgayBatDau(),
                km.getNgayKetThuc(),
                km.getTrangThai()
            });
        }
    }

    private void fillForm(int row) {
        view.getTxtMaKM().setText(view.getTblKhuyenMai().getValueAt(row, 0).toString());
        view.getTxtMaKM().setEditable(false);
        view.getTxtTenKM().setText(view.getTblKhuyenMai().getValueAt(row, 1).toString());
        String tien = view.getTblKhuyenMai().getValueAt(row, 2).toString().replace(",", "").replace(".", "");
        view.getTxtGiamGia().setText(tien);
        
        try {
            Object objStart = view.getTblKhuyenMai().getValueAt(row, 3);
            Object objEnd = view.getTblKhuyenMai().getValueAt(row, 4);
            if (objStart instanceof Date) view.getSpinNgayBatDau().setValue((Date) objStart);
            if (objEnd instanceof Date) view.getSpinNgayKetThuc().setValue((Date) objEnd);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void clearForm() {
        view.getTxtMaKM().setText("");
        view.getTxtMaKM().setEditable(true);
        view.getTxtTenKM().setText("");
        view.getTxtGiamGia().setText("");
        view.getSpinNgayBatDau().setValue(new Date());
        view.getSpinNgayKetThuc().setValue(new Date());
        view.getTblKhuyenMai().clearSelection();
    }
}