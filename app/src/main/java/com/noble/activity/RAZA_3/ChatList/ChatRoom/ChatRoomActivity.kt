package com.noble.activity.RAZA_3.ChatList.ChatRoom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.noble.activity.RAZA_3.ChatList.Adapter_ChatList
import com.noble.activity.RAZA_3.ChatList.ChatList_Load
import com.noble.activity.RAZA_3.ChatList.Presenter_ChatList
import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.views.BaseActivity2
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.activity_friends_profile.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomActivity : BaseActivity2(),Contract_ChatRoom.View{

    internal lateinit var preferences: SharedPreferences
    var  presenter_ChatRoom: Presenter_ChatRoom = Presenter_ChatRoom()
    private lateinit var chating_Text: EditText
    private lateinit var chat_Send_Button: Button
    //리사이클러뷰
    var arrayList = arrayListOf<Chat_Room>()
    val mAdapter = Adapter_ChatRoom(this, arrayList)
    private var Username: String? = null

    private var hasConnection: Boolean = false
    private var startTyping = false
    private var time = 2
    lateinit var uniqueId: String
    lateinit var friends_name:String
    lateinit var friends_image:String
    var friends_id:Int = 0
    var my_id:Int = 0

    var first_check:Boolean = true

    //소켓연결
    var mSocket: Socket = IO.socket("http://ec2-15-164-104-42.ap-northeast-2.compute.amazonaws.com:5000/")
    //상대방이 채팅을 치고있으면 타이핑중, 아니면 그냥 제목
    @SuppressLint("HandlerLeak")
    internal var handler2: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.e("챗룸", "handleMessage: typing stopped $startTyping")
            if (time == 0) {
                title = "SocketIO"
                Log.e("챗룸","handleMessage: typing stopped time is $time")
                startTyping = false
                time = 2
            }

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)

        my_id = preferences.getInt("id", 0)

        //친구프로필이나 채팅목록에서 넘어온 친구 정보를 기록함
        var intent:Intent=getIntent()
        friends_name = intent.getStringExtra("friends_name")
        friends_image = intent.getStringExtra("friends_image")
        friends_id = intent.getIntExtra("friends_id", 0)




        //대화방 상단에 친구이름 설정
        chat_Name.setText(friends_name)


        presenter_ChatRoom = Presenter_ChatRoom().apply {
            view = this@ChatRoomActivity
        }

        var roomName:String
        if(my_id > friends_id){
            Log.e("roomName", "$friends_id%%$my_id")
            roomName = "$friends_id%%$my_id"
        }else{
            Log.e("roomName", "$my_id%%$friends_id")
            roomName = "$my_id%%$friends_id"
        }

        presenter_ChatRoom.first_check(this, roomName)
        presenter_ChatRoom.load_item(this, roomName, arrayList)
        //어댑터 선언
        chat_recyclerview.adapter = mAdapter
        //레이아웃 매니저 선언
        val lm = LinearLayoutManager(this)
        chat_recyclerview.layoutManager = lm
        chat_recyclerview.setHasFixedSize(true)//아이템이 추가삭제될때 크기측면에서 오류 안나게 해줌



        //여기부터 저쪽코드

        Username = preferences.getString("name","")
        uniqueId = UUID.randomUUID().toString()
        //여기에 지난채팅 불러오는 코드 넣을 예정


        if (savedInstanceState != null) {
            hasConnection = savedInstanceState.getBoolean("hasConnection")

        }

        if (hasConnection) {

        } else {

            //소켓연결
            mSocket.connect()


            //서버에 신호 보내는거같음 밑에 에밋 리스너들 실행인듯?
            //socket.on은 수신이라고 함

            mSocket.on("connect user", onNewUser)
            mSocket.on("chat message", onNewMessage)

            val userId = JSONObject()
            try {

                //인텐트로 받은 유저 이름 josnobject에 넣어줌
                userId.put("username", "$Username Connected")
                if(my_id > friends_id){
                    userId.put("roomName", "$friends_id%%$my_id")
                    Log.e("roomName", "$friends_id%%$my_id")
                }else{
                    userId.put("roomName", "$my_id%%$friends_id")
                    Log.e("roomName", "$my_id%%$friends_id")
                }


                //socket.emit은 메세지 전송임
                mSocket.emit("connect user", userId)

                Log.e("asdasd", Username)


            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        Log.e("챗룸", "onCreate: $hasConnection")
        hasConnection = true


        Log.e("챗룸", "onCreate: $Username Connected")
        Log.e("챗룸", "onCreate: $Username Connected")

        chating_Text = findViewById<EditText>(R.id.chating_Text)
        chat_Send_Button = findViewById<Button>(R.id.chat_Send_Button)

        //여기까지지 저쪽코드




        chat_Send_Button.setOnClickListener {
            //아이템 추가 부분
            sendMessage()


        }

    }




    override fun first_false() {
        first_check =false
    }

    override fun first_true() {
        first_check = true
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
        chat_recyclerview.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    //여기 아래로 다 저쪽코드
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("hasConnection", hasConnection)
    }







    internal var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            Log.e("챗룸", "run: ")
            Log.e("챗룸", "run: " + args.size)
            val data = args[0] as JSONObject
            val roomName: String
            val user_one: Int
            val user_two: Int
            val user_id: Int
            val name: String
            val script: String
            val profile_image: String
            val date_time: String
            val read_check: Boolean
            try {
                roomName = data.getString("roomName")
                user_one = data.getInt("user_one")
                user_two = data.getInt("user_two")
                user_id = data.getInt("user_id")

                name = data.getString("name")
                script = data.getString("script")
                profile_image = data.getString("profile_image")
                date_time = data.getString("date_time")
                read_check = data.getBoolean("read_check")



                val format = Chat_Room(roomName,user_one, user_two, user_id,name, script, profile_image,date_time, read_check)
                mAdapter.addItem(format)
                mAdapter.notifyDataSetChanged()
                first_check = false
            } catch (e: Exception) {
                return@Runnable
            }
        })
    }

    internal var onNewUser: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val length = args.size

            if (length == 0) {
                return@Runnable
            }
            //Here i'm getting weird error..................///////run :1 and run: 0
            Log.e("챗룸", "run: ")
            Log.e("챗룸", "run: " + args.size)
            var username = args[0].toString()
            try {
                val `object` = JSONObject(username)
                username = `object`.getString("username")
                Log.e("username", username)


            } catch (e: JSONException) {
                e.printStackTrace()
            }

            //val format = Chat_Room(null, username, null)
            //mAdapter.addItem(format)
           // messageListView!!.smoothScrollToPosition(0)
            //messageListView!!.scrollTo(0, mAdapter.getCount() - 1)
            //Log.e("챗룸", "run: $username")
        })
    }


    internal var deConnect: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            Log.e("deconnect", "deconnect")
        })
    }


    fun sendMessage() {
        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)
        Log.e("챗룸", "sendMessage: ")
        val script = chating_Text.getText().toString().trim({ it <= ' ' })
        if (TextUtils.isEmpty(script)) {
            Log.e("챗룸", "sendMessage:2 ")
            return
        }

        val now = System.currentTimeMillis()
        val date = Date(now)
        //나중에 바꿔줄것
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val getTime = sdf.format(date)

        chating_Text.setText("")
        val jsonObject = JSONObject()
        try {
            //메세지 버블 객체
            //나랑 상대 아이디 비교해서 작은값이 one 큰값이 two
            if(my_id > friends_id){
                jsonObject.put("user_one", friends_id)
                jsonObject.put("user_two", my_id)
                jsonObject.put("roomName", "$friends_id%%$my_id")
            }else{
                jsonObject.put("user_one", my_id)
                jsonObject.put("user_two", friends_id)
                jsonObject.put("roomName", "$my_id%%$friends_id")
            }
            jsonObject.put("user_id", my_id)
            jsonObject.put("name", preferences.getString("name", ""))
            jsonObject.put("script", script)
            jsonObject.put("profile_image", friends_image)
            jsonObject.put("date_time", getTime)
            jsonObject.put("read_check", false)

            Log.e("asdasd", jsonObject.toString())

            //채팅을 친게 처음이라면 채팅방을 생성시킴
            if(first_check){//처음일경우에만 채팅방 생성
                val jsonObject2 = JSONObject()
                if(my_id > friends_id){
                    jsonObject2.put("user_one", friends_id)
                    jsonObject2.put("user_two", my_id)
                    jsonObject2.put("roomName", "$friends_id%%$my_id")
                }else{
                    jsonObject2.put("user_one", my_id)
                    jsonObject2.put("user_two", friends_id)
                    jsonObject2.put("roomName", "$my_id%%$friends_id")
                }
                jsonObject2.put("user_id", my_id)
                jsonObject2.put("name", preferences.getString("name", ""))
                jsonObject2.put("name2", friends_name)
                jsonObject2.put("script", script)
                jsonObject2.put("profile_image", friends_image)
                jsonObject2.put("date_time", getTime)
                jsonObject2.put("read_check", false)

                presenter_ChatRoom.create_list(this, jsonObject2)
            }else{ }
            //채팅내용 저장은 서버에서 함



        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.e("챗룸", "sendMessage: 1" + mSocket.emit("chat message", jsonObject))
        refresh()
    }

    public override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            Log.e("챗룸", "onDestroy: ")

            val data = JSONObject()
            try {
                if(my_id > friends_id){

                    data.put("roomName", "$friends_id%%$my_id")
                }else{

                    data.put("roomName", "$my_id%%$friends_id")
                }
                mSocket.emit("connect end", data)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            mSocket.disconnect()
            mSocket.off("chat message", onNewMessage)
            mSocket.off("connect user", onNewUser)
            Username = ""
            //mAdapter.clear()
            Log.e("챗룸", "onDestroy: 잘됨.....")
        } else {
            Log.e("챗룸", "onDestroy: is rotating.....")
        }

    }


}