package com.noble.activity.RAZA_3.LastestFriends

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.noble.activity.RAZA_3.Network.ApiFactory
import com.noble.activity.RAZA_3.Network.Retrofit_Frame
import com.noble.activity.RAZA_3.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.ArrayList

class Presenter_LastestFriends : Contract_LastestFriends.Presenter {

    lateinit override var view: Contract_LastestFriends.View
    override lateinit var context: Context
    override lateinit var myemail:String
    override fun loadItems(list: ArrayList<Json_Lastest_Load>) {
        Log.e("여기 널임", "1!")
        val service = ApiFactory.placeholderApi
        //GlobalScope는 코루틴 계층을 만들수있는 도구
        GlobalScope.launch(Dispatchers.Main) {
            val postRequest = service.get_Lastest("json")
            try {
                val response = postRequest.await()
                if(response.isSuccessful){
                    val posts = response.body()

                    var length:Int = 0//리스트 한번 돌리는 역할
                    if (posts != null) {
                        while(length < posts.size){
                            Log.e("여기 널임", "2!")
                            var aaa = posts.get(length)
                            //내 이메일과 돌리다 나온 subject이메일이 같으면
                            if(aaa.subject.equals(myemail)){
                                //실제로 데이터 가져오는 부분
                                Log.e("여기 널임", "3!")

                                val dict = Json_Lastest_Load(aaa.id,aaa.real_id, aaa.subject, aaa.email, aaa.name, aaa.country, aaa.profile_image, aaa.profile_image2, aaa.gender, aaa.profile_text, aaa.like_check, aaa.create_date)//제이슨파일에 있는 정보들을 새 객체로 저장

                                list.add(dict) //마지막 줄에 삽입
                                //화면 새로고침
                                //view?.refresh()
                                Log.e("여기 널임", list.toString())
                                view.refresh()

                            }else{
                                //view?.hideProgress()
                            }
                            length++
                        }
                    }
                    else{
                        Log.e("여기 널임", "에러남!")
                        //view?.hideProgress()
                    }

                }else{
                    Log.d("MainActivity ", response.errorBody()!!.string())
                }

            }catch (e: Exception){
                Log.e("로그", "ㅁㄴㅇ")
            }
        }

        //var item:LastestFriends = LastestFriends("g", "g", "g", "g", "g")

        //list.add(item)


    }


    override fun removeItems(position: Int, id:Int, context: Context) {
        var dialog = AlertDialog.Builder(context)
        dialog.setTitle("친구 삭제")
        dialog.setMessage("친구목록에서 지우시겠습니까?")
        dialog.setIcon(R.mipmap.ic_launcher)

        fun toast_p() {
            Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()

            remove_task(id)
            //view?.refresh()

            val intent = Intent(context, LastestFriendsActivity::class.java)
            context.startActivity(intent)
            System.exit(0)
        }
        fun toast_n(){
            //Toast.makeText(this@FriendsListActivity, "Negative 버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show()
           //view?.refresh()
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
    //실제로 삭제가 일어나는 부분
    fun remove_task(id:Int){
        var retrofit_Frame:Retrofit_Frame = Retrofit_Frame()
        retrofit_Frame.delete_method("last_friends",id)
    }

    override fun addItems(position: Int) {
    }
    override fun updateItems(position: Int) {
    }



}