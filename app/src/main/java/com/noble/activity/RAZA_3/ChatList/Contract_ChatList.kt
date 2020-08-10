package com.noble.activity.RAZA_3.ChatList

import android.content.Context
import java.util.ArrayList

interface Contract_ChatList {


    interface View{
        fun showProgress()
        fun hideProgress()
        fun showAlert()
        fun startMainTabActivity()
        fun refresh()
    }

    interface Presenter {

        var view:View
        fun go_chatroom(context: Context, roomName:String, user_id:Int, name:String, image:String, friends_id:Int)

        fun loadItems(context: Context, arrayList: ArrayList<ChatList_Load>, user_id:Int)

        fun addItems(context: Context, position:Int, arrayList: ArrayList<ChatList>)

        fun removeItems(context: Context, position:Int, arrayList: ArrayList<ChatList_Load>)

        fun updateItems(context: Context, position:Int, arrayList: ArrayList<ChatList>)

        fun clickSetting(context:Context)

    }
}