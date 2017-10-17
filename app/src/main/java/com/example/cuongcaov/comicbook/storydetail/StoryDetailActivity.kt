package com.example.cuongcaov.comicbook.storydetail

import android.os.Bundle
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.base.BaseActivity
import com.example.cuongcaov.comicbook.main.Comic

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
class StoryDetailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val comic = intent.extras.getSerializable(DetailFragment.KEY_COMIC) as? Comic
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.flContent, DetailFragment.getInstance(comic!!))
        transaction.commit()
    }
}
