package com.noble.activity.RAZA_3.Network;





import com.noble.activity.RAZA_3.ChatList.ChatList;
import com.noble.activity.RAZA_3.FriendsList.Json_Friends;
import com.noble.activity.RAZA_3.LastestFriends.Json_Lastest;
import com.noble.activity.RAZA_3.Register.Json_Account;
import com.noble.activity.RAZA_3.Register.Json_Account_Load;
import com.noble.activity.RAZA_3.test_file.Json_Account_test;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    public static final String API_URL = "http://ec2-15-164-104-42.ap-northeast-2.compute.amazonaws.com:8000/";



    @GET("{path}/")
    Call<ResponseBody> get_Data(@Path("path") String rest, @Query("format") String json);


    //@ FormUrlEncoded 있으면 안됨
    @POST("{path}/")
    Call<Json_Lastest> post_Data_Lastest_friends(@Path("path") String rest, @Query("format") String json, @Body Json_Lastest json_lastest);

    @POST("registers/")
    Call<Json_Account> post_Data_Json_Account(@Query("format") String json, @Body Json_Account json_account);

    @POST("chat_lists/")
    Call<ChatList> create_chat_list(@Query("format") String json, @Body ChatList chatList);

    @POST("friends/")
    Call<Json_Friends> post_Data_Json_Friends(@Query("format") String json, @Body Json_Friends json_friends);

    @FormUrlEncoded
    @PATCH("registers/{pk}/")
    Call<ResponseBody> patch_Name(@Path("pk") int pk,@Query("format") String json,@Field("name") String name);

    @FormUrlEncoded
    @PATCH("registers/{pk}/")
    Call<ResponseBody> patch_Country(@Path("pk") int pk,@Query("format") String json,@Field("country") String country);


    @DELETE("{path}/{pk}/")
    Call<ResponseBody> delete_Data(@Path("path") String rest,@Path("pk") int pk, @Query("format") String json);


    @GET("{path}/{pk}/")
    Call<Json_Account_Load> get_pk_Data(@Path("path") String rest, @Path("pk") String pk, @Query("format") String json);

    @Multipart
    @POST("registers/?format=json")
    Call<RequestBody> signup(
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("country") RequestBody country,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part profile_image,
            @Part MultipartBody.Part profile_image2,
            @Part("gender") RequestBody gender,
            @Part("profile_text") RequestBody profile_text,
            @Part("like") RequestBody like

    );


    @Multipart
    @POST("chat_lists/?format=json")
    Call<RequestBody> create_room_small(
            @Part("user_one") RequestBody user_one,
            @Part("user_two") RequestBody user_two,
            @Part("name") RequestBody name,
            @Part("script") RequestBody script,
            @Part("profile_image") RequestBody profile_image,
            @Part("date_time") RequestBody date_time,
            @Part("non_read_count") RequestBody non_read_count

    );


    //이미지만 바꾸는거 안돼서 이메일을 같이 바꿔주는데 이메일은 원래 있던 값 그대로
    @Multipart
    @PATCH("{path}/{id}/")
    Call<Json_Account>  uploadFile(@Path("path") String path,@Path("id") String id,@Query("format") String json,@Part MultipartBody.Part profile_image,  @Part("email") RequestBody email);



    @POST("myapp/upload/")
    Call<Json_Account_test>  post_uploadFile(@Body Json_Account_test test);





}
