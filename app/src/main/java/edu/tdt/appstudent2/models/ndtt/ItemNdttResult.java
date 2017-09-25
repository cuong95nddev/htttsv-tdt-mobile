package edu.tdt.appstudent2.models.ndtt;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bichan on 9/25/17.
 */

public class ItemNdttResult extends RealmObject {
    @PrimaryKey
    private String id;
    private String type;
    private String hk;
    private String dateRequest;
    private String dateResponse;
    private String status;
    private String note;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHk() {
        return hk;
    }

    public void setHk(String hk) {
        this.hk = hk;
    }

    public String getDateRequest() {
        return dateRequest;
    }

    public void setDateRequest(String dateRequest) {
        this.dateRequest = dateRequest;
    }

    public String getDateResponse() {
        return dateResponse;
    }

    public void setDateResponse(String dateResponse) {
        this.dateResponse = dateResponse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
