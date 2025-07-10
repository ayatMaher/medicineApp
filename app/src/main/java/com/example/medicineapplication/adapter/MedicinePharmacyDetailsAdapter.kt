package com.example.medicineapplication.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicineapplication.R
import com.example.medicineapplication.databinding.MedicinePharmacyDetailsItemBinding
import com.example.medicineapplication.model.Medicine

class MedicinePharmacyDetailsAdapter(
    private var activity: Activity,
    private var data: ArrayList<Medicine>,
    private var itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<MedicinePharmacyDetailsAdapter.ViewHolder>() {

    class ViewHolder(val binding: MedicinePharmacyDetailsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClickMedicine(position: Int, id: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MedicinePharmacyDetailsItemBinding.inflate(activity.layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.binding.txtMedicineName.text = item.name
        holder.binding.txtMedicineDescription.text = item.description

        // ✅ تحميل صورة الدواء باستخدام Glide
        Glide.with(holder.binding.root)
            .load(item.image)
            .placeholder(R.drawable.medicine_details)
            .into(holder.binding.medicineImage)

        // ✅ عرض السعر وسعر الخصم إن وُجد
        val stock = item.pharmacy_stock.firstOrNull()
        if (stock != null) {
            holder.binding.txtPrice.text = "السعر: ${stock.price} ₪"
            holder.binding.txtPriceAfterDiscount.text = "بعد الخصم: ${stock.price_after_discount} ₪"
        } else {
            holder.binding.txtPrice.text = ""
            holder.binding.txtPriceAfterDiscount.text = ""
        }

        // ✅ عرض الأيقونة حسب حالة is_favorite
        if (item.is_favorite) {
            holder.binding.favoriteImg.setImageResource(R.drawable.red_favorite)
        } else {
            holder.binding.favoriteImg.setImageResource(R.drawable.favorite)
        }

        holder.binding.root.setOnClickListener {
            try {
                itemClickListener.onItemClickMedicine(position, item.id.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ✅ دالة لتحديث البيانات بعد البحث
    fun updateList(newList: List<Medicine>) {
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }
}
