package com.example.jokot.mendiam

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jokot.mendiam.model.Story
import kotlinx.android.synthetic.main.item_view_sotries.view.*

class StoriesAdapter(
    private var description: String,
    private var listStory: MutableList<Story>,
    private var clickListener: (Story) -> Unit,
    private var clickEdit: (Story) -> Unit,
    private var clickDelete: (Story) -> Unit
) : RecyclerView.Adapter<StoriesAdapter.ItemViewHolder>() {
    private val main = MainApps()

    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(group.context).inflate(
                R.layout.item_view_sotries,
                group,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItem(
            description,
            listStory[position],
            clickListener,
            clickEdit,
            clickDelete
        )
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(
            description: String,
            listStory: Story,
            clickListener: (Story) -> Unit,
            clickEdit: (Story) -> Unit,
            clickDelete: (Story) -> Unit
        ) {

            if (listStory.uid != main.getUId()) {
                Log.d("UID gone", "${listStory.uid} ? ${main.getUId()}")
                itemView.iv_more.visibility = View.GONE
            } else {
                Log.d("UID visible", "${listStory.uid} ? ${main.getUId()}")
                itemView.iv_more.visibility = View.VISIBLE
            }

            itemView.ll_more.visibility = View.GONE
            itemView.ll_for_gone.visibility = View.GONE

            itemView.iv_more.setOnClickListener {
                itemView.ll_more.visibility = View.VISIBLE
                itemView.ll_for_gone.visibility = View.VISIBLE
            }

            itemView.tv_edit.setOnClickListener {
                clickEdit(listStory)
            }

            itemView.tv_delete.setOnClickListener {
                itemView.ll_more.visibility = View.GONE
                itemView.ll_for_gone.visibility = View.GONE
                clickDelete(listStory)
            }

            itemView.ll_for_gone.setOnClickListener {
                itemView.ll_more.visibility = View.GONE
                itemView.ll_for_gone.visibility = View.GONE
            }

            itemView.tv_information.text = description
            itemView.tv_tittle.text = listStory.judul
            itemView.tv_description.text = listStory.deskripsi
            itemView.tv_date.text = listStory.date

            itemView.setOnClickListener {
                clickListener(listStory)
            }
        }
    }
}