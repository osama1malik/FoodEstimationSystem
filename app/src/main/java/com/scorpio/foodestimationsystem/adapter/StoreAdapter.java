package com.scorpio.foodestimationsystem.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Store;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {

    private Context context;
    private final ArrayList<Store> list;
    private final StoreItemClickListener listener;

    public StoreAdapter(ArrayList<Store> list, StoreItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        context = parent.getContext();
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Store currentItem = list.get(position);

        holder.name.setText(currentItem.getName());
        holder.availableQuantity.setText(Integer.toString(currentItem.getQuantityAvailable()));
        holder.requiredQuantity.setText(Integer.toString(currentItem.getQuantityRequired()));

        if (currentItem.getQuantityRequired() > currentItem.getQuantityAvailable()) {
            holder.requiredQuantity.setTextColor(context.getColor(R.color.colorPrimaryDark));
        } else {
            holder.requiredQuantity.setTextColor(context.getColor(R.color.black));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, availableQuantity, requiredQuantity;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.ingredient_name);
            availableQuantity = itemView.findViewById(R.id.ingredient_quantity_total);
            requiredQuantity = itemView.findViewById(R.id.ingredient_quantity_to_be_used);
        }
    }

    public interface StoreItemClickListener {
        void onStoreItemClickListener(int position);
    }
}
