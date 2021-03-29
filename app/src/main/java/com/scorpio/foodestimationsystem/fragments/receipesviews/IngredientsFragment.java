package com.scorpio.foodestimationsystem.fragments.receipesviews;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.adapter.DishAdapter;
import com.scorpio.foodestimationsystem.databinding.FragmentDishesBinding;
import com.scorpio.foodestimationsystem.databinding.FragmentIngredientsBinding;
import com.scorpio.foodestimationsystem.modal.Dishes;

import java.util.ArrayList;

public class IngredientsFragment extends Fragment {

    private FragmentIngredientsBinding binding = null;
    private DishAdapter dishAdapter;
    private ArrayList<Dishes> list = new ArrayList();

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
            list.add(new Dishes("", "Fries"));
            list.add(new Dishes("", "Sushi"));
            list.add(new Dishes("", "Pizza"));
            list.add(new Dishes("", "Noodles"));
            list.add(new Dishes("", "Burger"));
            list.add(new Dishes("", "Cupcake"));
            list.add(new Dishes("", "Eggs"));
            list.add(new Dishes("", "Melon"));

            showHideEmptyLayout();
            dishAdapter.notifyDataSetChanged();
        });
    }


    /**
     * Populate Available Dishes in recyclerView.
     */
    private void populateDishesRV() {
        dishAdapter = new DishAdapter(list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        binding.ingredientsRv.setLayoutManager(layoutManager);
        binding.ingredientsRv.setAdapter(dishAdapter);
        dishAdapter.notifyDataSetChanged();
        showHideEmptyLayout();
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
}