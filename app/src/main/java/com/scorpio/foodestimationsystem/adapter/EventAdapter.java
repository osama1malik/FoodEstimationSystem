package com.scorpio.foodestimationsystem.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private Context context;
    private final ArrayList<Events> list;
    private final EventAdapter.EventClickListener listener;

    public EventAdapter(ArrayList<Events> list, EventAdapter.EventClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_events, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.eventName.setVisibility(View.VISIBLE);
        holder.eventDate.setVisibility(View.VISIBLE);
        holder.eventName.setText(list.get(position).getName());
        holder.eventDate.setText(getFormattedDate(list.get(position).getDate()));

        holder.itemView.setOnClickListener(v -> {
            listener.onEventClickListener(position);
        });
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
        TextView eventName;
        TextView eventDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
        }
    }

    public interface EventClickListener {
        void onEventClickListener(int position);
    }
}