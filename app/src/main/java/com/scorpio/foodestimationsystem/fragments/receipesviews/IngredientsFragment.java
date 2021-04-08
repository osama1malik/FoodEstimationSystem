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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.adapter.DishAdapter;
import com.scorpio.foodestimationsystem.adapter.IngredientAdapter;
import com.scorpio.foodestimationsystem.databinding.DialogAddDishesBinding;
import com.scorpio.foodestimationsystem.databinding.DialogAddIngredientBinding;
import com.scorpio.foodestimationsystem.databinding.FragmentIngredientsBinding;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Ingredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IngredientsFragment extends Fragment implements IngredientAdapter.IngredientClickListener {

    private FragmentIngredientsBinding binding = null;
    private IngredientAdapter ingredientAdapter;
    private ArrayList<Ingredients> list = new ArrayList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentIngredientsBinding.inflate(getLayoutInflater());
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
            showAddIngredientDialog();
        });
    }

    /**
     * Populate Available Dishes in recyclerView.
     */
    private void populateDishesRV() {
        list.clear();
        ((MainActivity) requireActivity()).database.collection("Ingredients").addSnapshotListener((value, error) -> {
            if (value != null && value.size() > 0) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Map<String, Object> data = dc.getDocument().getData();
                            String name = data.get("name").toString();
                            String unit = data.get("unit").toString();
                            int quantity = Integer.parseInt(data.get("quantity").toString());
                            int price = Integer.parseInt(data.get("price").toString());
                            list.add(new Ingredients(name, unit, quantity, price));
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
                ingredientAdapter.notifyDataSetChanged();

            }
        });


        ingredientAdapter = new IngredientAdapter(list, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        binding.ingredientsRv.setLayoutManager(layoutManager);
        binding.ingredientsRv.setAdapter(ingredientAdapter);
        ingredientAdapter.notifyDataSetChanged();
        showHideEmptyLayout();
    }

    private void showAddIngredientDialog() {
        Dialog dialog = new Dialog(requireContext());
        DialogAddIngredientBinding dBinding = DialogAddIngredientBinding.inflate(getLayoutInflater());
        dialog.setContentView(dBinding.getRoot());

        dBinding.btnCancel.setOnClickListener(view -> dialog.dismiss());
        dBinding.btnSave.setOnClickListener(view -> {
            String name = dBinding.edIngredientName.getText().toString();
            int price = Integer.parseInt(dBinding.edIngredientPrice.getText().toString());
            int quantity = Integer.parseInt(dBinding.edIngredientQuantity.getText().toString());
            String unit = dBinding.edIngredientUnit.getText().toString();

            final Map<String, Object> ingredient = new HashMap<>();
            ingredient.put("name", name);
            ingredient.put("price", price);
            ingredient.put("quantity", quantity);
            ingredient.put("unit", unit);

            String id = UUID.randomUUID().toString();
            ((MainActivity) requireActivity()).database.collection("Ingredients").document(id).set(ingredient, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(requireContext(), "Ingredient Added!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(requireContext(), "Failed to add ingredient, Please try again!", Toast.LENGTH_SHORT).show();
                }
            });

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
        if (list.size() > 0) {
            binding.ingredientsRv.setVisibility(View.VISIBLE);
            binding.emptyLayout.getRoot().setVisibility(View.GONE);
        } else {
            binding.ingredientsRv.setVisibility(View.GONE);
            binding.emptyLayout.getRoot().setVisibility(View.VISIBLE);
            binding.emptyLayout.emptyText.setText("No Ingredients Added");
        }
    }

    @Override
    public void onIngredientClickListener(int position) {
        if(position == -1){
            showAddIngredientDialog();
        }
    }
}