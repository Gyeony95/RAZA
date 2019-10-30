package com.noble.activity.RAZA_3.FindFriends.Agent;

/**
 * Created by cx on 2018/12/21.
 */

public class Agent {

    private String mID;

    private String mName;

    private String mType;

    private  Boolean mConnect;

    public Agent(String id, String name, String type, Boolean connect) {
        mID = id;
        mName = name;
        mType = type;
        mConnect = connect;
    }

    public String id() {
            return mID;
        }

    public String name() {
        return mName;
    }

    public String type() {
        return mType;
    }

    public Boolean connect() {
        return mConnect;
    }

}