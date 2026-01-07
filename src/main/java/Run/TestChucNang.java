package Run;

// SỬA LỖI Ở ĐÂY: View viết thường/hoa đầu dòng, và thêm dấu chấm phẩy
import VIEW.DialogDatPhong; 
import javax.swing.UIManager;

/**
 *
 * @author TUF GAMING
 */
public class TestChucNang {
    
    public static void main(String[] args) {
        // Làm đẹp giao diện (Optional)
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) {}

        // Chạy thử Dialog
        // Vì đã import bên trên rồi nên ở đây chỉ cần gọi tên Class là được
        new DialogDatPhong(null, true).setVisible(true);
    }
}