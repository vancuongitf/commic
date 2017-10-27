package com.example.cuongcaov.comicbook.saved

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.base.BaseActivity
import com.example.cuongcaov.comicbook.detail.DetailActivity
import com.example.cuongcaov.comicbook.main.MainActivity
import com.example.cuongcaov.comicbook.main.MenuAdapter
import com.example.cuongcaov.comicbook.main.StoryListAdapter
import com.example.cuongcaov.comicbook.model.APIResult
import com.example.cuongcaov.comicbook.model.Comic
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import com.example.cuongcaov.comicbook.ultis.Constants
import kotlinx.android.synthetic.main.activity_saved.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 16/10/2017
 */
class SavedActivity : BaseActivity() {

    private var mComics = mutableListOf<Comic>()
    private var mComicIds = mutableSetOf<Long>()
    private var mPageCount = 1
    private var mLikedStory = false
    private var mCurrentPage = 1
    private var mAdapter = StoryListAdapter(mComics, MainActivity.getMacAddr(), object : MenuAdapter.RecyclerViewOnItemClickListener {
        override fun onLikeAction(storyId: Long, liked: Boolean) {
            savedStory(Constants.KEY_LIKE, storyId, liked)
        }

        override fun onItemClick(item: Any) {
            val comic = item as? Comic
            if (comic != null) {
                val intent = Intent(this@SavedActivity, DetailActivity::class.java)
                val bundle = Bundle()
                savedStory(Constants.KEY_HISTORY, comic.storyId)
                RetrofitClient.getAPIService().read(comic.storyId)
                        .enqueue(object : Callback<Int> {
                            override fun onFailure(call: Call<Int>?, t: Throwable?) = Unit

                            override fun onResponse(call: Call<Int>?, response: Response<Int>?) {
                                if (response?.body() != null) {
                                    comic.readCount = response.body()!!
                                }
                            }

                        })
                comic.seen = true
                comic.readCount++
                bundle.putSerializable(Constants.KEY_COMIC, comic)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved)
        mLikedStory = intent.extras.getBoolean(Constants.KEY_LIKE)
    }

    private fun getSavedStory() {
        val list = mutableSetOf<Long>()
        mComicIds.withIndex().forEach {
            if (it.index >= (mCurrentPage - 1) * 20 && it.index < mCurrentPage * 20) {
                list.add(it.value)
            }
        }
        val thread = Thread({
            RetrofitClient.getAPIService().getSavedHistory(mComicIds.toString())
                    .enqueue(object : Callback<APIResult> {
                        override fun onResponse(call: Call<APIResult>?, response: Response<APIResult>?) {

                        }

                        override fun onFailure(call: Call<APIResult>?, t: Throwable?) {
                            Toast.makeText(this@SavedActivity, "Danh sách trống", Toast.LENGTH_LONG).show()
                            swipeRefreshLayout.isRefreshing = false
                        }

                    })
        })
        swipeRefreshLayout.isRefreshing = true
        thread.start()
    }

    private fun updateFooter() {
        val storyCount = mComicIds.size
        mPageCount = if (storyCount % 20 > 0) {
            storyCount / 20 + 1
        } else {
            storyCount / 20
        }
        if (mCurrentPage < 2) {
            imgPreviousPage.isEnabled = false
            imgToFirstPage.isEnabled = false
        } else {
            imgPreviousPage.isEnabled = true
            imgToFirstPage.isEnabled = true
        }
        if (mCurrentPage > mPageCount - 1) {
            imgNextPage.isEnabled = false
            imgToLastPage.isEnabled = false
        } else {
            imgNextPage.isEnabled = true
            imgToLastPage.isEnabled = true
        }
        tvCurrentPage.text = getString(R.string.footerText, mCurrentPage, mPageCount)
    }
}