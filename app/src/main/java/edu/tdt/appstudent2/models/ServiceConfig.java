package edu.tdt.appstudent2.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bichan on 9/7/17.
 */

public class ServiceConfig extends RealmObject {
    @PrimaryKey
    private int id;
    private boolean open;
    private long timeReplay;
    private boolean sound;
    private boolean vibrate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public long getTimeReplay() {
        return timeReplay;
    }

    public void setTimeReplay(long timeReplay) {
        this.timeReplay = timeReplay;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }
}
