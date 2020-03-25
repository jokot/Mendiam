package com.example.jokot.mendiam

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TopicAdapter(private val clickListener: (String) -> Unit) :
    RecyclerView.Adapter<TopicAdapter.ItemViewHolder>() {

    private val listTopic = mutableListOf("Book", "Coding", "Computer", "Comedy", "Productivity")
    private var n = 0

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(group.context).inflate(
                R.layout.item_view_topic,
                group,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listTopic.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItem(listTopic[position], clickListener)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var topic: TextView = view.findViewById(R.id.tv_topic)
        var follow: Button = view.findViewById(R.id.btn_follow)

        fun bindItem(list: String, clickListener: (String) -> Unit) {
            topic.text = list

            follow.setOnClickListener {
                if (n == 0) {
                    follow.text = itemView.context.getString(R.string.following)
                    follow.setBackgroundResource(R.drawable.rectangle_btn_following)
                    follow.setTextColor(Color.parseColor("#ffffff"))
                    n++
                } else {
                    follow.text = itemView.context.getString(R.string.follow)
                    follow.setBackgroundResource(R.drawable.rectangle_btn_follow)
                    follow.setTextColor(Color.parseColor("#AF0505"))
                    n--
                }
            }
            itemView.setOnClickListener {
                clickListener(list)

            }
        }
    }

}