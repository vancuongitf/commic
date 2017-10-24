package com.example.cuongcaov.comicbook.detail

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.main.MainActivity
import com.example.cuongcaov.comicbook.model.Comment
import kotlinx.android.synthetic.main.item_comment.view.*

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 20/10/2017
 */
class CommentAdapter(val mComments: MutableList<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    override fun getItemCount(): Int {
        return mComments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder?, position: Int) {
        holder?.onBind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind() {
            with(mComments[adapterPosition]) {
                itemView.tvUserName.text = userName
                itemView.tvTime.text = time
                itemView.tvComment.text = comment
                if (avatar != "non-avatar") {
                    Glide.with(itemView.context)
                            .load("http://freestory.000webhostapp.com/api/" + avatar)
                            .into(itemView.imgAvatar)
                } else {
                    itemView.imgAvatar.setImageResource(R.drawable.ic_yasuo)
                }
                if (macAddress == MainActivity.getMacAddr()) {
                    itemView.tvUserName.setTextColor(Color.RED)
                } else {
                    itemView.tvUserName.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorName))
                }
            }
        }
    }
}