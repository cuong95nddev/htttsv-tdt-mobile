package edu.tdt.appstudent2.models.hocphi;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiHeaderItem {
    private String title;
    private String date;

    public HocphiHeaderItem(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
