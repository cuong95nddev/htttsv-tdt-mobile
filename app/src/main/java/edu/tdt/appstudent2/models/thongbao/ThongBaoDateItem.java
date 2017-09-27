package edu.tdt.appstudent2.models.thongbao;

import com.brandongogetap.stickyheaders.exposed.StickyHeader;

/**
 * Created by bichan on 4/11/2017.
 */

public class ThongBaoDateItem implements StickyHeader {
    private String title;

    public ThongBaoDateItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
