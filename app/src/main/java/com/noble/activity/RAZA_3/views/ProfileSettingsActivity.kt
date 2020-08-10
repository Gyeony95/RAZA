package com.noble.activity.RAZA_3.views

import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.noble.activity.RAZA_3.Login.LoginActivity

import kotlinx.android.synthetic.main.activity_profile_settings.*


class ProfileSettingsActivity : AppCompatActivity(){
    //private lateinit var mFirebase: FirebaseHelper
    internal lateinit var preferences: SharedPreferences

    //var presenterProfileSetting :PresenterProfileSetting = PresenterProfileSetting()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.noble.activity.RAZA_3.R.layout.activity_profile_settings)

        //presenterProfileSetting.onCreate()

        back_image.setOnClickListener {

            finish()
        }


        logout_text.setOnClickListener {
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("로그아웃")
            dialog.setMessage("정말로 로그아웃 하시겠습니까?")
            dialog.setIcon(com.noble.activity.RAZA_3.R.mipmap.ic_launcher)

            fun toast_p() {
                Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                preferences = getSharedPreferences("MAIN_OBJECT", Context.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putString("user", "").apply()




/*
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
*/
               //앱 재시작
                restart()



            }
            fun toast_n(){


            }

            //버튼들 눌렀을때의 웬
            var dialog_listener = object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when(which){
                        DialogInterface.BUTTON_POSITIVE ->
                            toast_p()
                        DialogInterface.BUTTON_NEGATIVE ->
                            toast_n()
                    }
                }
            }

            dialog.setPositiveButton("YES",dialog_listener)
            dialog.setNegativeButton("NO",dialog_listener)
            //dialog.setNeutralButton("Cancel", null)
            dialog.show()
        }


    }


    fun restart() {


/*
        val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    */

        /*
        System.runFinalizersOnExit(true)
        System.exit(0)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
*/
        finishAffinity()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        //System.runFinalization()
        System.exit(0)


    }
}
