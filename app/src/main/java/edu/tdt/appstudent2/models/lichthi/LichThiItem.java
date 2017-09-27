package edu.tdt.appstudent2.models.lichthi;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cuong on 5/3/2017.
 */

public class LichThiItem extends RealmObject{
    @PrimaryKey
    private String idHocKy;
    private RealmList<LichThiLichItem> giuaKy;
    private RealmList<LichThiLichItem> cuoiKy;

    public String getIdHocKy() {
        return idHocKy;
    }

    public void setIdHocKy(String idHocKy) {
        this.idHocKy = idHocKy;
    }

    public RealmList<LichThiLichItem> getGiuaKy() {
        if(giuaKy == null)
            giuaKy = new RealmList<>();
        return giuaKy;
    }

    public void setGiuaKy(RealmList<LichThiLichItem> giuaKy) {
        this.giuaKy = giuaKy;
    }

    public RealmList<LichThiLichItem> getCuoiKy() {
        if(cuoiKy == null)
            cuoiKy = new RealmList<>();
        return cuoiKy;
    }

    public void setCuoiKy(RealmList<LichThiLichItem> cuoiKy) {
        this.cuoiKy = cuoiKy;
    }
}
