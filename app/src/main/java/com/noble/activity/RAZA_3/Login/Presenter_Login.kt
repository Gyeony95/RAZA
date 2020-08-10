package com.noble.activity.RAZA_3.Login

import android.Manifest
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
//import com.google.android.gms.flags.impl.SharedPreferencesFactory.getSharedPreferences
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.noble.activity.RAZA_3.Network.ApiFactory
import com.noble.activity.RAZA_3.Register.Json_Account
import com.noble.activity.RAZA_3.Register.Json_Account_Load
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import java.util.ArrayList

class Presenter_Login:Contract_Login.Presenter {
    override fun ted(context: Context) {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                // 권한 요청 성공

            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                // 권한 요청 실패

            }
        }

        TedPermission.with(context)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("사진 및 파일을 저장하기 위하여 접근 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check()
    }


    internal var preferences: SharedPreferences? = null
    var view: Contract_Login.View? = null
    var compositeDisposable: CompositeDisposable? = CompositeDisposable()
    lateinit var mContext:Context


    override fun setMyInfo(myemail: String?, mypass:String?) {
        view?.showProgress()
        preferences = mContext.getSharedPreferences("USERSIGN", 0)
        //데이터 불러오는 부분
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
                            Log.e("myemail", myemail)
                            Log.e("aaa.email", aaa.email)
                            if(aaa.email.equals(myemail)){
                                if(aaa.password.equals(mypass)){
                                    //내 객체 생성
                                    //my_object = Json_Account_Load(aaa.id,aaa.name, aaa.email, aaa.country, aaa.password, aaa.profile_image, aaa.profile_image2, aaa.gender, aaa.profile_text, aaa.like)
                                    my_object = aaa
                                    identifyNewUser(my_object)
                                    break
                                }
                            }else{
                                Log.e("일치 없음", "에러남!")
                                view?.hideProgress()
                            }
                            length++
                        }
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

    private fun identifyNewUser(user: Json_Account_Load) {

        val editor = preferences!!.edit()
        editor.putString("user", user.email)
        editor.putString("profile_image", user.profile_image)
        editor.putString("country", user.country)
        editor.putString("name", user.name)
        editor.putInt("id", user.id)
        editor.apply()

        Log.e("aaaa", "$user.profile_image")
        val image = preferences!!.getString("user", "")
        Log.e("asd", image)
        view?.hideProgress()
        //view?.my_account = Json_Account(user.name, user.email, user.country,user.password,user.profile_image,user.profile_image2, user.gender, user.profile_text, user.like)
        view?.my_account = user
        view?.startMainTabActivity()
    }


    override fun close() {
        view = null
        compositeDisposable?.clear()
    }
}