package edu.tdt.appstudent2.models.thongtin;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/17/2016.
 */
public class Thongtin extends RealmObject{
    @PrimaryKey
    private String id;
    private RealmList<ThongtinItem> syll = new RealmList<ThongtinItem>();
    private RealmList<ThongtinItem> nt = new RealmList<ThongtinItem>();
    private RealmList<ThongtinItem> hsts = new RealmList<ThongtinItem>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<ThongtinItem> getSyll() {
        return syll;
    }

    public void setSyll(RealmList<ThongtinItem> syll) {
        this.syll = syll;
    }

    public RealmList<ThongtinItem> getNt() {
        return nt;
    }

    public void setNt(RealmList<ThongtinItem> nt) {
        this.nt = nt;
    }

    public RealmList<ThongtinItem> getHsts() {
        return hsts;
    }

    public void setHsts(RealmList<ThongtinItem> hsts) {
        this.hsts = hsts;
    }
}
