package DAO;

import Model.KhachHang;
import Database.DBConnect;
import java.sql.*;

/**
 * Lớp xử lý dữ liệu Khách Hàng (CRUD)
 * Nhiệm vụ: Tìm kiếm khách cũ, Thêm khách mới
 */
public class KhachHangDAO {

    // ============================================================
    // PHẦN 1: CÁC HÀM LẤY DỮ LIỆU (READ)
    // ============================================================

    /**
     * Tìm khách hàng dựa trên số CCCD.
     * Dùng để kiểm tra xem khách đã từng đến khách sạn chưa.
     * @param cccd Số Căn cước công dân
     * @return Đối tượng KhachHang hoặc null nếu không tìm thấy
     */
    public KhachHang getKhachHangByCCCD(String cccd) {
        String sql = "SELECT * FROM KhachHang WHERE CCCD = ?";
        
        // Sử dụng try-with-resources để tự động đóng kết nối
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cccd);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Map dữ liệu từ SQL sang Object Java
                return new KhachHang(
                    rs.getInt("MaKhachHang"),
                    rs.getString("HoTen"),
                    rs.getString("CCCD"),
                    rs.getString("SDT"),
                    rs.getString("DiaChi")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không tìm thấy hoặc lỗi
    }

    /**
     * Lấy thông tin khách hàng dựa trên ID (Primary Key).
     * Dùng khi cần hiển thị chi tiết đơn đặt phòng cũ.
     */
    public KhachHang getKhachHangById(int maKhachHang) {
        String sql = "SELECT * FROM KhachHang WHERE MaKhachHang = ?";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maKhachHang);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new KhachHang(
                    rs.getInt("MaKhachHang"),
                    rs.getString("HoTen"),
                    rs.getString("CCCD"),
                    rs.getString("SDT"),
                    rs.getString("DiaChi")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ============================================================
    // PHẦN 2: CÁC HÀM THÊM SỬA XÓA (CUD)
    // ============================================================

    /**
     * Thêm khách hàng mới vào CSDL.
     * QUAN TRỌNG: Hàm này trả về ID tự tăng vừa được tạo ra.
     * Lý do: Cần ID này để lưu ngay vào bảng DatPhong (Khóa ngoại).
     * * @param kh Đối tượng khách hàng chứa thông tin nhập từ form
     * @return ID (MaKhachHang) vừa tạo, hoặc -1 nếu lỗi
     */
    public int addKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KhachHang(HoTen, CCCD, SDT, DiaChi) VALUES(?,?,?,?)";
        
        // Tham số Statement.RETURN_GENERATED_KEYS dùng để lấy lại ID tự tăng
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getCccd());
            ps.setString(3, kh.getSdt());
            // Kiểm tra null để tránh lỗi CSDL, nếu null thì lưu chuỗi rỗng
            ps.setString(4, kh.getDiaChi() == null ? "" : kh.getDiaChi());
            
            int affectedRows = ps.executeUpdate();
            
            // Nếu insert thành công
            if (affectedRows > 0) {
                // Lấy khóa chính (ID) vừa sinh ra
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Trả về ID (MaKhachHang)
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Trả về -1 báo hiệu thất bại
    }
}