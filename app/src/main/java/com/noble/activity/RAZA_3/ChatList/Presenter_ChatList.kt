package com.noble.activity.RAZA_3.ChatList

import android.content.Context
import android.content.Intent
import android.util.Log
import com.noble.activity.RAZA_3.ChatList.ChatRoom.ChatRoomActivity
import com.noble.activity.RAZA_3.Network.ApiFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.ArrayList

class Presenter_ChatList() : Contract_ChatList.Presenter {
    override lateinit var view: Contract_ChatList.View



    override fun loadItems(context: Context, arrayList: ArrayList<ChatList_Load>, user_id: Int) {
        view?.showProgress()
        //데이터 불러오는 부분
        val service = ApiFactory.placeholderApi
        //GlobalScope는 코루틴 계층을 만들수있는 도구
        GlobalScope.launch(Dispatchers.Main) {
            val postRequest = service.get_Chat_Small("json")
            try {
                val response = postRequest.await()
                if(response.isSuccessful){
                    val posts = response.body()

                    var length:Int = 0//리스트 한번 돌리는 역할
                    if (posts != null) {
                        while(length < posts.size){

                            var aaa = posts.get(length)
                            //내 이메일과 돌리다 나온 subject이메일이 같으면
                            Log.e("user_one", aaa.user_one.toString())
                            Log.e("user_two", aaa.user_two.toString())
                            Log.e("id", user_id.toString())

                            //user_one user_two 둘중 하나라도 내아이디와 일치할 경우
                            if((aaa.user_one == user_id) || (aaa.user_two == user_id)){
                                //실제로 데이터 가져오는 부분
                                val dict = aaa
                                arrayList.add(dict) //마지막 줄에 삽입
                                //화면 새로고침
                                view.refresh()
                                view?.hideProgress()

                            }else{
                                view?.hideProgress()
                            }
                            length++
                        }
                        view?.hideProgress()
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

    override fun addItems(context: Context, position: Int, arrayList: ArrayList<ChatList>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeItems(context: Context, position: Int, arrayList: ArrayList<ChatList_Load>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateItems(context: Context, position: Int, arrayList: ArrayList<ChatList>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clickSetting(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    //방아이디와 내아이디 친구이름 친구 이미지 친구 아이디 전달받음
    override fun go_chatroom(context: Context,  roomName:String, user_id: Int, name:String, image:String, friends_id:Int) {
        val intent = Intent(context, ChatRoomActivity::class.java)
        intent.putExtra("friends_name",name)
        intent.putExtra("friends_image",image)
        intent.putExtra("friends_id",friends_id)
        intent.putExtra("roomName",roomName)

        context.startActivity(intent)
    }

}