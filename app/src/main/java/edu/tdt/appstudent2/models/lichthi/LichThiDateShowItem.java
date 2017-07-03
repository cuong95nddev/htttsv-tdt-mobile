package edu.tdt.appstudent2.models.lichthi;

import com.brandongogetap.stickyheaders.exposed.StickyHeader;

/**
 * Created by cuong on 5/3/2017.
 */

public class LichThiDateShowItem implements StickyHeader{
    private String day;
    private String date;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
