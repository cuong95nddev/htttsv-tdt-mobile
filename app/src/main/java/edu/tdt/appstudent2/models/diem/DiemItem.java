package edu.tdt.appstudent2.models.diem;

import io.realm.RealmObject;

/**
 * Created by Bichan on 7/17/2016.
 */
public class DiemItem extends RealmObject{
    private String monHocID;
    private String tenMH;
    private String diem1;
    private String diem2;
    private String diemThi1;
    private String diemThi2;
    private String dTB;
    private String soTC;
    private String diem1_1;
    private String ghiChu;

    public String getMonHocID() {
        return monHocID;
    }

    public void setMonHocID(String monHocID) {
        this.monHocID = monHocID;
    }

    public String getTenMH() {
        return tenMH;
    }

    public void setTenMH(String tenMH) {
        this.tenMH = tenMH;
    }

    public String getDiem1() {
        return diem1;
    }

    public void setDiem1(String diem1) {
        this.diem1 = diem1;
    }

    public String getDiem2() {
        return diem2;
    }

    public void setDiem2(String diem2) {
        this.diem2 = diem2;
    }

    public String getDiemThi1() {
        return diemThi1;
    }

    public void setDiemThi1(String diemThi1) {
        this.diemThi1 = diemThi1;
    }

    public String getDiemThi2() {
        return diemThi2;
    }

    public void setDiemThi2(String diemThi2) {
        this.diemThi2 = diemThi2;
    }

    public String getdTB() {
        return dTB;
    }

    public void setdTB(String dTB) {
        this.dTB = dTB;
    }

    public String getSoTC() {
        return soTC;
    }

    public void setSoTC(String soTC) {
        this.soTC = soTC;
    }

    public String getDiem1_1() {
        return diem1_1;
    }

    public void setDiem1_1(String diem1_1) {
        this.diem1_1 = diem1_1;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
