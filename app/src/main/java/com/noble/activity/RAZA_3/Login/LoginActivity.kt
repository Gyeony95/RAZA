package com.noble.activity.RAZA_3.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.Register.Json_Account_Load
import com.noble.activity.RAZA_3.Register.RegisterActivity
import com.noble.activity.RAZA_3.views.BaseActivity2
import com.noble.activity.RAZA_3.FindFriends.FindFriendsActivity
import kotlinx.android.synthetic.main.activity_login.*

import android.view.inputmethod.InputMethod
import com.kakao.auth.AuthType
import com.kakao.auth.Session


class LoginActivity() : Contract_Login.View , BaseActivity2(){

    private var callback: InputMethod.SessionCallback? = null

    lateinit var presenter: Presenter_Login
    lateinit internal var filePath: String
    override  lateinit var my_account: Json_Account_Load


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


       // uploadPhoto()
        var context: Context = this

        presenter = Presenter_Login().apply {
            view = this@LoginActivity
            mContext = this@LoginActivity

        }



        //카메라 권한 허용해주는 부분
        presenter.ted(context)

        //관리자 로그인 버튼
        logo_image.setOnClickListener {
            val intent = Intent(this, FindFriendsActivity::class.java)
            startActivity(intent)
            finish()
        }

        //로그인 버튼을 누르면?!
        login_btn.setOnClickListener {
            presenter.setMyInfo(email_input.text.toString(), password_input.text.toString())


        }

        //회원가입 버튼
        sign_up_btn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        btn_kakao_login.setOnClickListener {
            var session :Session = Session.getCurrentSession()

            session.addCallback(SessionCallback())

            session.open(AuthType.KAKAO_LOGIN_ALL, this@LoginActivity)
        }

    }


    /*
    //테스트
    private fun uploadPhoto() {

        val image_File = File("/storage/emulated/0/DCIM/Camera/crocoimage.png")

        val clientBuilder = Builder()
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
                .createFormData("profile_image", image_File.name, requestBody)


        var comment_test : Call<Json_Account> = apiService.uploadFile("json", multiPartBody ,RequestBody.create(okhttp3.MultipartBody.FORM, "a"))
        comment_test.enqueue(object : Callback<Json_Account> {

            override fun onResponse(call: Call<Json_Account>, response: Response<Json_Account>) {
                if (response.isSuccessful) {
                    Log.e("post", "성공")
                } else {
                    val StatusCode = response.code()
                    Log.e("post", "Status Code : $StatusCode")
                }
                Log.e("tag", "upload success:response=" + response.raw())

            }
            override fun onFailure(call: Call<Json_Account>, t: Throwable) {
                Log.e("aaa", image_File.toString())
                Log.e("aaa", image_File.name)
                Log.e("D_Test", "페일!")
            }
        })



    }


*/


    override fun showProgress() {
        showProgressDialog(R.string.basic_loading)
    }

    override fun hideProgress() {
        hideProgressDialog()
    }

    override fun showAlert() {
        showAlertDialog(R.string.basic_alert_dialog_msg) { finish() }
    }

    override fun startMainTabActivity() {
        val intent = Intent(this, FindFriendsActivity::class.java)
        //val intent = Intent(this, ChatListActivity::class.java)
        startActivity(intent)
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        presenter.close()
    }
    companion object {
        private const val RC_SIGN_IN = 101
    }



}
