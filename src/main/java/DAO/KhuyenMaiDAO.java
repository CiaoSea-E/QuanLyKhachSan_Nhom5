package DAO;

import Database.DBConnect;
import Model.KhuyenMai;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp xử lý dữ liệu cho chức năng Khuyến Mãi (Mã giảm giá)
 * Tương tác trực tiếp với bảng MaGiamGia trong CSDL.
 */
public class KhuyenMaiDAO {

    // ============================================================
    // PHẦN 1: CÁC HÀM LẤY DỮ LIỆU (READ / SEARCH)
    // ============================================================

    /**
     * Lấy toàn bộ danh sách mã giảm giá.
     */
    public List<KhuyenMai> getAll() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM MaGiamGia ORDER BY MaGiamGia DESC";

        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                // Gọi hàm phụ tên tiếng Việt ngắn gọn
                list.add(taoKhuyenMai(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tìm kiếm và Lọc danh sách khuyến mãi đa năng.
     */
    public List<KhuyenMai> timKiemVaLoc(String tuKhoa, int loaiGiam) {
        List<KhuyenMai> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM MaGiamGia WHERE 1=1 ");
        
        if (!tuKhoa.isEmpty()) {
            sql.append("AND (Code LIKE ? OR TenSuKien LIKE ?) ");
        }
        if (loaiGiam != -1) {
            sql.append("AND LoaiGiam = ? ");
        }
        sql.append("ORDER BY MaGiamGia DESC");

        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int index = 1;
            
            if (!tuKhoa.isEmpty()) {
                String key = "%" + tuKhoa + "%";
                ps.setString(index++, key);
                ps.setString(index++, key);
            }
            if (loaiGiam != -1) {
                ps.setInt(index++, loaiGiam);
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(taoKhuyenMai(rs)); // Tái sử dụng hàm
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Kiểm tra trùng Mã Code (Validate)
     */
    public boolean checkTrung(String code) {
        String sql = "SELECT COUNT(*) FROM MaGiamGia WHERE Code = ?";
        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ============================================================
    // PHẦN 2: CÁC HÀM THAY ĐỔI DỮ LIỆU (INSERT / UPDATE / DELETE)
    // ============================================================

    public boolean insert(KhuyenMai km) {
        String sql = "INSERT INTO MaGiamGia(Code, TenSuKien, GiamGia, LoaiGiam, SoLuong, NgayBatDau, NgayKetThuc, TrangThai) "
                   + "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            // Gọi hàm phụ set tham số để code gọn hơn (nếu muốn tách tiếp)
            // Nhưng ở đây viết trực tiếp cho rõ nghĩa cũng được
            ps.setString(1, km.getCode());
            ps.setString(2, km.getTenSuKien());
            ps.setDouble(3, km.getGiamGia());
            ps.setInt(4, km.getLoaiGiam());
            ps.setInt(5, km.getSoLuong());
            ps.setTimestamp(6, new java.sql.Timestamp(km.getNgayBatDau().getTime()));
            ps.setTimestamp(7, new java.sql.Timestamp(km.getNgayKetthuc().getTime()));
            ps.setString(8, km.getTrangThai());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(KhuyenMai km) {
        String sql = "UPDATE MaGiamGia SET TenSuKien=?, GiamGia=?, LoaiGiam=?, SoLuong=?, NgayBatDau=?, NgayKetThuc=?, TrangThai=? "
                   + "WHERE Code=?";
        
        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, km.getTenSuKien());
            ps.setDouble(2, km.getGiamGia());
            ps.setInt(3, km.getLoaiGiam());
            ps.setInt(4, km.getSoLuong());
            ps.setTimestamp(5, new java.sql.Timestamp(km.getNgayBatDau().getTime()));
            ps.setTimestamp(6, new java.sql.Timestamp(km.getNgayKetthuc().getTime()));
            ps.setString(7, km.getTrangThai());
            ps.setString(8, km.getCode());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String code) {
        String sql = "DELETE FROM MaGiamGia WHERE Code = ?";
        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, code);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // ============================================================
    // PHẦN 3: HÀM PHỤ TRỢ (HELPER METHOD)
    // ============================================================

    /**
     * Chuyển đổi dữ liệu từ ResultSet (SQL) thành Object KhuyenMai (Java).
     * Tên hàm ngắn gọn, dễ hiểu.
     */
    private KhuyenMai taoKhuyenMai(ResultSet rs) throws SQLException {
        KhuyenMai km = new KhuyenMai();
        km.setMaGiamGia(rs.getInt("MaGiamGia"));
        km.setCode(rs.getString("Code"));
        km.setTenSuKien(rs.getString("TenSuKien"));
        km.setGiamGia(rs.getDouble("GiamGia"));
        km.setLoaiGiam(rs.getInt("LoaiGiam"));
        km.setSoLuong(rs.getInt("SoLuong"));
        km.setNgayBatDau(rs.getTimestamp("NgayBatDau"));
        km.setNgayKetthuc(rs.getTimestamp("NgayKetThuc"));
        km.setTrangThai(rs.getString("TrangThai"));
        return km;
    }
}