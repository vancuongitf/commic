package com.example.cuongcaov.comicbook.googleapi

import android.content.Context
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient

/**
 *
 *Created by CaoVanCuong on 11/1/2017.
 */
class GoogleSignInClient {

    companion object {
        private var sInstance: GoogleApiClient? = null

        fun getInstance(context: Context, listener: GoogleApiConnectionCallback): GoogleApiClient {
            if (sInstance == null) {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                sInstance = GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(listener)
                        .addOnConnectionFailedListener(listener)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build()
            }
            return sInstance!!
        }
    }
}