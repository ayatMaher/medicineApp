package com.example.medicineapplication.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicineapplication.R
import com.example.medicineapplication.databinding.CategoryItemsBinding
import com.example.medicineapplication.databinding.MedicineTypeBinding
import com.example.medicineapplication.model.Category
import com.example.medicineapplication.model.MedicineType

class CategoryAdapter(
    private var activity: Activity,
    var data: ArrayList<Category>,
    private var itemClickListener: ItemClickListener,
    private val selectedName: String? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val backgroundColor = listOf(
        R.color.light_blue,
        R.color.yellow_green,
        R.color.dark_orange,
        R.color.light_yellow,
        R.color.purple,
        R.color.orange,
        R.color.green,
        R.color.gray
    )

    private var selectedPosition = -1

    init {
        selectedName?.let { name ->
            val initialIndex = data.indexOfFirst { it.name == name }
            if (initialIndex != -1) {
                selectedPosition = initialIndex
            }
        }
    }

    companion object {
        private const val TYPE_HOME = 0
        private const val TYPE_CATEGORY = 1
    }

    class ViewHolderCategoryHome(val binding: MedicineTypeBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderCategoryActivity(val binding: CategoryItemsBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClick(position: Int, id: String)
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].isFeatured==true) TYPE_CATEGORY else TYPE_HOME
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HOME) {
            val binding = MedicineTypeBinding.inflate(activity.layoutInflater, parent, false)
            ViewHolderCategoryHome(binding)
        } else {
            val binding = CategoryItemsBinding.inflate(activity.layoutInflater, parent, false)
            ViewHolderCategoryActivity(binding)
        }
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = data[position]

        when (holder) {
            is ViewHolderCategoryHome -> {
                holder.binding.txtName.text = item.name
                Glide.with(activity)
                    .load(item.image)
                    .into(holder.binding.imgType)
                holder.binding.root.setOnClickListener {
                    try {
                        itemClickListener.onItemClick(position, item.id.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


                val colorResId = backgroundColor[position % backgroundColor.size]
                holder.binding.medicineLayout.setBackgroundColor(activity.getColor(colorResId))

            }

            is ViewHolderCategoryActivity -> {

                holder.binding.txtCategory.text = item.name

                val isSelected = position == selectedPosition
                if (isSelected) {
                    holder.binding.categoryItem.background =
                        ContextCompat.getDrawable(activity, R.drawable.category_click)
                    holder.binding.txtCategory.setTextColor(Color.WHITE)
                } else {
                    holder.binding.categoryItem.background =
                        ContextCompat.getDrawable(activity, R.drawable.category)
                    holder.binding.txtCategory.setTextColor(Color.BLACK)
                }

                holder.binding.root.setOnClickListener {
                    try {
                        val previousPosition = selectedPosition
                        selectedPosition = position
                        notifyItemChanged(previousPosition)
                        notifyItemChanged(selectedPosition)
                        itemClickListener.onItemClick(position, item.id.toString())
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