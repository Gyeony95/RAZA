package com.noble.activity.RAZA_3.ChatList

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.noble.activity.RAZA_3.etc_process.URL_to_Bitmap_Task
import java.net.URL
import java.util.ArrayList

class Adapter_ChatList(val context: Context, val arrayList: ArrayList<ChatList_Load>) :
        RecyclerView.Adapter<Adapter_ChatList.Holder>()  {

    internal lateinit var preferences: SharedPreferences

    var presenter_ChatList:Presenter_ChatList = Presenter_ChatList()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(com.noble.activity.RAZA_3.R.layout.item_chat_list, parent, false)
        return Holder(view)    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(arrayList[position], context)
    }

    //삭제하는 부분
    fun removeAt(position: Int, context: Context) {
        //presenter_ChatList.removeItems(context, position, arrayList)
        notifyItemRemoved(position)
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        //친구목록 모델의 변수들 정의하는부분
        val chat_List_Image = itemView?.findViewById<ImageView>(com.noble.activity.RAZA_3.R.id.chat_List_Image)
        val chat_List_Name = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.chat_List_Name)
        val chat_List_Script = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.chat_List_Script)
        val chat_List_Date = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.chat_List_Date)
        val non_read_count = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.non_read_count)
        val id_holder = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.chat_id_holder)

        fun bind (chatlist: ChatList_Load, context: Context) {
            preferences = context.getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)

            //이미지 설정해주는 부분
            var test_task: URL_to_Bitmap_Task = URL_to_Bitmap_Task()
            test_task = URL_to_Bitmap_Task().apply {
                url = URL(chatlist.profile_image)

            }
            var bitmap: Bitmap = test_task.execute().get()
            if (chat_List_Image != null) {
                chat_List_Image.setImageBitmap(bitmap)
            }else{
                chat_List_Image?.setImageResource(com.noble.activity.RAZA_3.R.mipmap.ic_default_profile)
            }

            //나머지 친구의 이름과 국적 정의
            if(chatlist.name.equals(preferences.getString("name",""))){
                chat_List_Name?.text = chatlist.name2
            }else{
                chat_List_Name?.text = chatlist.name
            }
            chat_List_Script?.text = chatlist.script
            chat_List_Date?.text = chatlist.date_time
            chat_List_Script?.text = chatlist.script
            id_holder?.text = chatlist.id.toString()

            //읽지않은 메세지의 개수가 0이면 표시안하게
            if(chatlist.non_read_count == 0){
                non_read_count!!.visibility = View.GONE
            }
            else{
                non_read_count?.text = chatlist.non_read_count.toString()
            }

            itemView.setOnClickListener {
                //여기가 채팅방으로 가는부분분
                //채팅 리스트의 아이디를 같이 보내줘서 어느방과 매칭되어있는지 확인해준다.
                preferences = context.getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)

                if(chatlist.user_one == preferences.getInt("id",0)){
                    if(chatlist.name.equals(preferences.getString("name",""))){
                        presenter_ChatList.go_chatroom(context, chatlist.roomName, preferences.getInt("id", 0),chatlist.name2, chatlist.profile_image, chatlist.user_two )
                    }else{
                        presenter_ChatList.go_chatroom(context, chatlist.roomName, preferences.getInt("id", 0),chatlist.name, chatlist.profile_image, chatlist.user_two )
                    }

                }else{
                    if(chatlist.name.equals(preferences.getString("name",""))){
                        presenter_ChatList.go_chatroom(context, chatlist.roomName, preferences.getInt("id", 0),chatlist.name2, chatlist.profile_image, chatlist.user_one )
                    }else{
                        presenter_ChatList.go_chatroom(context, chatlist.roomName, preferences.getInt("id", 0),chatlist.name, chatlist.profile_image, chatlist.user_one )
                    }

                }

            }


        }
    }
}