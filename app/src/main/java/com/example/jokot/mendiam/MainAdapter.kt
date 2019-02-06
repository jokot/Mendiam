package com.example.jokot.mendiam

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MainAdapter() : RecyclerView.Adapter<MainAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(group: ViewGroup, position: Int): MainAdapter.ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(group.context).inflate(R.layout.item_view,group,false))
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view){

        fun bindItem(){

        }
    }

}