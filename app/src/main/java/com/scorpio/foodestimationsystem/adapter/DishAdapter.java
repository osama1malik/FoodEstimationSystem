package com.scorpio.foodestimationsystem.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.fragments.receipesviews.DishesFragment;
import com.scorpio.foodestimationsystem.fragments.receipesviews.IngredientsFragment;
import com.scorpio.foodestimationsystem.model.Dishes;

import java.util.ArrayList;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.MyViewHolder> {

    private Context context;
    private final ArrayList<Dishes> list;
    private final DishClickListener listener;

    public DishAdapter(ArrayList<Dishes> list, DishClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dishes, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (position == list.size()) {
            holder.dishImage.setImageResource(R.drawable.item_add);
            holder.dishName.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(view -> listener.onDishClickListener(-1));
        } else {

            if (DishesFragment.selectedList.contains(list.get(position))) {
                holder.mainCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardSelected));
            } else {
                holder.mainCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }

            holder.dishImage.setImageResource(R.drawable.ic_placeholder);
            holder.dishName.setVisibility(View.VISIBLE);
            holder.dishName.setText(list.get(position).getName());
            holder.itemView.setOnClickListener(view -> listener.onDishClickListener(position));
            holder.itemView.setOnLongClickListener(view -> {
                listener.onDishLongClickListener(position);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImage;
        TextView dishName;
        MaterialCardView mainCard;

        public MyViewHolder(View itemView) {
            super(itemView);
            dishImage = itemView.findViewById(R.id.image_dish);
            dishName = itemView.findViewById(R.id.text_dish);
            mainCard = itemView.findViewById(R.id.main_card);

        }
    }

    public interface DishClickListener {
        void onDishClickListener(int position);

        void onDishLongClickListener(int position);
    }
}
