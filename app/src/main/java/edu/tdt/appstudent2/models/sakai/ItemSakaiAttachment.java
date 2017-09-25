package edu.tdt.appstudent2.models.sakai;

import io.realm.RealmObject;

/**
 * Created by bichan on 9/25/17.
 */

public class ItemSakaiAttachment extends RealmObject{
    private String name;
    private String url;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
