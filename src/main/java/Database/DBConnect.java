/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database;
import java.sql.Connection;
import java.sql.DriverManager;
/**
 *
 * @author TUF GAMING
 */
public class DBConnect {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Cấu hình kết nối SQL Server
            String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyKhachSan_Nhom5;encrypt=true;trustServerCertificate=true;";
            String user = "sa"; 
            String pass = "123"; 

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(dbURL, user, pass);
            System.out.println("Kết nối thành công!");
        } catch (Exception e) {
            System.out.println("Kết nối thất bại!");
            e.printStackTrace();
        }
        return conn;
    }
}
