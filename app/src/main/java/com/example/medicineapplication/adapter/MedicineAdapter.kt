package com.example.medicineapplication.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.R
import com.example.medicineapplication.databinding.CategoryMedicineItemBinding
import com.example.medicineapplication.databinding.MedicineItemBinding
import com.example.medicineapplication.model.Medicine

class MedicineAdapter(
    private var activity: Activity,
    var data: ArrayList<Medicine>,
    private var itemClickListener: ItemClickListener,

    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val backgroundColor = listOf(
        R.color.mid_green,
        R.color.light_beige,
        R.color.dark_beige,
        R.color.light_orange,
        R.color.purple2,
        R.color.dark_yellow_green
    )

    companion object {
        private const val TYPE_HOME = 0
        private const val TYPE_CATEGORY = 1
    }

    class ViewHolderHome(var binding: MedicineItemBinding) : RecyclerView.ViewHolder(binding.root)
    class ViewHolderCategory(var binding: CategoryMedicineItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClickMedicine(position: Int, id: String)
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].isFeatured) TYPE_CATEGORY else TYPE_HOME
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HOME) {
            val binding = MedicineItemBinding.inflate(activity.layoutInflater, parent, false)
            ViewHolderHome(binding)
        } else {
            val binding = CategoryMedicineItemBinding.inflate(activity.layoutInflater, parent, false)
            ViewHolderCategory(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when (holder) {
            is ViewHolderHome -> {
                holder.binding.medicineName.text = item.medicineName
                holder.binding.medicineImg.setImageResource(item.medicineImage)
                holder.binding.price.text = item.price.toString()
                holder.binding.medicineDescription.text = item.description
                holder.binding.root.setOnClickListener {
                    try {
                        itemClickListener.onItemClickMedicine(
                            position,
                            item.id
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            is ViewHolderCategory -> {
                val colorResId = backgroundColor[position % backgroundColor.size]
                holder.binding.txtMedicineName.text = item.medicineName
                holder.binding.categoryImage.setImageResource(item.medicineImage)
                holder.binding.oldPrice.text = item.price.toString()
                holder.binding.newPrice.text = item.priceAfterDiscount.toString()
                holder.binding.txtMedicineDescription.text = item.description
                holder.binding.medicineCard.setCardBackgroundColor(activity.getColor(colorResId))
                holder.binding.root.setOnClickListener {
                    try {
                        itemClickListener.onItemClickMedicine(
                            position,
                            item.id
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }
}