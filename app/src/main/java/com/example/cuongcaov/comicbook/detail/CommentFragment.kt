package com.example.cuongcaov.comicbook.detail

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.main.MainActivity
import com.example.cuongcaov.comicbook.model.*
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import com.example.cuongcaov.comicbook.splash.SplashActivity
import com.example.cuongcaov.comicbook.ultis.Constants
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_comment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
class CommentFragment : Fragment() {
    companion object {

        fun getInstance(storyId: Long): CommentFragment {
            val instance = CommentFragment()
            val bundle = Bundle()
            bundle.putLong(Constants.KEY_STORY_ID, storyId)
            instance.arguments = bundle
            return instance
        }

        fun encodeImage(bitmap: Bitmap): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val b = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }
    }

    private lateinit var mImgAvatar: ImageView
    private lateinit var mImgChangeName: ImageView
    private lateinit var mEdtName: EditText
    private lateinit var mEdtComment: EditText
    private lateinit var mImgComment: CircleImageView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mRecyclerViewComments: RecyclerView
    private var mStoryId: Long = 0L
    private var mMacAddress: String = ""
    private lateinit var mUser: User
    private lateinit var mShared: SharedPreferences
    private var mEditEnable = false
    private var mCommentCount = 20
    private var mComments = mutableListOf<Comment>()
    private val mAdapter = CommentAdapter(mComments)
    private var mIsVisible = false
    private var mDataLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStoryId = arguments.getLong(Constants.KEY_STORY_ID)
        mMacAddress = MainActivity.getMacAddr()
        mShared = activity.getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE)
        mUser = SplashActivity.getUser(mShared)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comment, container, false)
        mImgAvatar = view.imgAvatar
        mImgChangeName = view.imgChangeUsername
        mEdtName = view.edtUserName
        mImgComment = view.btnComment
        mEdtComment = view.edtComment
        mSwipeRefreshLayout = view.swipeRefreshLayout
        mRecyclerViewComments = view.recyclerViewComments
        mEdtName.setText(mUser.name)
        if (mUser.avatar != "non-avatar") {
            Glide.with(context).load("http://freestory.000webhostapp.com/api/" + mUser.avatar)
                    .into(mImgAvatar)
        }
        onClick()
        mRecyclerViewComments.layoutManager = LinearLayoutManager(context)
        mRecyclerViewComments.adapter = mAdapter
        mRecyclerViewComments.visibility = View.VISIBLE
        if (mIsVisible && !mDataLoaded) {
            getComments()
        }
        return view
    }

    override fun getUserVisibleHint(): Boolean {
        return mIsVisible
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        mIsVisible = isVisible
        if (mIsVisible && !mDataLoaded) {
            getComments()
            mDataLoaded = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                cropImage(uri)
            }
            return
        }
        if (requestCode == Constants.REQUEST_CODE_CROP && resultCode == Activity.RESULT_OK) {
            val bundle = data?.extras
            if (bundle != null) {
                val bm = bundle.getParcelable<Bitmap>("data")
                val encodeImage = encodeImage(bm)
                RetrofitClient.getAPIService().changeAvatar(mUser.macAddress, encodeImage)
                        .enqueue(object : Callback<AvatarResult> {
                            override fun onFailure(call: Call<AvatarResult>?, t: Throwable?) {
                                Log.i("tag11", "Fail: " + t?.message)
                            }

                            override fun onResponse(call: Call<AvatarResult>?, response: Response<AvatarResult>?) {
                                val path = response?.body()?.path
                                if (path != null) {
                                    mUser.avatar = path
                                }
                                SplashActivity.saveUser(mShared, mUser)
                                Glide.with(context).load("http://freestory.000webhostapp.com/api/" + mUser.avatar)
                                        .into(mImgAvatar)
                                mCommentCount = 20
                                getComments()
                            }

                        })
            }
        }
    }

    private fun onClick() {
        mImgAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY)
        }
        mImgChangeName.setOnClickListener {
            if (mEditEnable) {
                mEditEnable = false
                mEdtName.isEnabled = false
                mImgChangeName.setImageResource(R.drawable.ic_border_color_indigo_500_36dp)
                var name = mEdtName.text.toString().trim()
                while (name.contains("  ")) {
                    name = name.replace("  ", " ")
                }
                RetrofitClient.getAPIService().changeName(mUser.macAddress, name)
                        .enqueue(object : Callback<NameResult> {
                            override fun onFailure(call: Call<NameResult>?, t: Throwable?) {
                                Toast.makeText(context, "Không thành công", Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<NameResult>?, response: Response<NameResult>?) {
                                val status = response?.body()?.status
                                if (status == true) {
                                    mUser.name = name
                                    SplashActivity.saveUser(mShared, mUser)
                                    mEdtName.setText(mUser.name)
                                    mCommentCount = 20
                                    getComments()
                                } else {
                                    Toast.makeText(context, "Không thành công", Toast.LENGTH_LONG).show()
                                }
                            }

                        })
            } else {
                mEditEnable = true
                mEdtName.isEnabled = true
                mImgChangeName.setImageResource(R.drawable.ic_check_indigo_500_36dp)
            }
        }

        mImgComment.setOnClickListener {
            val comment = mEdtComment.text.toString().trim()
            if (comment != "") {
                RetrofitClient.getAPIService().comment(mStoryId, mUser.macAddress, comment)
                        .enqueue(object : Callback<StatusResult> {
                            override fun onResponse(call: Call<StatusResult>?, response: Response<StatusResult>?) {
                                val status = response?.body()?.status
                                if (status == true) {
                                    getComments()
                                } else {
                                    Toast.makeText(context, "Không thành công", Toast.LENGTH_LONG).show()
                                    getComments()
                                }
                            }

                            override fun onFailure(call: Call<StatusResult>?, t: Throwable?) {
                                Toast.makeText(context, "Không thành công", Toast.LENGTH_LONG).show()
                            }

                        })
            }
            mEdtComment.setText("")
        }
        mSwipeRefreshLayout.setOnRefreshListener {
            mCommentCount += 20
            getComments()
        }
    }

    private fun getComments() {
        RetrofitClient.getAPIService().getComments(mStoryId, mCommentCount)
                .enqueue(object : Callback<List<Comment>> {
                    override fun onFailure(call: Call<List<Comment>>?, t: Throwable?) {
                        if (mSwipeRefreshLayout.isRefreshing) {
                            mSwipeRefreshLayout.isRefreshing = false
                        }
                        Toast.makeText(context, "Danh sách trống", Toast.LENGTH_LONG).show()
                        mComments.clear()
                        mAdapter.notifyDataSetChanged()
                        mRecyclerViewComments.visibility = View.GONE
                    }

                    override fun onResponse(call: Call<List<Comment>>?, response: Response<List<Comment>>?) {
                        var loadMore = false
                        if (mSwipeRefreshLayout.isRefreshing) {
                            mSwipeRefreshLayout.isRefreshing = false
                            loadMore = false
                        }
                        val list = response?.body()
                        if (list != null) {
                            mComments.clear()
                            mRecyclerViewComments.visibility = View.VISIBLE
                            mComments.addAll(list.reversed())
                            mAdapter.notifyDataSetChanged()
                        }
                        if (mComments.size == 0) {
                            Toast.makeText(context, "Danh sách trống", Toast.LENGTH_LONG).show()
                            mComments.clear()
                            mAdapter.notifyDataSetChanged()
                            mRecyclerViewComments.visibility = View.GONE
                        }
                        if (!loadMore) {
                            mRecyclerViewComments.scrollToPosition(mComments.size - 1)
                        } else {
                            mRecyclerViewComments.scrollToPosition(0)
                        }
                    }

                })
    }

    private fun cropImage(uri: Uri) {
        try {
            val cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri, "image/*")
            cropIntent.putExtra("crop", "true")
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            cropIntent.putExtra("outputX", 256)
            cropIntent.putExtra("outputY", 256)
            cropIntent.putExtra("return-data", true)
            startActivityForResult(cropIntent, Constants.REQUEST_CODE_CROP)
        } catch (e: ActivityNotFoundException) {
            val toast = Toast.makeText(context, R.string.crop_error, Toast.LENGTH_SHORT)
            toast.show()
        }
    }

}