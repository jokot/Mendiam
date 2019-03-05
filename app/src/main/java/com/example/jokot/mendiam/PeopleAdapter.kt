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
    private val listFollowingId: MutableList<String>,
    private val clickListener: (User) -> Unit,
    private val clickUnFollow: (User) -> Unit,
    private val clickFollowing: (User) -> Unit
) : RecyclerView.Adapter<PeopleAdapter.ItemViewHolder>() {

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
            list[position],
            listFollowingId,
            clickListener,
            clickUnFollow,
            clickFollowing
        )
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(
            list: User,
            listUserFollowing: MutableList<String>,
            clickListener: (User) -> Unit,
            clickUnFollow: (User) -> Unit,
            clickFollowing: (User) -> Unit
        ) {
            for (uid in listUserFollowing) {
                if (uid == list.id) {
                    n = 1
                    itemView.btn_follow.setBackgroundResource(R.drawable.rectangle_btn_following)
                    itemView.btn_follow.setTextColor(Color.parseColor("#ffffff"))
                    itemView.btn_follow.text = "Following"
                    break
                } else {
                    itemView.btn_follow.setBackgroundResource(R.drawable.rectangle_btn_follow)
                    itemView.btn_follow.setTextColor(Color.parseColor("#04b595"))
                    itemView.btn_follow.text = "Follow"
                }
            }

            itemView.tv_nama_people.text = list.userName
            itemView.tv_about.text  = list.about


            itemView.btn_follow.setOnClickListener {

                if (n == 0) {
                    n++
                    itemView.btn_follow.setBackgroundResource(R.drawable.rectangle_btn_following)
                    itemView.btn_follow.setTextColor(Color.parseColor("#ffffff"))
                    itemView.btn_follow.text = "Following"
                    clickFollowing(list)
                } else {
                    n--
                    itemView.btn_follow.setBackgroundResource(R.drawable.rectangle_btn_follow)
                    itemView.btn_follow.setTextColor(Color.parseColor("#04b595"))
                    itemView.btn_follow.text = "Follow"
                    clickUnFollow(list)
                }

            }

            itemView.setOnClickListener {
                clickListener(list)
            }
        }
    }
}
