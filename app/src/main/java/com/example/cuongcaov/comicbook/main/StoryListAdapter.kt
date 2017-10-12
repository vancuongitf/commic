package com.example.cuongcaov.comicbook.main

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cuongcaov.comicbook.R
import kotlinx.android.synthetic.main.item_comic.view.*

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
class StoryListAdapter(private val mComics: List<Comic>, private val mListener: MenuAdapter.RecyclerViewOnItemClickListener) : RecyclerView.Adapter<StoryListAdapter.StoryViewHolder>() {

    override fun onBindViewHolder(holder: StoryViewHolder?, position: Int) {
        holder?.onBind()
    }

    override fun getItemCount(): Int = mComics.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comic, parent, false)
        return StoryViewHolder(view)
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                mListener.onItemClick(mComics[adapterPosition])
            }
        }

        fun onBind() {
            if (adapterPosition % 2 == 0) {
                itemView.rlContent.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorComicItemBackground))
            } else {
                itemView.rlContent.setBackgroundColor(Color.WHITE)
            }
            with(mComics[adapterPosition]) {
                itemView.tvComicName.text = storyName
                itemView.tvComicName.isSelected = true
                itemView.tvAuthor.text = itemView.context.getString(R.string.author, author)
                itemView.tvAuthor.isSelected = true
                itemView.tvComicTypes.text = itemView.context.getString(R.string.types, getTypes())
                itemView.tvComicTypes.isSelected = true
                itemView.tvNumOfChapters.text = itemView.context.getString(R.string.numOfChapter, numberOfChapters)
                itemView.tvStatus.text = itemView.context.getString(R.string.status, status)
            }
        }
    }
}
