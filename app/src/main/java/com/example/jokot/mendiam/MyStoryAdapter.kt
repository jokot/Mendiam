package com.example.jokot.mendiam

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jokot.mendiam.model.Story
import kotlinx.android.synthetic.main.item_view.view.*

class MyStoryAdapter(
    private var listStory : MutableList<Story>
):RecyclerView.Adapter<MyStoryAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(group.context).inflate(R.layout.item_view,group,false))
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItem(listStory[position])
    }

    inner class ItemViewHolder(view : View):RecyclerView.ViewHolder(view) {
        fun bindItem(listStory: Story){
            itemView.tv_name.text = listStory.name
            itemView.tv_judul.text = listStory.judul
        }
    }
}