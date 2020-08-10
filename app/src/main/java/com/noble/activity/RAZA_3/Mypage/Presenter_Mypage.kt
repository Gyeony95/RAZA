package com.noble.activity.RAZA_3.Mypage

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.noble.activity.RAZA_3.Network.ApiFactory
import com.noble.activity.RAZA_3.Network.ApiService
import com.noble.activity.RAZA_3.Network.Retrofit_Frame
import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.Register.Json_Account
import kotlinx.android.synthetic.main.acitivity_friends_list.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class Presenter_Mypage:Contract_Mypage.Presenter {




    lateinit override var view: Contract_Mypage.View
    internal var preferences: SharedPreferences? = null
    lateinit var context: Context
    var filePath : String? = null


    //권한 허용해주는 부분
    override fun tedPermission(context: Context) {
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

    override fun image_set() {
        if (filePath != null) {
            //uploadFoto(filePath)
        } else {
            Toast.makeText(context, "Image not selected!", Toast.LENGTH_LONG).show()
        }
    }
    private fun uploadFoto(imageFile: String) {

        val image_File = File(imageFile)

        //var postApi: Retrofit_Frame = Retrofit_Frame()


        val retrofit = Retrofit.Builder()
                .baseUrl(ApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()


        val postApi = retrofit.create(ApiService::class.java!!)

        val requestBody = RequestBody.create(MediaType.parse("multipart/data"), image_File)
        val multiPartBody = MultipartBody.Part
                .createFormData("model_pic", image_File.name, requestBody)


    }

    //이름이나 국적 변경할때 뷰에서 선언되는 부분
    override fun chang_profile(context: Context, id:Int, dialogView:View) {
        var dialog = AlertDialog.Builder(context)
        dialog.setTitle("프로필 변경")
        dialog.setMessage("프로필을 변경하시겠습니까?")
        dialog.setIcon(R.mipmap.ic_launcher)

        fun toast_p() {
            var dialog = AlertDialog.Builder(context)
            dialog.setTitle("프로필 변경")
            dialog.setMessage("프로필을 변경하시겠습니까?")
            dialog.setIcon(R.mipmap.ic_launcher)

            fun toast_p() {
               name_change(id,context, dialogView)
            }
            fun toast_n(){
                country_change(id,context,dialogView)
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

            dialog.setPositiveButton("이름변경",dialog_listener)
            dialog.setNegativeButton("국적변경",dialog_listener)
            //dialog.setNeutralButton("Cancel", null)
            dialog.show()


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

    //상위 메소드에서 호출되는 부분, 이름바꿀때 사용
    fun name_change(id:Int,context: Context, dialogView:View){
        //바꿔야함
        val builder = AlertDialog.Builder(context)
        val dialogText = dialogView.findViewById<EditText>(R.id.update_Friends_Name_Text)

        //val dialogRatingBar = dialogView.findViewById<RatingBar>(R.id.dialogRb)




        builder.setView(dialogView)
                .setPositiveButton("확인") { dialogInterface, i ->
                    var mainString:String = "asd"


                    mainString = dialogText.text.toString()
                    preferences = context.getSharedPreferences("USERSIGN", 0)

                    val editor = preferences!!.edit()
                    editor.putString("name", mainString)
                    editor.apply()
                    var retrofit_Frame:Retrofit_Frame = Retrofit_Frame()
                    retrofit_Frame.patch_name_method(id,mainString)
                    Log.e("dd", "저장!")

                }
                .setNegativeButton("취소") { dialogInterface, i ->
                    /* 취소일 때 아무 액션이 없으므로 빈칸 */
                }
                .show()



    }


    //상위 메소드에서 호출되는 부분, 국적바꿀때 사용
    fun country_change(id:Int,context: Context, dialogView:View){
        //바꿔야함
        val builder = AlertDialog.Builder(context)
        val dialogText = dialogView.findViewById<EditText>(R.id.update_Friends_Name_Text)

        //val dialogRatingBar = dialogView.findViewById<RatingBar>(R.id.dialogRb)

        builder.setView(dialogView)
                .setPositiveButton("확인") { dialogInterface, i ->
                    var mainString:String = "asd"


                    mainString = dialogText.text.toString()
                    preferences = context.getSharedPreferences("USERSIGN", 0)

                    val editor = preferences!!.edit()
                    editor.putString("country", mainString)
                    editor.apply()
                    var retrofit_Frame:Retrofit_Frame = Retrofit_Frame()
                    retrofit_Frame.patch_country_method(id,mainString)
                    Log.e("dd", "저장!")

                }
                .setNegativeButton("취소") { dialogInterface, i ->
                    /* 취소일 때 아무 액션이 없으므로 빈칸 */
                }
                .show()

    }


    override fun getPath(contentUri: Uri, context: Context): String? {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val loader = CursorLoader(context, contentUri, proj, null, null, null)
            val cursor = loader.loadInBackground()
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val result = cursor.getString(column_index)
            cursor.close()
            return result


    }

    //사진 올리는부분
    override fun uploadPhoto(context: Context,id:String, path:String?, email:String) {


        val image_File = File(path)

        Log.e("asd", "여기서 업로드!")
        Log.e("asd", image_File.name)

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
        val requestBody = RequestBody.create(MediaType.parse("multipart/data"), image_File)
        val multiPartBody = MultipartBody.Part
                .createFormData("profile_image", image_File.name, requestBody)


        var comment_test : Call<Json_Account> = apiService.uploadFile("registers",id,"json", multiPartBody ,RequestBody.create(okhttp3.MultipartBody.FORM, email))
        comment_test.enqueue(object : Callback<Json_Account> {

            override fun onResponse(call: Call<Json_Account>, response: Response<Json_Account>) {
                if (response.isSuccessful) {
                    Log.e("post", "성공")
                    var retrofit_Frame:Retrofit_Frame = Retrofit_Frame()
                    retrofit_Frame.get_id_method(context, id)
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


    //이미지 클릭하면 이거 나옴
    override fun change_dialog(context: Context) {
        var dialog = AlertDialog.Builder(context)
        dialog.setTitle("프로필 변경")
        dialog.setMessage("프로필사진을 바꾸시겠습니까?")

        fun toast_p() {


            var dialog = AlertDialog.Builder(context)
            dialog.setTitle("프로필 변경")
            dialog.setMessage("프로필사진을 바꾸시겠습니까?")

            fun toast_p() {
                //앨범 꺼내주는 함수
                view.imageBrowse()

            }
            fun toast_n(){
                view.cameraBrowse()
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

            dialog.setPositiveButton("앨범",dialog_listener)
            dialog.setNegativeButton("카메라",dialog_listener)
//dialog.setNeutralButton("Cancel", null)
            dialog.show()

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

        dialog.setPositiveButton("예",dialog_listener)
        dialog.setNegativeButton("아니오",dialog_listener)
//dialog.setNeutralButton("Cancel", null)
        dialog.show()
    }

    //사진파일 만들어주는 부분
    @Throws(IOException::class)
    override fun createImageFile(): File {
        // 이미지 파일 이름 ( blackJin_{시간}_ )
        val timeStamp = SimpleDateFormat("HHmmss").format(Date())
        val imageFileName = "RAZA_" + timeStamp + "_"

        // 이미지가 저장될 폴더 이름 ( RAZA )
        val storageDir = File(Environment.getExternalStorageDirectory().toString() + "/RAZA/")
        if (!storageDir.exists()) storageDir.mkdirs()

        // 파일 생성
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        Log.d("Tag", "createImageFile : " + image.absolutePath)
        //lastUri = image.getAbsolutePath();
        return image
    }


    override fun close() {
    }
}