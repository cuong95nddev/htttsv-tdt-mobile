package edu.tdt.appstudent2.models;

/**
 * Created by Bichan on 7/19/2016.
 */
public class TietHoc {
    public static final TietHoc[] tietHocs = {new TietHoc("1","06:50", "07:35")
            ,new TietHoc("2","07:35", "08:30")
            ,new TietHoc("3","08:30", "09:15")
            ,new TietHoc("4","09:25", "10:10")
            ,new TietHoc("5","10:10", "11:05")
            ,new TietHoc("6","11:05", "11:50")
            ,new TietHoc("7","12:30", "13:15")
            ,new TietHoc("8","13:15", "14:10")
            ,new TietHoc("9","14:10", "14:55")
            ,new TietHoc("10","15:05", "15:50")
            ,new TietHoc("11","15:50", "16:45")
            ,new TietHoc("12","16:45", "17:30")
            ,new TietHoc("13","17:45", "18:30")
            ,new TietHoc("14","18:30", "19:30")
            ,new TietHoc("15","19:30", "20:15")
            ,new TietHoc("16","20:15", "21:00")};

    private String ten;
    private String tgBatDau;
    private String tgKetThuc;

    public TietHoc(String ten, String tgBatDau, String tgKetThuc) {
        this.ten = ten;
        this.tgBatDau = tgBatDau;
        this.tgKetThuc = tgKetThuc;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getTgBatDau() {
        return tgBatDau;
    }

    public void setTgBatDau(String tgBatDau) {
        this.tgBatDau = tgBatDau;
    }

    public String getTgKetThuc() {
        return tgKetThuc;
    }

    public void setTgKetThuc(String tgKetThuc) {
        this.tgKetThuc = tgKetThuc;
    }
}
