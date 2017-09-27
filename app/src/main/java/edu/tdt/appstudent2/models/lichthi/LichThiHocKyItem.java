package edu.tdt.appstudent2.models.lichthi;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cuong on 5/3/2017.
 */

public class LichThiHocKyItem extends RealmObject {
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
