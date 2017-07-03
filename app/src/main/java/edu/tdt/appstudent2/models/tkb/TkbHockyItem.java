package edu.tdt.appstudent2.models.tkb;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/18/2016.
 */
public class TkbHockyItem extends RealmObject{
    @PrimaryKey
    private String id;
    private String tenHocKy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenHocKy() {
        return tenHocKy;
    }

    public void setTenHocKy(String tenHocKy) {
        this.tenHocKy = tenHocKy;
    }
}
