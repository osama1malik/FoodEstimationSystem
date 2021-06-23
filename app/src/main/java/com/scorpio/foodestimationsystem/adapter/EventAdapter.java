package com.scorpio.foodestimationsystem.adapter;

import android.content.Context;
import android.text.format.DateFormat;
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
import com.scorpio.foodestimationsystem.fragments.EventsFragment;
import com.scorpio.foodestimationsystem.fragments.receipesviews.IngredientsFragment;
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

        if (EventsFragment.selectedList.contains(list.get(position))) {
            holder.mainCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardSelected));
        } else {
            holder.mainCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.eventName.setVisibility(View.VISIBLE);
        holder.eventDate.setVisibility(View.VISIBLE);
        holder.eventName.setText(list.get(position).getName());

        String date = getFormattedDate(list.get(position).getDate());

        holder.eventDate.setText(date);

        holder.itemView.setOnClickListener(v -> listener.onEventClickListener(position));

        holder.itemView.setOnLongClickListener(v -> {
            listener.onEventLongClickListener(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String getFormattedDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        return DateFormat.format("hh:mm a dd MMM,yy", cal).toString();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView eventDate;
        MaterialCardView mainCard;

        public MyViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
            mainCard = itemView.findViewById(R.id.main_card);
        }
    }

    public interface EventClickListener {
        void onEventClickListener(int position);

        void onEventLongClickListener(int position);
    }
}