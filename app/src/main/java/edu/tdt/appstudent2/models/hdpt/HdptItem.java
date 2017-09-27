package edu.tdt.appstudent2.models.hdpt;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/27/2016.
 */
public class HdptItem extends RealmObject{
    @PrimaryKey
    private String idHocKy;

    private String diem;

    private RealmList<HdptTagDanhgiaItem> hdptTagDanhgiaItems;
    private RealmList<HdptHoatdongItem> hdptHoatdongItems;
    public String getIdHocKy() {
        return idHocKy;
    }

    public void setIdHocKy(String idHocKy) {
        this.idHocKy = idHocKy;
    }

    public String getDiem() {
        return diem;
    }

    public void setDiem(String diem) {
        this.diem = diem;
    }

    public RealmList<HdptTagDanhgiaItem> getHdptTagDanhgiaItems() {
        if(hdptTagDanhgiaItems == null)
            hdptTagDanhgiaItems = new RealmList<HdptTagDanhgiaItem>();
        return hdptTagDanhgiaItems;
    }

    public RealmList<HdptHoatdongItem> getHdptHoatdongItems() {
        if(hdptHoatdongItems == null)
            hdptHoatdongItems = new RealmList<HdptHoatdongItem>();
        return hdptHoatdongItems;
    }
}
