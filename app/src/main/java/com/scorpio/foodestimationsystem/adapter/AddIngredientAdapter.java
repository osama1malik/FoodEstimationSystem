package com.scorpio.foodestimationsystem.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.fragments.receipesviews.AddDishFragment;
import com.scorpio.foodestimationsystem.fragments.receipesviews.IngredientsFragment;
import com.scorpio.foodestimationsystem.model.Ingredients;

import java.util.ArrayList;

public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Ingredients> list;
    private IngredientClickListener listener;

    public AddIngredientAdapter(ArrayList<Ingredients> list, IngredientClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_ingredient, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (IngredientsFragment.selectedList.contains(list.get(position))) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.cardSelected));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.name.setText(list.get(position).getName());

        try {
            holder.quantity.setText(AddDishFragment.quantityList.get(position) + "");
        } catch (Exception e) {
            Log.i("TAG", "onBindViewHolder: ", e);
            holder.quantity.setText("1");
        }

        int cost = (Integer.parseInt(holder.quantity.getText().toString())) * list.get(position).getPrice();
        holder.cost.setText("Rs " + cost + " /-");

        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!holder.quantity.getText().toString().isEmpty()) {
                    int quantity = Integer.parseInt(holder.quantity.getText().toString());
                    long cost = quantity * list.get(position).getPrice();
                    holder.cost.setText("Rs " + cost + " /-");
                    listener.onCostChangeListener(position, quantity);
                } else {
                    holder.cost.setText("Rs 0 /-");
                    listener.onCostChangeListener(position, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        holder.itemView.setOnLongClickListener(v -> {
            listener.onIngredientLongListener(position);
            return true;
        });
        holder.itemView.setOnClickListener(v -> listener.onIngredientClickListener(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, cost;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ing_name);
            quantity = itemView.findViewById(R.id.ing_quantity);
            cost = itemView.findViewById(R.id.ing_cost);
        }
    }

    public interface IngredientClickListener {
        void onIngredientClickListener(int position);

        void onIngredientLongListener(int position);

        void onCostChangeListener(int position, int quantity);
    }
}
