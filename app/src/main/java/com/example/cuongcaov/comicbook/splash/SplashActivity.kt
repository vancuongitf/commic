package com.example.cuongcaov.comicbook.splash

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.base.BaseActivity
import com.example.cuongcaov.comicbook.main.MainActivity
import com.example.cuongcaov.comicbook.model.HistoryResult
import com.example.cuongcaov.comicbook.model.User
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 13/10/2017
 */
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val KEY_DATA_LOADED = "loaded"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_AVATAR = "avatar"
        private const val KEY_MAC_ADDRESS = "mac_address"

        fun saveUser(shared: SharedPreferences, user: User) {
            val editor = shared.edit()
            editor.putString(KEY_USER_NAME, user.name)
            editor.putString(KEY_AVATAR, user.avatar)
            editor.putString(KEY_MAC_ADDRESS, user.macAddress)
            editor.apply()
        }

        fun getUser(shared: SharedPreferences): User {
            return User(shared.getString(KEY_USER_NAME, "Bạn đọc"),
                    shared.getString(KEY_MAC_ADDRESS, ""),
                    shared.getString(KEY_AVATAR, "non-avatar"))
        }
    }

    private lateinit var mShared: SharedPreferences
    private lateinit var mMacAddress: String
    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mShared = getSharedPreferences(BaseActivity.SHARED_NAME, Context.MODE_PRIVATE)
        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Load dữ liệu.")
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
        mMacAddress = MainActivity.getMacAddr()
        val intent = Intent(this, MainActivity::class.java)
        if (!mShared.getBoolean(KEY_DATA_LOADED, false)) {
            RetrofitClient.getAPIService().getHistory(MainActivity.getMacAddr())
                    .enqueue(object : Callback<HistoryResult> {
                        override fun onResponse(call: Call<HistoryResult>?, response: Response<HistoryResult>?) {
                            val user = response?.body()?.user
                            if (user != null) {
                                saveUser(mShared, user)
                            }

                            val likeSet = response?.body()?.favorite
                            if (likeSet != null) {
                                val editor = mShared.edit()
                                editor.putString(BaseActivity.KEY_LIKE, likeSet.toString())
                                editor.putBoolean(KEY_DATA_LOADED, true)
                                editor.apply()
                                startActivity(intent)
                            }
                        }

                        override fun onFailure(call: Call<HistoryResult>?, t: Throwable?) {
                            startActivity(intent)
                        }

                    })
        } else {
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
    }

}