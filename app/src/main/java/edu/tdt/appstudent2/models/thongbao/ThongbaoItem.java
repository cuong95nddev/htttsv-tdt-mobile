package edu.tdt.appstudent2.models.thongbao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/15/2016.
 */
public class ThongbaoItem extends RealmObject {
    @PrimaryKey
    private String id;
    private String idDonVi;
    private String title;
    private boolean isNew = false;
    private String date;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id + "-" + idDonVi;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdDonVi() {
        return idDonVi;
    }

    public void setIdDonVi(String idDonVi) {
        this.idDonVi = idDonVi;
    }
}
