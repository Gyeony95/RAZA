package com.noble.activity.RAZA_3.ChatList

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.etc_process.SwipeToDeleteCallback
import com.noble.activity.RAZA_3.etc_process.SwipeToUpdateCallback
import com.noble.activity.RAZA_3.views.BaseActivity
import kotlinx.android.synthetic.main.activity_chat.*

//채팅방의 리스트를 보여주는 액티비티
class ChatListActivity: BaseActivity(3),Contract_ChatList.View {

    internal lateinit var preferences: SharedPreferences
    var  presenter_ChatList:Presenter_ChatList = Presenter_ChatList()

    //리사이클러뷰
    var arrayList = arrayListOf<ChatList_Load>()
    val mAdapter = Adapter_ChatList(this, arrayList)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        presenter_ChatList = Presenter_ChatList().apply {
            view = this@ChatListActivity
        }
        var context: Context = this

        //어댑터 선언
        chat_List_Recyclerview.adapter = mAdapter
        //레이아웃 매니저 선언
        val lm = LinearLayoutManager(this)
        chat_List_Recyclerview.layoutManager = lm
        chat_List_Recyclerview.setHasFixedSize(true)//아이템이 추가삭제될때 크기측면에서 오류 안나게 해줌
        setupBottomNavigation()

        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)

        //데이터 불러오는 코드 , db에서 불러옴
        presenter_ChatList.loadItems(this, arrayList, preferences.getInt("id", 0))




        //여기 좌우 스와이프 설정하는부분
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = chat_List_Recyclerview.adapter as Adapter_ChatList
                adapter.removeAt(viewHolder.adapterPosition, context)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(chat_List_Recyclerview)


        val R_swipeHandler = object : SwipeToUpdateCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = chat_List_Recyclerview.adapter as Adapter_ChatList
                adapter.removeAt(viewHolder.adapterPosition, context)
            }
        }
        val R_itemTouchHelper = ItemTouchHelper(R_swipeHandler)
        R_itemTouchHelper.attachToRecyclerView(chat_List_Recyclerview)

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

    override fun refresh() {
        mAdapter.notifyDataSetChanged()
    }
}