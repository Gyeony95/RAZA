package com.noble.activity.RAZA_3.Register

import android.content.Context

interface Contract_Register {

    interface View{

    }

    interface Presenter {
        var view: View

        fun add_account( account: Json_Account)

    }
}