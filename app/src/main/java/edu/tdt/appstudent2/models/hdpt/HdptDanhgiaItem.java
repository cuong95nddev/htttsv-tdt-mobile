package edu.tdt.appstudent2.models.hdpt;

import io.realm.RealmObject;

/**
 * Created by Bichan on 7/27/2016.
 */
public class HdptDanhgiaItem extends RealmObject{
    private String sTT;
    private String noiDung;
    private String ketQua;
    private String diem;

    public String getsTT() {
        return sTT;
    }

    public void setsTT(String sTT) {
        this.sTT = sTT;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getKetQua() {
        return ketQua;
    }

    public void setKetQua(String ketQua) {
        this.ketQua = ketQua;
    }

    public String getDiem() {
        return diem;
    }

    public void setDiem(String diem) {
        this.diem = diem;
    }
}
