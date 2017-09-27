package edu.tdt.appstudent2.models.hocphi;

import io.realm.RealmObject;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiThanhtoanItem extends RealmObject{
    private String ngayThanhToan;
    private String hinhThucThanhToan;
    private String soTienThanhToan;


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
