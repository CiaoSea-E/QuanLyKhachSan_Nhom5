/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import java.util.Date;
/**
 *
 * @author TUF GAMING
 */
public class KhuyenMai {
    private String maKM;
    private String tenKM;
    private double giamGia;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private String trangThai;

    public KhuyenMai() {
    }

    public KhuyenMai(String maKM, String tenKM, double giamGia, Date ngayBatDau, Date ngayKetThuc, String trangThai) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.giamGia = giamGia;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    public String getMaKM() {
        return maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public double getGiamGia() {
        return giamGia;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }
    @Override
    public String toString() {
        return maKM + " - " + tenKM;
    }
}
