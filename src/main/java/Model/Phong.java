/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author TUF GAMING
 */
public class Phong {

    private int maPhong;
    private String soPhong;
    private int maLoaiPhong;
    private String trangThai;

    public Phong() {
    }

    public Phong(int maPhong, String soPhong, int maLoaiPhong, String trangThai) {
        this.maPhong = maPhong;
        this.soPhong = soPhong;
        this.maLoaiPhong = maLoaiPhong;
        this.trangThai = trangThai;
    }

    public int getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(int maPhong) {
        this.maPhong = maPhong;
    }

    public String getSoPhong() {
        return soPhong;
    }

    public void setSoPhong(String soPhong) {
        this.soPhong = soPhong;
    }

    public int getMaLoaiPhong() {
        return maLoaiPhong;
    }

    public void setMaLoaiPhong(int maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "Phòng " + soPhong + " - Trạng thái: " + trangThai;
    }
    
}
