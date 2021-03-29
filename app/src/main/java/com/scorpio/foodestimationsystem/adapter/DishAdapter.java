package com.scorpio.foodestimationsystem.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.modal.Dishes;

import java.util.ArrayList;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Dishes> list;

    public DishAdapter(ArrayList<Dishes> list) {
        this.list = list;
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
        } else {
            holder.dishName.setText(list.get(position).getDishName());
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImage;
        TextView dishName;

        public MyViewHolder(View itemView) {
            super(itemView);
            dishImage = itemView.findViewById(R.id.image_dish);
            dishName = itemView.findViewById(R.id.text_dish);
        }
    }
}
