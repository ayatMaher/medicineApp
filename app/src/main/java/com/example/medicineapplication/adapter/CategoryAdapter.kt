package com.example.medicineapplication.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.R
import com.example.medicineapplication.databinding.CategoryItemsBinding
import com.example.medicineapplication.databinding.MedicineTypeBinding
import com.example.medicineapplication.model.MedicineType

class CategoryAdapter(
    private var activity: Activity,
    var data: ArrayList<MedicineType>,
    private var itemClickListener: ItemClickListener,
    private val selectedName: String? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition = -1

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
        return if (data[position].isFeatured) TYPE_CATEGORY else TYPE_HOME
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
                holder.binding.txtName.text = item.nameType
                holder.binding.imgType.setImageResource(item.imageType)
                holder.binding.root.setOnClickListener {
                    try {
                        itemClickListener.onItemClick(position, item.id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                when (position) {
                    0 -> {
                        holder.binding.medicineLayout.setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.light_blue
                            )
                        )
                    }

                    1 -> {
                        holder.binding.medicineLayout.setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.yellow_green
                            )
                        )
                    }

                    2 -> {
                        holder.binding.medicineLayout.setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.dark_orange
                            )
                        )
                    }

                    3 -> {
                        holder.binding.medicineLayout.setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.light_yellow
                            )
                        )
                    }

                    4 -> {
                        holder.binding.medicineLayout.setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.purple
                            )
                        )
                    }

                    5 -> {
                        holder.binding.medicineLayout.setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.orange
                            )
                        )
                    }

                    6 -> {
                        holder.binding.medicineLayout.setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.green
                            )
                        )
                    }

                    7 -> {
                        holder.binding.medicineLayout.setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.gray
                            )
                        )
                    }
                }
            }

            is ViewHolderCategoryActivity -> {
                // تمييز العنصر إذا تطابق الاسم
                val isSelected = item.nameType == selectedName
                holder.binding.root.isSelected = isSelected
                if ( isSelected) {
                    holder.binding.categoryItem.background =
                        ContextCompat.getDrawable(activity, R.drawable.category_click)
                    holder.binding.txtCategory.setTextColor(Color.WHITE)
                } else {
                    holder.binding.categoryItem.background =
                        ContextCompat.getDrawable(activity, R.drawable.category)
                    holder.binding.txtCategory.setTextColor(Color.BLACK)
                }
                holder.binding.txtCategory.text = item.nameType
                holder.binding.root.setOnClickListener {
                    try {
                        val previousPosition = selectedPosition
                        selectedPosition = position
                        notifyItemChanged(previousPosition)
                        notifyItemChanged(selectedPosition)
                        itemClickListener.onItemClick(position, item.id)
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