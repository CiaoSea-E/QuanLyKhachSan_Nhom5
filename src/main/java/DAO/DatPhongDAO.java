/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.DatPhong;
import Database.DBConnect;
import java.sql.*;
import java.util.ArrayList; // Import thêm cái này cho chắc
import java.util.List;

public class DatPhongDAO {

    public boolean insertDatPhong(DatPhong dp) {
        String sql = "INSERT INTO DatPhong(MaNhanVien, MaKhachHang, MaPhong, NgayDat, NgayCheckIn, NgayCheckOut, TienDatCoc, TrangThai) "
                + "VALUES(?,?,?, GETDATE(), ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dp.getMaNhanVien());
            ps.setInt(2, dp.getMaKhachHang());
            ps.setInt(3, dp.getMaPhong());
            ps.setTimestamp(4, new java.sql.Timestamp(dp.getNgayCheckIn().getTime()));
            ps.setTimestamp(5, new java.sql.Timestamp(dp.getNgayCheckOut().getTime()));
            ps.setDouble(6, dp.getTienDatCoc());
            ps.setString(7, dp.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm lấy danh sách đặt phòng kèm Tên Khách và Số Phòng
    public List<String[]> getAllDatPhong() {
        List<String[]> list = new ArrayList<>();

        String sql = "SELECT dp.MaDatPhong, kh.HoTen, p.SoPhong, dp.NgayCheckIn, dp.NgayCheckOut, dp.TrangThai "
                + "FROM DatPhong dp "
                + "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang "
                + "JOIN Phong p ON dp.MaPhong = p.MaPhong "
                + "ORDER BY dp.MaDatPhong DESC";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String[] row = new String[6];
                row[0] = String.valueOf(rs.getInt("MaDatPhong"));
                row[1] = rs.getString("HoTen");   
                row[2] = rs.getString("SoPhong"); 
                row[3] = String.valueOf(rs.getTimestamp("NgayCheckIn"));
                row[4] = String.valueOf(rs.getTimestamp("NgayCheckOut"));
                row[5] = rs.getString("TrangThai");
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean huyDatPhong(int maDatPhong) {
        String sqlUpdateDon = "UPDATE DatPhong SET TrangThai = N'Đã hủy' WHERE MaDatPhong = ?";
        String sqlUpdatePhong = "UPDATE Phong SET TrangThai = N'Trống' WHERE MaPhong = (SELECT MaPhong FROM DatPhong WHERE MaDatPhong = ?)"; 
        
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false); // Transaction

            try (PreparedStatement ps1 = conn.prepareStatement(sqlUpdateDon)) {
                ps1.setInt(1, maDatPhong);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdatePhong)) {
                ps2.setInt(1, maDatPhong);
                ps2.executeUpdate();
            }

            conn.commit(); 
            return true;
        } catch (Exception e) {
            try { if(conn != null) conn.rollback(); } catch(Exception ex){}
            e.printStackTrace();
        } finally {
            try { if(conn != null) conn.setAutoCommit(true); conn.close(); } catch(Exception ex){}
        }
        return false;
    }

