package com.example.jokot.mendiam

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class TopicAdapter():RecyclerView.Adapter<TopicAdapter.ItemViewHolder>(){
    override fun onCreateViewHolder(group: ViewGroup, viewType: Int):TopicAdapter.ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(group.context).inflate(R.layout.item_view_topic,group,false))
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: TopicAdapter.ItemViewHolder, p1: Int) {

    }

    inner class ItemViewHolder(view: View):RecyclerView.ViewHolder(view){
        private fun bindItem(){

        }
    }

}