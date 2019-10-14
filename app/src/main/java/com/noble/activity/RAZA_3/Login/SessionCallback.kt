package com.noble.activity.RAZA_3.Login

import android.util.Log
import com.kakao.auth.ISessionCallback
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import com.kakao.util.exception.KakaoException

class SessionCallback : ISessionCallback {
    override fun onSessionOpenFailed(exception: KakaoException?) {
        Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception!!.message)
    }

    override fun onSessionOpened() {
        requestMe()
    }



    fun requestMe() {

        // 사용자정보 요청 결과에 대한 Callback

        UserManagement.getInstance().requestMe(object : MeResponseCallback() {

            // 세션 오픈 실패. 세션이 삭제된 경우,

            override fun onSessionClosed(errorResult: ErrorResult) {
                Log.e("SessionCallback :: ", "onSessionClosed : " + errorResult.errorMessage)
            }


            // 회원이 아닌 경우,

            override fun onNotSignedUp() {
                Log.e("SessionCallback :: ", "onNotSignedUp")
            }


            // 사용자정보 요청에 성공한 경우,

            override fun onSuccess(userProfile: UserProfile) {
                Log.e("SessionCallback :: ", "onSuccess")
                val nickname = userProfile.nickname

                val email = userProfile.email

                val profileImagePath = userProfile.profileImagePath

                val thumnailPath = userProfile.thumbnailImagePath

                val UUID = userProfile.uuid

                val id = userProfile.id

                Log.e("Profile : ", nickname + "")

                Log.e("Profile : ", email + "")

                Log.e("Profile : ", profileImagePath + "")

                Log.e("Profile : ", thumnailPath + "")

                Log.e("Profile : ", UUID + "")

                Log.e("Profile : ", id.toString() + "")
            }
            // 사용자 정보 요청 실패
            override fun onFailure(errorResult: ErrorResult?) {
                Log.e("SessionCallback :: ", "onFailure : " + errorResult!!.errorMessage)
            }

        })

    }

}