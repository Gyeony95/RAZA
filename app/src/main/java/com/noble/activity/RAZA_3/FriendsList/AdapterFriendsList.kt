package com.noble.activity.RAZA_3.FriendsList

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Log
import android.view.*
import com.google.gson.Gson
import com.noble.activity.RAZA_3.ChatList.ChatRoom.ChatRoomActivity
import com.noble.activity.RAZA_3.FriendsList.FriendsProfile.FriendsProfileActivity
import com.noble.activity.RAZA_3.Network.Retrofit_Frame
import com.noble.activity.RAZA_3.etc_process.URL_to_Bitmap_Task
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URL


class AdapterFriendsList(val context: Context, val arrayList: ArrayList<Json_Friends_Load>) :
        RecyclerView.Adapter<AdapterFriendsList.Holder>() {


    internal  var json: String? = null
    var retrofit_Frame:Retrofit_Frame = Retrofit_Frame()
    internal lateinit var preferences: SharedPreferences
    internal var presenter_FriendsList:Presenter_FriendsList = Presenter_FriendsList()



    fun addItem(item: Json_Friends_Load) {//아이템 추가
        if (arrayList != null) {//널체크 해줘야함
            arrayList.add(item)
        }
    }


    //아이템 삭제 해주는부분
    fun removeAt(position: Int) {
        var i:Int = 0
        while (i < arrayList.size){
            if(position == i){
                retrofit_Frame.delete_method("friends", arrayList[position].id)
                break
            }

            i++
        }
        arrayList.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(com.noble.activity.RAZA_3.R.layout.item_friends_list, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(arrayList[position], context)

    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
/*

        private val onEditMenu = MenuItem.OnMenuItemClickListener { item ->
            when (item.itemId) {
                1001 -> {
                }

                1002 -> {
                    //아이템 삭제하는 부분


                    //여기 이하는 그냥 아이템 뷰를 지우는것
                    arrayList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    notifyItemRangeChanged(adapterPosition, arrayList.size)
                }
            }
            true
        }

*/
        //친구목록 모델의 변수들 정의하는부분
        val friends_image = itemView.findViewById<ImageView>(com.noble.activity.RAZA_3.R.id.friends_List_Image)
        val friends_name = itemView.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.friends_List_Name)
        val friends_country = itemView.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.friends_List_Country)
        val id_holder = itemView.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.id_holder)
        fun bind (friendslist: Json_Friends_Load, context: Context) {


            var test_task: URL_to_Bitmap_Task = URL_to_Bitmap_Task()
            test_task = URL_to_Bitmap_Task().apply {
                url = URL(friendslist.profile_image)
            }
            var bitmap: Bitmap = test_task.execute().get()
            if (friends_image != null) {
                friends_image.setImageBitmap(bitmap)
            }else{
                friends_image?.setImageResource(com.noble.activity.RAZA_3.R.mipmap.ic_default_profile)
            }
            Log.e("asd", friendslist.profile_image)
            Log.e("asd", URL(friendslist.profile_image).authority)



            //나머지 친구의 이름과 국적 정의
            friends_name?.text = friendslist.name
            friends_country?.text = friendslist.country
            id_holder.text = friendslist.id.toString()

            //친구 프로필 화면으로 가는부분
            itemView.setOnClickListener {
                val intent = Intent(context, FriendsProfileActivity::class.java)
                intent.putExtra("friends_email", friendslist.email)
                intent.putExtra("friends_image", friendslist.profile_image)
                intent.putExtra("friends_name", friendslist.name)
                intent.putExtra("friends_country", friendslist.country)
                intent.putExtra("friends_like", friendslist.like)
                intent.putExtra("friends_id", friendslist.real_id)
                context.startActivity(intent)
            }
    }
    }



}