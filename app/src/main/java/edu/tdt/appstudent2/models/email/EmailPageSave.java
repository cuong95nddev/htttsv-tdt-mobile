package edu.tdt.appstudent2.models.email;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/20/2016.
 */
public class EmailPageSave extends RealmObject {
    @PrimaryKey
    private int id = 0;
    private int idLoadedTop;
    private int idLoadedBottom;

    public int getIdLoadedTop() {
        return idLoadedTop;
    }

    public void setIdLoadedTop(int idLoadedTop) {
        this.idLoadedTop = idLoadedTop;
    }

    public int getIdLoadedBottom() {
        return idLoadedBottom;
    }

    public void setIdLoadedBottom(int idLoadedBottom) {
        this.idLoadedBottom = idLoadedBottom;
    }
}
