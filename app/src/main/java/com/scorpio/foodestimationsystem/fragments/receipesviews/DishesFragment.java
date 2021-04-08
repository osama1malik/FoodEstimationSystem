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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.adapter.DishAdapter;
import com.scorpio.foodestimationsystem.databinding.DialogAddDishesBinding;
import com.scorpio.foodestimationsystem.databinding.FragmentDishesBinding;
import com.scorpio.foodestimationsystem.model.Dishes;

import java.util.ArrayList;
import java.util.Map;

public class DishesFragment extends Fragment implements DishAdapter.DishClickListener {

    private FragmentDishesBinding binding = null;
    private DishAdapter dishAdapter;
    private ArrayList<Dishes> list = new ArrayList();

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
        ((MainActivity) requireActivity()).database.collection("Dishes").addSnapshotListener((value, error) -> {
            if (value != null && value.size() > 0) {
                list.clear();
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Map<String, Object> data = dc.getDocument().getData();
                            String image = data.get("image").toString();
                            String name = data.get("name").toString();
                            ArrayList<String> ingredients = (ArrayList<String>) data.get("ingredients");
                            int price = Integer.parseInt(data.get("price").toString());
                            list.add(new Dishes(image, name, price, ingredients));

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
        dishAdapter = new DishAdapter(list, this);
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

        dBinding.btnCancel.setOnClickListener(view -> dialog.dismiss());
        dBinding.btnSave.setOnClickListener(view -> dialog.dismiss());

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
        if(position == -1){
            showAddDishDialog();
        }
    }
}