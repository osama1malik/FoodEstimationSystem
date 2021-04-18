package com.scorpio.foodestimationsystem.fragments.receipesviews;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.google.firebase.firestore.SetOptions;
import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.adapter.AddIngredientAdapter;
import com.scorpio.foodestimationsystem.databinding.DialogAddDishesBinding;
import com.scorpio.foodestimationsystem.databinding.FragmentAddDishBinding;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Ingredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddDishFragment extends Fragment implements AddIngredientAdapter.IngredientClickListener {

    private FragmentAddDishBinding binding;
    private Dishes currentDish;
    private ArrayList<Ingredients> dishIngredientsList = new ArrayList<>();
    private ArrayList<String> finalIngredientsList = new ArrayList<>();
    private AddIngredientAdapter dishAdapter;
    private int totalCost = 0;
    public static ArrayList<Integer> quantityList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddDishBinding.inflate(getLayoutInflater());
        init();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListeners();
    }

    private void init() {
        ((MainActivity) getActivity()).binding.appbar.btnSearch.setVisibility(View.INVISIBLE);
        ((MainActivity) getActivity()).binding.appbar.btnDone.setVisibility(View.VISIBLE);
        currentDish = new Dishes();
        currentDish.setName(MainActivity.currentDishName);
        MainActivity.currentDishName = "";

        if (MainActivity.currentDish != null && MainActivity.currentDish.getId() != null) {
            currentDish = MainActivity.currentDish;
            MainActivity.currentDish = null;
            finalIngredientsList.addAll(currentDish.getIngredients());
            quantityList = currentDish.getQuantity();

            for (String id : finalIngredientsList) {
                for (Ingredients ingredients : MainActivity.ingredientsList) {
                    if(id.equalsIgnoreCase(ingredients.getId())){
                        dishIngredientsList.add(ingredients);
                        break;
                    }
                }
            }
            calculateCost();
        }

        populateDishesRV();
    }

    private void initListeners() {
        binding.addIngredient.setOnClickListener(v -> {
            showAddDishDialog();
        });
        ((MainActivity) getActivity()).binding.appbar.btnDone.setOnClickListener(v -> {
            currentDish.setPrice(totalCost);
            finalIngredientsList.clear();
            for (Ingredients ingredients : dishIngredientsList) {
                finalIngredientsList.add(ingredients.getId());
            }
            currentDish.setIngredients(finalIngredientsList);
            currentDish.setQuantity(quantityList);

            final Map<String, Object> ingredient = new HashMap<>();
            ingredient.put("image", "image here");
            ingredient.put("name", currentDish.getName());
            ingredient.put("price", currentDish.getPrice());
            ingredient.put("ingredients", currentDish.getIngredients());
            ingredient.put("quantity", currentDish.getQuantity());

            String id = "";

            if (currentDish.getId() == null || currentDish.getId().isEmpty()) {
                id = UUID.randomUUID().toString();
            } else {
                id = currentDish.getId();
            }


            ((MainActivity) requireActivity()).database.collection("Dishes").document(id).set(ingredient, SetOptions.merge()).addOnCompleteListener(task -> {
                try {
                    Toast.makeText(requireContext(), "Dish Added!", Toast.LENGTH_SHORT).show();
                } catch (Exception ignored) {

                }
            }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to add Dish, Please try again!", Toast.LENGTH_SHORT).show());

        });
    }

    private void showAddDishDialog() {
        Dialog dialog = new Dialog(requireContext());
        DialogAddDishesBinding dBinding = DialogAddDishesBinding.inflate(getLayoutInflater());
        dialog.setContentView(dBinding.getRoot());

        dBinding.heading.setText("Select Ingredients");
        dBinding.edDishName.setVisibility(View.GONE);
        dBinding.edDishPrice.setVisibility(View.GONE);

        final ArrayList<Ingredients> selected = new ArrayList<>();
        ArrayList<KeyPairBoolData> data = new ArrayList<>();
        for (Ingredients ingredients : MainActivity.ingredientsList) {
            KeyPairBoolData data1 = new KeyPairBoolData(ingredients.getName(), false);
            data1.setObject(ingredients);
            data.add(data1);
        }

        dBinding.selectIngredients.setSearchEnabled(true);
        dBinding.selectIngredients.setSearchHint("Search Ingredients");
        dBinding.selectIngredients.setEmptyTitle("Not Ingredient Found!");
        dBinding.selectIngredients.setShowSelectAllButton(true);
        dBinding.selectIngredients.setClearText("Close & Clear");
        dBinding.selectIngredients.setItems(data, items -> {
            for (KeyPairBoolData item : items) {
                selected.add(((Ingredients) item.getObject()));
            }
        });

        dBinding.btnCancel.setOnClickListener(view -> dialog.dismiss());
        dBinding.btnSave.setOnClickListener(view -> {
            dishIngredientsList.addAll(selected);
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
        dishAdapter = new AddIngredientAdapter(dishIngredientsList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.dishIngredientsRv.setLayoutManager(layoutManager);
        binding.dishIngredientsRv.setAdapter(dishAdapter);
        dishAdapter.notifyDataSetChanged();

    }

    private void calculateCost() {
        totalCost = 0;
        for (int i = 0; i < dishIngredientsList.size(); i++) {
            int price = dishIngredientsList.get(i).getPrice();
            String str = quantityList.get(i) + "";
            int quantity = Integer.valueOf(str);
            totalCost += (price * quantity);
        }

        currentDish.setPrice(totalCost);
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