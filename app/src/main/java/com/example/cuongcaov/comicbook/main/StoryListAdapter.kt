package com.example.cuongcaov.comicbook.main

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.networking.APIResultLike
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import kotlinx.android.synthetic.main.item_comic.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 28/09/2017
 */
class StoryListAdapter(private val mComics: List<Comic>, private val mMacAddress: String,
                       private val mListener: MenuAdapter.RecyclerViewOnItemClickListener)
    : RecyclerView.Adapter<StoryListAdapter.StoryViewHolder>() {

    override fun onBindViewHolder(holder: StoryViewHolder?, position: Int) {
        holder?.onBind()
    }

    override fun getItemCount(): Int = mComics.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comic, parent, false)
        return StoryViewHolder(view)
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                mListener.onItemClick(mComics[adapterPosition])
            }
            itemView.imgLike.setOnClickListener {
                var actionLike = 1
                if (mComics[adapterPosition].like) {
                    actionLike = 0
                }
                RetrofitClient.getAPIService()
                        .actionLike(mComics[adapterPosition].storyId, mMacAddress, actionLike)
                        .enqueue(object : Callback<APIResultLike> {
                            override fun onResponse(call: Call<APIResultLike>?,
                                                    response: Response<APIResultLike>?) {
                                val status = response?.body()?.status
                                if (status == true) {
                                    mListener.onLikeAction(mComics[adapterPosition].storyId, actionLike != 0)
                                    mComics[adapterPosition].like = actionLike != 0
                                    mComics[adapterPosition].likeCount = response.body()?.count!!
                                    notifyItemChanged(adapterPosition)
                                }
                            }

                            override fun onFailure(call: Call<APIResultLike>?, t: Throwable?) = Unit

                        })
            }
        }

        fun onBind() {
            if (adapterPosition % 2 == 0) {
                itemView.rlContent.setBackgroundColor(
                        ContextCompat.getColor(itemView.context, R.color.colorComicItemBackground))
            } else {
                itemView.rlContent.setBackgroundColor(Color.WHITE)
            }
            with(mComics[adapterPosition]) {
                itemView.tvComicName.text = storyName
                itemView.tvComicName.isSelected = true
                itemView.tvAuthor.text = itemView.context.getString(R.string.author, author)
                itemView.tvComicTypes.text = itemView.context.getString(R.string.types, getTypes())
                itemView.tvNumOfChapters.text = itemView.context.getString(R.string.numOfChapter, numberOfChapters)
                itemView.tvStatus.text = itemView.context.getString(R.string.status, status)
                itemView.tvLikeCount.text = likeCount.toString()
                itemView.tvReadCount.text = readCount.toString()
                if (like) {
                    itemView.imgLike.setImageResource(R.drawable.ic_star_red_500_18dp)
                } else {
                    itemView.imgLike.setImageResource(R.drawable.ic_star_deep_purple_200_18dp)
                }
                if (seen) {
                    itemView.tvSeen.visibility = View.VISIBLE
                } else {
                    itemView.tvSeen.visibility = View.GONE
                }
            }
        }
    }
}
