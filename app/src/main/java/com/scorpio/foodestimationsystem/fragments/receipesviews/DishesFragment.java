package com.scorpio.foodestimationsystem.fragments.receipesviews;

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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.SetOptions;
import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.adapter.DishAdapter;
import com.scorpio.foodestimationsystem.databinding.DialogAddDishesBinding;
import com.scorpio.foodestimationsystem.databinding.FragmentDishesBinding;
import com.scorpio.foodestimationsystem.interfaces.BackPressListener;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Ingredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DishesFragment extends Fragment implements DishAdapter.DishClickListener, BackPressListener {

    private FragmentDishesBinding binding = null;
    private DishAdapter dishAdapter;
    public static ArrayList<Dishes> selectedList = new ArrayList<>();
    public boolean isSelectedLayout = false;

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

        binding.deleteFab.setOnClickListener(v -> {
            ProgressDialog dialog = new ProgressDialog(requireContext());
            dialog.setTitle("Deleting Ingredients");
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);

            for (Dishes dishes : selectedList) {
                dialog.show();
                deleteDishes(dishes, () -> {
                    MainActivity.dishesList.remove(dishes);
                    dishAdapter.notifyDataSetChanged();
                    setDeleteLayout(false);
                    hideLoading(dialog::dismiss);
                });
            }
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
                            try {
                                Map<String, Object> data = dc.getDocument().getData();
                                String userid = data.get("user_id").toString();
                                String image = data.get("image").toString();
                                String name = data.get("name").toString();
                                ArrayList<String> ingredients = (ArrayList<String>) data.get("ingredients");
                                ArrayList<Long> quantity = (ArrayList<Long>) data.get("quantity");
                                int price = Integer.parseInt(data.get("price").toString());
                                if (userid.equalsIgnoreCase(((MainActivity) requireActivity()).currentUser.getUid())) {
                                    MainActivity.dishesList.add(new Dishes(((MainActivity) requireActivity()).currentUser.getUid(), dc.getDocument().getId(), image, name, price, ingredients, quantity));
                                }
                            } catch (Exception e) {

                            }
                            Log.i("TAG", "onEvent ADDED: " + dc.getDocument().getData().get("ingredients"));
                            break;
                        case MODIFIED:
                            populateDishesRV();
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

        dBinding.edDishPrice.setVisibility(View.GONE);
        dBinding.selectIngredients.setVisibility(View.GONE);

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
        });

        dBinding.btnCancel.setOnClickListener(view -> dialog.dismiss());
        dBinding.btnSave.setOnClickListener(view -> {
            String name = dBinding.edDishName.getText().toString();
            MainActivity.currentDishName = name;
            ((MainActivity) getActivity()).changeFragment(new AddDishFragment(), name);
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
            dishAdapter.notifyDataSetChanged();
        }
    }

    private void deleteDishes(Dishes dishes, Runnable callback) {
        ((MainActivity) requireActivity()).database.collection("Dishes").document(dishes.getId()).delete().addOnCompleteListener(new OnCompleteListener() {
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

    @Override
    public void onDishClickListener(int position) {
        if (position == -1) {
            if (!isSelectedLayout) {
                showAddDishDialog();
            }
        } else {

            if (isSelectedLayout) {
                if (selectedList.contains(MainActivity.dishesList.get(position))) {
                    selectedList.remove(MainActivity.dishesList.get(position));
                    if (selectedList.isEmpty()) {
                        setDeleteLayout(false);
                    }
                } else {
                    selectedList.add(MainActivity.dishesList.get(position));
                }
                dishAdapter.notifyItemChanged(position);
            } else {
                MainActivity.currentDish = MainActivity.dishesList.get(position);
                ((MainActivity) getActivity()).changeFragment(new AddDishFragment(), MainActivity.dishesList.get(position).getName());
            }


        }
    }

    @Override
    public void onDishLongClickListener(int position) {
        if (!isSelectedLayout) {
            setDeleteLayout(true);
            selectedList.add(MainActivity.dishesList.get(position));
            dishAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).binding.appbar.btnSearch.setVisibility(View.INVISIBLE);
        ((MainActivity) getActivity()).binding.appbar.btnDone.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).binding.appbar.textHeading.setText("Recipes");

    }

    @Override
    public void onBackPressListener() {
        setDeleteLayout(false);
    }
}