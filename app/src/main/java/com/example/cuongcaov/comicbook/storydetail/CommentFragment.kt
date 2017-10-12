package com.example.cuongcaov.comicbook.storydetail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cuongcaov.comicbook.R


/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
class CommentFragment : Fragment() {
    companion object {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comment, container, false)
        return view
    }
}