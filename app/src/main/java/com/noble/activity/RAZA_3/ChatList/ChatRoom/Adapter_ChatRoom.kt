package com.noble.activity.RAZA_3.ChatList.ChatRoom

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

class Adapter_ChatRoom(val context: Context, val arrayList: ArrayList<Chat_Room>)
    :  RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    internal lateinit var preferences: SharedPreferences



    fun addItem(item: Chat_Room) {//아이템 추가
        if (arrayList != null) {//널체크 해줘야함
            arrayList.add(item)


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view:View
        if(viewType == 1){
            view = LayoutInflater.from(context).inflate(com.noble.activity.RAZA_3.R.layout.item_my_chat, parent, false)
            return Holder(view)
        }else{
            view = LayoutInflater.from(context).inflate(com.noble.activity.RAZA_3.R.layout.item_your_chat, parent, false)
            return Holder2(view)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {

        //ChatMe chatme = items.get(i);
        //뷰홀더가 뷰홀더1의 인스턴스이면 뷰횰더1 안의 두 텍스트뷰를 각각 셋한다.



        if (viewHolder is Holder) {
            (viewHolder as Holder).chat_Text?.setText(arrayList.get(i).script)
            (viewHolder as Holder).chat_Time?.setText(arrayList.get(i).date_time)
        } else if(viewHolder is Holder2) {

            //이미지 설정해주는 부분
            var test_task: URL_to_Bitmap_Task = URL_to_Bitmap_Task()
            test_task = URL_to_Bitmap_Task().apply {
                url = URL(arrayList.get(i).profile_image)

            }

            var bitmap: Bitmap = test_task.execute().get()
            if ( (viewHolder as Holder2).chat_You_Image != null) {
                (viewHolder as Holder2).chat_You_Image?.setImageBitmap(bitmap)
            }else{
                (viewHolder as Holder2).chat_You_Image?.setImageResource(com.noble.activity.RAZA_3.R.mipmap.ic_default_profile)
            }
            (viewHolder as Holder2).chat_You_Name?.setText(arrayList.get(i).name)
            (viewHolder as Holder2).chat_Text?.setText(arrayList.get(i).script)
            (viewHolder as Holder2).chat_Time?.setText(arrayList.get(i).date_time)
        }


    }


    //내가친 채팅 뷰홀더
    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        //친구목록 모델의 변수들 정의하는부분
        val chat_Text = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.chat_Text)
        val chat_Time = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.chat_Time)
    }

    //상대가친 채팅 뷰홀더
    inner class Holder2(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        //친구목록 모델의 변수들 정의하는부분
        val chat_You_Image = itemView?.findViewById<ImageView>(com.noble.activity.RAZA_3.R.id.chat_You_Image)
        val chat_You_Name = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.chat_You_Name)
        val chat_Text = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.chat_Text)
        val chat_Time = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.chat_Time)


    }


    override fun getItemViewType(position: Int): Int {//여기서 뷰타입을 1, 2로 바꿔서 지정해줘야 내채팅 너채팅을 바꾸면서 쌓을 수 있음
        preferences = context.getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)

        //내 아이디와 어레이 리스트 user_id가 같다면 myitem 아니면 youitem
        return if (arrayList.get(position).user_id == preferences.getInt("id",0)) {
            1
        } else {
            2
        }
    }

}