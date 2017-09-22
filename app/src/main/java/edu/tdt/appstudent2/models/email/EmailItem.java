package edu.tdt.appstudent2.models.email;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/20/2016.
 */
public class EmailItem extends RealmObject{
    @PrimaryKey
    private int mId;
    private String mFrom;
    private String mTo;
    private String mCc;
    private String mSubject;
    private String mBody;
    private String mPersonal;
    private boolean isNew = true;
    private long mSentDate;
    private RealmList<EmailAttachment>  emailAttachments;

    public RealmList<EmailAttachment> getEmailAttachments() {
        if(emailAttachments == null)
            emailAttachments = new RealmList<EmailAttachment>();
        return emailAttachments;
    }

    public void setEmailAttachments(RealmList<EmailAttachment> emailAttachments) {
        this.emailAttachments = emailAttachments;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmFrom() {
        return mFrom;
    }

    public void setmFrom(String mFrom) {
        this.mFrom = mFrom;
    }

    public String getmTo() {
        return mTo;
    }

    public void setmTo(String mTo) {
        this.mTo = mTo;
    }

    public String getmCc() {
        return mCc;
    }

    public void setmCc(String mCc) {
        this.mCc = mCc;
    }

    public String getmSubject() {
        return mSubject;
    }

    public void setmSubject(String mSubject) {
        this.mSubject = mSubject;
    }

    public String getmBody() {
        return mBody;
    }

    public void setmBody(String mBody) {
        this.mBody = mBody;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getmPersonal() {
        return mPersonal;
    }

    public void setmPersonal(String mPersonal) {
        this.mPersonal = mPersonal;
    }

    public long getmSentDate() {
        return mSentDate;
    }

    public void setmSentDate(long mSentDate) {
        this.mSentDate = mSentDate;
    }
}
