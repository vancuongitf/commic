package com.example.cuongcaov.comicbook.content

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.cuongcaov.comicbook.model.Content

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 30/09/2017
 */
class ContentAdapter(fm: FragmentManager,
                     val mContents: List<Content>,
                     val mHaveNextChapter: Boolean,
                     val mHavePreviousChapter: Boolean,
                     val mListener: OnItemClick) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return ContentFragment.getNewInstance(mContents[position].source, position, mHaveNextChapter || position < mContents.size - 1, mHavePreviousChapter || position > 0, mListener)
    }

    override fun getCount(): Int = mContents.size


    interface OnItemClick {
        fun onBack(position: Int)

        fun onNext(position: Int)
    }
}