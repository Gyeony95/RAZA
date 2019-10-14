package com.noble.activity.RAZA_3.ChatList.ChatRoom

import android.content.Context
import org.json.JSONObject

interface Contract_ChatRoom {
    interface View{
        fun refresh()
        fun first_false()
        fun first_true()
        fun showProgress()
        fun hideProgress()
        fun showAlert()
        fun startMainTabActivity()

    }

    interface Presenter {

        var view:View

        fun load_item(context: Context, roomName:String, arrayList:ArrayList<Chat_Room>)
        fun first_check(context: Context, roomName:String)
        fun create_list(context: Context, jsonObject: JSONObject)
    }
}