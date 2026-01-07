package Model;

import java.util.Date;

public class DatPhong {
    private int maDatPhong;
    private int maNhanVien;
    private int maKhachHang;
    private int maPhong;
    private Date ngayDat;       // Ngày tạo phiếu
    private Date ngayCheckIn;   // Ngày khách đến
    private Date ngayCheckOut;  // Ngày khách đi
    private double tienDatCoc;
    private String trangThai;   // "Đã đặt", "Đang ở", "Đã trả"...

    // 1. Constructor rỗng (bắt buộc phải có)
    public DatPhong() {
    }

    // 2. Constructor đầy đủ
    public DatPhong(int maDatPhong, int maNhanVien, int maKhachHang, int maPhong, Date ngayDat, Date ngayCheckIn, Date ngayCheckOut, double tienDatCoc, String trangThai) {
        this.maDatPhong = maDatPhong;
        this.maNhanVien = maNhanVien;
        this.maKhachHang = maKhachHang;
        this.maPhong = maPhong;
        this.ngayDat = ngayDat;
        this.ngayCheckIn = ngayCheckIn;
        this.ngayCheckOut = ngayCheckOut;
        this.tienDatCoc = tienDatCoc;
        this.trangThai = trangThai;
    }
    //HÂM LUU TONG TIỀN THANH TOÁN
    private double tongTien; 

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    // 3. Getter và Setter (Dùng để lấy và gán dữ liệu)
    public int getMaDatPhong() {
        return maDatPhong;
    }

    public void setMaDatPhong(int maDatPhong) {
        this.maDatPhong = maDatPhong;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public int getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(int maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public int getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(int maPhong) {
        this.maPhong = maPhong;
    }

    public Date getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(Date ngayDat) {
        this.ngayDat = ngayDat;
    }

    public Date getNgayCheckIn() {
        return ngayCheckIn;
    }

    public void setNgayCheckIn(Date ngayCheckIn) {
        this.ngayCheckIn = ngayCheckIn;
    }

    public Date getNgayCheckOut() {
        return ngayCheckOut;
    }

    public void setNgayCheckOut(Date ngayCheckOut) {
        this.ngayCheckOut = ngayCheckOut;
    }

    public double getTienDatCoc() {
        return tienDatCoc;
    }

    public void setTienDatCoc(double tienDatCoc) {
        this.tienDatCoc = tienDatCoc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}