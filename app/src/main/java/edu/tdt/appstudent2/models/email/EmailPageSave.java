package edu.tdt.appstudent2.models.email;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/20/2016.
 */
public class EmailPageSave extends RealmObject {
    @PrimaryKey
    private int id = 0;
    private long idLoadedTop;
    private long idLoadedBottom;

    public long getIdLoadedTop() {
        return idLoadedTop;
    }

    public void setIdLoadedTop(long idLoadedTop) {
        this.idLoadedTop = idLoadedTop;
    }

    public long getIdLoadedBottom() {
        return idLoadedBottom;
    }

    public void setIdLoadedBottom(long idLoadedBottom) {
        this.idLoadedBottom = idLoadedBottom;
    }
}
