package edu.tdt.appstudent2.models.thongbao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/15/2016.
 */
public class DonviItem  extends RealmObject {
    @PrimaryKey
    private String id;
    private String title;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
