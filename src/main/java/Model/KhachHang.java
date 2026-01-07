package Model;



public class KhachHang {
    // Chỉ cần khai báo mấy cái cơ bản để chạy được thôi
    private int maKhachHang;
    private String hoTen;
    private String cccd;
    private String sdt;
    private String diaChi;

    public KhachHang() { }

    // Constructor đầy đủ
    public KhachHang(int maKhachHang, String hoTen, String cccd, String sdt, String diaChi) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.cccd = cccd;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    // Getter Setter (Tạo nhanh bằng Alt+Insert)
    public int getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(int maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
}