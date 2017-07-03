package edu.tdt.appstudent2.models.thongtin;

import io.realm.RealmObject;

/**
 * Created by Bichan on 7/17/2016.
 */
public class ThongtinItem extends RealmObject{
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
