package edu.tdt.appstudent2.models.diem;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/17/2016.
 */
public class Diem extends RealmObject{
    @PrimaryKey
    private String id;
    private RealmList<DiemItem> diemItems = new RealmList<DiemItem>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<DiemItem> getDiemItems() {
        return diemItems;
    }

    public void setDiemItems(RealmList<DiemItem> diemItems) {
        this.diemItems = diemItems;
    }
}
