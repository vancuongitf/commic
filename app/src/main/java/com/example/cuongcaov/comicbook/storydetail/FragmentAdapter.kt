package com.example.cuongcaov.comicbook.storydetail

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.cuongcaov.comicbook.main.Comic

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 29/09/2017
 */
class FragmentAdapter(fm: FragmentManager, val mComic: Comic) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            DetailFragment.getInstance(mComic)

        } else {
            CommentFragment()
        }
    }

    override fun getCount() = 2
}