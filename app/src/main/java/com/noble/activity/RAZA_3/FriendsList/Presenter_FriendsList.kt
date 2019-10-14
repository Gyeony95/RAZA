package com.noble.activity.RAZA_3.FriendsList

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.noble.activity.RAZA_3.Network.ApiFactory
import com.noble.activity.RAZA_3.Network.Retrofit_Frame
import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.Register.Json_Account
import com.noble.activity.RAZA_3.Register.Json_Account_Load
import kotlinx.android.synthetic.main.acitivity_friends_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList

class Presenter_FriendsList:Contract_FriendsList.Presenter {


    lateinit override var view: Contract_FriendsList.View

    override fun loadItems(context: Context, arrayList: ArrayList<Json_Friends_Load>) {

    }

    override fun addItems(context: Context, position: Int, arrayList: ArrayList<Json_Friends_Load>) {
    }

    override fun removeItems(context: Context,  arrayList: ArrayList<Json_Friends_Load>, mAdapter:AdapterFriendsList, position:Int) {
        var dialog = AlertDialog.Builder(context)
        dialog.setTitle("친구 삭제")
        dialog.setMessage("친구목록에서 지우시겠습니까?")
        dialog.setIcon(R.mipmap.ic_launcher)

        fun toast_p() {
            Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            //val adapter = Friends_List_Recyclerview.adapter as AdapterFriendsList
            mAdapter.removeAt(position)
            mAdapter.notifyDataSetChanged()


        }
        fun toast_n(){
            //Toast.makeText(this@FriendsListActivity, "Negative 버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show()
            mAdapter.notifyDataSetChanged()
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




    override fun updateItems(context: Context, position: Int, arrayList: ArrayList<Json_Friends_Load>) {
    }
    //친구추가 버튼
    override fun plus_Friends_Button(v: View, ediText:EditText,myemail: String, mAdapter:AdapterFriendsList) {
        val builder = AlertDialog.Builder(v.context)

        builder.setView(v)
                .setPositiveButton("확인") { dialogInterface, i ->

                    var mainString:String = "asd"
                    mainString = ediText.text.toString()

                    view?.showProgress()
                    //데이터 불러오는 부분
                    val service = ApiFactory.placeholderApi
                    //GlobalScope는 코루틴 계층을 만들수있는 도구
                    GlobalScope.launch(Dispatchers.Main) {
                        val postRequest = service.getPosts()
                        try {
                            val response = postRequest.await()
                            if(response.isSuccessful){
                                val posts = response.body()

                                var my_object:Json_Friends
                                var length:Int = 0//리스트 한번 돌리는 역할
                                if (posts != null) {
                                    while(length < posts.size){
                                        var aaa = posts.get(length)
                                        //내 이메일과 돌리다 나온 이메일이 같으면 + 비밀번호가 같으면
                                        Log.e("email", aaa.email)
                                        Log.e("mainString", mainString)
                                        if(aaa.email.equals(mainString)){
                                            //내 객체 생성
                                            my_object = Json_Friends(myemail,aaa.id, aaa.email, aaa.name, aaa.country, aaa.profile_image, aaa.profile_image2, aaa.gender, aaa.like, aaa.profile_text)
                                            var retrofit_Frame:Retrofit_Frame = Retrofit_Frame()
                                            retrofit_Frame.post_method_json_friends(my_object)
                                            //mAdapter.addItem(my_object)
                                            view?.hideProgress()
                                            view?.refresh()
                                            break
                                        }else{
                                            view?.hideProgress()
                                        }
                                        length++
                                    }
                                    view.refresh()
                                }
                                else{
                                    view?.hideProgress()
                                }

                            }else{
                                Log.d("MainActivity ", response.errorBody()!!.string())
                            }

                        }catch (e: Exception){
                        }
                    }


                }
                .setNegativeButton("취소") { dialogInterface, i ->
                    /* 취소일 때 아무 액션이 없으므로 빈칸 */
                }
                .show()


    }



    //친구정보 셋팅
    override fun setMyFriendsInfo(myemail: String?, arrayList: ArrayList<Json_Friends_Load>) {
       // view?.showProgress()
        //데이터 불러오는 부분
        Log.e("asd", "2")
        val service = ApiFactory.placeholderApi
        //GlobalScope는 코루틴 계층을 만들수있는 도구
        GlobalScope.launch(Dispatchers.Main) {
            val postRequest = service.get_Friends("json")
            try {
                val response = postRequest.await()
                if(response.isSuccessful){
                    val posts = response.body()

                    var friends_object:Json_Friends_Load
                    var length:Int = 0//리스트 한번 돌리는 역할
                    Log.e("asd", "2-2")

                    if (posts != null) {
                        Log.e("asd", "2-3")

                        while(length < posts.size){
                            Log.e("asd", "2-4")

                            var aaa = posts.get(length)
                            //내 이메일과 돌리다 나온 subject이메일이 같으면
                            if(aaa.subject.equals(myemail)){
                                //실제로 데이터 가져오는 부분
                                Log.e("asd", "2-5")

                                val dict = aaa//제이슨파일에 있는 정보들을 새 객체로 저장

                                arrayList.add(dict) //마지막 줄에 삽입
                                Log.e("asdasd", dict.name)
                                //화면 새로고침
                                view.refresh()

                            }else{
                                Log.e("asd", "2-6")

                                //view?.hideProgress()
                            }
                            length++
                        }
                        view.refresh()
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


    }


    override fun close() {
    }

}