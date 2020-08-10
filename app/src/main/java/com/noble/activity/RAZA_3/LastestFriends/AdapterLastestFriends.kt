package com.noble.activity.RAZA_3.LastestFriends

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.etc_process.URL_to_Bitmap_Task
import java.net.URL
import java.util.ArrayList



class AdapterLastestFriends (val context: Context, val arrayList: ArrayList<Json_Lastest_Load>) :
        RecyclerView.Adapter<AdapterLastestFriends.Holder>() {


    var presenter_LastestFriends:Presenter_LastestFriends = Presenter_LastestFriends()


    fun addItem(item: Json_Lastest_Load) {//아이템 추가
        if (arrayList != null) {//널체크 해줘야함
            arrayList.add(item)

        }
    }

    fun removeAt(position: Int) {
        arrayList.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(com.noble.activity.RAZA_3.R.layout.item_lastest_friends, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(arrayList[position], context)



    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        //친구목록 모델의 변수들 정의하는부분
        val lastest_Friends_Name = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.lastest_Friends_Name)
        val lastest_Friends_Country = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.lastest_Friends_Country)
        val lastest_Friends_Date = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.lastest_Friends_Date)
        val lastest_id_holder = itemView?.findViewById<TextView>(com.noble.activity.RAZA_3.R.id.lastest_id_holder)
        val lastest_Friends_Big_Image = itemView?.findViewById<ImageView>(com.noble.activity.RAZA_3.R.id.lastest_Friends_Big_Image)
        val lastest_Friends_Image = itemView?.findViewById<ImageView>(com.noble.activity.RAZA_3.R.id.lastest_Friends_Image)
        val lastest_Friends_Trash_Button = itemView?.findViewById<ImageView>(com.noble.activity.RAZA_3.R.id.lastest_Friends_Trash_Button)



        fun bind (lastest_friends_list: Json_Lastest_Load, context: Context) {

            /*
            if (lastest_friends_list.photo != "") {//친구 프사가 없는게 아니라면
                //친구 프사는 이것이다.
                val resourceId = context.resources.getIdentifier(lastest_friends_list.photo, "drawable", context.packageName)
                lastest_Friends_Image?.setImageResource(resourceId)
            } else {//아니면 이것이다.
                lastest_Friends_Image?.setImageResource(com.noble.activity.RAZA_3.R.mipmap.ic_default_profile)
            }

            if (lastest_friends_list.big_photo != "") {//친구 프사가 없는게 아니라면
                //친구 프사는 이것이다.
                val resourceId = context.resources.getIdentifier(lastest_friends_list.big_photo, "drawable", context.packageName)
                lastest_Friends_Big_Image?.setImageResource(resourceId)
            } else {//아니면 이것이다.
                lastest_Friends_Big_Image?.setImageResource(com.noble.activity.RAZA_3.R.mipmap.ic_default_profile)
            }
*/
            Log.e("asd", "asd")

            //나머지 친구의 이름과 국적 정의
            lastest_Friends_Name?.text = lastest_friends_list.name
            lastest_Friends_Country?.text = lastest_friends_list.country
            lastest_id_holder?.text = lastest_friends_list.id.toString()
            var test_task: URL_to_Bitmap_Task = URL_to_Bitmap_Task()
            test_task = URL_to_Bitmap_Task().apply {
                url = URL(lastest_friends_list.profile_image)
            }
            var bitmap: Bitmap = test_task.execute().get()
            if (lastest_Friends_Image != null) {
                lastest_Friends_Image.setImageBitmap(bitmap)
            }else{
                lastest_Friends_Image?.setImageResource(com.noble.activity.RAZA_3.R.mipmap.ic_default_profile)
            }
            Log.e("asd", "asd")

            var test_task2: URL_to_Bitmap_Task = URL_to_Bitmap_Task()
            test_task2 = URL_to_Bitmap_Task().apply {
                url = URL(lastest_friends_list.profile_image2)
            }
            var bitmap2: Bitmap = test_task2.execute().get()
            if (lastest_Friends_Big_Image != null) {
                lastest_Friends_Big_Image.setImageBitmap(bitmap2)
            }else{
                lastest_Friends_Big_Image?.setImageResource(com.noble.activity.RAZA_3.R.mipmap.ic_default_profile)
            }
            Log.e("asd", "asd")

            //삭제하는 버튼
            if (lastest_Friends_Trash_Button != null) {
                lastest_Friends_Trash_Button.setOnClickListener {

                    presenter_LastestFriends.removeItems(position, lastest_friends_list.id, context)


                }
            }



        }
    }



}