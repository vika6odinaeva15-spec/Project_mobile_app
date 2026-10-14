package com.socialnavigator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.socialnavigator.R;
import com.socialnavigator.models.Discount;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder> {

    // Список данных
    private final List<Discount> discounts;

    // Слушатель клика
    private final OnDiscountClickListener listener;

    // Интерфейс для обработки клика
    public interface OnDiscountClickListener {
        void onDiscountClick(Discount discount);
    }

    // Конструктор
    public DiscountAdapter(List<Discount> discounts, OnDiscountClickListener listener) {
        this.discounts = discounts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создаём View из layout item_discount.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discount, parent, false);
        return new DiscountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountViewHolder holder, int position) {
        // Берём скидку по позиции
        Discount discount = discounts.get(position);

        // Заполняем поля карточки
        holder.tvName.setText(discount.getName());
        holder.tvType.setText(discount.getType());
        holder.tvPromotion.setText(discount.getPromotional());
        holder.tvAddress.setText("📍 " + discount.getAddress());
        holder.tvTime.setText("🕐 " + discount.getTimeOfApplication());

        // Обработка клика
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

    // ViewHolder — хранит ссылки на View карточки
    public static class DiscountViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvType;
        TextView tvPromotion;
        TextView tvAddress;
        TextView tvTime;

        public DiscountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvType = itemView.findViewById(R.id.tvItemType);
            tvPromotion = itemView.findViewById(R.id.tvItemPromotion);
            tvAddress = itemView.findViewById(R.id.tvItemAddress);
            tvTime = itemView.findViewById(R.id.tvItemTime);
        }
    }
}
