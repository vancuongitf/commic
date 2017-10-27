package com.example.cuongcaov.comicbook.main

import android.content.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.detail.DetailActivity
import com.example.cuongcaov.comicbook.model.Comic
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import com.example.cuongcaov.comicbook.ultis.Constants
import kotlinx.android.synthetic.main.fragment_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 17/10/2017
 */
abstract class BaseFragment : Fragment() {

    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mRecyclerView: RecyclerView
    lateinit var mImgToFirstPage: ImageView
    lateinit var mImgPrevious: ImageView
    lateinit var mImgNext: ImageView
    lateinit var mImgToLastPage: ImageView
    lateinit var mTvCurrentPage: TextView

    val mComics = mutableListOf<Comic>()
    var mComicCount = 0
    var mCurrentPage = 1
    var mPageCount = 1
    lateinit var mSharedPreferences: SharedPreferences
    val mLiked = mutableSetOf<String>()
    val mHistory = mutableSetOf<String>()

    private var mIsVivible = false
    private var mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val comic = intent.extras.getSerializable(MainActivity.ACTION_LIKE) as? Comic
            if (comic != null) {
                val comic1 = mComics.find {
                    it.storyId == comic.storyId
                }
                comic1?.like = comic.like
                comic1?.likeCount = comic.likeCount
                savedStory(Constants.KEY_LIKE, comic.storyId, comic.like)
                if (mIsVivible) {
                    mAdapter.notifyDataSetChanged()
                }
            }
        }

    }
    var mAdapter = StoryListAdapter(mComics, MainActivity.getMacAddr(),
            object : MenuAdapter.RecyclerViewOnItemClickListener {
                override fun onLikeAction(storyId: Long, liked: Boolean) {
                    savedStory(Constants.KEY_LIKE, storyId, liked)
                }

                override fun onItemClick(item: Any) {
                    val comic = item as? Comic
                    if (comic != null) {
                        val intent = Intent(activity, DetailActivity::class.java)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mSharedPreferences = activity.getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE)
        getSavedStory(Constants.KEY_LIKE)
        getSavedStory(Constants.KEY_HISTORY)
        mIsVivible = true
        activity.registerReceiver(mReceiver, IntentFilter(MainActivity.ACTION_LIKE))
        val itemView = inflater.inflate(R.layout.fragment_main, container, false)
        mSwipeRefreshLayout = itemView.swipeRefreshLayout
        mRecyclerView = itemView.recyclerViewComics
        mImgToFirstPage = itemView.imgToFirstPage
        mImgPrevious = itemView.imgPreviousPage
        mImgNext = itemView.imgNextPage
        mImgToLastPage = itemView.imgToLastPage
        mTvCurrentPage = itemView.tvCurrentPage
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = mAdapter
        getStories()
        onClick()
        return itemView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        mIsVivible = isVisibleToUser
        if (mIsVivible) {
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun getUserVisibleHint(): Boolean {
        return mIsVivible
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
                mLiked.add(storyId.toString())
            } else {
                mLiked.remove(storyId.toString())
            }
            editor.putString(Constants.KEY_LIKE, mLiked.toString())
        } else {
            mHistory.add(storyId.toString())
            editor.putString(Constants.KEY_HISTORY, mHistory.toString())
        }
        editor.apply()
    }

    abstract fun getStories()

    fun onClick() {
        mSwipeRefreshLayout.setOnRefreshListener {
            getStories()
        }
        mImgToFirstPage.setOnClickListener {
            if (mCurrentPage in 2..mPageCount) {
                mCurrentPage = 1
                getStories()
            }
        }
        mImgPrevious.setOnClickListener {
            if (mCurrentPage in 2..mPageCount) {
                mCurrentPage--
                getStories()
            }
        }
        mImgNext.setOnClickListener {
            if (mCurrentPage in 1..(mPageCount - 1)) {
                mCurrentPage++
                getStories()
            }
        }
        mImgToLastPage.setOnClickListener {
            if (mCurrentPage in 1..(mPageCount - 1)) {
                mCurrentPage = mPageCount
                getStories()
            }
        }
    }

    fun updateFooter() {
        mPageCount = if (mComicCount % 20 > 0) {
            mComicCount / 20 + 1
        } else {
            mComicCount / 20
        }
        if (mComicCount == 0) {
            mCurrentPage = 0
        }

        if (mCurrentPage < 2) {
            mImgPrevious.isEnabled = false
            mImgToFirstPage.isEnabled = false
        } else {
            mImgPrevious.isEnabled = true
            mImgToFirstPage.isEnabled = true
        }
        if (mCurrentPage > mPageCount - 1) {
            mImgNext.isEnabled = false
            mImgToLastPage.isEnabled = false
        } else {
            mImgNext.isEnabled = true
            mImgToLastPage.isEnabled = true
        }
        mTvCurrentPage.text = getString(R.string.footerText, mCurrentPage, mPageCount)
    }
}