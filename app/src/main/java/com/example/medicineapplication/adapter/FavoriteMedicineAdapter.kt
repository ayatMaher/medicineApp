package com.example.medicineapplication.adapter

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.R
import com.example.medicineapplication.databinding.MedicineFavoriteItemBinding
import com.example.medicineapplication.model.Medicine
import androidx.navigation.findNavController

class FavoriteMedicineAdapter(
    private var activity: Activity,
    var data: ArrayList<Medicine>,
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
        val item = data[position]
        holder.binding.medicineImage.setImageResource(item.medicineImage)
        holder.binding.txtMedicineName.text = item.medicineName
        holder.binding.txtMedicinePrice.text = item.price.toString()
        holder.binding.btnPharmacy.setOnClickListener {
            // go to Pharmacy Page
            val bundle = Bundle().apply {
                putString("medicine Name", item.medicineName)
                putString("page_type", "favorite")
            }
            holder.itemView.findNavController()
                .navigate(R.id.navigation_pharmacies, bundle)
        }
        holder.binding.deleteFavoriteMedicine.setOnClickListener {
            //delete medicine from image
            Log.e("Delete Medicine", "Medicine Delete")
        }
        holder.binding.root.setOnClickListener {
            try {
                itemClickListener.onItemClickMedicine(position, item.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}