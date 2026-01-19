package DAO;

import Database.DBConnect;
import Model.Phong;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp xử lý dữ liệu cho đối tượng Phòng (CRUD)
 * Tương tác trực tiếp với bảng Phong trong CSDL.
 */
public class PhongDAO {

    // ============================================================
    // PHẦN 1: CÁC HÀM LẤY DỮ LIỆU (READ / SEARCH)
    // ============================================================

    /**
     * Lấy toàn bộ danh sách phòng có trong hệ thống.
     */
    public List<Phong> getAllPhong() {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT MaPhong, SoPhong, MaLoaiPhong, TrangThai FROM Phong";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                // Gọi hàm phụ để code gọn gàng, tránh lặp lại
                list.add(taoDoiTuongPhong(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tìm kiếm và Lọc đa năng (Chức năng quan trọng nhất)
     * @param soPhong: Từ khóa tìm kiếm số phòng (tìm gần đúng)
     * @param maLoai: ID loại phòng (-1 nghĩa là lấy tất cả)
     * @param trangThai: Trạng thái phòng ("Tất cả" nghĩa là không lọc)
     */
    public List<Phong> timKiemVaLoc(String soPhong, int maLoai, String trangThai) {
        List<Phong> list = new ArrayList<>();
        
        // 1. Khởi tạo câu SQL động với điều kiện giả 1=1 để dễ nối chuỗi
        StringBuilder sql = new StringBuilder("SELECT * FROM Phong WHERE 1=1 ");

        // 2. Cộng chuỗi SQL dựa trên điều kiện đầu vào
        if (!soPhong.isEmpty()) {
            sql.append("AND SoPhong LIKE ? ");
        }
        if (maLoai != -1) { 
            sql.append("AND MaLoaiPhong = ? ");
        }
        if (!trangThai.equals("Tất cả")) {
            sql.append("AND TrangThai = ? ");
        }

        // 3. Thực thi SQL
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1; // Biến đếm vị trí dấu hỏi (?)

            // Gán giá trị cho các dấu hỏi theo đúng thứ tự logic ở trên
            if (!soPhong.isEmpty()) {
                ps.setString(index++, "%" + soPhong + "%");
            }
            if (maLoai != -1) {
                ps.setInt(index++, maLoai);
            }
            if (!trangThai.equals("Tất cả")) {
                ps.setString(index++, trangThai);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(taoDoiTuongPhong(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách các phòng còn TRỐNG theo Loại phòng cụ thể.
     * Dùng cho chức năng Đặt phòng (chỉ hiện phòng trống để chọn).
     */
    public List<Phong> getPhongTrongByLoai(int maLoai) {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT * FROM Phong WHERE MaLoaiPhong = ? AND TrangThai = N'Trống'";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maLoai);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(taoDoiTuongPhong(rs));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    /**
     * Tìm kiếm đơn giản theo Số phòng (Tìm gần đúng).
     */
    public List<Phong> timKiemPhong(String tuKhoa) {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT * FROM Phong WHERE SoPhong LIKE ?";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + tuKhoa + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(taoDoiTuongPhong(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ============================================================
    // PHẦN 2: CÁC HÀM THAY ĐỔI DỮ LIỆU (INSERT / UPDATE / DELETE)
    // ============================================================

    /**
     * Thêm mới một phòng.
     */
    public boolean insertPhong(Phong p) {
        String sql = "INSERT INTO Phong(SoPhong, MaLoaiPhong, TrangThai) VALUES(?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, p.getSoPhong());
            ps.setInt(2, p.getMaLoaiPhong());
            ps.setString(3, p.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật thông tin phòng.
     * Lưu ý: Cập nhật dựa trên MaPhong (ID ẩn).
     */
    public boolean updatePhong(Phong p) {
        String sql = "UPDATE Phong SET SoPhong=?, MaLoaiPhong=?, TrangThai=? WHERE MaPhong=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, p.getSoPhong());
            ps.setInt(2, p.getMaLoaiPhong());
            ps.setString(3, p.getTrangThai());
            ps.setInt(4, p.getMaPhong()); 
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Chỉ cập nhật riêng Trạng thái phòng.
     * Dùng khi Check-in (Trống -> Đang ở) hoặc Check-out (Đang ở -> Trống).
     */
    public boolean updateTrangThaiPhong(int maPhong, String trangThaiMoi) {
        String sql = "UPDATE Phong SET TrangThai = ? WHERE MaPhong = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, trangThaiMoi);
            ps.setInt(2, maPhong);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa phòng khỏi hệ thống.
     */
    public boolean deletePhong(int maPhong) {
        String sql = "DELETE FROM Phong WHERE MaPhong=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maPhong);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ============================================================
    // PHẦN 3: CÁC HÀM KIỂM TRA & HỖ TRỢ (VALIDATION & HELPER)
    // ============================================================

    /**
     * Kiểm tra xem Số phòng đã tồn tại chưa.
     * Dùng để ngăn chặn việc tạo 2 phòng cùng số (VD: hai phòng 101).
     */
    public boolean checkTonTaiSoPhong(String soPhong) {
        String sql = "SELECT COUNT(*) FROM Phong WHERE SoPhong = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, soPhong);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0; // Nếu count > 0 nghĩa là đã có
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Hàm phụ: Chuyển đổi 1 dòng dữ liệu SQL (ResultSet) thành đối tượng Phong.
     * Giúp code ngắn gọn, không phải set thủ công lặp lại nhiều lần.
     */
    private Phong taoDoiTuongPhong(ResultSet rs) throws SQLException {
        Phong p = new Phong();
        p.setMaPhong(rs.getInt("MaPhong"));
        p.setSoPhong(rs.getString("SoPhong"));
        p.setMaLoaiPhong(rs.getInt("MaLoaiPhong"));
        p.setTrangThai(rs.getString("TrangThai"));
        return p;
    }
}