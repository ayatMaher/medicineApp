package com.example.medicineapplication.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.databinding.PharmacyItemBinding
import com.example.medicineapplication.databinding.PharmacySearchItemBinding
import com.example.medicineapplication.model.Pharmacy

class PharmacyAdapter(
    private var activity: Activity,
    var data: ArrayList<Pharmacy>,
    private var itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_HOME = 0
        private const val TYPE_PHARMACY_SEARCH = 1
    }

    class ViewHolderPharmacyHome(var binding: PharmacyItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderPharmacySearch(var binding: PharmacySearchItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClickPharmacy(position: Int, id: String)
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

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = data[position]
        when (holder) {
            is ViewHolderPharmacyHome -> {
                holder.binding.pharmacyName.text = item.pharmacyName
                holder.binding.pharmacyImg.setImageResource(item.pharmacyImage)
                holder.binding.pharmacyAddress.text = item.pharmacyAddress
                holder.binding.rate.text = item.rate.toString()
                holder.binding.root.setOnClickListener {
                    try {
                        itemClickListener.onItemClickPharmacy(
                            position, item.id
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            is ViewHolderPharmacySearch -> {
                holder.binding.pharmacyName.text = item.pharmacyName
                holder.binding.pharmacyImg.setImageResource(item.pharmacyImage)
                holder.binding.pharmacyAddress.text = item.pharmacyAddress
                holder.binding.rate.text = item.rate.toString()
                holder.binding.time.text = item.time
                holder.binding.distance.text = item.distance.toString()
                holder.binding.root.setOnClickListener {
                    try {
                        itemClickListener.onItemClickPharmacy(
                            position, item.id
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}