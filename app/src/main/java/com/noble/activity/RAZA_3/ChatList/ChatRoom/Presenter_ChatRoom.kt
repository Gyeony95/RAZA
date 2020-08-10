package com.noble.activity.RAZA_3.ChatList.ChatRoom

import android.content.Context
import android.util.Log
import com.noble.activity.RAZA_3.ChatList.ChatList
import com.noble.activity.RAZA_3.Network.ApiFactory
import com.noble.activity.RAZA_3.Network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class Presenter_ChatRoom() : Contract_ChatRoom.Presenter {

    override lateinit var view: Contract_ChatRoom.View

    override fun first_check(context: Context, roomName: String) {
        view.showProgress()
        val service = ApiFactory.placeholderApi
        //GlobalScope는 코루틴 계층을 만들수있는 도구
        GlobalScope.launch(Dispatchers.Main) {
            val postRequest = service.get_check_first("json")
            try {
                val response = postRequest.await()
                if(response.isSuccessful){
                    val posts = response.body()

                    var length:Int = 0//리스트 한번 돌리는 역할
                    if (posts != null) {
                        while(length < posts.size){

                            var aaa = posts.get(length)
                            //내 이메일과 돌리다 나온 subject이메일이 같으면
                            Log.e("user_one", aaa.user_one.toString())

                            //user_one user_two 둘중 하나라도 내아이디와 일치할 경우
                            if(aaa.roomName.equals(roomName)){
                               view.first_false()
                                view.hideProgress()

                            }else{
                            }
                            length++
                        }
                        view?.hideProgress()
                    }
                    else{
                        Log.e("여기 널임", "에러남!")
                        view?.hideProgress()
                    }


                }else{
                    Log.d("MainActivity ", response.errorBody()!!.string())
                }

            }catch (e: Exception){
                Log.e("로그", "ㅁㄴㅇ")
            }
        }
    }

    override fun load_item(context: Context, roomName:String, arrayList:ArrayList<Chat_Room>) {
        view.showProgress()
        val service = ApiFactory.placeholderApi
        //GlobalScope는 코루틴 계층을 만들수있는 도구
        GlobalScope.launch(Dispatchers.Main) {
            val postRequest = service.get_chat_bubble("json")
            try {
                val response = postRequest.await()
                if(response.isSuccessful){
                    val posts = response.body()

                    var length:Int = 0//리스트 한번 돌리는 역할
                    if (posts != null) {
                        while(length < posts.size){

                            var aaa = posts.get(length)
                            Log.e("roomName", roomName)
                            Log.e("roomName2", aaa.roomName)


                            if(aaa.roomName.equals(roomName)){
                                var dict:Chat_Room = Chat_Room(aaa.roomName,aaa.user_one,aaa.user_two,aaa.user_id,aaa.name,aaa.script,aaa.profile_image,aaa.date_time,aaa.non_read_check)
                                Log.e("user_id", aaa.user_id.toString())
                                arrayList.add(dict)
                                view.refresh()


                            }
                            length++
                        }
                        view?.hideProgress()
                    }
                    else{
                        Log.e("여기 널임", "에러남!")
                        view?.hideProgress()
                    }


                }else{
                    Log.d("MainActivity ", response.errorBody()!!.string())
                }

            }catch (e: Exception){
                Log.e("로그", "ㅁㄴㅇ")
            }
        }
    }


    override fun create_list(context: Context, jsonObject: JSONObject) {
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
        var chatList:ChatList = ChatList(jsonObject.getString("roomName"),jsonObject.getInt("user_one"),jsonObject.getInt("user_two"),jsonObject.getString("name"),jsonObject.getString("name2"),jsonObject.getString("profile_image"),jsonObject.getString("script"),jsonObject.getString("date_time"),0)
        var comment_test : Call<ChatList> = apiService.create_chat_list("json",chatList)
        comment_test.enqueue(object : Callback<ChatList> {

            override fun onResponse(call: Call<ChatList>, response: Response<ChatList>) {
                if (response.isSuccessful) {
                    Log.e("post", "성공")
                } else {
                    val StatusCode = response.code()
                    Log.e("post", "Status Code : $StatusCode")
                }
                Log.e("tag", "upload success:response=" + response.raw())

            }
            override fun onFailure(call: Call<ChatList>, t: Throwable) {

                Log.e("D_Test", "페일!")
            }
        })

    }
}