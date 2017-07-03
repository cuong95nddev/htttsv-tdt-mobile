package edu.tdt.appstudent2.models.trangchu;

/**
 * Created by Bichan on 7/18/2016.
 */
public class TrangchuMenuItem {
    private int tag;
    private String title;
    private String info;
    private int idImage;
    private String color;

    public TrangchuMenuItem(int tag,String title, String info, int idImage, String color) {
        this.title = title;
        this.info = info;
        this.idImage = idImage;
        this.color = color;
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getIdImage() {
        return idImage;
    }

    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
