package com.noble.activity.RAZA_3.FriendsList.FriendsProfile

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.noble.activity.RAZA_3.ChatList.ChatRoom.ChatRoomActivity
import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.etc_process.URL_to_Bitmap_Task
import com.noble.activity.RAZA_3.views.ProfileSettingsActivity
import kotlinx.android.synthetic.main.activity_friends_profile.*
import kotlinx.android.synthetic.main.activity_mypage.*
import java.net.URL

class FriendsProfileActivity : AppCompatActivity(), Contract_FriendsProfile.View {

    lateinit var presenter_FriendsProfile: Presenter_FriendsProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_profile)
        var intent:Intent  = getIntent()

        presenter_FriendsProfile = Presenter_FriendsProfile().apply {
            view = this@FriendsProfileActivity
        }

        var friends_email = intent.getStringExtra("friends_email")
        var friends_name = intent.getStringExtra("friends_name")
        var friends_image = intent.getStringExtra("friends_image")
        var friends_id = intent.getIntExtra("friends_id", 0)

        friends_Profile_Name.text = friends_name
        friends_Profile_Country.text = intent.getStringExtra("friends_country")
        friends_Profile_Like_Count.text = intent.getIntExtra("friends_like", 0).toString()


        var test_task: URL_to_Bitmap_Task = URL_to_Bitmap_Task()
        test_task = URL_to_Bitmap_Task().apply {
            url = URL(friends_image)
        }

        var bitmap: Bitmap = test_task.execute().get()


        val resizedbitmap = Bitmap.createScaledBitmap(bitmap, 650, 650, true)
        friends_Profile_Image.setImageBitmap(resizedbitmap)


        friends_Profile_Exit.setOnClickListener {
            finish()

        }
        friends_Profile_Chat.setOnClickListener {
            //채팅방 가는부분
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("friends_email",friends_email)
            intent.putExtra("friends_name",friends_name)
            intent.putExtra("friends_image",friends_image)
            intent.putExtra("friends_id",friends_id)

            startActivity(intent)
        }

    }
}
