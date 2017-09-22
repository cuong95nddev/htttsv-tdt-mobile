package edu.tdt.appstudent2.models.email;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bichan on 9/21/17.
 */

public class EmailAttachment extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String type;
    private int size;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
