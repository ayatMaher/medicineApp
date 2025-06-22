package com.example.medicineapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicineapplication.Quation_item
import com.example.medicineapplication.R
import com.example.medicineapplication.model.quationItem

class QuationsAdapter(private val faqList: List<quationItem>) :
    RecyclerView.Adapter<QuationsAdapter.FaqViewHolder>() {

    inner class FaqViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
        val tvAnswer: TextView = itemView.findViewById(R.id.tvAnswer)
        val arrowIcon: ImageView = itemView.findViewById(R.id.ivArrow)
        val questionLayout: LinearLayout = itemView.findViewById(R.id.questionLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quation_item, parent, false)
        return FaqViewHolder(view)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val item = faqList[position]
        holder.tvQuestion.text = item.question
        holder.tvAnswer.text = item.answer

        // Expand/Collapse
        holder.tvAnswer.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
        holder.arrowIcon.rotation = if (item.isExpanded) 180f else 0f

        holder.questionLayout.setOnClickListener {
            item.isExpanded = !item.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = faqList.size
}
