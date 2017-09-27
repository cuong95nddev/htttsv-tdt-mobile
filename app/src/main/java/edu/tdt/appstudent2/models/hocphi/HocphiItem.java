package edu.tdt.appstudent2.models.hocphi;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiItem extends RealmObject {
    @PrimaryKey
    private String id;
    private String idHocKy;
    private String noHocKyTruoc;
    private String hocPhiHocKy;
    private String mienGiam;
    private String hocPhiPhaiNop;
    private String hocPhiDaNop;
    private String hocPhiConPhaiNop;
    private String ngayCapNhap;

    private RealmList<HocphiThanhtoanItem> hocphiThanhtoanItems;

    private RealmList<HocphiChitiet> hocphiChitiets = new RealmList<HocphiChitiet>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdHocKy() {
        return idHocKy;
    }

    public void setIdHocKy(String idHocKy) {
        this.idHocKy = idHocKy;
    }

    public String getNoHocKyTruoc() {
        return noHocKyTruoc;
    }

    public void setNoHocKyTruoc(String noHocKyTruoc) {
        this.noHocKyTruoc = noHocKyTruoc;
    }

    public String getHocPhiHocKy() {
        return hocPhiHocKy;
    }

    public void setHocPhiHocKy(String hocPhiHocKy) {
        this.hocPhiHocKy = hocPhiHocKy;
    }

    public String getMienGiam() {
        return mienGiam;
    }

    public void setMienGiam(String mienGiam) {
        this.mienGiam = mienGiam;
    }

    public String getHocPhiPhaiNop() {
        return hocPhiPhaiNop;
    }

    public void setHocPhiPhaiNop(String hocPhiPhaiNop) {
        this.hocPhiPhaiNop = hocPhiPhaiNop;
    }

    public String getHocPhiDaNop() {
        return hocPhiDaNop;
    }

    public void setHocPhiDaNop(String hocPhiDaNop) {
        this.hocPhiDaNop = hocPhiDaNop;
    }

    public String getHocPhiConPhaiNop() {
        return hocPhiConPhaiNop;
    }

    public void setHocPhiConPhaiNop(String hocPhiConPhaiNop) {
        this.hocPhiConPhaiNop = hocPhiConPhaiNop;
    }

    public String getNgayCapNhap() {
        return ngayCapNhap;
    }

    public void setNgayCapNhap(String ngayCapNhap) {
        this.ngayCapNhap = ngayCapNhap;
    }

    public RealmList<HocphiThanhtoanItem> getHocphiThanhtoanItems() {
        if(hocphiThanhtoanItems == null)
            hocphiThanhtoanItems = new RealmList<>();
        return hocphiThanhtoanItems;
    }

    public void setHocphiThanhtoanItems(RealmList<HocphiThanhtoanItem> hocphiThanhtoanItems) {
        this.hocphiThanhtoanItems = hocphiThanhtoanItems;
    }

    public RealmList<HocphiChitiet> getHocphiChitiets() {
        return hocphiChitiets;
    }

    public void setHocphiChitiets(RealmList<HocphiChitiet> hocphiChitiets) {
        this.hocphiChitiets = hocphiChitiets;
    }
}
