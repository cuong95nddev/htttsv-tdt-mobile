package edu.tdt.appstudent2.models.tkb;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/18/2016.
 */
public class TkbItem extends RealmObject{
    @PrimaryKey
    private String idHocKy;
    private String dateStart;
    private RealmList<TkbMonhocItem> tkbMonhocItems;

    public String getIdHocKy() {
        return idHocKy;
    }

    public void setIdHocKy(String idHocKy) {
        this.idHocKy = idHocKy;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public RealmList<TkbMonhocItem> getTkbMonhocItems() {
        if(tkbMonhocItems == null)
            tkbMonhocItems = new RealmList<TkbMonhocItem>();
        return tkbMonhocItems;
    }

    public void setTkbMonhocItems(RealmList<TkbMonhocItem> tkbMonhocItems) {
        this.tkbMonhocItems = tkbMonhocItems;
    }
}
