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
                    .enqueue(object : Callback<Set<Long>> {
                        override fun onResponse(call: Call<Set<Long>>?, response: Response<Set<Long>>?) {
                            val likeSet = response?.body()
                            if (likeSet != null) {
                                val editor = mShared.edit()
                                editor.putString(BaseActivity.KEY_LIKE, likeSet.toString())
                                editor.putBoolean(KEY_DATA_LOADED, true)
                                editor.apply()
                                startActivity(intent)
                            }
                        }

                        override fun onFailure(call: Call<Set<Long>>?, t: Throwable?) {
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