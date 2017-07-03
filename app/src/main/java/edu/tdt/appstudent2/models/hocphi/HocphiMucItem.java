package edu.tdt.appstudent2.models.hocphi;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiMucItem {
    private String title;
    private String money;

    public HocphiMucItem(String title, String money) {
        this.title = title;
        this.money = money;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
