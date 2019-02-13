package com.example.jokot.mendiam

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class PeopleAdapter():RecyclerView.Adapter<PeopleAdapter.ItemViewHolder>(){
    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): PeopleAdapter.ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(group.context).inflate(R.layout.item_view_people,group,false))
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    }

    inner class ItemViewHolder(view: View):RecyclerView.ViewHolder(view){
        private fun bindItem(){

        }
    }
}