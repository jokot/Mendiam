package com.example.jokot.mendiam

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_view.view.*

class MainAdapter(private var clickListener: (String) -> Unit) : RecyclerView.Adapter<MainAdapter.ItemViewHolder>() {

    private val listStory = mutableListOf("introduction","apa ini","anak rumah tangga","lebih comel dari 24","jujur","msgsng","nuliso su")
    private var n = 0

    override fun onCreateViewHolder(group: ViewGroup, position: Int): MainAdapter.ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(group.context).inflate(R.layout.item_view,group,false))
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItem(listStory[position],clickListener)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var judul = view.findViewById<TextView>(R.id.tv_judul)
        var bookmark = view.findViewById<ImageView>(R.id.iv_bookmark)

        fun bindItem(listStory:String, clickListener: (String) -> Unit){
            judul.text = listStory


            itemView.iv_bookmark.setOnClickListener {
                if(n==0){
                    n++
                    itemView.iv_bookmark.setBackgroundResource(R.drawable.ic_bookmark_green_24dp)
                }else{
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