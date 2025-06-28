package com.example.medicineapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.databinding.ActivityNotificationItemBinding
import com.example.medicineapplication.databinding.ItemSectionHeaderBinding
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val binding = ItemSectionHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            HeaderViewHolder(binding)
        } else {
            val binding = ActivityNotificationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            NotificationViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is NotificationListItem.SectionHeader -> (holder as HeaderViewHolder).bind(item)
            is NotificationListItem.NotificationItem -> (holder as NotificationViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size


    class HeaderViewHolder(private val binding: ItemSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationListItem.SectionHeader) {
            binding.tvSectionTitle.text = item.title
        }
    }


    class NotificationViewHolder(private val binding: ActivityNotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationListItem.NotificationItem) {
            binding.tvMessage.text = item.message
            binding.tvTime.text = item.time
            binding.imgMedicine.setImageResource(item.imageResId)
        }
    }
}
