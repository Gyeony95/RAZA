package com.noble.activity.RAZA_3.Mypage

import android.content.Context
import android.net.Uri
import android.view.View
import com.noble.activity.RAZA_3.Register.Json_Account_Load
import java.io.File
import java.net.URI

interface Contract_Mypage {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showAlert()
        fun startMainTabActivity()
        fun tedPermission()
        fun imageBrowse()
        fun cameraBrowse()
    }

    interface Presenter {

        var view: View

        fun tedPermission(context: Context)
        fun createImageFile(): File
        fun change_dialog(context: Context)
        fun getPath(uri: Uri, context: Context):String?
        fun uploadPhoto(context: Context,id:String, path:String?, email:String)
        fun image_set()
        fun chang_profile(context: Context, id:Int, dialogView:android.view.View)
        fun close()
    }
}