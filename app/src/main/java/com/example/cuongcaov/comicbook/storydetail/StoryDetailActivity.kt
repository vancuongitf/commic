package com.example.cuongcaov.comicbook.storydetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.main.Comic

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
class StoryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val comic = intent.extras.getSerializable(DetailFragment.KEY_COMIC) as? Comic
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.flContent, DetailFragment.getInstance(comic!!))
        transaction.commit()
    }
}
