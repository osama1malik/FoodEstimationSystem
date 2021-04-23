package com.scorpio.foodestimationsystem.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.adapter.DishesExpensesAdapter;
import com.scorpio.foodestimationsystem.databinding.DialogAddDishesBinding;
import com.scorpio.foodestimationsystem.databinding.FragmentDiscountBinding;
import com.scorpio.foodestimationsystem.databinding.FragmentExpensesBinding;
import com.scorpio.foodestimationsystem.model.Dishes;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DiscountFragment extends Fragment implements DishesExpensesAdapter.IngredientClickListener {

    private FragmentDiscountBinding binding;
    private Dishes currentDish;
    private DishesExpensesAdapter dishAdapter;
    private ArrayList<Dishes> list = new ArrayList<>();
    public static ArrayList<Integer> quantityList = new ArrayList<>();
    private double totalCost = 0;
    private double discountPercent = 0.0;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDiscountBinding.inflate(getLayoutInflater());
        init();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListeners();
    }

    private void init() {
        ((MainActivity) requireActivity()).binding.appbar.btnDone.setVisibility(View.INVISIBLE);
        populateDishesRV();
    }

    private void initListeners() {
        binding.addDishFab.setOnClickListener(v -> {
            showAddDishDialog();
        });

        binding.discountPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!binding.discountPercent.getText().toString().isEmpty()) {
                    double num = Double.parseDouble(binding.discountPercent.getText().toString());

                    if (num >= 0 && num <= 100) {
                        discountPercent = num;
                        calculateCost();
                    } else {
                        binding.discountPercent.setError("Input should be between 0 - 100");
                        binding.discountPercent.requestFocus();
                    }

                } else {
                    discountPercent = 0;
                    calculateCost();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showAddDishDialog() {
        Dialog dialog = new Dialog(requireContext());
        DialogAddDishesBinding dBinding = DialogAddDishesBinding.inflate(getLayoutInflater());
        dialog.setContentView(dBinding.getRoot());

        dBinding.heading.setText("Select Dishes");
        dBinding.edDishName.setVisibility(View.GONE);
        dBinding.edDishPrice.setVisibility(View.GONE);

        final ArrayList<Dishes> selected = new ArrayList<>();
        ArrayList<KeyPairBoolData> data = new ArrayList<>();
        for (Dishes dish : MainActivity.dishesList) {
            KeyPairBoolData data1 = new KeyPairBoolData(dish.getName(), false);
            data1.setObject(dish);
            data.add(data1);
        }

        dBinding.selectIngredients.setHintText("Dishes");
        dBinding.selectIngredients.setSearchEnabled(true);
        dBinding.selectIngredients.setSearchHint("Search Dish");
        dBinding.selectIngredients.setEmptyTitle("Not Dish Found!");
        dBinding.selectIngredients.setShowSelectAllButton(true);
        dBinding.selectIngredients.setClearText("Close & Clear");
        dBinding.selectIngredients.setItems(data, items -> {
            for (KeyPairBoolData item : items) {
                selected.add(((Dishes) item.getObject()));
            }
        });

        dBinding.btnCancel.setOnClickListener(view -> dialog.dismiss());
        dBinding.btnSave.setOnClickListener(view -> {
            list.addAll(selected);
            for (int i = 0; i < selected.size(); i++) {
                quantityList.add(1);
            }
            dishAdapter.notifyDataSetChanged();
            calculateCost();

            dialog.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom((dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);

        dialog.show();
    }

    private void populateDishesRV() {
        dishAdapter = new DishesExpensesAdapter(list, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.dishesRv.setLayoutManager(layoutManager);
        binding.dishesRv.setAdapter(dishAdapter);
        dishAdapter.notifyDataSetChanged();

    }

    private void calculateCost() {
        totalCost = 0;
        for (int i = 0; i < list.size(); i++) {
            int price = list.get(i).getPrice();
            String str = quantityList.get(i) + "";
            int quantity = Integer.valueOf(str);
            totalCost += (price * quantity);
        }

        if (discountPercent > 0) {
            double discountCost = totalCost * discountPercent / 100;
            binding.discountPrice.setText("Rs " + discountCost + "/-");
            totalCost -= discountCost;
        }

        binding.totalCost.setText("Rs " + totalCost + "/-");
    }

    @Override
    public void onIngredientClickListener(int position) {

    }

    @Override
    public void onIngredientLongListener(int position) {

    }

    @Override
    public void onCostChangeListener(int position, int quantity) {
        quantityList.set(position, quantity);
        calculateCost();
    }
}