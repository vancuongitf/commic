package com.example.cuongcaov.comicbook.main

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cuongcaov.comicbook.R
import kotlinx.android.synthetic.main.item_type_menu.view.*

/**
 * MenuAdapter.
 *
 * @author CuongCV
 */
class MenuAdapter(var listener: RecyclerViewOnItemClickListener) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val mTypes = MenuItem.getMenuList()
    private var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_type_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int = mTypes.size

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.onBind()
    }

    fun deleteSelected() {
        if (mSelectedPosition > -1) {
            mTypes[mSelectedPosition].isSelected = false
            notifyItemChanged(mSelectedPosition)
            mSelectedPosition = -1
        }
    }

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                if (mSelectedPosition > -1) {
                    mTypes[mSelectedPosition].isSelected = false
                    notifyItemChanged(mSelectedPosition)
                }
                mSelectedPosition = adapterPosition
                mTypes[adapterPosition].isSelected = true
                notifyItemChanged(adapterPosition)
                listener.onItemClick(mTypes[adapterPosition])
            }
        }

        fun onBind() {
            with(mTypes[adapterPosition]) {
                itemView.tvTypeName.isSelected = true
                itemView.tvTypeName.text = mTypes[adapterPosition].typeName
                if (isSelected) {
                    itemView.tvTypeName.setTextColor(Color.RED)
                } else {
                    itemView.tvTypeName.setTextColor(Color.WHITE)
                }
            }
        }
    }

    interface RecyclerViewOnItemClickListener {
        fun onItemClick(item: Any)

        fun onLikeAction(storyId: Long, liked: Boolean) {

        }
    }
}