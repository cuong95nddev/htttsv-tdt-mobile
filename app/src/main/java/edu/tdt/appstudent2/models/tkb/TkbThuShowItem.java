package edu.tdt.appstudent2.models.tkb;

import com.brandongogetap.stickyheaders.exposed.StickyHeader;

/**
 * Created by Bichan on 7/19/2016.
 */
public class TkbThuShowItem implements StickyHeader {
    private String ten;
    private String color;
    public String getTen() {
        return ten;
    }

    public TkbThuShowItem(String ten, String color) {
        this.ten = ten;
        this.color = color;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getColor() {
        if(color == null)
            color = "#303F9F";
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
