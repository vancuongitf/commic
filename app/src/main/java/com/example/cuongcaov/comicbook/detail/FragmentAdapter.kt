package com.example.cuongcaov.comicbook.detail

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.cuongcaov.comicbook.model.Comic

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 29/09/2017
 */
class FragmentAdapter(fm: FragmentManager, val mComic: Comic) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            DetailFragment.getInstance(mComic)

        } else {
            CommentFragment.getInstance(mComic.storyId)
        }
    }

    override fun getCount() = 2
}