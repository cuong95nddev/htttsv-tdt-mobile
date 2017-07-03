package edu.tdt.appstudent2.models.hocphi;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiThanhtoanItem {
    private String ngayThanhToan;
    private String hinhThucThanhToan;
    private String soTienThanhToan;

    public HocphiThanhtoanItem(String ngayThanhToan, String hinhThucThanhToan, String soTienThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
        this.hinhThucThanhToan = hinhThucThanhToan;
        this.soTienThanhToan = soTienThanhToan;
    }

    public String getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(String ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public String getSoTienThanhToan() {
        return soTienThanhToan;
    }

    public void setSoTienThanhToan(String soTienThanhToan) {
        this.soTienThanhToan = soTienThanhToan;
    }
}
