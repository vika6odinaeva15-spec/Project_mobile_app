package com.socialnavigator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import com.socialnavigator.utils.TypeImageMapper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.socialnavigator.R;
import com.socialnavigator.models.Discount;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder> {

    private final List<Discount> discounts;
    private final OnDiscountClickListener listener;

    public interface OnDiscountClickListener {
        void onDiscountClick(Discount discount);
    }

    public DiscountAdapter(List<Discount> discounts, OnDiscountClickListener listener) {
        this.discounts = discounts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discount, parent, false);
        return new DiscountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountViewHolder holder, int position) {
        Discount discount = discounts.get(position);

        holder.tvName.setText(discount.getName());
        holder.tvType.setText(discount.getType());
        holder.tvPromotion.setText(discount.getPromotional());
        holder.tvAddress.setText("📍 " + discount.getAddress());
        holder.tvTime.setText("🕐 " + discount.getTimeOfApplication());

        // Картинка по типу заведения
        holder.ivImage.setImageResource(
                TypeImageMapper.getImageResByType(discount.getType())
        );

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDiscountClick(discount);
            }
        });
    }

    @Override
    public int getItemCount() {
        return discounts.size();
    }

    public static class DiscountViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvType;
        TextView tvPromotion;
        TextView tvAddress;
        TextView tvTime;
        ImageView ivImage;

        public DiscountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvType = itemView.findViewById(R.id.tvItemType);
            tvPromotion = itemView.findViewById(R.id.tvItemPromotion);
            tvAddress = itemView.findViewById(R.id.tvItemAddress);
            tvTime = itemView.findViewById(R.id.tvItemTime);
            ivImage = itemView.findViewById(R.id.ivItemImage);  // ← ключевая строка
        }
    }
}