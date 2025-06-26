package com.example.medicineapplication.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicineapplication.databinding.RateItemBinding
import com.example.medicineapplication.model.User

class RatingAdapter(
    private var activity: Activity,
    var data: ArrayList<User>,
    private var itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<RatingAdapter.ViewHolder>() {
    class ViewHolder(var binding: RateItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClick(position: Int, id: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RateItemBinding.inflate(activity.layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.binding.txtUserName.text = item.name
//        holder.binding.userImage.setImageResource(item.image)
        Glide.with(holder.binding.root)
            .load(item.image)
            .into(holder.binding.userImage)
        holder.binding.txtRateTime.text = item.rateDate
        holder.binding.txtComment.text=item.userComment
        holder.binding.root.setOnClickListener {
            try {
                itemClickListener.onItemClick(position, item.id.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}