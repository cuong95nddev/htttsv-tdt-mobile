package edu.tdt.appstudent2.models.email;

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
    private String mSentDate;
    private String mSentDateShort;
    private String mSubject;
    private String mBody;
    private String mPersonal;
    private boolean isNew = true;

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

    public String getmSentDate() {
        return mSentDate;
    }

    public void setmSentDate(String mSentDate) {
        this.mSentDate = mSentDate;
    }

    public String getmSentDateShort() {
        return mSentDateShort;
    }

    public void setmSentDateShort(String mSentDateShort) {
        this.mSentDateShort = mSentDateShort;
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
}
