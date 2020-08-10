
package com.noble.activity.RAZA_3.FindFriends.FindFriendsView.data;

/**
 * Created by duongnx on 3/14/2017.
 */

public class User {
    private String name;
    private String id;
    private Boolean connect;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
        this.connect = connect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getConnect() {
        return connect;
    }

    public void setConnect(Boolean connect) {
        this.connect = connect;
    }
}
