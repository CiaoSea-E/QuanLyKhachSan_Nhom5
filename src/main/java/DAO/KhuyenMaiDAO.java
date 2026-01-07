/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Database.DBConnect;
import Model.KhuyenMai;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TUF GAMING
 */
public class KhuyenMaiDAO {

    public List<KhuyenMai> getAllKhuyenMai() {
        List<KhuyenMai> list = new ArrayList<>();

        String sql = "SELECT * FROM KhuyenMai WHERE TrangThai != N'Đã xóa'";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                KhuyenMai km = new KhuyenMai(
                        rs.getString("MaKM"),
                        rs.getString("TenKM"),
                        rs.getDouble("GiamGia"),
                        rs.getTimestamp("NgayBatDau"),
                        rs.getTimestamp("NgayKetThuc"),
                        rs.getNString("TrangThai")
                );
                list.add(km);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertKhuyenMai(KhuyenMai km) {
        String sql = "INSERT INTO KhuyenMai(MaKM, TenKM, GiamGia, NgayBatDau, NgayKetThuc, TrangThai) VALUES(?,?,?,?,?,?)";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, km.getMaKM());
            ps.setString(2, km.getTenKM());
            ps.setDouble(3, km.getGiamGia());
            //chuyen doi date sql ra java
            ps.setTimestamp(4, new java.sql.Timestamp(km.getNgayBatDau().getTime()));
            ps.setTimestamp(5, new java.sql.Timestamp(km.getNgayKetThuc().getTime()));
            //,,,,,,,
            ps.setString(6, "Đang hoạt động");
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteKhuyenMai(String maKM) {
        // Thay đổi câu lệnh SQL: Dùng DELETE FROM
        String sql = "DELETE FROM KhuyenMai WHERE MaKM=?";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);

            // executeUpdate sẽ trả về số dòng bị xóa. > 0 là thành công.
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkTonTai(String maKM) {
        String sql = "SELECT COUNT(*) FROM KhuyenMai WHERE MaKM = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<KhuyenMai> timKiem(String keyword) {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai WHERE TrangThai != N'Đã xóa' AND (MaKM LIKE ? OR TenKM LIKE ?)";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new KhuyenMai(
                        rs.getString("MaKM"),
                        rs.getString("TenKM"),
                        rs.getDouble("GiamGia"),
                        rs.getTimestamp("NgayBatDau"),
                        rs.getTimestamp("NgayKetThuc"),
                        rs.getNString("TrangThai")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean updateKhuyenMai(KhuyenMai km) {
        // Cập nhật thông tin dựa trên MaKM
        String sql = "UPDATE KhuyenMai SET TenKM=?, GiamGia=?, NgayBatDau=?, NgayKetThuc=?, TrangThai=? WHERE MaKM=?";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, km.getTenKM());
            ps.setDouble(2, km.getGiamGia());
            ps.setTimestamp(3, new java.sql.Timestamp(km.getNgayBatDau().getTime()));
            ps.setTimestamp(4, new java.sql.Timestamp(km.getNgayKetThuc().getTime()));
            ps.setString(5, km.getTrangThai()); // Hoặc cứng là "Đang hoạt động" tùy bạn
            
            ps.setString(6, km.getMaKM()); // Điều kiện WHERE quan trọng nhất
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
