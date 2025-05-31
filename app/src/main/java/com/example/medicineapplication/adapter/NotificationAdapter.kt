package com.example.medicineapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.R
import com.example.medicineapplication.model.NotificationListItem

class NotificationAdapter(
    private val items: List<NotificationListItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NotificationListItem.SectionHeader -> TYPE_HEADER
            is NotificationListItem.NotificationItem -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_section_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_notification_item, parent, false)
            NotificationViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (val item = items[position]) {
            is NotificationListItem.SectionHeader -> (holder as HeaderViewHolder).bind(item)
            is NotificationListItem.NotificationItem -> (holder as NotificationViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_section_title)
        fun bind(item: NotificationListItem.SectionHeader) {
            title.text = item.title
        }
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val message: TextView = itemView.findViewById(R.id.tv_message)
        private val time: TextView = itemView.findViewById(R.id.tv_time)
        private val image: ImageView = itemView.findViewById(R.id.img_medicine)
        fun bind(item: NotificationListItem.NotificationItem) {
            message.text = item.message
            time.text = item.time
            image.setImageResource(item.imageResId)
        }
    }
}