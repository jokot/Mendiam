package com.example.jokot.mendiam

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.jokot.mendiam.model.Story
import kotlinx.android.synthetic.main.item_view.view.*

class MainAdapter(
    private var list: MutableList<Story>
    , private var clickListener: (Story) -> Unit
) : RecyclerView.Adapter<MainAdapter.ItemViewHolder>() {
    private var n = 0
    override fun onCreateViewHolder(group: ViewGroup, position: Int): MainAdapter.ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(group.context).inflate(R.layout.item_view, group, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItem(list[position], clickListener)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var judul = view.findViewById<TextView>(R.id.tv_judul)
        var deskripsi = view.findViewById<TextView>(R.id.tv_deskripsi)

        fun bindItem(listStory: Story, clickListener: (Story) -> Unit) {
            judul.text = listStory.judul
            deskripsi.text = listStory.deskripsi
            itemView.tv_name.text = listStory.name

            itemView.iv_bookmark.setOnClickListener {
                if (n == 0) {
                    n++
                    itemView.iv_bookmark.setBackgroundResource(R.drawable.ic_bookmark_green_24dp)
                } else {
                    n--
                    itemView.iv_bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp)
                }

            }

            itemView.setOnClickListener {
                clickListener(listStory)
            }
        }
    }

}