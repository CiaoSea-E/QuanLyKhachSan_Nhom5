package DAO;

import Model.KhachHang;
import Database.DBConnect; // Import file kết nối của bạn
import java.sql.*;

public class KhachHangDAO {

    // 1. Tìm khách hàng bằng CCCD
    public KhachHang getKhachHangByCCCD(String cccd) {
        String sql = "SELECT * FROM KhachHang WHERE CCCD = ?";
        
        // Dùng try-with-resources để tự động đóng kết nối
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cccd);
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

    // 2. Thêm khách mới và TRẢ VỀ ID VỪA TẠO
    public int addKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KhachHang(HoTen, CCCD, SDT, DiaChi) VALUES(?,?,?,?)";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getCccd());
            ps.setString(3, kh.getSdt());
            ps.setString(4, kh.getDiaChi() == null ? "" : kh.getDiaChi());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Trả về ID vừa sinh ra
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    // --- Thêm vào KhachHangDAO.java ---
    
    public Model.KhachHang getKhachHangById(int maKhachHang) {
        String sql = "SELECT * FROM KhachHang WHERE MaKhachHang = ?";
        try (java.sql.Connection conn = Database.DBConnect.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maKhachHang);
            java.sql.ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Model.KhachHang(
                    rs.getInt("MaKhachHang"),
                    rs.getString("HoTen"),
                    rs.getString("CCCD"),
                    rs.getString("SDT"),
                    rs.getString("DiaChi")
                    // Thêm các trường khác nếu có (ví dụ: GioiTinh)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}