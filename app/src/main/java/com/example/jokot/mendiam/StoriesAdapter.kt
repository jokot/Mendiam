package com.example.jokot.mendiam

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jokot.mendiam.model.Story
import kotlinx.android.synthetic.main.item_view_sotries.view.*

class StoriesAdapter(
    private var keterangan: String,
    private var listStory: MutableList<Story>,
    private var clickListener:(Story) -> Unit,
    private var clickEdit: (Story) -> Unit,
    private var clickDelete: (Story) -> Unit
) : RecyclerView.Adapter<StoriesAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(group.context).inflate(R.layout.item_view_sotries, group, false))
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItem(keterangan,
            listStory[position],
            clickListener,
            clickEdit,
            clickDelete)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(keterangan: String,
                     listStory: Story,
                     clickListener: (Story) -> Unit,
                     clickEdit: (Story) -> Unit,
                     clickDelete: (Story) -> Unit) {

            itemView.ll_more.visibility = View.GONE
            itemView.ll_for_gone.visibility = View.GONE

            itemView.iv_more.setOnClickListener{
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

            itemView.tv_keterangan.text = keterangan
            itemView.tv_judul.text = listStory.judul
            itemView.tv_deskripsi.text = listStory.deskripsi
            itemView.tv_date.text = listStory.date

            itemView.setOnClickListener{
                clickListener(listStory)
            }
        }
    }
}