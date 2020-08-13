package com.noble.activity.RAZA_3.Register

import android.content.Context
import android.util.Log
import com.noble.activity.RAZA_3.Network.ApiService
import com.noble.activity.RAZA_3.Network.Retrofit_Frame
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class Presenter_Register() :Contract_Register.Presenter {


    lateinit override var view: Contract_Register.View

    override fun add_account( account: Json_Account) {

        val image_File = File("/storage/emulated/0/DCIM/Camera/20200807_214259.jpg")

        Log.e("aaa", image_File.toString())
        Log.e("aaa", image_File.name)

        //var test : Json_Account_test = Json_Account_test("b", "b", "b", "b", multiPartBody, multiPartBody, "b", "b", 0)


        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)


        val retrofit = Retrofit.Builder()
                .baseUrl(ApiService.API_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()


        val apiService = retrofit.create(ApiService::class.java!!)

        val requestBody = RequestBody.create(MediaType.parse("multipart/data"), image_File)
        val multiPartBody = MultipartBody.Part
                .createFormData("dd", image_File.name, requestBody)

        val multiPartBody2 = MultipartBody.Part
                .createFormData("dd", image_File.name, requestBody)
        Log.e("bbb", multiPartBody.toString())
        Log.e("bbb",image_File.name)
        //lateinit var comment_test: Call<Json_Account_test>



        var comment_test : Call<RequestBody> = apiService.signup(
                RequestBody.create(okhttp3.MultipartBody.FORM, account.name),
                RequestBody.create(okhttp3.MultipartBody.FORM, account.email),
                RequestBody.create(okhttp3.MultipartBody.FORM, account.country),
                RequestBody.create(okhttp3.MultipartBody.FORM, account.password),
                multiPartBody,
                multiPartBody2,
                RequestBody.create(okhttp3.MultipartBody.FORM, account.gender),
                RequestBody.create(okhttp3.MultipartBody.FORM, account.profile_text),
                RequestBody.create(okhttp3.MultipartBody.FORM, account.like.toString())
        )
        comment_test.enqueue(object : Callback<RequestBody> {

            override fun onResponse(call: Call<RequestBody>, response: Response<RequestBody>) {
                if (response.isSuccessful) {
                    Log.e("post", "성공")
                } else {
                    val StatusCode = response.code()
                    Log.e("post", "Status Code : $StatusCode")
                }
                Log.e("tag", "upload success:response=" + response.raw())

            }
            override fun onFailure(call: Call<RequestBody>, t: Throwable) {
                Log.e("aaa", image_File.toString())
                Log.e("aaa", image_File.name)
                Log.e("D_Test", "페일!")
            }
        })

    }
}