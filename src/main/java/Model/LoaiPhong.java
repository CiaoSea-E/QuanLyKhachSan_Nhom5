package Model;

public class LoaiPhong {
    private int maLoaiPhong;
    private String tenLoai;
    private double giaTheoNgay; // Hoặc BigDecimal nếu muốn chuẩn tiền tệ

    public LoaiPhong() {
    }

    public LoaiPhong(int maLoaiPhong, String tenLoai, double giaTheoNgay) {
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoai = tenLoai;
        this.giaTheoNgay = giaTheoNgay;
    }

    public int getMaLoaiPhong() {
        return maLoaiPhong;
    }

    public void setMaLoaiPhong(int maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public double getGiaTheoNgay() {
        return giaTheoNgay;
    }

    public void setGiaTheoNgay(double giaTheoNgay) {
        this.giaTheoNgay = giaTheoNgay;
    }

    // QUAN TRỌNG: Để ComboBox hiện tên loại phòng (thay vì mã)
    @Override
    public String toString() {
        return tenLoai; 
    }
}