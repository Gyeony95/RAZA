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

package com.noble.activity.RAZA_3.FindFriends.FindFriendsView.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.activity.ListUserActivity;
import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.data.User;
import com.noble.activity.RAZA_3.R;

import java.util.ArrayList;



/**
 * Created by duongnx on 3/14/2017.
 */

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.VhUser> implements View.OnClickListener {
    public interface OnCallListener {
        void onCall(int position);
    }
    SharedPreferences preferences;

    private Context mContext;
    private static OnCallListener onCallListener;
    private ArrayList<User> users = new ArrayList<>();

    public AdapterUser(Context context, OnCallListener listener) {
        super();
        this.mContext = context;
        onCallListener = listener;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public VhUser onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_vhuser, parent, false);
        VhUser vhUser = new VhUser(view);
        vhUser.getBtCall().setOnClickListener(this);
        return vhUser;
    }

    @Override
    public void onBindViewHolder(VhUser holder, int position) {


        holder.setData(users.get(position), position);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class VhUser extends RecyclerView.ViewHolder {
        private TextView tvUserName, tvId, tvconnect;
        private Button btCall;

        public VhUser(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvId = (TextView) itemView.findViewById(R.id.tvId);
            tvconnect = (TextView) itemView.findViewById(R.id.tvconnect);
            btCall = (Button) itemView.findViewById(R.id.btCall);
        }

        public void setData(User user, int position) {


            tvUserName.setText(user.getName());
            tvId.setText(user.getId());
            Log.e("adapteruser", user.getConnect().toString());
            tvconnect.setText(user.getConnect().toString());
            btCall.setTag(position);


            if(!user.getConnect()){
                if (onCallListener != null){
                    onCallListener.onCall(position);
                    return;
                }
            }
            Log.e("position",""+position);
            Log.e("users.size()",""+users.size());
            if(position == users.size()-1){
                ListUserActivity.startCall(mContext);
            }
        }

        public Button getBtCall() {
            return btCall;
        }
    }

    @Override
    public void onClick(View v) {
        if (onCallListener != null)
            onCallListener.onCall((Integer) v.getTag());
        Log.e("onclick", ""+(Integer) v.getTag());


    }

    public  static void clickCall(int position){
        if (onCallListener != null)
            onCallListener.onCall(position);
    }
}
