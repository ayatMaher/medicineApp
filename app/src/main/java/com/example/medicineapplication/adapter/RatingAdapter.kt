import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicineapplication.R
import com.example.medicineapplication.databinding.RateItemBinding
import com.example.medicineapplication.model.Rating

class RatingAdapter(
    private val activity: Activity,
    private val data: List<Rating>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<RatingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RateItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClick(position: Int, id: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rating = data[position]
        val user = rating.user

        holder.binding.txtUserName.text = user?.name ?: "مستخدم"

        Glide.with(holder.binding.root)
            .load(user?.image)
            .placeholder(R.drawable.user) // صورة أثناء التحميل
            .error(R.drawable.user)       // صورة افتراضية إذا كانت null أو فشل التحميل
            .into(holder.binding.userImage)

        holder.binding.txtRateTime.text = rating.created_at.take(10) // "YYYY-MM-DD"
        holder.binding.txtComment.text = rating.comment ?: ""
        holder.binding.txtRate.text = rating.rating


        // تحويل التقييم من String إلى Float
        val ratingFloat = rating.rating.toFloatOrNull() ?: 0f
        setStars(holder, ratingFloat)

        holder.binding.root.setOnClickListener {
            try {
                itemClickListener.onItemClick(position, rating.id.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //  هنا دالة تعيين النجوم حسب قيمة التقييم
    private fun setStars(holder: ViewHolder, ratingValue: Float) {
        val stars = listOf(
            holder.binding.star1,
            holder.binding.star2,
            holder.binding.star3,
            holder.binding.star4,
            holder.binding.star5
        )

        for (i in stars.indices) {
            if (i < ratingValue) {
                stars[i].setImageResource(com.example.medicineapplication.R.drawable.fill_star)
            } else {
                stars[i].setImageResource(com.example.medicineapplication.R.drawable.empty_start)
            }
        }
    }
}
