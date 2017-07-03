package edu.tdt.appstudent2.models.hdpt;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Bichan on 7/27/2016.
 */
public class HdptTagDanhgiaItem extends RealmObject{
    private String title;
    private RealmList<HdptDanhgiaItem> hdptDanhgiaItems;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<HdptDanhgiaItem> getHdptDanhgiaItems() {
        if(hdptDanhgiaItems == null)
            hdptDanhgiaItems = new RealmList<HdptDanhgiaItem>();
        return hdptDanhgiaItems;
    }
}
