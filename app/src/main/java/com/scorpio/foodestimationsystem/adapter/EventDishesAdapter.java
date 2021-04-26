package com.scorpio.foodestimationsystem.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.model.Dishes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EventDishesAdapter extends RecyclerView.Adapter<EventDishesAdapter.MyViewHolder> {
    private Context context;
    private final ArrayList<Dishes> list;

    public EventDishesAdapter(ArrayList<Dishes> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_dishes, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.eventDishName.setText(list.get(position).getName().toString());
        holder.eventDishCost.setText("Rs" + list.get(position).getPrice() + "/-");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String getFormattedDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        return DateFormat.format("dd-MMM-yyyy", cal).toString();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView eventDishName;
        TextView eventDishQuantity;
        TextView eventDishCost;

        public MyViewHolder(View itemView) {
            super(itemView);
            eventDishName = itemView.findViewById(R.id.tv_dish_name);
            eventDishCost = itemView.findViewById(R.id.tv_dish_cost);
        }
    }
}