    // 1. Hàm lấy thông tin chi tiết của 1 đơn đặt phòng (để hiện lên form sửa)
    public DatPhong getDatPhongById(int maDatPhong) {
        String sql = "SELECT * FROM DatPhong WHERE MaDatPhong = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maDatPhong);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                DatPhong dp = new DatPhong();
                dp.setMaDatPhong(rs.getInt("MaDatPhong"));
                dp.setMaNhanVien(rs.getInt("MaNhanVien"));
                dp.setMaKhachHang(rs.getInt("MaKhachHang"));
                dp.setMaPhong(rs.getInt("MaPhong"));
                dp.setNgayDat(rs.getTimestamp("NgayDat"));
                dp.setNgayCheckIn(rs.getTimestamp("NgayCheckIn"));
                dp.setNgayCheckOut(rs.getTimestamp("NgayCheckOut"));
                dp.setTienDatCoc(rs.getDouble("TienDatCoc"));
                dp.setTrangThai(rs.getString("TrangThai"));
                return dp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. Hàm thực hiện lệnh UPDATE xuống cơ sở dữ liệu
    public boolean updateDatPhong(DatPhong dp) {
        String sql = "UPDATE DatPhong SET MaPhong=?, NgayCheckIn=?, NgayCheckOut=?, TienDatCoc=? WHERE MaDatPhong=?";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dp.getMaPhong());
            ps.setTimestamp(2, new java.sql.Timestamp(dp.getNgayCheckIn().getTime()));
            ps.setTimestamp(3, new java.sql.Timestamp(dp.getNgayCheckOut().getTime()));
            ps.setDouble(4, dp.getTienDatCoc());
            ps.setInt(5, dp.getMaDatPhong());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- 3. HÀM TÌM KIẾM THEO TRẠNG THÁI (ĐÃ BỔ SUNG) ---
    public List<String[]> timKiemTheoTrangThai(String trangThaiCanTim) {
        List<String[]> list = new ArrayList<>();
        
        // Nếu chọn "Tất cả" thì lấy hết
        if (trangThaiCanTim.equals("Tất cả")) {
            return getAllDatPhong();
        }
        
        // Ngược lại thì lọc WHERE TrangThai = ?
        String sql = "SELECT dp.MaDatPhong, kh.HoTen, p.SoPhong, dp.NgayCheckIn, dp.NgayCheckOut, dp.TrangThai "
                + "FROM DatPhong dp "
                + "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang "
                + "JOIN Phong p ON dp.MaPhong = p.MaPhong "
                + "WHERE dp.TrangThai = ? "
                + "ORDER BY dp.MaDatPhong DESC";

        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThaiCanTim);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String[] row = new String[6];
                row[0] = String.valueOf(rs.getInt("MaDatPhong"));
                row[1] = rs.getString("HoTen");
                row[2] = rs.getString("SoPhong");
                row[3] = String.valueOf(rs.getTimestamp("NgayCheckIn"));
                row[4] = String.valueOf(rs.getTimestamp("NgayCheckOut"));
                row[5] = rs.getString("TrangThai");
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    // 3. Hàm lấy giá phòng dựa vào Mã Đặt Phòng (để tính tiền)
    public double getGiaPhongByMaDatPhong(int maDatPhong) {
        double gia = 0;
        // Logic: Từ DatPhong -> Phong -> LoaiPhong -> lấy GiaTheoNgay
        String sql = "SELECT lp.GiaTheoNgay " +
                     "FROM DatPhong dp " +
                     "JOIN Phong p ON dp.MaPhong = p.MaPhong " +
                     "JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " +
                     "WHERE dp.MaDatPhong = ?";
        try (Connection conn = Database.DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maDatPhong);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                gia = rs.getDouble("GiaTheoNgay");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gia;
    }

    // 4. HÀM TRẢ PHÒNG (CHECK-OUT) & THANH TOÁN
    public boolean traPhong(int maDatPhong, double tongTienThucTe) {
        // Cập nhật Đơn: Lưu tổng tiền, đổi trạng thái thành "Đã trả"
        String sqlUpdateDon = "UPDATE DatPhong SET TrangThai = N'Đã trả', TongTien = ? WHERE MaDatPhong = ?";
        
        // Cập nhật Phòng: Trả về trạng thái "Trống"
        String sqlUpdatePhong = "UPDATE Phong SET TrangThai = N'Trống' WHERE MaPhong = (SELECT MaPhong FROM DatPhong WHERE MaDatPhong = ?)";
        
        Connection conn = null;
        try {
            conn = Database.DBConnect.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction (để đảm bảo cả 2 lệnh cùng chạy)

            // B1. Cập nhật đơn
            try (PreparedStatement ps1 = conn.prepareStatement(sqlUpdateDon)) {
                ps1.setDouble(1, tongTienThucTe);
                ps1.setInt(2, maDatPhong);
                ps1.executeUpdate();
            }

            // B2. Cập nhật phòng
            try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdatePhong)) {
                ps2.setInt(1, maDatPhong);
                ps2.executeUpdate();
            }

            conn.commit(); // Chốt lưu thay đổi
            return true;
        } catch (Exception e) {
            try { if(conn != null) conn.rollback(); } catch(Exception ex){}
            e.printStackTrace();
        } finally {
            try { if(conn != null) conn.setAutoCommit(true); conn.close(); } catch(Exception ex){}
        }
        return false;
    }
    // 5. HÀM NHẬN PHÒNG (CHECK-IN)
    public boolean nhanPhong(int maDatPhong) {
        String sql = "UPDATE DatPhong SET TrangThai = N'Đang ở' WHERE MaDatPhong = ?";
        try (java.sql.Connection conn = Database.DBConnect.getConnection(); 
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDatPhong);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // 6. HÀM TÌM KIẾM THEO NGÀY CHECK-IN
    public java.util.List<String[]> timKiemTheoNgay(java.util.Date ngayCanTim) {
        java.util.List<String[]> list = new java.util.ArrayList<>();
        
        // SQL: So sánh phần NGÀY (DATE) của NgayCheckIn
        // Lưu ý: CAST(dp.NgayCheckIn AS DATE) giúp bỏ qua phần giờ phút giây
        String sql = "SELECT dp.MaDatPhong, kh.HoTen, p.SoPhong, dp.NgayCheckIn, dp.NgayCheckOut, dp.TrangThai "
                   + "FROM DatPhong dp "
                   + "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang "
                   + "JOIN Phong p ON dp.MaPhong = p.MaPhong "
                   + "WHERE CAST(dp.NgayCheckIn AS DATE) = ? " 
                   + "ORDER BY dp.MaDatPhong DESC";

        try (java.sql.Connection conn = Database.DBConnect.getConnection(); 
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Chuyển java.util.Date sang java.sql.Date
            ps.setDate(1, new java.sql.Date(ngayCanTim.getTime()));
            
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] row = new String[6];
                row[0] = String.valueOf(rs.getInt("MaDatPhong"));
                row[1] = rs.getString("HoTen");
                row[2] = rs.getString("SoPhong");
                row[3] = String.valueOf(rs.getTimestamp("NgayCheckIn"));
                row[4] = String.valueOf(rs.getTimestamp("NgayCheckOut"));
                row[5] = rs.getString("TrangThai");
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}