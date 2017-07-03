package edu.tdt.appstudent2.models.hdpt;

import io.realm.RealmObject;

/**
 * Created by Bichan on 7/27/2016.
 */
public class HdptHoatdongItem extends RealmObject{
    private String sTT;
    private String tenSuKien;
    private String thoiGian;
    private String diemRL;

    public String getsTT() {
        return sTT;
    }

    public void setsTT(String sTT) {
        this.sTT = sTT;
    }

    public String getTenSuKien() {
        return tenSuKien;
    }

    public void setTenSuKien(String tenSuKien) {
        this.tenSuKien = tenSuKien;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getDiemRL() {
        return diemRL;
    }

    public void setDiemRL(String diemRL) {
        this.diemRL = diemRL;
    }
}
