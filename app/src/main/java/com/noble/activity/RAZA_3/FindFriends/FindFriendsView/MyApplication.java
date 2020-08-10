package com.noble.activity.RAZA_3.FindFriends.FindFriendsView;

import android.app.Application;

/**
 * Created by duongnx on 3/14/2017.
 */

public class MyApplication extends Application {
    static MyApplication instance;
    private String loginUser;
    private Boolean connectUser;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public Boolean getConnectUser() {
        return connectUser;
    }

    public void setConnectUser(Boolean loginUser) {
        this.connectUser = connectUser;
    }
}
