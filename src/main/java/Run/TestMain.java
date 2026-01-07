package Run; // <--- QUAN TRỌNG: Dòng này phải khớp với tên package bạn vừa tạo

import Controller.PhongController;
import VIEW.QuanLyPhongPanel;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class TestMain {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        JFrame frame = new JFrame("Phần mềm Quản Lý Khách Sạn - Nhóm 5");
        frame.setSize(1000, 650);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- KHỞI TẠO MVC ---
        QuanLyPhongPanel view = new QuanLyPhongPanel(); 
        new PhongController(view);                      
        
        frame.add(view); 
        frame.setVisible(true); 
    }
}