package com.example.medicineapplication.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicineapplication.R
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

        // تحميل الصورة من URL باستخدام Glide
        Glide.with(holder.itemView.context)
            .load(item.image_pharmacy)
            .placeholder(R.drawable.new_background)
            .into(holder.binding.pharmacyImg)

        holder.binding.txtPharmacyName.text = item.name_pharmacy
        holder.binding.txtPharmacyLocation.text = item.address.formatted_address
        holder.binding.PharmacyRate.text = item.average_rating.toString()

        // إظهار القلب الأحمر إن كانت الصيدلية مفضلة
        if (item.is_favorite) {
            holder.binding.favoriteImg.setImageResource(R.drawable.red_favorite)
        } else {
            holder.binding.favoriteImg.setImageResource(R.drawable.favorite)
        }

// يمكن لاحقاً استخدام هذا للضغط على الأيقونة وتغيير الحالة
        holder.binding.favoriteImg.setOnClickListener {
            // لا يتم تغيير الشكل فقط، بل يجب إرسال الطلب لإزالة من المفضلة (لاحقاً)
        }


        holder.binding.deleteFavoritePharmacy.setOnClickListener {
            // حذف الصيدلية من المفضلة
        }

        holder.binding.root.setOnClickListener {
            itemClickListener.onItemClickPharmacy(position, item.id.toString())
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }
}