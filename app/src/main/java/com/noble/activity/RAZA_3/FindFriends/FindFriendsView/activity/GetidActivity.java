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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.MyApplication;
import com.noble.activity.RAZA_3.R;

import java.util.Random;


/**
 * Created by duongnx on 3/14/2017.
 */

public class GetidActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getid);
        final EditText etUserName = (EditText) findViewById(R.id.etUserName);
        Random random = new Random();
        etUserName.setText("duongnx" + random.nextInt(1000));
        findViewById(R.id.btLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etUserName.getText().toString())) {
                    Intent intent = new Intent(GetidActivity.this, ListUserActivity.class);
                    MyApplication.getInstance().setLoginUser(etUserName.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
}
