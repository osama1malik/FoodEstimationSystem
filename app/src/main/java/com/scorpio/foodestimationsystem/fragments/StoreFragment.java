package com.scorpio.foodestimationsystem.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.adapter.StoreAdapter;
import com.scorpio.foodestimationsystem.databinding.FragmentStoreBinding;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Events;
import com.scorpio.foodestimationsystem.model.Ingredients;
import com.scorpio.foodestimationsystem.model.Store;

import java.util.ArrayList;

import static com.scorpio.foodestimationsystem.MainActivity.dishesList;
import static com.scorpio.foodestimationsystem.MainActivity.eventsList;
import static com.scorpio.foodestimationsystem.MainActivity.ingredientsList;

public class StoreFragment extends Fragment implements StoreAdapter.StoreItemClickListener {

    private FragmentStoreBinding binding;
    private StoreAdapter adapter;
    private ArrayList<Store> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStoreBinding.inflate(getLayoutInflater());
        init();
        return binding.getRoot();
    }

    private void init() {
        populateIngredients();
        populateStoreRv();
    }

    private void populateStoreRv() {
        adapter = new StoreAdapter(list, this);
        binding.storeRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.storeRv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void populateIngredients() {
        new Thread(() -> {

            for (Ingredients ingredient : ingredientsList) {

                int count = 0;

                Log.i("TAG", "populateIngredients: " + ingredient.getName());

                for (Events events : eventsList) {
                    for (Dishes dishes : dishesList) {
                        if (events.getDishes().contains(dishes.getId())) {
                            if (dishes.getIngredients().contains(ingredient.getId())) {
                                int index = dishes.getIngredients().indexOf(ingredient.getId());
                                long value = dishes.getQuantity().get(index);
                                Log.i("TAG", "populateIngredients: " + value);
                                count += Math.toIntExact(value);
                            }
                        }
                    }
                }

                list.add(new Store(ingredient.getName(), ingredient.getQuantity(), Math.toIntExact(count)));
            }

            requireActivity().runOnUiThread(() -> {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            });

        }).start();
    }

    @Override
    public void onStoreItemClickListener(int position) {

    }
}