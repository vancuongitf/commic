package com.example.cuongcaov.comicbook.content

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.ultis.Constants
import kotlinx.android.synthetic.main.fragment_content.view.*

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 09/10/2017
 */
class ContentFragment : Fragment() {

    companion object {

        fun getNewInstance(source: String, position: Int, showNextArrow: Boolean, showPreviousArrow: Boolean, listener: ContentAdapter.OnItemClick): ContentFragment {
            val contentFragment = ContentFragment()
            val bundle = Bundle()
            bundle.putString(Constants.KEY_SOURCE, source)
            bundle.putInt(Constants.KEY_POSITION, position)
            contentFragment.setListener(listener)
            contentFragment.mShowNextArrow = showNextArrow
            contentFragment.mShowPreviousArrow = showPreviousArrow
            contentFragment.arguments = bundle
            return contentFragment
        }
    }

    private var mPosition: Int = 0
    private var mSource: String = ""
    private var mListener: ContentAdapter.OnItemClick? = null
    private var mShowNextArrow = true
    private var mShowPreviousArrow = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPosition = arguments.getInt(Constants.KEY_POSITION)
        mSource = arguments.getString(Constants.KEY_SOURCE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_content, container, false)
        Glide.with(context).load(mSource).into(view.imgContent)
        if (!mShowNextArrow) {
            view.imgNextContent.visibility = View.GONE
        }
        if (!mShowPreviousArrow) {
            view.imgPreviousContent.visibility = View.GONE
        }
        view.imgContent.setOnClickListener {
            if (mShowNextArrow) {
                if (view.imgNextContent.visibility == View.GONE) {
                    view.imgNextContent.visibility = View.VISIBLE
                } else {
                    view.imgNextContent.visibility = View.GONE
                }
            }
            if (mShowPreviousArrow) {
                if (view.imgPreviousContent.visibility == View.GONE) {
                    view.imgPreviousContent.visibility = View.VISIBLE
                } else {
                    view.imgPreviousContent.visibility = View.GONE
                }
            }
        }
        view.imgNextContent.setOnClickListener {
            mListener?.onNext(mPosition)
        }
        view.imgPreviousContent.setOnClickListener {
            mListener?.onBack(mPosition)
        }
        return view
    }

    private fun setListener(listener: ContentAdapter.OnItemClick) {
        this.mListener = listener
    }

}
