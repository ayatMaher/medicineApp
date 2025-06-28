package com.example.medicineapplication.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.R
import com.example.medicineapplication.adapter.FavoriteMedicineAdapter.ItemClickListener
import com.example.medicineapplication.databinding.PharmacyFavoriteItemBinding
import com.example.medicineapplication.model.Pharmacy

class FavoritePharmacyAdapter(
    private var activity: Activity,
    var data: ArrayList<Pharmacy>,
    private var itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<FavoritePharmacyAdapter.ViewHolder>() {
    class ViewHolder(var binding: PharmacyFavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClickPharmacy(position: Int, id: String)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = PharmacyFavoriteItemBinding.inflate(activity.layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.binding.pharmacyImg.setImageResource(item.pharmacyImage)
        holder.binding.txtPharmacyName.text = item.pharmacyName
        holder.binding.txtPharmacyLocation.text = item.pharmacyAddress
        holder.binding.PharmacyRate.text = item.rate.toString()
        holder.binding.favoriteImg.setOnClickListener {
            holder.binding.favoriteImg.setImageResource(R.drawable.favorite)
        }
        holder.binding.deleteFavoritePharmacy.setOnClickListener {
            //delete pharmacy from favorite
        }
        holder.binding.root.setOnClickListener {
            itemClickListener.onItemClickPharmacy(position, item.id)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}