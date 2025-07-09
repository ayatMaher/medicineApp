package com.example.medicineapplication.adapter

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicineapplication.R
import com.example.medicineapplication.databinding.MedicineFavoriteItemBinding
import androidx.navigation.findNavController
import com.example.medicineapplication.model.FavoriteTreatmentItem

class FavoriteMedicineAdapter(
    private var activity: Activity,
    var data: ArrayList<FavoriteTreatmentItem>,
    private var itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<FavoriteMedicineAdapter.ViewHolder>() {

    class ViewHolder(var binding: MedicineFavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClickMedicine(position: Int, id: String)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = MedicineFavoriteItemBinding.inflate(activity.layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position].treatment

        // اسم الدواء
        holder.binding.txtMedicineName.text = item.name

        // تحميل الصورة باستخدام Glide
        Glide.with(activity)
            .load(item.image)
            .placeholder(R.drawable.medicine_img)
            .into(holder.binding.medicineImage)

        // الذهاب إلى شاشة الصيدليات
        holder.binding.btnPharmacy.setOnClickListener {
            val bundle = Bundle().apply {
                putString("medicine_name", item.name)
                putString("page_type", "favorite")
            }
            holder.itemView.findNavController()
                .navigate(R.id.navigation_pharmacies, bundle)
        }

        // حذف من المفضلة (تنفيذ لاحقًا)
        holder.binding.deleteFavoriteMedicine.setOnClickListener {
            Log.e("Delete Medicine", "Medicine Delete")
            // يمكنك إضافة دالة onDelete هنا لاحقاً
        }

        // الضغط على العنصر نفسه
        holder.binding.root.setOnClickListener {
            try {
                itemClickListener.onItemClickMedicine(position, item.id.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int = data.size
}
