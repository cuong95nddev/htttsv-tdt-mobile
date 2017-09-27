package edu.tdt.appstudent2.models.tkb;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Bichan on 7/18/2016.
 */
public class TkbMonhocItem extends RealmObject{
    private String maMH;
    private String tenMH;
    private String nhom;
    private String to;
    private RealmList<TkbLichItem> tkbLichItems;

    public String getMaMH() {
        return maMH;
    }

    public void setMaMH(String maMH) {
        this.maMH = maMH;
    }

    public String getTenMH() {
        return tenMH;
    }

    public void setTenMH(String tenMH) {
        this.tenMH = tenMH;
    }

    public String getNhom() {
        return nhom;
    }

    public void setNhom(String nhom) {
        this.nhom = nhom;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public RealmList<TkbLichItem> getTkbLichItems() {
        if(tkbLichItems == null){
            tkbLichItems = new RealmList<TkbLichItem>();
        }
        return tkbLichItems;
    }

    public void setTkbLichItems(RealmList<TkbLichItem> tkbLichItems) {
        this.tkbLichItems = tkbLichItems;
    }

    @Override
    public String toString() {
        return maMH + " | " + tenMH;
    }
}
