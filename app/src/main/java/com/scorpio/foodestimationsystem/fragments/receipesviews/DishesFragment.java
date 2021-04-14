package com.scorpio.foodestimationsystem.fragments.receipesviews;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.SetOptions;
import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.adapter.DishAdapter;
import com.scorpio.foodestimationsystem.databinding.DialogAddDishesBinding;
import com.scorpio.foodestimationsystem.databinding.FragmentDishesBinding;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Ingredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DishesFragment extends Fragment implements DishAdapter.DishClickListener {

    private FragmentDishesBinding binding = null;
    private DishAdapter dishAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDishesBinding.inflate(getLayoutInflater());
        initFragment();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initClickListeners();
    }

    private void initFragment() {
        populateDishesRV();
    }

    private void initClickListeners() {
        binding.emptyLayout.btnAddEmpty.setOnClickListener(v -> {
            showAddDishDialog();
        });
    }

    /**
     * Populate Available Dishes in recyclerView.
     * This method also get dishes from firebase firestore database before populating the database.
     */
    private void populateDishesRV() {
        MainActivity.dishesList.clear();
        ((MainActivity) requireActivity()).database.collection("Dishes").addSnapshotListener((value, error) -> {
            if (value != null && value.size() > 0) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Map<String, Object> data = dc.getDocument().getData();
                            String image = data.get("image").toString();
                            String name = data.get("name").toString();
                            ArrayList<String> ingredients = (ArrayList<String>) data.get("ingredients");
                            int price = Integer.parseInt(data.get("price").toString());
                            MainActivity.dishesList.add(new Dishes(dc.getDocument().getId(), image, name, price, ingredients));

                            Log.i("TAG", "onEvent ADDED: " + dc.getDocument().getData().get("ingredients"));
                            break;
                        case MODIFIED:
                            Log.i("TAG", "onEvent MODIFIED: " + dc.getDocument().getId());
                            break;
                        case REMOVED:
                            Log.i("TAG", "onEvent REMOVED: " + dc.getDocument().getId());
                            break;
                    }
                }

                showHideEmptyLayout();
                dishAdapter.notifyDataSetChanged();
            }
        });
        dishAdapter = new DishAdapter(MainActivity.dishesList, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        binding.dishesRv.setLayoutManager(layoutManager);
        binding.dishesRv.setAdapter(dishAdapter);
        dishAdapter.notifyDataSetChanged();
        showHideEmptyLayout();
    }

    private void showAddDishDialog() {
        Dialog dialog = new Dialog(requireContext());
        DialogAddDishesBinding dBinding = DialogAddDishesBinding.inflate(getLayoutInflater());
        dialog.setContentView(dBinding.getRoot());

        final ArrayList<String> selected = new ArrayList<>();
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
                selected.add(((Ingredients) item.getObject()).getId());
            }
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).isSelected()) {
                    Log.i("TAG", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                }
            }
        });

        dBinding.btnCancel.setOnClickListener(view -> dialog.dismiss());
        dBinding.btnSave.setOnClickListener(view -> {
            String name = dBinding.edDishName.getText().toString();
            String price = dBinding.edDishPrice.getText().toString();
            if (name.isEmpty()) {
                dBinding.edDishName.setError("Enter Dish Name");
                dBinding.edDishName.requestFocus();
                return;
            }
            if (price.isEmpty()) {
                dBinding.edDishPrice.setError("Enter Dish Name");
                dBinding.edDishPrice.requestFocus();
                return;
            }

            final Map<String, Object> ingredient = new HashMap<>();
            ingredient.put("image", "image here");
            ingredient.put("name", name);
            ingredient.put("price", price);
            ingredient.put("ingredients", selected);

            String id = UUID.randomUUID().toString();
            ((MainActivity) requireActivity()).database.collection("Dishes").document(id).set(ingredient, SetOptions.merge()).addOnCompleteListener(task -> {
                Toast.makeText(requireContext(), "Dish Added!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to add Dish, Please try again!", Toast.LENGTH_SHORT).show());

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

    /**
     * Method to show/hide empty layout.
     */
    private void showHideEmptyLayout() {
        if (MainActivity.dishesList.size() > 0) {
            binding.dishesRv.setVisibility(View.VISIBLE);
            binding.emptyLayout.getRoot().setVisibility(View.GONE);
        } else {
            binding.dishesRv.setVisibility(View.GONE);
            binding.emptyLayout.getRoot().setVisibility(View.VISIBLE);
            binding.emptyLayout.emptyText.setText("No Dishes Added");
        }
    }

    @Override
    public void onDishClickListener(int position) {
        if (position == -1) {
            showAddDishDialog();
        }
    }
}