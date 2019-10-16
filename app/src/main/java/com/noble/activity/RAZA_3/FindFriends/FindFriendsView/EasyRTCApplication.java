package com.noble.activity.RAZA_3.FindFriends.FindFriendsView;

import android.app.Application;
import android.content.Context;
import android.util.Log;


import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.Socket.SocketWraper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cx on 2018/12/22.
 */

public class EasyRTCApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        Log.e("sliver", "EasyRTCApplication SocketWrpaer setURL");
        super.onCreate();

        mContext = getApplicationContext();

        SocketWraper.shareContext().connectToURL("http://ec2-15-164-104-42.ap-northeast-2.compute.amazonaws.com:3000/");
        initSocketHeartBeat();
    }

    public static Context getContext() {
        return mContext;
    }

    private void initSocketHeartBeat() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SocketWraper.shareContext().keepAlive();
            }
        }, 0, 2000);
    }
}
