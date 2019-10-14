package com.noble.activity.RAZA_3.Mypage

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.noble.activity.RAZA_3.views.BaseActivity2
import kotlinx.android.synthetic.main.activity_mypage.*

import java.net.URL

import android.util.Log
import android.widget.Toast

import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.etc_process.URL_to_Bitmap_Task
import java.io.File
import java.io.IOException
import java.util.ArrayList
import com.soundcloud.android.crop.Crop
import kotlinx.android.synthetic.main.activity_profile.*

class MypageActivity :BaseActivity2(), Contract_Mypage.View {


    internal lateinit var preferences: SharedPreferences

    var user_id:String=""
    internal val PICK_IMAGE_REQUEST = 1
    internal var filePath: String? = null
    lateinit var tempFile:File //찍은 사진 넣는부분
    var presenter_Mypage:Presenter_Mypage = Presenter_Mypage()
    private val PICK_FROM_ALBUM = 1
    private val PICK_FROM_CAMERA = 2
    internal lateinit var lastUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        tedPermission()

        var context:Context = this



        presenter_Mypage = Presenter_Mypage().apply {
            view = this@MypageActivity
            context = this@MypageActivity
        }



        my_Profile_Exit.setOnClickListener {
           finish()

        }

        my_Profile_Image.setOnClickListener{
           presenter_Mypage.change_dialog(this)
           // imageBrowse()
        }

        //이름과 국적 바꿀수 있게
        my_Profile_Profile_Change.setOnClickListener {


            val dialogView = layoutInflater.inflate(R.layout.item_update_firiends_list, null)

            presenter_Mypage.chang_profile(context, preferences.getInt("id",0),dialogView)
        }

        //누가 로그인했는지 보여주는 부분
        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)
        user_id = preferences.getString("user", "")

        my_Profile_Name.text = preferences.getString("name", "")
        my_Profile_Country.text = preferences.getString("country", "")
        my_Profile_Like_Count.text = preferences.getInt("like",0).toString()

        var test_task: URL_to_Bitmap_Task = URL_to_Bitmap_Task()
        test_task = URL_to_Bitmap_Task().apply {
            url = URL(preferences.getString("profile_image", ""))
        }

        var bitmap:Bitmap = test_task.execute().get()


        val resizedbitmap = Bitmap.createScaledBitmap(bitmap, 650, 650, true)
        my_Profile_Image.setImageBitmap(resizedbitmap)



    }

    //앨범 꺼내주는 부분
    override fun imageBrowse() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }
    //카메라 꺼내주는 부분
    override fun cameraBrowse() {
        presenter_Mypage = Presenter_Mypage().apply {
            view = this@MypageActivity
            context = this@MypageActivity
        }


        val cameraintent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            tempFile = presenter_Mypage.createImageFile()
        } catch (e: IOException) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            finish()
            e.printStackTrace()
        }

        if (tempFile != null) {

            val photoUri = Uri.fromFile(tempFile)
            Log.e("asd", tempFile.toString())
            Log.e("asd", photoUri.toString())

            cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(cameraintent, PICK_FROM_CAMERA)
            Log.e("asd", "일단 카메라 인텐트는 넘어감")
        }
    }

    private fun cropImage(photoUri: Uri) {//카메라 갤러리에서 가져온 사진을 크롭화면으로 보냄

        //Log.d("Tag", "tempFile : $tempFile")
        //갤러리에서 선택한 경우에는 tempFile 이 없으므로 새로 생성해줍니다.
        tempFile = presenter_Mypage.createImageFile()
        //크롭 후 저장할 Uri
        val savingUri = Uri.fromFile(tempFile)//사진촬여은 tempFile이 만들어져있어 넣어서 저장하면됨
        //하지만 갤러리는 크롭후에 이미지를 저장할 파일이 없기에 위 코드를넣어서 추가로 작성해줘야함

        lastUri = savingUri
        //이 유알아이가 최종적으로 내 프로필이 되는것임
        Log.e("saving", savingUri.toString())



        Crop.of(photoUri, savingUri).asSquare().start(this)
    }


    //권한 요청
    override fun tedPermission() {
       presenter_Mypage.tedPermission(this)
    }

    //결과 리턴받을수 있게끔 ㅇㅇ
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show()
            if (tempFile != null) {
                if (tempFile!!.exists()) {
                    if (tempFile!!.delete()) {
                        Log.e("Tag", tempFile!!.absolutePath + " 삭제 성공")
                        //tempFile = null
                    }
                }
            }
            return
        }
        when (requestCode) {
            PICK_FROM_ALBUM -> {

                val photoUri = data!!.data
                Log.e("픽프롬 앨범", photoUri!!.toString())
                cropImage(photoUri)
            }
            PICK_FROM_CAMERA -> {
                Log.e("asd", "1")
                val photoUri = Uri.fromFile(tempFile)
                Log.e("asd", "2")
                Log.e("픽프롬 카메라", photoUri.toString())

                cropImage(photoUri)
            }
            Crop.REQUEST_CROP -> {
                setImage()
            }
        }

    }


    fun setImage(){
        val options = BitmapFactory.Options()
        val originalBm = BitmapFactory.decodeFile(tempFile!!.getAbsolutePath(), options)
        Log.d("Tag", "setImage : " + tempFile!!.getAbsolutePath())


        val resizedbitmap = Bitmap.createScaledBitmap(originalBm, 650, 650, true) // 이미지 사이즈 조정
        my_Profile_Image.setImageBitmap(resizedbitmap) // 이미지뷰에 조정한 이미지 넣기

        //업로드를 위해 이메일을 함께 보내줌
        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)

        presenter_Mypage.uploadPhoto(this,preferences.getInt("id", 0).toString(),tempFile!!.getAbsolutePath(),preferences.getString("user", ""))
        //tempFile = null


    }

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
    }


}
