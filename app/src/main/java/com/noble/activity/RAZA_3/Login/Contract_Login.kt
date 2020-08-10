package com.noble.activity.RAZA_3.Login

import android.content.Context
import com.noble.activity.RAZA_3.Register.Json_Account
import com.noble.activity.RAZA_3.Register.Json_Account_Load

interface Contract_Login {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showAlert()
        fun startMainTabActivity()

        var my_account: Json_Account_Load
    }

    interface Presenter {
        fun setMyInfo(myemail: String?, mypass:String?)
        fun ted(context: Context)
        fun close()
    }

}