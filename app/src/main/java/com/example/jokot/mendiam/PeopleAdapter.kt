package com.example.jokot.mendiam

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.jokot.mendiam.model.User
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_view_people.view.*


class PeopleAdapter(
    private val list: MutableList<User>,
    private val listFollowingId: MutableList<String>,
    private val clickListener: (User) -> Unit,
    private val clickUnFollow: (User) -> Unit,
    private val clickFollowing: (User) -> Unit
) : RecyclerView.Adapter<PeopleAdapter.ItemViewHolder>() {

    private val main = MainApps()

    private var n = 0

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): ItemViewHolder {
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
        val progressBar: ProgressBar = view.findViewById(R.id.pb_item_people)

        fun bindItem(
            list: User,
            listUserFollowing: MutableList<String>,
            clickListener: (User) -> Unit,
            clickUnFollow: (User) -> Unit,
            clickFollowing: (User) -> Unit
        ) {
            if (list.id == main.getUId()) {
                itemView.btn_follow.visibility = View.GONE
            } else {
                itemView.btn_follow.visibility = View.VISIBLE
            }

            for (uid in listUserFollowing) {
                if (uid == list.id) {
                    n = 1
                    itemView.btn_follow.setBackgroundResource(R.drawable.rectangle_btn_following)
                    itemView.btn_follow.setTextColor(Color.parseColor("#ffffff"))
                    itemView.btn_follow.text = itemView.context.getString(R.string.following)
                    break
                } else {
                    itemView.btn_follow.setBackgroundResource(R.drawable.rectangle_btn_follow)
                    itemView.btn_follow.setTextColor(Color.parseColor("#AF0505"))
                    itemView.btn_follow.text = itemView.context.getString(R.string.follow)
                }
            }

            itemView.tv_name_people.text = list.userName
            itemView.tv_about.text = list.about


            itemView.btn_follow.setOnClickListener {

                if (n == 0) {
                    n++
                    itemView.btn_follow.setBackgroundResource(R.drawable.rectangle_btn_following)
                    itemView.btn_follow.setTextColor(Color.parseColor("#ffffff"))
                    itemView.btn_follow.text = itemView.context.getString(R.string.following)
                    clickFollowing(list)
                } else {
                    n--
                    itemView.btn_follow.setBackgroundResource(R.drawable.rectangle_btn_follow)
                    itemView.btn_follow.setTextColor(Color.parseColor("#AF0505"))
                    itemView.btn_follow.text = itemView.context.getString(R.string.follow)
                    clickUnFollow(list)
                }

            }
            if (list.urlPic != "") {
                Picasso
                    .get()
                    .load(list.urlPic)
                    .error(R.drawable.ic_broken_image_24dp)
                    .into(itemView.iv_user, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            progressBar.visibility = View.GONE
                        }

                    })
            } else {
                itemView.iv_user.setImageResource(R.drawable.ic_person_24dp)
                progressBar.visibility = View.GONE
            }

            itemView.setOnClickListener {
                clickListener(list)
            }
        }
    }
}
