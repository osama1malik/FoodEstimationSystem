package com.scorpio.foodestimationsystem.fragments.receipesviews;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.SetOptions;
import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.adapter.IngredientAdapter;
import com.scorpio.foodestimationsystem.databinding.DialogAddIngredientBinding;
import com.scorpio.foodestimationsystem.databinding.FragmentIngredientsBinding;
import com.scorpio.foodestimationsystem.model.Ingredients;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SuppressLint("SetTextI18n")
public class IngredientsFragment extends Fragment implements IngredientAdapter.IngredientClickListener {

    private FragmentIngredientsBinding binding = null;
    private IngredientAdapter ingredientAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
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
        binding.emptyLayout.btnAddEmpty.setOnClickListener(v -> showAddIngredientDialog());
    }

    /**
     * Populate Available Dishes in recyclerView.
     */
    private void populateDishesRV() {
        MainActivity.ingredientsList.clear();
        ((MainActivity) requireActivity()).database.collection("Ingredients").addSnapshotListener((value, error) -> {
            if (value != null && value.size() > 0) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Map<String, Object> data = dc.getDocument().getData();
                            String name = Objects.requireNonNull(data.get("name")).toString();
                            String unit = Objects.requireNonNull(data.get("unit")).toString();
                            int quantity = Integer.parseInt(Objects.requireNonNull(data.get("quantity")).toString());
                            int price = Integer.parseInt(Objects.requireNonNull(data.get("price")).toString());
                            MainActivity.ingredientsList.add(new Ingredients(dc.getDocument().getId(), name, unit, quantity, price));
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


        ingredientAdapter = new IngredientAdapter(MainActivity.ingredientsList, this);
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
            ((MainActivity) requireActivity()).database.collection("Ingredients").document(id).set(ingredient, SetOptions.merge()).addOnCompleteListener(task -> {
                Toast.makeText(requireContext(), "Ingredient Added!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to add ingredient, Please try again!", Toast.LENGTH_SHORT).show());

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
        if (MainActivity.ingredientsList.size() > 0) {
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
        if (position == -1) {
            showAddIngredientDialog();
        }
    }
}