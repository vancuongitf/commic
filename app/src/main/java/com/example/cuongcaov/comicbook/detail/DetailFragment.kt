package com.example.cuongcaov.comicbook.detail

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.content.ContentActivity
import com.example.cuongcaov.comicbook.database.DbComic
import com.example.cuongcaov.comicbook.main.MainActivity
import com.example.cuongcaov.comicbook.main.MenuAdapter
import com.example.cuongcaov.comicbook.model.APIResultChapter
import com.example.cuongcaov.comicbook.model.APIResultLike
import com.example.cuongcaov.comicbook.model.Chapter
import com.example.cuongcaov.comicbook.model.Comic
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import com.example.cuongcaov.comicbook.ultis.Constants
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

        fun getInstance(comic: Comic): DetailFragment {
            val detailFragment = DetailFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.KEY_COMIC, comic)
            detailFragment.arguments = bundle
            return detailFragment
        }
    }

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mRecyclerViewChapters: RecyclerView
    private lateinit var mImgToFirstPage: ImageView
    private lateinit var mImgPrevious: ImageView
    private lateinit var mImgNext: ImageView
    private lateinit var mImgToLastPage: ImageView
    private lateinit var mTvCurrentPage: TextView
    private lateinit var mImgLike: ImageView
    private lateinit var mTvLikeCount: TextView

    private lateinit var mDb: DbComic
    private lateinit var mComic: Comic
    private var mChapters: MutableList<Chapter> = mutableListOf()
    private var mChapterCount = 0
    private var mCurrentPage = 1
    private var mPageCount = 0
    private var mAdapter: ChapterListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDb = DbComic(context)
        mDb.open()
        mComic = arguments.getSerializable(Constants.KEY_COMIC) as Comic
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val itemView = LayoutInflater.from(context).inflate(R.layout.fragment_story_detail, container, false)
        mSwipeRefreshLayout = itemView.swipeRefreshLayout
        mRecyclerViewChapters = itemView.recyclerViewChapters
        mImgToFirstPage = itemView.imgToFirstPage
        mImgPrevious = itemView.imgPreviousPage
        mImgNext = itemView.imgNextPage
        mImgToLastPage = itemView.imgToLastPage
        mTvCurrentPage = itemView.tvCurrentPage
        mImgLike = itemView.imgLike
        mTvLikeCount = itemView.tvLikeCount
        with(mComic) {
            itemView.tvComicName.text = storyName
            itemView.tvComicName.isSelected = true
            itemView.tvAuthor.text = itemView.context.getString(R.string.author, author)
            itemView.tvAuthor.isSelected = true
            itemView.tvComicTypes.text = itemView.context.getString(R.string.types, getTypes())
            itemView.tvComicTypes.isSelected = true
            itemView.tvNumOfChapters.text = itemView.context.getString(R.string.numOfChapter, numberOfChapters)
            itemView.tvStatus.text = itemView.context.getString(R.string.status, status)
            itemView.tvLikeCount.text = likeCount.toString()
            itemView.tvReadCount.text = readCount.toString()
            itemView.tvCommentCount.text = commentCount.toString()
            Glide.with(context).load(intro).into(itemView.imgStoryAvatar)
            if (like) {
                itemView.imgLike.setImageResource(R.drawable.ic_star_red_500_18dp)
            } else {
                itemView.imgLike.setImageResource(R.drawable.ic_star_deep_purple_200_18dp)
            }
            if (seen) {
                itemView.tvSeen.visibility = View.VISIBLE
            } else {
                itemView.tvSeen.visibility = View.GONE
            }
        }
        mRecyclerViewChapters.layoutManager = LinearLayoutManager(context)
        mAdapter = ChapterListAdapter(mChapters, object : MenuAdapter.RecyclerViewOnItemClickListener {
            override fun onItemClick(item: Any) {
                val chapter = item as? Chapter
                if (chapter != null) {
                    val intent = Intent(activity, ContentActivity::class.java)
                    val bundle = Bundle()
                    bundle.putLong(Constants.KEY_STORY_ID, mComic.storyId)
                    bundle.putLong(Constants.KEY_CHAPTER_ID, chapter.chapterId)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }

        })
        itemView.recyclerViewChapters.adapter = mAdapter
        getChapterList()
        onClick()
        itemView.swipeRefreshLayout.setOnRefreshListener {
            getChapterList()
        }
        val history = mDb.getHistoryById(mComic.storyId)
        if (history != null) {
            val intent = Intent(activity, ContentActivity::class.java)
            val bundle = Bundle()
            bundle.putLong(Constants.KEY_STORY_ID, mComic.storyId)
            bundle.putLong(Constants.KEY_CHAPTER_ID, history.chapterId)
            bundle.putInt(Constants.KEY_POSITION, history.position)
            intent.putExtras(bundle)
            showDialog(intent)
        }
        return itemView
    }

    override fun onDetach() {
        super.onDetach()
        mDb.close()
    }

    private fun getChapterList() {
        val thread = Thread({
            val apiService = RetrofitClient.getAPIService()
            apiService.getChapterList(mComic.storyId, mCurrentPage)
                    .enqueue(object : Callback<APIResultChapter> {
                        override fun onFailure(call: Call<APIResultChapter>?, t: Throwable?) {
                            mSwipeRefreshLayout.isRefreshing = false
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
                                    mSwipeRefreshLayout.isRefreshing = false
                                    mRecyclerViewChapters.scrollToPosition(0)
                                }
                            }
                        }

                    })
        })
        thread.start()
        mSwipeRefreshLayout.isRefreshing = true
    }

    private fun onClick() {
        mImgToFirstPage.setOnClickListener {
            mCurrentPage = 1
            getChapterList()
        }
        mImgPrevious.setOnClickListener {
            mCurrentPage--
            getChapterList()
        }
        mImgNext.setOnClickListener {
            mCurrentPage++
            getChapterList()
        }
        mImgToLastPage.setOnClickListener {
            mCurrentPage = mPageCount
            getChapterList()
        }
        mImgLike.setOnClickListener {
            var actionLike = 1
            if (mComic.like) {
                actionLike = 0
            }
            RetrofitClient.getAPIService().actionLike(mComic.storyId, MainActivity.getMacAddr(), actionLike)
                    .enqueue(object : Callback<APIResultLike> {
                        override fun onResponse(call: Call<APIResultLike>?, response: Response<APIResultLike>?) {
                            val result = response?.body()
                            if (result != null) {
                                with(result) {
                                    if (status && mImgLike != null) {
                                        mComic.likeCount = count
                                        if (actionLike == 0) {
                                            mComic.like = false
                                            mImgLike.setImageResource(R.drawable.ic_star_deep_purple_200_18dp)
                                        } else {
                                            mComic.like = true
                                            mImgLike.setImageResource(R.drawable.ic_star_red_500_18dp)
                                        }
                                        mTvLikeCount.text = mComic.likeCount.toString()
                                        val intent = Intent(MainActivity.ACTION_LIKE)
                                        val bundle = Bundle()
                                        bundle.putSerializable(MainActivity.ACTION_LIKE, mComic)
                                        intent.putExtras(bundle)
                                        activity.sendBroadcast(intent)
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<APIResultLike>?, t: Throwable?) = Unit
                    })
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

    private fun showDialog(intent: Intent) {
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton("Ok") { _, _ ->
            activity.startActivity(intent)
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.setTitle("Đọc tiếp.")
        builder.setMessage("Bạn có muốn tiếp tục đọc từ lần trước.")
        builder.create().show()
    }
}
