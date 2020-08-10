package com.noble.activity.RAZA_3.FriendsList

import android.content.Context
import android.widget.EditText
import java.util.ArrayList

interface Contract_FriendsList {

    interface View{
        var freindslist_data:String

        fun showProgress()
        fun hideProgress()
        fun showAlert()
        fun startMainTabActivity()
        fun refresh()
    }

    interface Presenter {

        var view: View



        fun loadItems(context: Context, arrayList: ArrayList<Json_Friends_Load>)

        fun addItems(context: Context, position:Int, arrayList: ArrayList<Json_Friends_Load>)

        fun removeItems(context: Context, arrayList: ArrayList<Json_Friends_Load>, adapter:AdapterFriendsList,position:Int)

        fun updateItems(context: Context, position:Int, arrayList: ArrayList<Json_Friends_Load>)


        fun setMyFriendsInfo(myemail: String?, arrayList: ArrayList<Json_Friends_Load>)

        fun plus_Friends_Button(view:android.view.View, ediText:EditText,myemail: String, mAdapter:AdapterFriendsList)
        fun close()
    }

}