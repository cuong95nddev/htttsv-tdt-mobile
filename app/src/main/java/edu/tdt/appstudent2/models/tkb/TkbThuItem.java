package edu.tdt.appstudent2.models.tkb;

import java.util.ArrayList;

/**
 * Created by Bichan on 7/19/2016.
 */
public class TkbThuItem {
    private ArrayList<TkbMonhocShowItem> tkbMonhocShowItems;

    public ArrayList<TkbMonhocShowItem> getTkbMonhocShowItems() {
        if(tkbMonhocShowItems == null)
            tkbMonhocShowItems = new ArrayList<TkbMonhocShowItem>();
        return tkbMonhocShowItems;
    }

    public void setTkbMonhocShowItems(ArrayList<TkbMonhocShowItem> tkbMonhocShowItems) {
        this.tkbMonhocShowItems = tkbMonhocShowItems;
    }
}
