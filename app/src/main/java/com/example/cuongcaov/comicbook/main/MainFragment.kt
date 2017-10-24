package com.example.cuongcaov.comicbook.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.cuongcaov.comicbook.base.BaseActivity
import com.example.cuongcaov.comicbook.model.APIResult
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 17/10/2017
 */
class MainFragment : BaseFragment() {

    companion object {

        const val KEY_QUERY = "query"

        fun getInstance(query: String = ""): MainFragment {
            val instance = MainFragment()
            val bundle = Bundle()
            bundle.putString(KEY_QUERY, query)
            instance.arguments = bundle
            return instance
        }
    }

    private var mQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mQuery = arguments.getString(KEY_QUERY)
    }

    override fun onResume() {
        super.onResume()
        Log.i("tag11","asdgasd")
        mAdapter.notifyDataSetChanged()
    }

    override fun getStories() {
        mSwipeRefreshLayout.isRefreshing = true
        val apiService = RetrofitClient.getAPIService()
        apiService.getStories(mCurrentPage, mQuery).enqueue(object : Callback<APIResult> {
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
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                }
            }

            override fun onFailure(call: Call<APIResult>?, t: Throwable?) {
                activity.runOnUiThread {
                    Toast.makeText(activity, "Danh sách trống.", Toast.LENGTH_LONG).show()
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }

        })
    }

    fun setQuery(query: String) {
        mQuery = query
        mCurrentPage = 1
        getStories()
    }

}
