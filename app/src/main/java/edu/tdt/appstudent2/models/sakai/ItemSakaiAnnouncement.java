package edu.tdt.appstudent2.models.sakai;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bichan on 9/25/17.
 */

public class ItemSakaiAnnouncement extends RealmObject {
    @PrimaryKey
    private String id;
    private String body;
    private String createdByDisplayName;
    private long createdOn;
    private String title;
    private RealmList<ItemSakaiAttachment> attachments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }

    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<ItemSakaiAttachment> getAttachments() {
        if(attachments == null)
            attachments = new RealmList<>();
        return attachments;
    }

    public void setAttachments(RealmList<ItemSakaiAttachment> attachments) {
        this.attachments = attachments;
    }
}
