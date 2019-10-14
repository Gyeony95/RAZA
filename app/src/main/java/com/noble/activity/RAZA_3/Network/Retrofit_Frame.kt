package com.noble.activity.RAZA_3.Network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.noble.activity.RAZA_3.FriendsList.Json_Friends
import com.noble.activity.RAZA_3.FriendsList.Presenter_FriendsList
import com.noble.activity.RAZA_3.LastestFriends.Json_Lastest
import com.noble.activity.RAZA_3.Register.Json_Account
import com.noble.activity.RAZA_3.Register.Json_Account_Load
import com.noble.activity.RAZA_3.test_file.Json_Account_test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.RequestBody




class Retrofit_Frame {

    internal lateinit var retrofit: Retrofit
    internal lateinit var apiService: ApiService
    //internal lateinit var comment: Call<Json_Test_Java>
    internal lateinit var comment: Call<ResponseBody>
    internal lateinit var comment_json_account_load: Call<Json_Account_Load>
    internal lateinit var comment_lastest_friends: Call<Json_Lastest>
    internal lateinit var comment_account: Call<RequestBody>
    internal lateinit var comment_friends: Call<Json_Friends>

    internal lateinit var comment_test: Call<Json_Account_test>
    var result:String = "test"
    internal var preferences: SharedPreferences? = null

    //입력한 rest를 기준으로 어디서 가져올지 정함
    //subject를 기준으로 어디서 넘어온건지 판단
    fun get_method(rest:String, subject:String, presenter_FriendsList: Presenter_FriendsList){
        retrofit = Retrofit.Builder().baseUrl(ApiService.API_URL).build()
        apiService = retrofit.create(ApiService::class.java)
        comment = apiService.get_Data(rest,"json")
        comment.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {

                    /*
                    if(subject == "FriendsList"){
                        //var presenter_FriendsList:Presenter_FriendsList = Presenter_FriendsList()
                        presenter_FriendsList.view.freindslist_data = response.body()!!.string().toString()
                        Log.e("연결된 프레센트 리스트 데이타", presenter_FriendsList.view.freindslist_data)

                    }
*/

                } catch (e: Exception) {
                    e.printStackTrace()
                    result = "에러"
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                Log.e("D_Test", "페일!")
                result = "페일"
            }
        })

        Log.e("result", result)


    }

    //프사 바꾸고 쉐어드에 값 바꿔주는 곳에서 사용
    fun get_id_method(context: Context, id:String){
        val service = ApiFactory.placeholderApi
        Log.e("이전", "실행")
        //GlobalScope는 코루틴 계층을 만들수있는 도구
        GlobalScope.launch(Dispatchers.Main) {
            val postRequest = service.getPosts()
            try {
                val response = postRequest.await()
                if(response.isSuccessful){
                    val posts = response.body()
                    var my_object:Json_Account_Load
                    var length:Int = 0//리스트 한번 돌리는 역할
                    if (posts != null) {

                        while(length < posts.size){
                            var aaa = posts.get(length)
                            //내 이메일과 돌리다 나온 이메일이 같으면 + 비밀번호가 같으면
                            if(aaa.id.toString().equals(id)){
                                //실질적인 내용
                                preferences = context.getSharedPreferences("USERSIGN", 0)
                                val editor = preferences!!.edit()
                                editor.putString("profile_image", aaa.profile_image)
                                editor.apply()
                            }
                            length++
                        }
                    }
                    else{
                    }

                }else{
                    Log.d("MainActivity ", response.errorBody()!!.string())
                }

            }catch (e: java.lang.Exception){
                Log.e("로그", "ㅁㄴㅇ")
            }
        }


    }

