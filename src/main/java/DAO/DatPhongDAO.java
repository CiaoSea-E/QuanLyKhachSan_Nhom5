package DAO;

import Model.DatPhong;
import Database.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp xử lý dữ liệu cho đối tượng Đặt Phòng (Booking)
 * Chứa các câu lệnh SQL để thao tác với bảng DatPhong trong CSDL.
 */
public class DatPhongDAO {

    // ============================================================
    // PHẦN 1: CÁC HÀM LẤY DỮ LIỆU (READ / SEARCH)
    // ============================================================

    /**
     * Lấy danh sách tất cả đơn đặt phòng (Kèm Tên khách và Số phòng)
     * Dùng câu lệnh JOIN để lấy dữ liệu từ 3 bảng: DatPhong, KhachHang, Phong
     */
    public List<String[]> getAllDatPhong() {
        List<String[]> list = new ArrayList<>();

        String sql = "SELECT dp.MaDatPhong, kh.HoTen, p.SoPhong, dp.NgayCheckIn, dp.NgayCheckOut, dp.TrangThai "
                   + "FROM DatPhong dp "
                   + "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang "
                   + "JOIN Phong p ON dp.MaPhong = p.MaPhong "
                   + "ORDER BY dp.MaDatPhong DESC"; // Sắp xếp mới nhất lên đầu

        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {

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

    /**
     * Lấy chi tiết 1 đơn đặt phòng theo ID (Dùng để đổ dữ liệu lên Form Sửa)
     */
    public DatPhong getDatPhongById(int maDatPhong) {
        String sql = "SELECT * FROM DatPhong WHERE MaDatPhong = ?";
        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
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

    /**
     * Lấy giá tiền phòng/ngày của một đơn đặt phòng cụ thể
     * Logic: DatPhong -> Phong -> LoaiPhong -> GiaTheoNgay
     */
    public double getGiaPhongByMaDatPhong(int maDatPhong) {
        double gia = 0;
        String sql = "SELECT lp.GiaTheoNgay " 
                   + "FROM DatPhong dp " 
                   + "JOIN Phong p ON dp.MaPhong = p.MaPhong " 
                   + "JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " 
                   + "WHERE dp.MaDatPhong = ?";
                   
        try (Connection conn = DBConnect.getConnection(); 
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

    // --- CÁC HÀM TÌM KIẾM ---

    public List<String[]> timKiemTheoTrangThai(String trangThaiCanTim) {
        if (trangThaiCanTim.equals("Tất cả")) {
            return getAllDatPhong();
        }
        
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT dp.MaDatPhong, kh.HoTen, p.SoPhong, dp.NgayCheckIn, dp.NgayCheckOut, dp.TrangThai "
                   + "FROM DatPhong dp "
                   + "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang "
                   + "JOIN Phong p ON dp.MaPhong = p.MaPhong "
                   + "WHERE dp.TrangThai = ? "
                   + "ORDER BY dp.MaDatPhong DESC";

        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, trangThaiCanTim);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSetToRow(rs)); // Dùng hàm phụ cho gọn
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String[]> timKiemTheoNgay(java.util.Date ngayCanTim) {
        List<String[]> list = new ArrayList<>();
        // CAST AS DATE: Chỉ so sánh phần ngày, bỏ qua giờ phút giây
        String sql = "SELECT dp.MaDatPhong, kh.HoTen, p.SoPhong, dp.NgayCheckIn, dp.NgayCheckOut, dp.TrangThai "
                   + "FROM DatPhong dp "
                   + "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang "
                   + "JOIN Phong p ON dp.MaPhong = p.MaPhong "
                   + "WHERE CAST(dp.NgayCheckIn AS DATE) = ? " 
                   + "ORDER BY dp.MaDatPhong DESC";

        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(ngayCanTim.getTime()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSetToRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<String[]> timKiemTheoNgayOut(java.util.Date ngayCanTim) {
        List<String[]> list = new ArrayList<>();
        // Tìm kiếm theo ngày Check-out
        String sql = "SELECT dp.MaDatPhong, kh.HoTen, p.SoPhong, dp.NgayCheckIn, dp.NgayCheckOut, dp.TrangThai "
                   + "FROM DatPhong dp "
                   + "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang "
                   + "JOIN Phong p ON dp.MaPhong = p.MaPhong "
                   + "WHERE CAST(dp.NgayCheckOut AS DATE) = ? " 
                   + "ORDER BY dp.MaDatPhong DESC";

        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(ngayCanTim.getTime()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSetToRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ============================================================
    // PHẦN 2: CÁC HÀM THAO TÁC CƠ BẢN (INSERT / UPDATE)
    // ============================================================

    public boolean insertDatPhong(DatPhong dp) {
        String sql = "INSERT INTO DatPhong(MaNhanVien, MaKhachHang, MaPhong, NgayDat, NgayCheckIn, NgayCheckOut, TienDatCoc, TrangThai) "
                   + "VALUES(?,?,?, GETDATE(), ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

    public boolean updateDatPhong(DatPhong dp) {
        String sql = "UPDATE DatPhong SET MaPhong=?, NgayCheckIn=?, NgayCheckOut=?, TienDatCoc=? WHERE MaDatPhong=?";

        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
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

    // ============================================================
    // PHẦN 3: CÁC HÀM NGHIỆP VỤ PHỨC TẠP (TRANSACTION)
    // ============================================================

    /**
     * Check-in: Chỉ cập nhật trạng thái đơn thành 'Đang ở'
     */
    public boolean nhanPhong(int maDatPhong) {
        String sql = "UPDATE DatPhong SET TrangThai = N'Đang ở' WHERE MaDatPhong = ?";
        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maDatPhong);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check-out: Trả phòng và thanh toán
     * Transaction: 
     * 1. Cập nhật trạng thái đơn -> 'Đã trả'
     * 2. Cập nhật trạng thái phòng -> 'Trống'
     */
    public boolean traPhong(int maDatPhong, double tongTien) {
        String sqlUpdateDon = "UPDATE DatPhong SET TrangThai = N'Đã trả' WHERE MaDatPhong = ?";
        String sqlUpdatePhong = "UPDATE Phong SET TrangThai = N'Trống' WHERE MaPhong = (SELECT MaPhong FROM DatPhong WHERE MaDatPhong = ?)";
        
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // Bước 1: Cập nhật đơn
            try (PreparedStatement ps1 = conn.prepareStatement(sqlUpdateDon)) {
                ps1.setInt(1, maDatPhong);
                ps1.executeUpdate();
            }

            // Bước 2: Cập nhật phòng
            try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdatePhong)) {
                ps2.setInt(1, maDatPhong);
                ps2.executeUpdate();
            }

            conn.commit(); // Xác nhận thành công
            return true;
        } catch (Exception e) {
            try { if(conn != null) conn.rollback(); } catch(Exception ex){} // Hoàn tác nếu lỗi
            e.printStackTrace();
        } finally {
            try { if(conn != null) conn.setAutoCommit(true); conn.close(); } catch(Exception ex){}
        }
        return false;
    }

    /**
     * Hủy đặt phòng
     * Transaction:
     * 1. Cập nhật trạng thái đơn -> 'Đã hủy'
     * 2. Trả phòng về trạng thái -> 'Trống' (để khách khác đặt)
     */
    public boolean huyDatPhong(int maDatPhong) {
        String sqlUpdateDon = "UPDATE DatPhong SET TrangThai = N'Đã hủy' WHERE MaDatPhong = ?";
        String sqlUpdatePhong = "UPDATE Phong SET TrangThai = N'Trống' WHERE MaPhong = (SELECT MaPhong FROM DatPhong WHERE MaDatPhong = ?)"; 
        
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

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
    
    // --- HÀM PHỤ TRỢ ---
    private String[] mapResultSetToRow(ResultSet rs) throws SQLException {
        String[] row = new String[6];
        row[0] = String.valueOf(rs.getInt("MaDatPhong"));
        row[1] = rs.getString("HoTen");
        row[2] = rs.getString("SoPhong");
        row[3] = String.valueOf(rs.getTimestamp("NgayCheckIn"));
        row[4] = String.valueOf(rs.getTimestamp("NgayCheckOut"));
        row[5] = rs.getString("TrangThai");
        return row;
    }
}