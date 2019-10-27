/*
 * Copyright 2014 Pierre Chabardes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.noble.activity.RAZA_3.FindFriends.FindFriendsView.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.MyApplication;
import com.noble.activity.RAZA_3.R;
import com.noble.activity.RAZA_3.views.BaseActivity2;

import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;


import fr.pchab.webrtcclient.PeerConnectionParameters;
import fr.pchab.webrtcclient.WebRtcClient;

public class RtcActivity extends BaseActivity2 implements WebRtcClient.RtcListener {
    public static final String KEY_CALLER_ID = "caller_id";


    private final static int VIDEO_CALL_SENT = 666;
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";
    // Local preview screen position before call is connected.
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;
    // Local preview screen position after call is connected.
    private static final int LOCAL_X_CONNECTED = 72;
    private static final int LOCAL_Y_CONNECTED = 72;
    private static final int LOCAL_WIDTH_CONNECTED = 25;
    private static final int LOCAL_HEIGHT_CONNECTED = 25;
    // Remote video screen position
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;
    private RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
    private GLSurfaceView vsv;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;
    private WebRtcClient client;
    private String mSocketAddress;
    private String callerId;
    SharedPreferences preferences;
    Button next_Call;

    //BackgroundTask task;
    int value;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.main);
        //mSocketAddress = "https://" + getResources().getString(R.string.host);
        mSocketAddress = "http://ec2-15-164-104-42.ap-northeast-2.compute.amazonaws.com:3000/" ;
        //mSocketAddress = "https://03fe8a84.ngrok.io" ;
        //mSocketAddress += (":" + getResources().getString(R.string.port) + "/");
        preferences  = getSharedPreferences("USERSIGN", 0);


        next_Call = (Button)findViewById(R.id.next_Call);

        next_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.e("rtcactivity", "1");
                //vsv.destroyDrawingCache();
               finish();
//                task = new BackgroundTask();
//                task.execute(100);
                Log.e("rtcactivity", "2");

                Intent intent = new Intent(RtcActivity.this, GetidActivity.class);
                startActivity(intent);
                Log.e("rtcactivity", "3");


            }
        });


        vsv = (GLSurfaceView) findViewById(R.id.glview_call);
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);
        //vsv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        VideoRendererGui.setView(vsv, new Runnable() {
            @Override
            public void run() {
                init();
            }
        });

        // local and remote render
        remoteRender = VideoRendererGui.create(
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);

        localRender = VideoRendererGui.create(
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            callerId = bundle.getString(KEY_CALLER_ID);
        }
    }

//    //새로운 TASK정의 (AsyncTask)
//    // < >안에 들은 자료형은 순서대로 doInBackground, onProgressUpdate, onPostExecute의 매개변수 자료형을 뜻한다.(내가 사용할 매개변수타입을 설정하면된다)
//    class BackgroundTask extends AsyncTask<Integer , Integer , Integer> {
//        //초기화 단계에서 사용한다. 초기화관련 코드를 작성했다.
//        protected void onPreExecute() {
//            showProgress();
//
//        }
//
//        //스레드의 백그라운드 작업 구현
//        //여기서 매개변수 Intger ... values란 values란 이름의 Integer배열이라 생각하면된다.
//        //배열이라 여러개를 받을 수 도 있다. ex) excute(100, 10, 20, 30); 이런식으로 전달 받으면 된다.
//        protected Integer doInBackground(Integer ... values) {
//            //isCancelled()=> Task가 취소되었을때 즉 cancel당할때까지 반복
//            while (isCancelled() == false) {
//                value++;
//
//                if (value >= 50) {
//                    hideProgress();
//                    break;
//                } else {
//                    //publishProgress()는 onProgressUpdate()를 호출하는 메소드(그래서 onProgressUpdate의 매개변수인 int즉 Integer값을 보냄)
//                    //즉, 이 메소드를 통해 백그라운드 스레드작업을 실행하면서 중간중간  UI에 업데이트를 할 수 있다.
//
//                }
//
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException ex) {}
//            }
//
//            return value;
//        }
//
//        //UI작업 관련 작업 (백그라운드 실행중 이 메소드를 통해 UI작업을 할 수 있다)
//        //publishProgress(value)의 value를 값으로 받는다.values는 배열이라 여러개 받기가능
//        protected void onProgressUpdate(Integer ... values) {
//
//        }
//
//
//        //이 Task에서(즉 이 스레드에서) 수행되던 작업이 종료되었을 때 호출됨
//        protected void onPostExecute(Integer result) {
//
//        }
//
//        //Task가 취소되었을때 호출
//        protected void onCancelled() {
//
//        }
//    }



    private void init() {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                true, false, displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);

        //이걸 스레드로 매번 호출
        client = new WebRtcClient(this, mSocketAddress, params, VideoRendererGui.getEglBaseContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        vsv.onPause();
        if (client != null) {
            client.onPause();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        vsv.onResume();
        if (client != null) {
            client.onResume();
        }
    }

    @Override
    public void onDestroy() {
        if (client != null) {
            Log.e("rtcactivity 디스트로이!!", preferences.getString("webrtc_id", ""));
            client.onDestroy(preferences.getString("webrtc_remote_id", ""));
        }
        super.onDestroy();
    }

    @Override
    public void onCallReady(String callId) {
        Log.d("duongnx", "RtcListener onCallReady:" + callId);
        preferences  = getSharedPreferences("USERSIGN", 0);
        SharedPreferences.Editor editor = preferences.edit();

//        //디스트로이할때 줄 아이디값을 저장하기 위함
//        Log.e("listuseractivity", "저장!");
//        editor.putString("webrtc_id", callId);
//        editor.commit();
//        Log.e("listuseractivity", preferences.getString("webrtc_id", ""));

        //client.exchange_id(callId);


        if (!TextUtils.isEmpty(callerId)) {
            try {
                answer(callerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //call(callId);
            startCam();
        }
    }

    @Override
    public void onExchangeReady(String callId) {
        preferences  = getSharedPreferences("USERSIGN", 0);
        SharedPreferences.Editor editor = preferences.edit();
        //디스트로이할때 줄 아이디값을 저장하기 위함
        Log.e("listuseractivity", "저장!");
        editor.putString("webrtc_remote_id", callId);
        editor.commit();
        Log.e("listuseractivity", preferences.getString("webrtc_remote_id", ""));

    }

    public void answer(String callerId) throws JSONException {
        Log.d("duongnx", "answer:" + callerId);
        //
        client.sendMessage(callerId, "init", null);
        startCam();
    }


    public void startCam() {
        // Camera settings
        preferences  = getSharedPreferences("USERSIGN", MODE_PRIVATE);
        client.start(MyApplication.getInstance().getLoginUser(), preferences.getBoolean("connect", false));

    }

    @Override
    public void onStatusChanged(final String newStatus) {
        Log.d("duongnx", "RtcListener onStatusChanged:" + newStatus);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), newStatus, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
        Log.d("duongnx", "RtcListener onLocalStream:" + localStream.label());
        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, false);
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {
        Log.d("duongnx", "RtcListener onAddRemoteStream:" + remoteStream.label());
        remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
        VideoRendererGui.update(remoteRender,
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED,
                scalingType, false);
    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {
        Log.d("duongnx", "RtcListener onRemoveRemoteStream:");
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, false);
    }

    @Override
    public void onChangeConnecttrue() {
        MyApplication.getInstance().setConnectUser(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void showProgress() {
        showProgressDialog(R.string.basic_loading);
    }
    public void hideProgress() {
        hideProgressDialog();
    }
//    public void showAlert() {
//        showAlertDialog(R.string.basic_alert_dialog_msg) { finish(); }
//
//    }


}