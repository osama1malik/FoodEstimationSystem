package com.scorpio.foodestimationsystem.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.adapter.ViewPagerAdapter;
import com.scorpio.foodestimationsystem.databinding.FragmentRecipesBinding;
import com.scorpio.foodestimationsystem.fragments.receipesviews.DishesFragment;
import com.scorpio.foodestimationsystem.fragments.receipesviews.IngredientsFragment;
import com.scorpio.foodestimationsystem.fragments.receipesviews.StatsFragment;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT;

public class RecipesFragment extends Fragment {

    private FragmentRecipesBinding binding = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecipesBinding.inflate(getLayoutInflater());
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        initFragment();
        return binding.getRoot();
    }

    private void initFragment() {
        ((MainActivity) requireActivity()).binding.appbar.btnSearch.setVisibility(View.VISIBLE);
        ((MainActivity) requireActivity()).binding.appbar.btnDone.setVisibility(View.INVISIBLE);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), BEHAVIOR_SET_USER_VISIBLE_HINT);
        adapter.addFragment(new DishesFragment(), "Dishes");
        adapter.addFragment(new IngredientsFragment(), "Ingredients");
//        adapter.addFragment(new StatsFragment(), "Stats");

        binding.viewPager.setAdapter(adapter);

        binding.mainTabLayout.setupWithViewPager(binding.viewPager, true);

        binding.mainTabLayout.getTabAt(0).setText("Dishes");
        binding.mainTabLayout.getTabAt(1).setText("Ingredients");
//        binding.mainTabLayout.getTabAt(2).setText("Stats");

        binding.mainTabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#ffffff"));
        binding.mainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}