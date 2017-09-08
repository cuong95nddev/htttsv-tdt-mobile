package edu.tdt.appstudent2.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bichan on 7/14/2016.
 */
public class User extends RealmObject {
    @PrimaryKey
    private int id;
    private String userName;
    private String passWord;
    private String name;
    private String linkAvatar;
    private String linkHostMail;
    private Config config;
    private boolean lock;
    private boolean showAvatar;

    private ServiceConfig tbServiceConfig;
    private ServiceConfig emailServiceConfig;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public String getLinkHostMail() {
        return linkHostMail;
    }

    public void setLinkHostMail(String linkHostMail) {
        this.linkHostMail = linkHostMail;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public ServiceConfig getTbServiceConfig() {
        return tbServiceConfig;
    }

    public void setTbServiceConfig(ServiceConfig tbServiceConfig) {
        this.tbServiceConfig = tbServiceConfig;
    }

    public ServiceConfig getEmailServiceConfig() {
        return emailServiceConfig;
    }

    public void setEmailServiceConfig(ServiceConfig emailServiceConfig) {
        this.emailServiceConfig = emailServiceConfig;
    }

    public boolean isShowAvatar() {
        return showAvatar;
    }

    public void setShowAvatar(boolean showAvatar) {
        this.showAvatar = showAvatar;
    }
}


