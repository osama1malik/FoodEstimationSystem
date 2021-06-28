package com.scorpio.foodestimationsystem.fragments.receipesviews;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.scorpio.foodestimationsystem.interfaces.BackPressListener;
import com.scorpio.foodestimationsystem.model.Ingredients;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import kotlin.Unit;

@SuppressLint("SetTextI18n")
public class IngredientsFragment extends Fragment implements IngredientAdapter.IngredientClickListener, BackPressListener {

    private FragmentIngredientsBinding binding = null;
    private IngredientAdapter ingredientAdapter;
    public static ArrayList<Ingredients> selectedList = new ArrayList<>();
    public boolean isSelectedLayout = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentIngredientsBinding.inflate(getLayoutInflater());
        ((MainActivity) requireActivity()).binding.appbar.btnDone.setVisibility(View.INVISIBLE);
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
        binding.emptyLayout.btnAddEmpty.setOnClickListener(v -> showAddIngredientDialog(-1));
        binding.deleteFab.setOnClickListener(v -> {
            ProgressDialog dialog = new ProgressDialog(requireContext());
            dialog.setTitle("Deleting Ingredients");
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);

            for (Ingredients ingredients : selectedList) {
                dialog.show();
                deleteIngredient(ingredients, () -> {
                    MainActivity.ingredientsList.remove(ingredients);
                    ingredientAdapter.notifyDataSetChanged();
                    setDeleteLayout(false);
                    hideLoading(dialog::dismiss);
                });
            }
        });
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
                            String userid = Objects.requireNonNull(data.get("user_id")).toString();
                            String name = Objects.requireNonNull(data.get("name")).toString();
                            String unit = Objects.requireNonNull(data.get("unit")).toString();
                            int quantity = Integer.parseInt(Objects.requireNonNull(data.get("quantity")).toString());
                            int price = Integer.parseInt(Objects.requireNonNull(data.get("price")).toString());
                            if (userid.equalsIgnoreCase(((MainActivity) requireActivity()).currentUser.getUid())) {
                                Ingredients ingredient = new Ingredients(((MainActivity) requireActivity()).currentUser.getUid(), dc.getDocument().getId(), name, unit, quantity, price);

                                if (!MainActivity.ingredientsList.contains(ingredient)) {
                                    MainActivity.ingredientsList.add(ingredient);
                                }

                            }
                            Log.i("TAG", "onEvent ADDED: " + dc.getDocument().getData().get("ingredients"));
                            break;
                        case MODIFIED:
                            Log.i("TAG", "onEvent MODIFIED: " + dc.getDocument().getId());
                            if (ingredientAdapter != null) {
                                Map<String, Object> data1 = dc.getDocument().getData();
                                String name1 = Objects.requireNonNull(data1.get("name")).toString();
                                String unit1 = Objects.requireNonNull(data1.get("unit")).toString();
                                int quantity1 = Integer.parseInt(Objects.requireNonNull(data1.get("quantity")).toString());
                                int price1 = Integer.parseInt(Objects.requireNonNull(data1.get("price")).toString());

                                for (int i = 0; i < MainActivity.ingredientsList.size(); i++) {
                                    if (MainActivity.ingredientsList.get(i).getId().equalsIgnoreCase(dc.getDocument().getId())) {
                                        MainActivity.ingredientsList.get(i).setName(name1);
                                        MainActivity.ingredientsList.get(i).setPrice(price1);
                                        MainActivity.ingredientsList.get(i).setQuantity(quantity1);
                                        MainActivity.ingredientsList.get(i).setUnit(unit1);

                                        ingredientAdapter.notifyItemChanged(i);

                                        break;
                                    }
                                }
                            }
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

    private void showAddIngredientDialog(int pos) {
        Dialog dialog = new Dialog(requireContext());
        DialogAddIngredientBinding dBinding = DialogAddIngredientBinding.inflate(getLayoutInflater());
        dialog.setContentView(dBinding.getRoot());

        String id = "";
        if (pos != -1) {
            dBinding.heading.setText("Edit Ingredient");
            Ingredients ingredients = MainActivity.ingredientsList.get(pos);
            dBinding.edIngredientName.setText(ingredients.getName());
            dBinding.edIngredientPrice.setText(String.valueOf(ingredients.getPrice()));
            dBinding.edIngredientQuantity.setText(String.valueOf(ingredients.getQuantity()));
            dBinding.edIngredientUnit.setText(ingredients.getUnit());

            id = ingredients.getId();

        } else {
            dBinding.heading.setText("Add New Ingredient");
            id = UUID.randomUUID().toString();
        }

        final String fId = id;

        dBinding.btnCancel.setOnClickListener(view -> dialog.dismiss());
        dBinding.btnSave.setOnClickListener(view -> {
            String name = dBinding.edIngredientName.getText().toString();
            int price = 0;
            if (!dBinding.edIngredientPrice.getText().toString().isEmpty()) {
                price = Integer.parseInt(dBinding.edIngredientPrice.getText().toString());
            }
            int quantity = 0;
            if (!dBinding.edIngredientPrice.getText().toString().isEmpty()) {
                quantity = Integer.parseInt(dBinding.edIngredientQuantity.getText().toString());
            }

            String unit = dBinding.edIngredientUnit.getText().toString();

            final Map<String, Object> ingredient = new HashMap<>();
            ingredient.put("user_id", ((MainActivity) requireActivity()).currentUser.getUid());
            ingredient.put("name", name);
            ingredient.put("price", price);
            ingredient.put("quantity", quantity);
            ingredient.put("unit", unit);

            ((MainActivity) requireActivity()).database.collection("Ingredients").document(fId).set(ingredient, SetOptions.merge()).addOnCompleteListener(task -> {
                Toast.makeText(requireContext(), "Ingredient Added!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }).addOnFailureListener(e -> {
                Log.i("TAG", "showAddIngredientDialog: error file adding ingredient.", e);
                Toast.makeText(requireContext(), "Failed to add ingredient, Please try again!", Toast.LENGTH_SHORT).show();
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
        if (MainActivity.ingredientsList.size() > 0) {
            binding.ingredientsRv.setVisibility(View.VISIBLE);
            binding.emptyLayout.getRoot().setVisibility(View.GONE);
        } else {
            binding.ingredientsRv.setVisibility(View.GONE);
            binding.emptyLayout.getRoot().setVisibility(View.VISIBLE);
            binding.emptyLayout.emptyText.setText("No Ingredients Added");
        }
    }

    private void deleteIngredient(Ingredients ingredient, Runnable callback) {
        ((MainActivity) requireActivity()).database.collection("Ingredients").document(ingredient.getId()).delete().addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                callback.run();
            }
        });
    }

    private void hideLoading(Runnable callback) {
        if (selectedList.isEmpty()) {
            callback.run();
        }
    }

    private void setDeleteLayout(Boolean show) {
        if (show) {
            MainActivity.callBackListener = true;
            MainActivity.backPressListener = this;
            binding.deleteFab.setVisibility(View.VISIBLE);
            isSelectedLayout = true;
        } else {
            MainActivity.callBackListener = false;
            isSelectedLayout = false;
            selectedList.clear();
            binding.deleteFab.setVisibility(View.GONE);
            ingredientAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onIngredientClickListener(int position) {
        if (position == -1) {
            if (!isSelectedLayout) {
                showAddIngredientDialog(-1);
            }
        } else {
            if (isSelectedLayout) {
                if (selectedList.contains(MainActivity.ingredientsList.get(position))) {
                    selectedList.remove(MainActivity.ingredientsList.get(position));
                    if (selectedList.isEmpty()) {
                        setDeleteLayout(false);
                    }
                } else {
                    selectedList.add(MainActivity.ingredientsList.get(position));
                }
                ingredientAdapter.notifyItemChanged(position);
            } else {
                showAddIngredientDialog(position);
            }
        }
    }

    @Override
    public void onIngredientLongListener(int position) {
        if (!isSelectedLayout) {
            setDeleteLayout(true);
            selectedList.add(MainActivity.ingredientsList.get(position));
            ingredientAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onBackPressListener() {
        setDeleteLayout(false);
    }
}