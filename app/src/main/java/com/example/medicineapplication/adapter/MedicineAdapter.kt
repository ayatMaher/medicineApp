package com.example.medicineapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicineapplication.R;
import com.example.medicineapplication.model.Medicine;
import com.example.medicineapplication.model.Pharmacy;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {
    private final List<Medicine> mData;
    private final LayoutInflater inflater;
    private final MedicineAdapter.ItemClickListener itemClickListener;
    Context context;

    public MedicineAdapter(Context context, List<Medicine> data, MedicineAdapter.ItemClickListener onClick) {
        this.inflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.itemClickListener = onClick;
    }

    @NonNull
    @Override
    public MedicineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.medicine_item, parent, false);
        return new MedicineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineAdapter.ViewHolder holder, int position) {
        var medicineData = mData.get(position);
        Log.e("TAG", medicineData.getMedicineName());
        Log.e("TAG", medicineData.getDescription());
        Log.e("TAG", medicineData.getId());
        Log.e("TAG", medicineData.getPrice());
        Log.e("TAG", medicineData.getMedicineImage() + "");
        holder.txtMedicineName.setText(medicineData.getMedicineName());
        holder.txtMedicineName.setText(medicineData.getMedicineName());
        holder.medicineImg.setImageResource(medicineData.getMedicineImage());
        holder.txtPrice.setText(medicineData.getPrice());
        holder.txtMedicineDescription.setText(medicineData.getDescription());
        holder.container.setOnClickListener(v -> {
                    try {
                        itemClickListener.onItemClickMedicine(holder.getAdapterPosition(),
                                medicineData.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtMedicineName;
        ImageView medicineImg;
        TextView txtMedicineDescription;

        TextView txtPrice;
        CardView container;

        ViewHolder(View itemView) {
            super(itemView);
            this.txtMedicineName = itemView.findViewById(R.id.medicine_name);
            this.txtMedicineDescription = itemView.findViewById(R.id.medicine_description);
            this.txtPrice = itemView.findViewById(R.id.price);
            this.medicineImg = itemView.findViewById(R.id.medicine_img);
            this.container = itemView.findViewById(R.id.medicine_card);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

    }

    public interface ItemClickListener {
        void onItemClickMedicine(int position, String id);
    }

}
