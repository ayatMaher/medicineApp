package com.example.medicineapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.databinding.QuationItemBinding
import com.example.medicineapplication.model.quationItem

class QuationsAdapter(private val faqList: List<quationItem>) :
    RecyclerView.Adapter<QuationsAdapter.FaqViewHolder>() {

    inner class FaqViewHolder(val binding: QuationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val binding = QuationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val item = faqList[position]
        with(holder.binding) {
            tvQuestion.text = item.question
            tvAnswer.text = item.answer

            tvAnswer.visibility = if (item.isExpanded) android.view.View.VISIBLE else android.view.View.GONE
            ivArrow.rotation = if (item.isExpanded) 180f else 0f

            questionLayout.setOnClickListener {
                item.isExpanded = !item.isExpanded
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = faqList.size
}
