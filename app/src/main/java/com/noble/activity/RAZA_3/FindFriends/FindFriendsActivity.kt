package com.noble.activity.RAZA_3.FindFriends

import android.content.Intent
import android.os.Bundle

import android.hardware.Camera

import android.view.SurfaceView

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap

import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import com.google.gson.Gson
import com.noble.activity.RAZA_3.FindFriends.FindFriendsView.FindFriendsViewActivity
import com.noble.activity.RAZA_3.Mypage.MypageActivity
import com.noble.activity.RAZA_3.etc_process.CameraPreview
import com.noble.activity.RAZA_3.etc_process.URL_to_Bitmap_Task
import com.noble.activity.RAZA_3.views.BaseActivity
import com.noble.activity.RAZA_3.views.ProfileSettingsActivity
import kotlinx.android.synthetic.main.activity_find_friends.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL


class FindFriendsActivity : BaseActivity(1), ActivityCompat.OnRequestPermissionsResultCallback{

    internal lateinit var preferences: SharedPreferences
    internal lateinit var jsonObject: JSONObject
    internal var accountArray = JSONArray()
    internal var mainObject = JSONObject()
    internal  var json: String? = null
    internal var gson = Gson()

    internal var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private lateinit var surfaceView: SurfaceView//서페이스뷰 변수 생성
    private var mCameraPreview: CameraPreview? = null//카메라 프리뷰 변수 생성
    private var mLayout: View? = null  // Snackbar 사용하기 위해서는 View생성
    // (참고로 Toast에서는 Context가 필요)
    var check:Boolean = false
    var user_id:String = ""

    @SuppressLint("ResourceType")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.noble.activity.RAZA_3.R.layout.activity_find_friends)


        //누가 로그인했는지 보여주는 부분
        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)
        user_id = preferences.getString("user", "")




        var intent:Intent = getIntent()

        //프로필 바꿔주는 부분
        //제이슨어레이 길이만큼 반복
        // preview_Profile_Image.setCircleBackgroundColorResource(R.mipmap.ic_default_profile)





        /*
        // 상태바를 안보이도록 합니다.
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
*/
        // 화면 켜진 상태를 유지합니다.
        window.setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )


        preview_Profile_Image.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)

        }



        //mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK


        //레이아웃과 콘텐츠뷰 선언
        mLayout = findViewById(com.noble.activity.RAZA_3.R.id.layout_main)
        surfaceView = findViewById(com.noble.activity.RAZA_3.R.id.camera_preview_main)


        // 런타임 퍼미션 완료될때 까지 화면에서 보이지 않게 해야합니다.
        surfaceView!!.visibility = View.GONE



        setupBottomNavigation()




        val settings_image2 = findViewById(com.noble.activity.RAZA_3.R.id.settings_image) as ImageView

        settings_image2.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)

        }


        val button_test = findViewById(com.noble.activity.RAZA_3.R.id.button_test) as Button

        button_test.setOnClickListener {
            val intent = Intent(this, FindFriendsViewActivity::class.java)
            startActivity(intent)

        }
        /* 카메라 돌리는건 나중에 ,,
        val preview_Camera_Turn = findViewById(com.noble.activity.RAZA_3.R.id.preview_Camera_Turn) as ImageView

        preview_Camera_Turn.setOnClickListener {
            Log.e("asd", "asd")
            //CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_FRONT
            if(check){
                check = false
            }else{
                check = true
            }
            startCamera()
        }
*/

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            val writeExternalStoragePermission =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)


            if (cameraPermission == PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                startCamera()


            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                REQUIRED_PERMISSIONS[0]
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                ) {

                    Snackbar.make(
                            mLayout!!, "이 앱을 실행하려면 카메라와 외부 저장소 접근 권한이 필요합니다.",
                            Snackbar.LENGTH_INDEFINITE
                    ).setAction("확인") {
                        ActivityCompat.requestPermissions(
                                this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE
                        )
                    }.show()


                } else {
                    // 2. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                    // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                    ActivityCompat.requestPermissions(
                            this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE
                    )
                }

            }

        } else {

            val snackbar = Snackbar.make(
                    mLayout!!, "디바이스가 카메라를 지원하지 않습니다.",
                    Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setAction("확인") { snackbar.dismiss() }
            snackbar.show()
        }


    }

    public override fun onResume() {
        super.onResume()
        var test_task: URL_to_Bitmap_Task = URL_to_Bitmap_Task()
        test_task = URL_to_Bitmap_Task().apply {
            url = URL(preferences.getString("profile_image", ""))
        }
        var bitmap: Bitmap = test_task.execute().get()
        preview_Profile_Image.setImageBitmap(bitmap)

        preview_Profile_Name.setText(preferences!!.getString("name", ""))
        preview_Profile_Country.setText(preferences!!.getString("country", ""))
        preview_Profile_Like_Count.setText(preferences!!.getInt("like", 0).toString())
    }


    internal fun startCamera() {

        // Create the Preview view and set it as the content of this Activity.
        mCameraPreview = CameraPreview(this, this, CAMERA_FACING, surfaceView)

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grandResults: IntArray) {

        if (requestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {

            var check_result = true

            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }

            if (check_result) {

                startCamera()
            } else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                REQUIRED_PERMISSIONS[0]
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                ) {

                    Snackbar.make(
                            mLayout!!, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE
                    ).setAction("확인") { finish() }.show()

                } else {

                    Snackbar.make(
                            mLayout!!, "설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE
                    ).setAction("확인") { finish() }.show()
                }
            }

        }


    }




    companion object {

        private val TAG = "android_camera_example"
        private val PERMISSIONS_REQUEST_CODE = 100
        //private var CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK // Camera.CameraInfo.CAMERA_FACING_FRONT
        private var CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_FRONT
    }





}




