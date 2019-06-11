package com.example.jokot.mendiam

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.jokot.mendiam.model.Story
import kotlinx.android.synthetic.main.item_view.view.*
import kotlinx.android.synthetic.main.item_view_sotries.view.*

class StoryAdapter(
    private var list: MutableList<Story>
    , private var listBookmark: MutableList<String>
    , private var clickBookmark: (Story) -> Unit
    ,private var clickRemoveBookmark: (Story) -> Unit
    , private var clickListener: (Story) -> Unit
) : RecyclerView.Adapter<StoryAdapter.ItemViewHolder>() {
    private var n = 0
    override fun onCreateViewHolder(group: ViewGroup, position: Int): StoryAdapter.ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(group.context).inflate(R.layout.item_view, group, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItem(
            list[position],
            listBookmark,
            clickBookmark,
            clickRemoveBookmark,
            clickListener
        )
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var judul = view.findViewById<TextView>(R.id.tv_judul)
        var deskripsi = view.findViewById<TextView>(R.id.tv_deskripsi)
        var date = view.findViewById<TextView>(R.id.tv_date)

        fun bindItem(
            listStory: Story
            , listBookmarkId: MutableList<String>
            , clickBookmark: (Story) -> Unit
            , clickRemoveBookmark: (Story) -> Unit
            , clickListener: (Story) -> Unit
        ) {

            for (id in listBookmarkId) {
                if (id == listStory.sid) {
                    n =1
                    itemView.iv_bookmark.setBackgroundResource(R.drawable.ic_bookmark_green_24dp)
                    break
                } else {
                    n =0
                    itemView.iv_bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp)
                }
            }

            judul.text = listStory.judul
            date.text = listStory.date
            deskripsi.text = listStory.deskripsi
            itemView.tv_name.text = listStory.name

            itemView.iv_bookmark.setOnClickListener {
                if (n == 0) {
                    n++
                    clickBookmark(listStory)
                    itemView.iv_bookmark.setBackgroundResource(R.drawable.ic_bookmark_green_24dp)
                } else {
                    n--
                    clickRemoveBookmark(listStory)
                    itemView.iv_bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp)
                }

            }

            itemView.setOnClickListener {
                clickListener(listStory)
            }
        }
    }

}