package com.noble.activity.RAZA_3.FriendsList

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.noble.activity.RAZA_3.Network.ApiFactory
import com.noble.activity.RAZA_3.Network.ApiService
import com.noble.activity.RAZA_3.Network.Retrofit_Frame
import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.Register.Json_Account
import com.noble.activity.RAZA_3.etc_process.SwipeToDeleteCallback
import com.noble.activity.RAZA_3.etc_process.SwipeToUpdateCallback
import com.noble.activity.RAZA_3.etc_process.URL_to_Bitmap_Task
import com.noble.activity.RAZA_3.views.BaseActivity
import com.noble.activity.RAZA_3.views.ProfileSettingsActivity
import kotlinx.android.synthetic.main.acitivity_friends_list.*
import kotlinx.android.synthetic.main.activity_find_friends.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class FriendsListActivity() : BaseActivity(2) , Contract_FriendsList.View{



    internal lateinit var preferences: SharedPreferences
    internal lateinit var jsonObject: JSONObject
    internal var accountArray = JSONArray()
    internal var friendsArray = JSONArray()
    internal var mainObject = JSONObject()
    internal  var json: String? = null
    internal var gson = Gson()
    var user_id:String=""
    var user_num:Int =0
    var user_name:String =""
    var user_country:String = ""
    override lateinit var freindslist_data: String

    internal var testArray = JSONArray()
    internal var testObject = JSONObject()
    internal lateinit var presenter_FriendsList:Presenter_FriendsList
    //리사이클러뷰
    var arrayList = arrayListOf<Json_Friends_Load>(
            //Friends_List("김철수", "한국", "ic_default_profile")

    )
    val mAdapter = AdapterFriendsList(this, arrayList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_friends_list)

        setupBottomNavigation()


        var context: Context = this
        freindslist_data = ""

        presenter_FriendsList = Presenter_FriendsList().apply {
            view = this@FriendsListActivity
        }


        //어댑터 선언

        Friends_List_Recyclerview.adapter = mAdapter
        //레이아웃 매니저 선언
        val lm = LinearLayoutManager(this)
        Friends_List_Recyclerview.layoutManager = lm
        Friends_List_Recyclerview.setHasFixedSize(true)//아이템이 추가삭제될때 크기측면에서 오류 안나게 해줌
       // Friends_List_Recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))//임시


        //누가 로그인했는지 보여주는 부분
        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)
        user_id = preferences.getString("user", "")//이거 이메일
        user_num = preferences.getInt("id", 0)
        user_name = preferences.getString("name", "")
        user_country = preferences.getString("country", "")

        //내정보 셋팅해주는 부분
        friends_List_Name.setText(user_name)
        friends_List_Country.setText(user_country)
        var test_task: URL_to_Bitmap_Task = URL_to_Bitmap_Task()
        test_task = URL_to_Bitmap_Task().apply {
            url = URL(preferences.getString("profile_image", ""))
        }
        var bitmap: Bitmap = test_task.execute().get()
        friends_List_Image.setImageBitmap(bitmap)
        //친구 정보 불러오는 부분

        Log.e("asd", "1")
        presenter_FriendsList.setMyFriendsInfo(user_id , arrayList)
        Log.e("asd", "1-2")




        val settings_image2 = findViewById(R.id.settings_image) as ImageView

        settings_image2.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }





        //다이얼로그 띄울부분분
       plus_Friends_Button.setOnClickListener {
           val dialogView = layoutInflater.inflate(R.layout.custom_search_dialig, null)
           val dialogText = dialogView.findViewById<EditText>(R.id.search_Text)

            presenter_FriendsList.plus_Friends_Button(dialogView,dialogText ,user_id,mAdapter)
        }


        //오 -> 왼 스와이프 삭제과정
        val L_swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                presenter_FriendsList
                        .removeItems(context,arrayList,mAdapter,viewHolder.adapterPosition)

            }
        }

        val L_itemTouchHelper = ItemTouchHelper(L_swipeHandler)
        L_itemTouchHelper.attachToRecyclerView(Friends_List_Recyclerview)



        //왼 -> 오 스와이프 수정부분
        val R_swipeHandler = object : SwipeToUpdateCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {


                //바꿔야함
                val builder = AlertDialog.Builder(this@FriendsListActivity)
                val dialogView = layoutInflater.inflate(R.layout.item_update_firiends_list, null)
                val dialogText = dialogView.findViewById<EditText>(R.id.update_Friends_Name_Text)

                //val dialogRatingBar = dialogView.findViewById<RatingBar>(R.id.dialogRb)




                builder.setView(dialogView)
                        .setPositiveButton("확인") { dialogInterface, i ->
                            var mainString:String = "asd"

                            mainString = dialogText.text.toString()
                            friends_List_Name.setText(mainString)

                            mAdapter.notifyDataSetChanged() //변경된 데이터를 화면에 반영
                            Log.e("dd", "저장!")

                        }
                        .setNegativeButton("취소") { dialogInterface, i ->
                            /* 취소일 때 아무 액션이 없으므로 빈칸 */
                            mAdapter.notifyDataSetChanged()
                        }
                        .show()
            }
        }
        val R_itemTouchHelper = ItemTouchHelper(R_swipeHandler)
        R_itemTouchHelper.attachToRecyclerView(Friends_List_Recyclerview)


        //addItemBtn.setOnClickListener(this)





    }

    override fun refresh() {
        mAdapter.notifyDataSetChanged()

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}
