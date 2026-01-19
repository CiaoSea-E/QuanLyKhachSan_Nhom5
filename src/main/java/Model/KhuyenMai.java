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
    //Khai báo các thuộc tính trong database
    private int maGiamGia;
    private String code;
    private String tenSuKien;
    private double giamGia;
    private int loaiGiam;
    private int soLuong;
    private Date ngayBatDau;
    private Date ngayKetthuc;
    private String trangThai;
    //constr ko tham số để tạo object rỗng

    public KhuyenMai() {
    }
    //constr đầy đủ để nạp nhanh duw liệu

    public KhuyenMai(int maGiamGia, String code, String tenSK, double giamGia, int loaiGiam, int soLuong, Date ngayBatDau, Date ngayKetthuc, String trangThai) {
        this.maGiamGia = maGiamGia;
        this.code = code;
        this.tenSuKien = tenSuKien;
        this.giamGia = giamGia;
        this.loaiGiam = loaiGiam;
        this.soLuong = soLuong;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetthuc = ngayKetthuc;
        this.trangThai = trangThai;
    }

    public int getMaGiamGia() {
        return maGiamGia;
    }

    public void setMaGiamGia(int maGiamGia) {
        this.maGiamGia = maGiamGia;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTenSuKien() {
        return tenSuKien;
    }

    public void setTenSuKien(String tenSuKien) {
        this.tenSuKien = tenSuKien;
    }

    public double getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(double giamGia) {
        this.giamGia = giamGia;
    }

    public int getLoaiGiam() {
        return loaiGiam;
    }

    public void setLoaiGiam(int loaiGiam) {
        this.loaiGiam = loaiGiam;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetthuc() {
        return ngayKetthuc;
    }

    public void setNgayKetthuc(Date ngayKetthuc) {
        this.ngayKetthuc = ngayKetthuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    //Hàm hiển thị giá trị giảm giá 
    public String getHienThiGiamGia(){
    if (loaiGiam == 1 ) return (int) giamGia + " %";
    return String.format("% ,.0f VNĐ", giamGia);
    }
}
