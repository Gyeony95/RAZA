package com.noble.activity.RAZA_3.LastestFriends

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.widget.LinearLayout
import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.views.BaseActivity
import kotlinx.android.synthetic.main.activity_lastest_friends.*

class LastestFriendsActivity : BaseActivity(0) , Contract_LastestFriends.View{


    private val TAG = "LastestFriendsActivity"

    internal lateinit var presenter_LastestFriends:Presenter_LastestFriends
    internal lateinit var preferences: SharedPreferences


    //리사이클러뷰
    var arrayList = arrayListOf<Json_Lastest_Load>(
            //LastestFriends("김철수", "한국", "ic_default_profile", "duck", "2019-07-10 오후 1시 20분")

    )

    //어댑터 선언
    val mAdapter = AdapterLastestFriends(this, arrayList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lastest_friends)
        setupBottomNavigation()

        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)


        presenter_LastestFriends = Presenter_LastestFriends().apply {
            view = this@LastestFriendsActivity
            context = this@LastestFriendsActivity
            myemail = preferences.getString("user", "")

        }


        lastest_Friends_Recyclerview.adapter = mAdapter
        //레이아웃 매니저 선언
        val lm = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        lastest_Friends_Recyclerview.layoutManager = lm
        lastest_Friends_Recyclerview.setHasFixedSize(true)//아이템이 추가삭제될때 크기측면에서 오류 안나게 해줌


        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(lastest_Friends_Recyclerview)
        presenter_LastestFriends.loadItems(arrayList)


    }


    override fun refresh() {
        mAdapter.notifyDataSetChanged()
    }

}
