package com.example.medicineapplication.adapter

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicineapplication.R
import com.example.medicineapplication.databinding.PharmacyItemBinding
import com.example.medicineapplication.databinding.PharmacySearchItemBinding
import com.example.medicineapplication.model.Pharmacy

class PharmacyAdapter(
    private var activity: Activity,
    var data: List<Pharmacy>,
    private var itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HOME = 0
        private const val TYPE_PHARMACY_SEARCH = 1
    }

    class ViewHolderPharmacyHome(val binding: PharmacyItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderPharmacySearch(val binding: PharmacySearchItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClickPharmacy(position: Int, id: String)
        fun onAddToFavorite(pharmacyId: Int)
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].isFeatured) TYPE_PHARMACY_SEARCH else TYPE_HOME
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HOME) {
            val binding = PharmacyItemBinding.inflate(activity.layoutInflater, parent, false)
            ViewHolderPharmacyHome(binding)
        } else {
            val binding = PharmacySearchItemBinding.inflate(activity.layoutInflater, parent, false)
            ViewHolderPharmacySearch(binding)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]

        when (holder) {
            is ViewHolderPharmacyHome -> {
                holder.binding.pharmacyName.text = item.name_pharmacy
                holder.binding.pharmacyAddress.text = item.address.formatted_address
                holder.binding.rate.text = item.average_rating.toString()

                Glide.with(activity)
                    .load(item.image_pharmacy)
                    .placeholder(R.drawable.new_background)
                    .into(holder.binding.pharmacyImg)


                if (item.is_favorite) {
                    holder.binding.favoriteImg.setImageResource(R.drawable.red_favorite)
                } else {
                    holder.binding.favoriteImg.setImageResource(R.drawable.favorite)
                }

                holder.binding.favoriteImg.setOnClickListener {
                    itemClickListener.onAddToFavorite(item.id)
                }


                holder.binding.root.setOnClickListener {
                    itemClickListener.onItemClickPharmacy(position, item.id.toString())
                }
            }

            is ViewHolderPharmacySearch -> {
                holder.binding.pharmacyName.text = item.name_pharmacy
                holder.binding.pharmacyAddress.text = item.address.formatted_address
                holder.binding.rate.text = item.average_rating.toString()
                holder.binding.distance.text = item.distance

                Glide.with(activity)
                    .load(item.image_pharmacy)
                    .placeholder(R.drawable.new_background)
                    .into(holder.binding.pharmacyImg)

                holder.binding.contactWhatsApp.setOnClickListener {
                    val phoneNumber = item.phone_number_pharmacy.replaceFirst("0", "972")
                    val message = "مرحبا، أود الاستفسار من فضلك."
                    val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    try {
                        activity.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(activity, "واتساب غير مثبت على الجهاز", Toast.LENGTH_SHORT).show()
                    }
                }

                holder.binding.root.setOnClickListener {
                    itemClickListener.onItemClickPharmacy(position, item.id.toString())
                }
            }
        }
    }

    fun updateData(newData: List<Pharmacy>) {
        data = newData
        notifyDataSetChanged()
    }
}
