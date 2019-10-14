package com.noble.activity.RAZA_3.Network

import com.noble.activity.RAZA_3.ChatList.ChatList_Load
import com.noble.activity.RAZA_3.ChatList.ChatRoom.Chat_Room
import com.noble.activity.RAZA_3.ChatList.ChatRoom.Chat_Room_Load
import com.noble.activity.RAZA_3.FriendsList.Json_Friends_Load
import com.noble.activity.RAZA_3.LastestFriends.Json_Lastest_Load
import com.noble.activity.RAZA_3.Register.Json_Account
import com.noble.activity.RAZA_3.Register.Json_Account_Load

import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.POST
import retrofit2.http.Multipart



interface PlaceholderApi{

    @GET("registers/?format=json")
    fun getPosts() : Deferred<Response<List<Json_Account_Load>>>


    @GET("chat_lists/?format=json")
    fun getroom_smalls() : Deferred<Response<List<ChatList_Load>>>

    @GET("{path}/{id}/")
    fun get_id_Posts(@Path("path")  rest:String,@Path("id")  id:String, @Query("format")  json:String) : Deferred<Response<List<Json_Account_Load>>>


    @GET("registers/{num}")
    fun get_Choice( @Path("num") pk:Int, @Query("format") json:String ) : Deferred<Response<List<Json_Account>>>

    @GET("friends")
    fun get_Friends(@Query("format") json:String) : Deferred<Response<List<Json_Friends_Load>>>

    @GET("chat_lists")
    fun get_Chat_Small(@Query("format") json:String) : Deferred<Response<List<ChatList_Load>>>



    @GET("last_friends")
    fun get_Lastest(@Query("format") json:String) : Deferred<Response<List<Json_Lastest_Load>>>

    @GET("chat_lists")
    fun get_check_first(@Query("format") json:String) : Deferred<Response<List<ChatList_Load>>>

    @GET("chat_bubbles")
    fun get_chat_bubble(@Query("format") json:String) : Deferred<Response<List<Chat_Room_Load>>>

    @GET("/photos")
    fun getPhotos() : Deferred<Response<List<Json_Account>>>

    @Multipart
    @POST("razaapp/update")
    fun updateInfo(
            @Header("Authorization") token: String,
            @Part("fullName") fullName: RequestBody,
            @Part("address") address: RequestBody,
            @Part avatarPic: MultipartBody.Part?
    )

    @Multipart
    @POST("razaapp/upload")
    fun uploadImage(
    @Part("name") name:String,
    @Part("email") email:String,
    @Part("country") country:String,
    @Part("password") password:String,
    @Part profile_image:MultipartBody.Part ,
    @Part profile_image2:MultipartBody.Part ,
    @Part("gender") gender:String,
    @Part("profile_text") profile_text:String,
    @Part("like")  like:Int)


    @Multipart
    @POST("registers/?format=json")
    abstract fun chat_create(
            @Part("name") name: RequestBody,
            @Part("email") email: RequestBody,
            @Part("country") country: RequestBody,
            @Part("password") password: RequestBody,
            @Part profile_image: MultipartBody.Part,
            @Part profile_image2: MultipartBody.Part,
            @Part("gender") gender: RequestBody,
            @Part("profile_text") profile_text: RequestBody,
            @Part("like") like: RequestBody

    ): Call<RequestBody>

}