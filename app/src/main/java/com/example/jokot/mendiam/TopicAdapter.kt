package com.example.jokot.mendiam

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class TopicAdapter(private val clickListener: (String) -> Unit) : RecyclerView.Adapter<TopicAdapter.ItemViewHolder>() {

    private val listTopic = mutableListOf("Buku", "Produktivitas", "Ngoding", "Komputer", "Komedi")
    private var n = 0

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): TopicAdapter.ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(group.context).inflate(R.layout.item_view_topic, group, false))
    }

    override fun getItemCount(): Int {
        return listTopic.size
    }

    override fun onBindViewHolder(holder: TopicAdapter.ItemViewHolder, position: Int) {
        holder.bindItem(listTopic[position], clickListener)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var topic = view.findViewById<TextView>(R.id.tv_topic)
        var follow = view.findViewById<Button>(R.id.btn_follow)

        fun bindItem(list: String, clickListener: (String) -> Unit) {
            topic.text = list

            follow.setOnClickListener {
                if (n == 0) {
                    follow.text = "Following"
                    follow.setBackgroundResource(R.drawable.rectangle_btn_following)
                    follow.setTextColor(Color.parseColor("#ffffff"))
                    n++
                } else {
                    follow.text = "Follow"
                    follow.setBackgroundResource(R.drawable.rectangle_btn_follow)
                    follow.setTextColor(Color.parseColor("#04b595"))
                    n--
                }
            }
            itemView.setOnClickListener {
                clickListener(list)

            }
        }
    }

}