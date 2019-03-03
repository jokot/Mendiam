package com.example.jokot.mendiam

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jokot.mendiam.model.User
import kotlinx.android.synthetic.main.item_view_people.view.*


class PeopleAdapter(
    private val list: MutableList<User>,
    private val clickListener: (User) -> Unit
//    , private val btnClickListener: (String, Int) -> Unit
) : RecyclerView.Adapter<PeopleAdapter.ItemViewHolder>() {

//    private val listPeople = mutableListOf("Joko Triyanto", "Sajangnim", "Kang Comel", "Bin Nasrul", "Jkt12348")

    private var n = 0

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): PeopleAdapter.ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(group.context).inflate(R.layout.item_view_people, group, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItem(
            list[position], clickListener
//            , btnClickListener
        )
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindItem(list: User, clickListener: (User) -> Unit) {

            itemView.tv_nama_people.text = list.userName

            itemView.btn_follow.setOnClickListener {

                if (n == 0) {
                    n++
                    itemView.btn_follow.setBackgroundResource(R.drawable.rectangle_btn_following)
                    itemView.btn_follow.setTextColor(Color.parseColor("#ffffff"))
                    itemView.btn_follow.text = "Following"
                } else {
                    n--
                    itemView.btn_follow.setBackgroundResource(R.drawable.rectangle_btn_follow)
                    itemView.btn_follow.setTextColor(Color.parseColor("#04b595"))
                    itemView.btn_follow.text = "Follow"
                }

            }

            itemView.setOnClickListener {
                clickListener(list)
            }
        }
    }
}
