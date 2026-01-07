package DAO;

import Database.DBConnect;
import Model.Phong;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhongDAO {

    // 1. Lấy tất cả danh sách phòng
    public List<Phong> getAllPhong() {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT MaPhong, SoPhong, MaLoaiPhong, TrangThai FROM Phong";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Phong p = new Phong();
                p.setMaPhong(rs.getInt("MaPhong"));
                p.setSoPhong(rs.getString("SoPhong"));
                p.setMaLoaiPhong(rs.getInt("MaLoaiPhong"));
                p.setTrangThai(rs.getString("TrangThai"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm phòng mới
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

    // 3. Cập nhật phòng (Sửa) - NEW
    public boolean updatePhong(Phong p) {
        String sql = "UPDATE Phong SET SoPhong=?, MaLoaiPhong=?, TrangThai=? WHERE MaPhong=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, p.getSoPhong());
            ps.setInt(2, p.getMaLoaiPhong());
            ps.setString(3, p.getTrangThai());
            ps.setInt(4, p.getMaPhong()); // Quan trọng: Phải biết sửa phòng nào dựa trên ID
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. Xóa phòng - NEW
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

    // 5. Tìm kiếm theo Số phòng - NEW
    public List<Phong> timKiemPhong(String tuKhoa) {
        List<Phong> list = new ArrayList<>();
        // Tìm gần đúng (LIKE)
        String sql = "SELECT * FROM Phong WHERE SoPhong LIKE ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + tuKhoa + "%"); // %tuKhoa% để tìm mọi chỗ
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Phong p = new Phong();
                p.setMaPhong(rs.getInt("MaPhong"));
                p.setSoPhong(rs.getString("SoPhong"));
                p.setMaLoaiPhong(rs.getInt("MaLoaiPhong"));
                p.setTrangThai(rs.getString("TrangThai"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    // Hàm cập nhật trạng thái phòng (Ví dụ: Từ "Trống" -> "Đã đặt")
    public boolean updateTrangThaiPhong(int maPhong, String trangThaiMoi) {
        // Câu lệnh SQL update trạng thái
        String sql = "UPDATE Phong SET TrangThai = ? WHERE MaPhong = ?";
        
        try (java.sql.Connection conn = Database.DBConnect.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, trangThaiMoi); // Tham số 1: Trạng thái mới
            ps.setInt(2, maPhong);         // Tham số 2: Mã phòng cần sửa
            
            return ps.executeUpdate() > 0; // Trả về true nếu update thành công
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //loc phong trong
    public List<Model.Phong> getPhongTrongByLoai(int maLoai) {
        List<Model.Phong> list = new ArrayList<>();
        // Chỉ lấy phòng có trạng thái 'Trống' và đúng Loại phòng đã chọn
        String sql = "SELECT * FROM Phong WHERE MaLoaiPhong = ? AND TrangThai = N'Trống'";
        
        try (Connection conn = Database.DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maLoai);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Model.Phong(
                    rs.getInt("MaPhong"),
                    rs.getString("SoPhong"),
                    rs.getInt("MaLoaiPhong"),
                    rs.getString("TrangThai")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    public boolean checkTonTaiSoPhong(String soPhong) {
        // Đếm xem có bao nhiêu phòng trùng tên số phòng này
        String sql = "SELECT COUNT(*) FROM Phong WHERE SoPhong = ?";
        try (java.sql.Connection conn = Database.DBConnect.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, soPhong);
            java.sql.ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Nếu số lượng > 0 nghĩa là đã tồn tại
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
}