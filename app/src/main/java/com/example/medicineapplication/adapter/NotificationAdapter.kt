package com.example.medicineapplication.adapter

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.databinding.ActivityNotificationItemBinding
import com.example.medicineapplication.model.NotificationItem
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotificationAdapter(
    private val items: List<NotificationItem>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ActivityNotificationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class NotificationViewHolder(private val binding: ActivityNotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationItem) {
            binding.tvMessage.text = item.body
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")

                val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val date = inputFormat.parse(item.date)
                binding.tvTime.text = date?.let { outputFormat.format(it) } ?: item.date
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvTime.text = item.date
            }
            binding.tvTitle.text = item.title
            // إذا كان في صورة ممكن تضيفها هنا
            // binding.imgMedicine.setImageResource(...)
        }
    }
}
