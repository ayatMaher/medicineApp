package com.example.medicineapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicineapplication.R;
import com.example.medicineapplication.model.Pharmacy;

import java.util.List;

public class PharmacyHomeAdapter extends RecyclerView.Adapter<PharmacyHomeAdapter.ViewHolder> {
    private final List<Pharmacy> mData;
    private final LayoutInflater inflater;
    private final PharmacyHomeAdapter.ItemClickListener itemClickListener;
    Context context;

    public PharmacyHomeAdapter(Context context, List<Pharmacy> data, PharmacyHomeAdapter.ItemClickListener onClick) {
        this.inflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.itemClickListener = onClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.pharmacy_item, parent, false);
        return new PharmacyHomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyHomeAdapter.ViewHolder holder, int position) {
        holder.txtPharmacyName.setText(mData.get(position).getPharmacyName());
        holder.pharmacyImg.setImageResource(mData.get(position).getPharmacyImage());
        holder.txtRate.setText(mData.get(position).getRate());
        holder.txtPharmacyAddress.setText(mData.get(position).getPharmacyAddress());
        holder.container.setOnClickListener(v -> {
                    try {
                        itemClickListener.onItemClickPharmacy(holder.getAdapterPosition(),
                                mData.get(position).getId());
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
        TextView txtPharmacyName;
        ImageView pharmacyImg;
        TextView txtPharmacyAddress;

        TextView txtRate;
        CardView container;

        ViewHolder(View itemView) {
            super(itemView);
            this.txtPharmacyName = itemView.findViewById(R.id.pharmacy_name);
            this.txtPharmacyAddress = itemView.findViewById(R.id.pharmacy_address);
            this.txtRate = itemView.findViewById(R.id.rate);
            this.pharmacyImg = itemView.findViewById(R.id.pharmacy_img);
            this.container = itemView.findViewById(R.id.pharmacy_card);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

    }

    public interface ItemClickListener {
        void onItemClickPharmacy(int position, String id);
    }

}
