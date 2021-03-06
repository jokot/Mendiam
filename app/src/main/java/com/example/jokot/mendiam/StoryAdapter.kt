package com.example.jokot.mendiam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jokot.mendiam.model.Story
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_view.view.*

class StoryAdapter(
    private var list: MutableList<Story>
    , private var listBookmark: MutableList<String>
    , private var clickBookmark: (Story) -> Unit
    , private var clickRemoveBookmark: (Story) -> Unit
    , private var clickListener: (Story) -> Unit
) : RecyclerView.Adapter<StoryAdapter.ItemViewHolder>() {
    private var n = false
    override fun onCreateViewHolder(group: ViewGroup, position: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(group.context).inflate(
                R.layout.item_view,
                group,
                false
            )
        )
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
        var tittle: TextView = view.findViewById(R.id.tv_tittle)
        var description: TextView = view.findViewById(R.id.tv_description)
        var date: TextView = view.findViewById(R.id.tv_date)
        var image: ImageView = view.findViewById(R.id.iv_itemView_story)
        var relativeLayout: RelativeLayout = view.findViewById(R.id.rl_image)
        var progress: ProgressBar = view.findViewById(R.id.pb_image)

        fun bindItem(
            listStory: Story
            , listBookmarkId: MutableList<String>
            , clickBookmark: (Story) -> Unit
            , clickRemoveBookmark: (Story) -> Unit
            , clickListener: (Story) -> Unit
        ) {

            for (id in listBookmarkId) {
                if (id == listStory.sid) {
                    n = true
                    itemView.iv_bookmark.setBackgroundResource(R.drawable.ic_bookmark_red_24dp)
                    break
                } else {
                    n = false
                    itemView.iv_bookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp)
                }
            }

            tittle.text = listStory.judul
            date.text = listStory.date
            description.text = listStory.deskripsi
            itemView.tv_name.text = listStory.name

            if (listStory.image != "") {
                image.visibility = View.VISIBLE
                relativeLayout.visibility = View.VISIBLE

                progress.visibility = View.VISIBLE
                Picasso.get()
                    .load(listStory.image)
//                    .fit()
//                    .placeholder(R.color.colorBlack)
                    .error(R.drawable.ic_broken_image_24dp).into(image, object : Callback {
                        override fun onSuccess() {
                            progress.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            progress.visibility = View.GONE
                        }
                    })
            } else {
//                image.visibility = View.GONE
                relativeLayout.visibility = View.GONE
                image.setImageResource(R.color.colorWhite)
            }
            itemView.iv_bookmark.setOnClickListener {
                if (!n) {
                    n = true
                    clickBookmark(listStory)
                    itemView.iv_bookmark.setBackgroundResource(R.drawable.ic_bookmark_red_24dp)
                } else {
                    n = false
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