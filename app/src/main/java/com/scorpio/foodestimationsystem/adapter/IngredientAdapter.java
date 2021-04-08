package com.scorpio.foodestimationsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Ingredients;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Ingredients> list;
    private IngredientAdapter.IngredientClickListener listener;

    public IngredientAdapter(ArrayList<Ingredients> list, IngredientAdapter.IngredientClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredients, parent, false);
        return new IngredientAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientAdapter.MyViewHolder holder, int position) {

        if (position == list.size()) {
            holder.dishImage.setImageResource(R.drawable.item_add);
            holder.name.setVisibility(View.GONE);
            holder.info.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(view -> listener.onIngredientClickListener(-1));
        } else {
            holder.dishImage.setImageResource(R.drawable.ic_placeholder);
            holder.name.setVisibility(View.VISIBLE);
            holder.info.setVisibility(View.VISIBLE);
            holder.name.setText(list.get(position).getName());
            holder.info.setText(list.get(position).getPrice() + "/" + list.get(position).getUnit());
        }

    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImage;
        TextView name, info;

        public MyViewHolder(View itemView) {
            super(itemView);
            dishImage = itemView.findViewById(R.id.image_dish);
            name = itemView.findViewById(R.id.text_ingredient);
            info = itemView.findViewById(R.id.text_info);
        }
    }

    public interface IngredientClickListener {
        public void onIngredientClickListener(int position);
    }
}
