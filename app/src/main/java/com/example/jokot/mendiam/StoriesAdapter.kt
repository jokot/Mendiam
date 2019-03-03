package com.example.jokot.mendiam

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jokot.mendiam.model.Story
import kotlinx.android.synthetic.main.item_view_sotries.view.*

class StoriesAdapter(
    private var keterangan: String,
    private var listStory: MutableList<Story>,
    private var clickListener:(Story) -> Unit
) : RecyclerView.Adapter<StoriesAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(group.context).inflate(R.layout.item_view_sotries, group, false))
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItem(keterangan, listStory[position],clickListener)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(keterangan: String, listStory: Story,clickListener: (Story) -> Unit) {
            itemView.tv_keterangan.text = keterangan
            itemView.tv_judul.text = listStory.judul

            itemView.setOnClickListener{
                clickListener(listStory)
            }
        }
    }
}