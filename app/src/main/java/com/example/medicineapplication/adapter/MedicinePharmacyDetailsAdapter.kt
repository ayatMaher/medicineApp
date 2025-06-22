package com.example.medicineapplication.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.R
import com.example.medicineapplication.databinding.MedicinePharmacyDetailsItemBinding
import com.example.medicineapplication.model.Medicine

class MedicinePharmacyDetailsAdapter(
    private var activity: Activity,
    var data: ArrayList<Medicine>,
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


    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.binding.txtMedicineName.text = item.medicineName
        holder.binding.medicineImage.setImageResource(item.medicineImage)
        holder.binding.txtMedicineDescription.text = item.description
        holder.binding.txtPrice.text = item.price.toString()
        holder.binding.txtPriceAfterDiscount.text = item.priceAfterDiscount.toString()
        holder.binding.favoriteImg.setOnClickListener {
            holder.binding.favoriteImg.setImageResource(R.drawable.red_favorite)
        }
        holder.binding.root.setOnClickListener {
            try {
                itemClickListener.onItemClickMedicine(position, item.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}