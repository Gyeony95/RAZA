package com.noble.activity.RAZA_3.etc_process

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
//import com.google.android.gms.flags.impl.SharedPreferencesFactory.getSharedPreferences
import java.net.URL

class URL_to_Bitmap_Task() : AsyncTask<Void, Void, Bitmap>() {
    //액티비티에서 설정해줌
    lateinit var url:URL
    override fun doInBackground(vararg params: Void?): Bitmap {
        val bitmap = BitmapFactory.decodeStream(url.openStream())
        return bitmap
    }

    override fun onPreExecute() {
        super.onPreExecute()

    }

    override fun onPostExecute(result: Bitmap) {
        super.onPostExecute(result)

    }



}