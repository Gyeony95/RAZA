
package com.noble.activity.RAZA_3.Register


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.Network.ApiService
import com.noble.activity.RAZA_3.Login.LoginActivity
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit


class RegisterActivity : AppCompatActivity(), Contract_Register.View{

    var check:Boolean = false//중복확인을 위한 불린값
    var check2:Boolean = false//비번, 비번확인 같은지

    internal lateinit var preferences: SharedPreferences
    internal lateinit var jsonObject: JSONObject
    internal var accountArray = JSONArray()
    internal var mainObject = JSONObject()
    internal  var json: String? = null

    lateinit var context:Context



    private lateinit var presenter: Presenter_Register

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var contract_Register:Contract_Register
        mainObject.put("accountInfo", accountArray)

        presenter = Presenter_Register().apply {
            view = this@RegisterActivity//프레젠터랑 이어주는 부분

        }


        next_btn.setOnClickListener {
            //빈칸있는지 검사
            if(register_Email.text.toString().equals("") || register_Name.text.toString().equals("") || register_Country.text.toString().equals("") ||register_PW.text.toString().equals("") ||register_PW_2.text.toString().equals("")){
                Toast.makeText(this, "빈칸을 모두 채워주세요", Toast.LENGTH_SHORT).show()
                Log.e("asd", "Asd")
            }else if(check == false){ //중복체크가 안되었을 경우 검사
                Toast.makeText(this, "아이디 중복체크를 해주세요", Toast.LENGTH_SHORT).show()
            }

            //비밀번호와 비밀번호 확인이 같을때
            if(register_PW.text.toString().equals(register_PW_2.text.toString())){
                check2 = true

            }else{//같지않을때
                check2 = false
            }

            //중복체크가 완료되고 비밀번호와 비밀번호 확인이 같을때
            if(check && check2){
                //일반 바로 회원가입 완료시킴
                Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                //데이터베이스에 저장

                var item_data= Json_Account(register_Name.text.toString(),
                        register_Email.text.toString(),
                        register_Country.text.toString(),
                        register_PW.text.toString(),
                        "R.mipmap.ic_default_profile",
                        "R.mipmap.ic_default_profile",
                        register_Gender.text.toString(),
                        register_Profile_Text.text.toString(),
                        0)
                //프레젠터로 넘겨서 데이터 추가시키기
                presenter.add_account(item_data)

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        }



        regist_Button.setOnClickListener {
            Toast.makeText(this, "중복체크를 완료했습니다.", Toast.LENGTH_SHORT).show()
            check = true//일단 데이터베이스 없으니까 바로 트루로 만들어줌

        }



    }



}
