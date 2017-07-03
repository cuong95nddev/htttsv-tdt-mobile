package edu.tdt.appstudent2.models.hocphi;

import io.realm.RealmObject;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiChitiet extends RealmObject {
    private String maMonHoc;
    private String tenMonHoc;
    private String soTien;

    public String getMaMonHoc() {
        return maMonHoc;
    }

    public void setMaMonHoc(String maMonHoc) {
        this.maMonHoc = maMonHoc;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }

    public String getSoTien() {
        return soTien;
    }

    public void setSoTien(String soTien) {
        this.soTien = soTien;
    }
}
