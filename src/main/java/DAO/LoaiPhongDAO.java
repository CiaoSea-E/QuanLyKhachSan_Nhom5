package DAO;

import Database.DBConnect;
import Model.LoaiPhong;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiPhongDAO {

    // Lấy tất cả loại phòng hiện có
    public List<LoaiPhong> getAllLoaiPhong() {
        List<LoaiPhong> list = new ArrayList<>();
        String sql = "SELECT * FROM LoaiPhong";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(new LoaiPhong(
                    rs.getInt("MaLoaiPhong"),
                    rs.getString("TenLoai"),
                    rs.getDouble("GiaTheoNgay")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}