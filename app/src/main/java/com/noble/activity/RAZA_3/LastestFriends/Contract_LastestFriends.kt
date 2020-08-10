package com.noble.activity.RAZA_3.LastestFriends

import android.content.Context
import java.util.ArrayList

interface Contract_LastestFriends {

    interface View{
        fun refresh()

    }

    interface Presenter {

        var view: View
        var context:Context
        var myemail:String

        fun loadItems(list:ArrayList<Json_Lastest_Load>)

        fun addItems(position:Int)

        fun removeItems(position:Int, id:Int, context: Context)

        fun updateItems(position:Int)
    }

}