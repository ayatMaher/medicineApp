package com.example.medicineapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicineapplication.R;
import com.example.medicineapplication.model.MedicineType;

import java.util.List;

public class MedicineTypeAdapter extends RecyclerView.Adapter<MedicineTypeAdapter.ViewHolder> {
    private final List<MedicineType> mData;
    private final LayoutInflater inflater;
    private final ItemClickListener itemClickListener;
    Context context;

    public MedicineTypeAdapter(Context context, List<MedicineType> data, ItemClickListener onClick) {
        this.inflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.itemClickListener = onClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.medicine_type, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MedicineTypeAdapter.ViewHolder holder, int position) {
        holder.txtTypeName.setText(mData.get(position).getNameType());
        holder.imgTypeName.setImageResource(mData.get(position).getImageType());
        holder.container.setOnClickListener(v -> {
                    try {
                        itemClickListener.onItemClick(holder.getAdapterPosition(),
                                mData.get(position).getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        switch (position) {
            case 0: {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
                break;
            }
            case 1: {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_green));
                break;
            }
            case 2: {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_orange));
                break;
            }
            case 3: {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.light_yellow));
                break;
            }
            case 4: {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.purple));
                break;
            }
            case 5: {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
                break;
            }
            case 6: {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                break;
            }
            case 7: {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTypeName;
        ImageView imgTypeName;
        LinearLayout container;

        ViewHolder(View itemView) {
            super(itemView);
            this.txtTypeName = itemView.findViewById(R.id.txtName);
            this.imgTypeName = itemView.findViewById(R.id.imgType);
            this.container = itemView.findViewById(R.id.container);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface ItemClickListener {
        void onItemClick(int position, String id);
    }
}
