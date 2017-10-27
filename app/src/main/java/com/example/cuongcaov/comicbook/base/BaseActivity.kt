package com.example.cuongcaov.comicbook.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cuongcaov.comicbook.ultis.Constants

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 16/10/2017
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    lateinit var mSharedPreferences: SharedPreferences
    val mLiked = mutableSetOf<String>()
    val mHistory = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPreferences = getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE)
    }

    fun getSavedStory(key: String) {
        val savedStory = mSharedPreferences.getString(key, "[]")
        if (savedStory.length < 3) {
            return
        }
        if (key == Constants.KEY_LIKE) {
            mLiked.addAll(savedStory.substring(1, savedStory.length - 1).split(", "))
        } else {
            mHistory.addAll(savedStory.substring(1, savedStory.length - 1).split(", "))
        }
    }

    fun savedStory(key: String, storyId: Long, liked: Boolean = false) {
        val editor = mSharedPreferences.edit()
        if (key == Constants.KEY_LIKE) {
            if (liked) {
                mLiked.remove(storyId.toString())
                val list = mLiked.toMutableList()
                list.add(0, storyId.toString())
                mLiked.clear()
                mLiked.addAll(list)
            } else {
                mLiked.remove(storyId.toString())
            }
            editor.putString(Constants.KEY_LIKE, mLiked.toString())
        } else {
            mHistory.remove(storyId.toString())
            val list = mHistory.toMutableList()
            list.add(0, storyId.toString())
            mHistory.clear()
            mHistory.addAll(list)
            editor.putString(Constants.KEY_HISTORY, mHistory.toString())
        }
        editor.apply()
    }
}
