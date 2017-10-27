package com.example.cuongcaov.comicbook.content

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.database.DbComic
import com.example.cuongcaov.comicbook.model.ChapterContents
import com.example.cuongcaov.comicbook.model.Content
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import com.example.cuongcaov.comicbook.ultis.Constants
import kotlinx.android.synthetic.main.activity_content.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 29/09/2017
 */
class ContentActivity : AppCompatActivity() {

    private val mContents = mutableListOf<Content>()
    private var mAdapter: ContentAdapter? = null
    private var mChapterId = -1L
    private var mPreviousChapter = -1L
    private var mNextChapter = -1L
    private lateinit var mDb: DbComic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDb = DbComic(this)
        mDb.open()
        initView()
        with(intent.extras) {
            mChapterId = getLong(Constants.KEY_CHAPTER_ID)
        }
        getContents()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDb.close()
    }

    private fun initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_content)
        viewPagerContents.offscreenPageLimit = 2
    }

    private fun getContents() {
        mContents.clear()
        mAdapter?.notifyDataSetChanged()
        viewPagerContents.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
            override fun onPageSelected(position: Int) {

            }

        })
        val thread = Thread({
            RetrofitClient.getAPIService().getContentList(mChapterId)
                    .enqueue(object : Callback<ChapterContents> {
                        override fun onResponse(call: Call<ChapterContents>?, response: Response<ChapterContents>?) {
                            val chapterContents = response?.body()
                            if (chapterContents != null) {
                                mNextChapter = chapterContents.nextChapter
                                mPreviousChapter = chapterContents.previousChapter
                                runOnUiThread {
                                    if (chapterContents.contentList.size > 0) {
                                        mContents.addAll(chapterContents.contentList)
                                        mAdapter = ContentAdapter(supportFragmentManager,
                                                mContents,
                                                mNextChapter != -1L,
                                                mPreviousChapter != -1L,
                                                object : ContentAdapter.OnItemClick {
                                                    override fun onBack(position: Int) {
                                                        if (position > 0) {
                                                            viewPagerContents.currentItem--
                                                        } else {
                                                            showDialog("Đọc chương trước",
                                                                    "Bạn có muốn đọc chương trước",
                                                                    mPreviousChapter)
                                                        }

                                                    }

                                                    override fun onNext(position: Int) {
                                                        if (position < mContents.size - 1) {
                                                            viewPagerContents.currentItem++
                                                        } else {
                                                            showDialog("Đọc chương tiếp theo.",
                                                                    "Bạn có muốn đọc chương tiếp theo",
                                                                    mNextChapter)
                                                        }
                                                    }

                                                })
                                        viewPagerContents.adapter = mAdapter
                                    } else {
                                        Toast.makeText(this@ContentActivity, "Danh sách trống.", Toast.LENGTH_LONG).show()
                                        this@ContentActivity.finish()
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<ChapterContents>?, t: Throwable?) {

                        }

                    })
        })
        thread.start()
    }

    private fun showDialog(title: String, message: String, chapterId: Long) {
        Log.i("tag11", chapterId.toString())
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Ok", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                val intent = Intent(this@ContentActivity, ContentActivity::class.java)
                val bundle = Bundle()
                bundle.putLong(Constants.KEY_CHAPTER_ID, chapterId)
                intent.putExtras(bundle)
                this@ContentActivity.startActivity(intent)
            }
        }).setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                dialog.dismiss()
            }

        })
        builder.setTitle(title)
        builder.setMessage(message)
        builder.create().show()
    }
}