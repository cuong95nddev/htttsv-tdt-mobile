package edu.tdt.appstudent2.models.tkb;

import io.realm.RealmObject;

/**
 * Created by Bichan on 7/18/2016.
 */
public class TkbLichItem extends RealmObject{
    private String tiet;
    private String thu;
    private String phong;
    private String tuan;

    public String getTiet() {
        return tiet;
    }

    public void setTiet(String tiet) {
        this.tiet = tiet;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getPhong() {
        return phong;
    }

    public void setPhong(String phong) {
        this.phong = phong;
    }

    public String getTuan() {
        return tuan;
    }

    public void setTuan(String tuan) {
        this.tuan = tuan;
    }

    @Override
    public String toString() {
        return tiet + " | " + thu + " | " + tuan + " | " + phong;
    }
}
