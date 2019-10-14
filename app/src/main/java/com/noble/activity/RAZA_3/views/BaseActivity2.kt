package com.noble.activity.RAZA_3.views

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.noble.activity.RAZA_3.R

abstract class BaseActivity2: AppCompatActivity() {
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