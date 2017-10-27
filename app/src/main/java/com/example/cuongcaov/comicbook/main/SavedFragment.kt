package com.example.cuongcaov.comicbook.main

import android.os.Bundle
import android.widget.Toast
import com.example.cuongcaov.comicbook.model.APIResult
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import com.example.cuongcaov.comicbook.ultis.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 17/10/2017
 */
class SavedFragment : BaseFragment() {

    companion object {

        fun getInstance(likedFragment: Boolean = false): SavedFragment {
            val instance = SavedFragment()
            val bundle = Bundle()
            bundle.putBoolean(Constants.KEY_LIKED_FRAGMENT, likedFragment)
            instance.arguments = bundle
            return instance
        }
    }

    private var mIsLikedFragment: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIsLikedFragment = arguments.getBoolean(Constants.KEY_LIKED_FRAGMENT)
    }

    override fun getStories() {
        val savedSet = mutableSetOf<Long>()
        getSavedStory(Constants.KEY_LIKE)
        getSavedStory(Constants.KEY_HISTORY)
        if (mIsLikedFragment) {
            mComicCount = mLiked.size
            mLiked.withIndex().forEach {
                if (it.index >= (mCurrentPage - 1) * 20 && it.index < mCurrentPage * 20) {
                    savedSet.add(it.value.toLong())
                }
            }
        } else {
            mComicCount = mHistory.size
            mHistory.withIndex().forEach {
                if (it.index >= (mCurrentPage - 1) * 20 && it.index < mCurrentPage * 20) {
                    savedSet.add(it.value.toLong())
                }
            }
        }
        mSwipeRefreshLayout.isRefreshing = true
        RetrofitClient.getAPIService().getSavedHistory(savedSet.toString())
                .enqueue(object : Callback<APIResult> {
                    override fun onResponse(call: Call<APIResult>?, response: Response<APIResult>?) {
                        val list = response?.body()?.data
                        updateFooter()
                        getSavedStory(Constants.KEY_LIKE)
                        getSavedStory(Constants.KEY_HISTORY)
                        list?.forEach {
                            if (mLiked.contains(it.storyId.toString())) {
                                it.like = true
                            }
                            if (mHistory.contains(it.storyId.toString())) {
                                it.seen = true
                            }
                        }
                        if (list != null) {
                            activity.runOnUiThread {
                                mComics.clear()
                                mComics.addAll(list)
                                mAdapter.notifyDataSetChanged()
                                mRecyclerView.scrollToPosition(0)
                            }
                        }
                        mSwipeRefreshLayout.isRefreshing = false
                    }

                    override fun onFailure(call: Call<APIResult>?, t: Throwable?) {
                        Toast.makeText(activity, "Danh sách trống.", Toast.LENGTH_LONG).show()
                        mSwipeRefreshLayout.isRefreshing = false
                    }

                })
    }
}