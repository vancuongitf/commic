package com.example.cuongcaov.comicbook.storydetail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.main.MenuAdapter
import com.example.cuongcaov.comicbook.networking.Chapter
import kotlinx.android.synthetic.main.item_chapter.view.*

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 29/09/2017
 */

class ChapterListAdapter(val mChapters: MutableList<Chapter>,
                         val mListener: MenuAdapter.RecyclerViewOnItemClickListener)
    : RecyclerView.Adapter<ChapterListAdapter.ChapterViewHolder>() {
    override fun getItemCount() = mChapters.size

    override fun onBindViewHolder(holder: ChapterViewHolder?, position: Int) {
        holder?.onBind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chapter, parent, false)
        return ChapterViewHolder(view)
    }

    inner class ChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                mListener.onItemClick(mChapters[adapterPosition])
            }
        }

        fun onBind() {
            with(mChapters[adapterPosition]) {
                itemView.tvChapterPosition.text = "Chapter: $position"
            }
        }
    }
}