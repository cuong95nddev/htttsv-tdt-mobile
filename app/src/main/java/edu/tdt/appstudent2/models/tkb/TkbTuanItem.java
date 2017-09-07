package edu.tdt.appstudent2.models.tkb;

import java.util.Calendar;

import edu.tdt.appstudent2.utils.StringUtil;

/**
 * Created by bichan on 9/7/17.
 */

public class TkbTuanItem {
    private int tuan;
    private Calendar ngayBatDau;
    private Calendar ngayKetThuc;

    public TkbTuanItem(){
        tuan = 0;
        ngayBatDau = Calendar.getInstance();
        ngayKetThuc = Calendar.getInstance();
    }

    public int getTuan() {
        return tuan;
    }

    public void setTuan(int tuan) {
        this.tuan = tuan;
    }

    public Calendar getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Calendar ngayBatDau) {
        this.ngayBatDau = (Calendar) ngayBatDau.clone();
        this.ngayKetThuc = (Calendar) ngayBatDau.clone();;
        ngayKetThuc.add(Calendar.DAY_OF_YEAR, 7);
    }

    public Calendar getNgayKetThuc() {
        return ngayKetThuc;
    }

    public String getTile(){
        return ""+tuan;
    }

    public String getDate(){
        return StringUtil.getDate(this.ngayBatDau.getTimeInMillis(), "dd/MM");
    }
}
