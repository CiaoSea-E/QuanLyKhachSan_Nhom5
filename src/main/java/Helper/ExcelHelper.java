package Helper; // Nhớ dòng này phải trùng với tên package bạn tạo

import java.awt.Component;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

public class ExcelHelper {

    // Hàm Static: Gọi trực tiếp không cần new ExcelHelper()
    public static void exportToExcel(JTable table, Component parentWindow) {
        
        // 1. Dựng hộp thoại chọn file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));
        
        int userSelection = fileChooser.showSaveDialog(parentWindow);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Tự động thêm đuôi .csv nếu người dùng quên
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            // 2. Bắt đầu ghi file
            try (BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileToSave), StandardCharsets.UTF_8))) {
                
                // --- QUAN TRỌNG: Ký tự BOM để Excel Windows hiểu tiếng Việt ---
                bw.write("\uFEFF"); 
                
                TableModel model = table.getModel();

                // 3. Ghi Tiêu đề cột
                for (int i = 0; i < model.getColumnCount(); i++) {
                    bw.write(formatCell(model.getColumnName(i)));
                    if (i < model.getColumnCount() - 1) bw.write(",");
                }
                bw.newLine();

                // 4. Ghi Dữ liệu từng dòng
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        bw.write(formatCell(value));
                        if (j < model.getColumnCount() - 1) bw.write(",");
                    }
                    bw.newLine();
                }

                // 5. Thông báo và mở file
                int confirm = JOptionPane.showConfirmDialog(parentWindow, 
                        "Xuất file thành công! Bạn có muốn mở ngay không?", 
                        "Hoàn tất", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(fileToSave);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentWindow, "Lỗi khi xuất file: " + ex.getMessage());
            }
        }
    }
    
    // Hàm phụ: Xử lý dữ liệu cell (nếu có dấu phẩy thì phải đặt trong ngoặc kép)
    private static String formatCell(Object value) {
        if (value == null) return "";
        String data = value.toString();
        // Nếu dữ liệu chứa dấu phẩy, ngoặc kép hoặc xuống dòng -> Phải bọc trong ngoặc kép
        if (data.contains(",") || data.contains("\"") || data.contains("\n")) {
            data = data.replace("\"", "\"\""); // Double quote để escape
            data = "\"" + data + "\"";
        }
        return data;
    }
}