/*
    //실질적인 내용
    Log.e("profile_image", preferences!!.getString("profile_image", ""))
    preferences = context.getSharedPreferences("USERSIGN", 0)
    val editor = preferences!!.edit()
    editor.putString("profile_image", aaa.profile_image)
    editor.apply()
    Log.e("profile_image", preferences!!.getString("profile_image", ""))
  */
    fun post_method_lastest_friends(rest:String, lastest_friends:Json_Lastest){


        retrofit = Retrofit
                .Builder()
                .baseUrl(ApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        apiService = retrofit.create(ApiService::class.java)


        comment_lastest_friends = apiService.post_Data_Lastest_friends(rest, "json", lastest_friends)
        comment_lastest_friends.enqueue(object : Callback<Json_Lastest> {

            override fun onResponse(call: Call<Json_Lastest>, response: Response<Json_Lastest>) {
                if (response.isSuccessful) {
                    Log.e("post", "성공")
                } else {
                    val StatusCode = response.code()
                    Log.e("post", "Status Code : $StatusCode")
                }
            }
            override fun onFailure(call: Call<Json_Lastest>, t: Throwable) {

                Log.e("D_Test", "페일!")
            }
        })
    }
/*
    fun post_test(json_Account_test: Json_Account_test, multiPartBody:MultipartBody.Part){

        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)


        retrofit = Retrofit
                .Builder()
                .client(clientBuilder.build())
                .baseUrl(ApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        apiService = retrofit.create(ApiService::class.java)

        comment_test = apiService.uploadFile(json_Account_test)
        comment_test.enqueue(object : Callback<Json_Account_test> {

            override fun onResponse(call: Call<Json_Account_test>, response: Response<Json_Account_test>) {
                if (response.isSuccessful) {
                    Log.e("post", "성공")
                } else {
                    val StatusCode = response.code()
                    Log.e("post", "Status Code : $StatusCode")
                }
                Log.e("tag", "upload success:response=" + response.raw())

            }
            override fun onFailure(call: Call<Json_Account_test>, t: Throwable) {

                Log.e("D_Test", "페일!")
            }
        })
    }
*/



    fun post_method_json_friends(friends:Json_Friends){

        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)


        retrofit = Retrofit.Builder()
                .baseUrl(ApiService.API_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create()).build()
        apiService = retrofit.create(ApiService::class.java)


        comment_friends = apiService.post_Data_Json_Friends("json", friends)
        comment_friends.enqueue(object : Callback<Json_Friends> {

            override fun onResponse(call: Call<Json_Friends>, response: Response<Json_Friends>) {
                if (response.isSuccessful) {
                    Log.e("post", "성공")
                } else {
                    val StatusCode = response.code()
                    Log.e("post", "Status Code : $StatusCode")
                }
            }
            override fun onFailure(call: Call<Json_Friends>, t: Throwable) {

                Log.e("D_Test", "페일!")
            }
        })
    }


    //이거는 수정할 필요가 있을듯
    fun patch_name_method( id:Int, name:String){
        retrofit = Retrofit.Builder().baseUrl(ApiService.API_URL).build()
        apiService = retrofit.create(ApiService::class.java)
        comment = apiService.patch_Name(id, "json", name)
        comment.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    Log.e("D_Test", "post submitted to API." + response.body().toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                Log.e("D_Test", "페일!")
            }
        })
    }

    fun patch_country_method(id:Int, country:String){
        retrofit = Retrofit.Builder().baseUrl(ApiService.API_URL).build()
        apiService = retrofit.create(ApiService::class.java)
        comment = apiService.patch_Country(id, "json", country)
        comment.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    Log.e("D_Test", "post submitted to API." + response.body().toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                Log.e("D_Test", "페일!")
            }
        })
    }


    fun delete_method(rest:String, id:Int){
        retrofit = Retrofit.Builder().baseUrl(ApiService.API_URL).build()
        apiService = retrofit.create(ApiService::class.java)
        comment = apiService.delete_Data(rest, id, "json")
        comment.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    //Log.e("D_Test", response.body()!!.string())

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                Log.e("D_Test", "페일!")
            }
        })
    }



}