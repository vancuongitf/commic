package com.example.cuongcaov.comicbook.storydetail

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.content.ContentActivity
import com.example.cuongcaov.comicbook.main.Comic
import com.example.cuongcaov.comicbook.main.MenuAdapter
import com.example.cuongcaov.comicbook.networking.APIResultChapter
import com.example.cuongcaov.comicbook.networking.Chapter
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import kotlinx.android.synthetic.main.fragment_story_detail.view.*
import kotlinx.android.synthetic.main.item_comic.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
class DetailFragment : Fragment() {

    companion object {

        const val KEY_COMIC = "comic"
        const val KEY_CHAPTER_ID = "storyId"

        fun getInstance(comic: Comic): DetailFragment {
            val detailFragment = DetailFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_COMIC, comic)
            detailFragment.arguments = bundle
            return detailFragment
        }
    }

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mImgToFirstPage: ImageView? = null
    private var mImgPrevious: ImageView? = null
    private var mImgNext: ImageView? = null
    private var mImgToLastPage: ImageView? = null
    private var mTvCurrentPage: TextView? = null

    private var mComic: Comic? = null
    private var mChapters: MutableList<Chapter> = mutableListOf()
    private var mChapterCount = 0
    private var mCurrentPage = 1
    private var mPageCount = 0
    private var mAdapter: ChapterListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mComic = arguments.getSerializable(KEY_COMIC) as? Comic
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val itemView = LayoutInflater.from(context).inflate(R.layout.fragment_story_detail, container, false)

        mSwipeRefreshLayout = itemView.swipeRefreshLayout
        mImgToFirstPage = itemView.imgToFirstPage
        mImgPrevious = itemView.imgPreviousPage
        mImgNext = itemView.imgNextPage
        mImgToLastPage = itemView.imgToLastPage
        mTvCurrentPage = itemView.tvCurrentPage

        if (mComic != null) {
            itemView.tvComicName.text = mComic?.storyName
            itemView.tvAuthor.text = mComic?.author
            itemView.tvComicTypes.text = mComic?.getTypes()
            itemView.tvNumOfChapters.text = getString(R.string.numOfChapter, mComic?.numberOfChapters)
            itemView.tvStatus.text = getString(R.string.status, mComic?.status)
            itemView.recyclerViewChapters.layoutManager = LinearLayoutManager(context)
            mAdapter = ChapterListAdapter(mChapters, object : MenuAdapter.RecyclerViewOnItemClickListener {
                override fun onItemClick(item: Any) {
                    val chapter = item as? Chapter
                    if (chapter != null) {
                        val intent = Intent(activity, ContentActivity::class.java)
                        val bundle = Bundle()
                        bundle.putLong(KEY_CHAPTER_ID, chapter.chapterId)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }

            })
            itemView.recyclerViewChapters.adapter = mAdapter
            getChapterList()
        }
        onClick()
        return itemView
    }

    private fun getChapterList() {
        val thread = Thread({
            val apiService = RetrofitClient.getAPIService()
            apiService.getChapterList(mComic!!.storyId, mCurrentPage)
                    .enqueue(object : Callback<APIResultChapter> {
                        override fun onFailure(call: Call<APIResultChapter>?, t: Throwable?) {
                            mSwipeRefreshLayout?.isRefreshing = false
                            Toast.makeText(context, "Xãy ra lỗi.", Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<APIResultChapter>?, response: Response<APIResultChapter>?) {
                            val apiResultChapter = response?.body()
                            if (apiResultChapter != null) {
                                mChapterCount = apiResultChapter.chapterCount
                                updateFooter(mChapterCount)
                                activity.runOnUiThread {
                                    mChapters.clear()
                                    mChapters.addAll(apiResultChapter.data)
                                    mAdapter?.notifyDataSetChanged()
                                    mSwipeRefreshLayout?.isRefreshing = false
                                }
                            }
                        }

                    })
        })
        thread.start()
        mSwipeRefreshLayout?.isRefreshing = true
    }

    private fun onClick() {
        mImgToFirstPage?.setOnClickListener {
            mCurrentPage = 1
            getChapterList()
        }
        mImgPrevious?.setOnClickListener {
            mCurrentPage--
            getChapterList()
        }
        mImgNext?.setOnClickListener {
            mCurrentPage++
            getChapterList()
        }
        mImgToLastPage?.setOnClickListener {
            mCurrentPage = mPageCount
            getChapterList()
        }
    }

    private fun updateFooter(storyCount: Int?) {
        mPageCount = if (storyCount == null) {
            1
        } else {
            if (storyCount % 100 > 0) {
                storyCount / 100 + 1
            } else {
                storyCount / 100
            }

        }
        if (mCurrentPage < 2) {
            mImgPrevious?.isEnabled = false
            mImgToFirstPage?.isEnabled = false
        } else {
            mImgPrevious?.isEnabled = true
            mImgToFirstPage?.isEnabled = true
        }
        if (mCurrentPage > mPageCount - 1) {
            mImgNext?.isEnabled = false
            mImgToLastPage?.isEnabled = false
        } else {
            mImgNext?.isEnabled = true
            mImgToLastPage?.isEnabled = true
        }
        mTvCurrentPage?.text = getString(R.string.footerText, mCurrentPage, mPageCount)
    }

}