package com.noble.activity.RAZA_3.FindFriends.FindFriendsView.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.Agent.Agent;
import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.Agent.AgentListAdapter;
import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.EasyRTCApplication;
import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.Socket.SocketWraper;
import com.noble.activity.RAZA_3.R;

import java.util.ArrayList;

public class FindFriendsViewActivity extends AppCompatActivity implements SocketWraper.SocketDelegate{

    private final String TAG = FindFriendsViewActivity.class.getName();

    private ListView mAgentsView;

    private AgentListAdapter mAgentListAdapter;

    private AlertDialog mAlertDialog;

    private ImageButton mCallButton;
    private static final String[] RequiredPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    protected PermissionChecker permissionChecker = new PermissionChecker();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends_view);

        checkPermissions();

        Log.e("skruazzz", "FindFriendsViewActivity onCreate");

        setUI();
        setAgents();
        setSocket();
        updateRemoteAgent();
    }


    private void checkPermissions() {
        permissionChecker.verifyPermissions(this, RequiredPermissions, new PermissionChecker.VerifyPermissionsCallback() {

            @Override
            public void onPermissionAllGranted() {

            }

            @Override
            public void onPermissionDeny(String[] permissions) {
                Toast.makeText(FindFriendsViewActivity.this, "Please grant required permissions.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStop() {
        Log.e("skruazzz", "FindFriendsViewActivity onStop");
        SocketWraper.shareContext().removeListener(this);

        super.onStop();
    }

    private void setSocket() {
        SocketWraper.shareContext().addListener(this);
    }

    private void updateRemoteAgent() {
        SocketWraper.shareContext().updateRemoteAgent();
    }

    private void setUI() {
        mAgentsView = findViewById(R.id.AgentsView);
        setAlertDialog();
        setButton();
    }

    private void setAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FindFriendsViewActivity.this);
        builder.setTitle("전화옴");
        builder.setPositiveButton("ㅇㅋ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("sliver", "FindFriendsViewActivity accept invite");
                jumpToNextActivity("recv");

            }
        });
        builder.setNegativeButton("ㄴㄴ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("sliver", "FindFriendsViewActivity refuse invite");
                SocketWraper.shareContext().ack(false);
            }
        });
        mAlertDialog = builder.create();
    }

    private void setButton() {
        mCallButton = findViewById(R.id.CallButton);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("sliver", "FindFriendsViewActivity calll button clicked");
                Agent agent = mAgentListAdapter.getChooseAgent();
                if (agent != null) {
                    SocketWraper.shareContext().setTarget(agent.id());
                    SocketWraper.shareContext().invite();
                }

            }
        });
    }

    private void setAgents() {
        mAgentListAdapter = new AgentListAdapter(this);
        mAgentsView.setAdapter(mAgentListAdapter);
    }

    private void jumpToNextActivity(String status) {
        Intent intent = new Intent(FindFriendsViewActivity.this, RTCActivity.class);
        intent.putExtra("status", status);

        Agent agent = mAgentListAdapter.getChooseAgent();
        if (agent.type().equals("Android_Camera")) {
            intent.putExtra("type", "camera");
        } else {
            intent.putExtra("type", "client");
        }

        startActivity(intent);
        finish();
    }

    private void processSignal(String source, String target, String type, String value) {
        if (target.equals(SocketWraper.shareContext().getUid())) {
            if (type.equals("invite")) {
                SocketWraper.shareContext().setTarget(source);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAlertDialog.show();
                    }
                });
            }
            if (type.equals("ack")) {
                if(value.equals("yes")) {
                    jumpToNextActivity("send");
                }
            }
        } else {
            Log.e("sliver", "FindFriendsViewActivity get error target");
        }
    }

    @Override
    public void onUserAgentsUpdate(ArrayList<Agent> agents) {
        Log.e("sliver", "FindFriendsViewActivity onUserAgentsUpdate");
        mAgentListAdapter.reset();
        for (int i = 0; i < agents.size(); i++) {
            mAgentListAdapter.addAgent(agents.get(i));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAgentListAdapter.update();
            }
        });
    }

    @Override
    public void onDisConnect() {
        Log.e("sliver", "FindFriendsViewActivity onDisConnect");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EasyRTCApplication.getContext(), "can't connect to server", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRemoteEventMsg(String source, String target, String type, String value) {
        processSignal(source, target, type, value);
    }

    @Override
    public void onRemoteCandidate(int label, String mid, String candidate) {

    }
}