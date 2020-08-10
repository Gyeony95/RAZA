package com.noble.activity.RAZA_3.views

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.noble.activity.RAZA_3.ChatList.ChatListActivity
import com.noble.activity.RAZA_3.FindFriends.FindFriendsActivity
import com.noble.activity.RAZA_3.FriendsList.FriendsListActivity
import com.noble.activity.RAZA_3.LastestFriends.LastestFriendsActivity
import com.noble.activity.RAZA_3.R
import com.noble.activity.RAZA_3.Register.Json_Account
import kotlinx.android.synthetic.main.bottom_navigation_view.*


abstract class BaseActivity(val navNumber: Int) : AppCompatActivity() {
    //private val TAG = "BaseActivity"

    lateinit var my_account:Json_Account

    fun setupBottomNavigation() {//네비게이션 불러주는 메소드 다른액티비티에서 이 메소드를 사용함
        bottom_navigation_view.setIconSize(29f, 29f)
        bottom_navigation_view.setTextVisibility(true)
        bottom_navigation_view.enableItemShiftingMode(true)
        bottom_navigation_view.enableShiftingMode(false)
        bottom_navigation_view.enableAnimation(false)

        for (i in 0 until bottom_navigation_view.menu.size())
            bottom_navigation_view.setIconTintList(i, null)

        bottom_navigation_view.setOnNavigationItemSelectedListener {
            val nextActivity =
                    when (it.itemId) {
                        R.id.nav_item_home -> LastestFriendsActivity::class.java
                        R.id.nav_item_search -> FindFriendsActivity::class.java
                        //R.id.nav_item_share -> ShareActivity::class.java
                        R.id.nav_item_likes -> FriendsListActivity::class.java
                        R.id.nav_item_profile -> ChatListActivity::class.java
                        else -> {
                            //Log.e(TAG, "unknown nav item")
                            null
                        }
                    }
            if (nextActivity != null) {
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                finish()
                overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }


    }

    override fun onResume() {
        super.onResume()
        if (bottom_navigation_view != null) {
            bottom_navigation_view.menu.getItem(navNumber).isChecked = true
        }
    }
    //추가한부분

    //ProgressDialog가 API 26부터 deprecated. 구글은 ProgressBar 사용 권장함
    val progressDialog by lazy {
        //var의 lateinit와 같은 기능, 선언 후 접근할 때 초기화하여 메모리 절약. val에서만 사용 가능
        ProgressDialog(this)
    }

    fun showProgressDialog(xmlStrings: Int) {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
        progressDialog.setMessage(getString(xmlStrings))
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun hideKeyboard(view: View?) {
        if (view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showAlertDialog(dialogMsgId: Int, callback: (() -> Unit)?) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(dialogMsgId)
                .setCancelable(true) // enable backbtn
                .setNeutralButton(R.string.basic_alert_dialog_neutralbtn_name) { dialog, _ ->
                    callback?.invoke()
                    dialog.dismiss()
                }

        val dialog = builder.create()
        dialog.show()
    }

    fun showAlertDialogWithConfirmCancel(dialogMsgId: Int, positiveBtnMsgId: Int, negativeBtnMsgId: Int, callback: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(dialogMsgId)
                .setCancelable(true) // enable backbtn
                .setPositiveButton(positiveBtnMsgId) { _, _ ->
                    callback.invoke()
                }
                .setNegativeButton(negativeBtnMsgId) { dialog, _ ->
                    dialog.cancel()
                }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }



}