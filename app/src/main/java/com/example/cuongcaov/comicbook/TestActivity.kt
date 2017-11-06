package com.example.cuongcaov.comicbook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.cuongcaov.comicbook.googleapi.GoogleApiConnectionCallback
import com.example.cuongcaov.comicbook.googleapi.GoogleSignInClient
import com.example.cuongcaov.comicbook.ultis.Constants
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.test.*

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 27/10/2017
 */
class TestActivity : AppCompatActivity() {
    private lateinit var mGGSignInApi: GoogleApiClient
    private var mGoogleApiConnected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)

        mGGSignInApi = GoogleSignInClient.getInstance(this, object : GoogleApiConnectionCallback {
            override fun onConnectionFailed(p0: ConnectionResult) {
                Log.i("tag11", "Fail")
                mGoogleApiConnected = false
            }

            override fun onConnected(p0: Bundle?) {
                Log.i("tag11", "Success")
                mGoogleApiConnected = true
            }

            override fun onConnectionSuspended(p0: Int) {
                Log.i("tag11", "Suspended")
                mGoogleApiConnected = false
            }

        })

        mGGSignInApi.connect()

        imgTest.setOnClickListener {
            signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_GOOGLE_SIGN_IN -> {
                    val account = Auth.GoogleSignInApi.getSignInResultFromIntent(data).signInAccount
                    if (account != null) {
                        Log.i("tag11", account.email)
                    }
                }
            }
        }
    }

    private fun signIn() {
        if (mGoogleApiConnected) {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGGSignInApi)
            startActivityForResult(signInIntent, Constants.REQUEST_GOOGLE_SIGN_IN)
        }
    }
}