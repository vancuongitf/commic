package com.example.cuongcaov.comicbook.detail

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.base.BaseActivity
import com.example.cuongcaov.comicbook.model.Comic
import kotlinx.android.synthetic.main.activity_detail.*

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
class DetailActivity : BaseActivity() {

    lateinit var mComic: Comic
    lateinit var mAdapter: FragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        mComic = intent.extras.getSerializable(DetailFragment.KEY_COMIC) as Comic
        initView()
    }

    private fun initView() {
        mAdapter = FragmentAdapter(supportFragmentManager, mComic)
        tabLayout.setupWithViewPager(viewPagerMainContent)
        viewPagerMainContent.adapter = mAdapter
        tabLayout.getTabAt(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_library_books_red_700_24dp)
        tabLayout.getTabAt(1)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_message_deep_purple_200_24dp)
        tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit

            override fun onTabUnselected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    tab.icon = ContextCompat.getDrawable(this@DetailActivity, R.drawable.ic_library_books_deep_purple_200_24dp)
                } else {
                    tab.icon = ContextCompat.getDrawable(this@DetailActivity, R.drawable.ic_message_deep_purple_200_24dp)
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position==0){
                    tab.icon = ContextCompat.getDrawable(this@DetailActivity, R.drawable.ic_library_books_red_700_24dp)
                } else {
                    tab.icon = ContextCompat.getDrawable(this@DetailActivity, R.drawable.ic_message_red_700_24dp)
                }
            }
        })
    }
}
