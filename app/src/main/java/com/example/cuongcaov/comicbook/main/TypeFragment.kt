package com.example.cuongcaov.comicbook.main

import android.os.Bundle
import android.widget.Toast
import com.example.cuongcaov.comicbook.base.BaseActivity
import com.example.cuongcaov.comicbook.model.APIResult
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 17/10/2017
 */
class TypeFragment : BaseFragment() {

    var mTypeId: Int = 0

    companion object {

        const val KEY_TYPE_ID = "typeId"

        fun getInstance(typeId: Int): TypeFragment {
            val instance = TypeFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE_ID, typeId)
            instance.arguments = bundle
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTypeId = arguments.getInt(KEY_TYPE_ID)
    }

    override fun getStories() {
        mSwipeRefreshLayout.isRefreshing = true
        val apiService = RetrofitClient.getAPIService()
        apiService.getStoriesByType(mCurrentPage, mTypeId).enqueue(object : Callback<APIResult> {
            override fun onResponse(call: Call<APIResult>?, response: Response<APIResult>?) {
                val list = response?.body()?.data
                val storyCount = response?.body()?.count
                if (storyCount != null) {
                    mComicCount = storyCount
                }
                updateFooter()
                getSavedStory(BaseActivity.KEY_LIKE)
                getSavedStory(BaseActivity.KEY_HISTORY)
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
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }

            override fun onFailure(call: Call<APIResult>?, t: Throwable?) {
                Toast.makeText(activity, "Danh sách trống.", Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }

        })
    }

}