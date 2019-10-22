package com.noble.activity.RAZA_3.FindFriends.FindFriendsView.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.MyApplication;
import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.adapter.AdapterUser;
import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.data.User;
import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.data.WebService;
import com.noble.activity.RAZA_3.R;

import java.util.ArrayList;



/**
 * Created by duongnx on 3/14/2017.
 */

public class ListUserActivity extends AppCompatActivity implements WebService.OnServerListener, AdapterUser.OnCallListener {
    private View rootView;
    private RecyclerView recyclerView;
    private static AdapterUser mAdapter;
    private TextView tvMessage;
    private ArrayList<User> users = new ArrayList<>();
    static Context List_User_context;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        List_User_context = this;
        //뷰와 리사이클러뷰 초기화 시켜줌
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //리사이클러뷰 셋팅
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //어탭터가 설정되어있지 않다면 설정해줌
        if (mAdapter == null) {
            mAdapter = new AdapterUser(this, this);
        }
        //어뎁터 셋팅
        recyclerView.setAdapter(mAdapter);
        //소켓 주소 설정
        //new WebService(this).execute("https://" + getResources().getString(R.string.host) + "/streams.json");
        new WebService(this).execute("http://ec2-15-164-104-42.ap-northeast-2.compute.amazonaws.com:3000/" + "/streams.json");

        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(MyApplication.getInstance().getLoginUser());



//        //시작버튼을 누르면?! ->  여기를 리스트의 위에가 통신중이라면 시작하는걸로 바꾸면 되나봄
//        findViewById(R.id.btStart).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ListUserActivity.this, RtcActivity.class);
//                startActivity(intent);
//            }
//        });


    }

    public static void startCall(Context context){
        Intent intent = new Intent(context, RtcActivity.class);
        context.startActivity(intent);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onCall(int position) {
        User user = mAdapter.getUsers().get(position);
        if (user != null) {
            Intent intent = new Intent(this, RtcActivity.class);
            intent.putExtra(RtcActivity.KEY_CALLER_ID, user.getId());
            startActivity(intent);
            finish();
            // ((ListUserActivity) getActivity()).replaceFragment(new FrgVideoChat());
        }
    }

    @Override
    public void onServerComplete(ArrayList<User> users) {
        // Log.d("duongnx","onServerComplete:"+users.size());
        if (users != null && users.size() > 0) {
            mAdapter.setUsers(users);
            mAdapter.notifyDataSetChanged();
            tvMessage.setVisibility(View.GONE);
        } else{
            tvMessage.setVisibility(View.VISIBLE);
            startCall(List_User_context);
            finish();
        }
    }

    @Override
    public void onServerFailed() {
        tvMessage.setVisibility(View.VISIBLE);
    }
}
