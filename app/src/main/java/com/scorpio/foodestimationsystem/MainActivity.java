package com.scorpio.foodestimationsystem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.scorpio.foodestimationsystem.databinding.ActivityMainBinding;
import com.scorpio.foodestimationsystem.fragments.DiscountFragment;
import com.scorpio.foodestimationsystem.fragments.EventsFragment;
import com.scorpio.foodestimationsystem.fragments.ExpensesFragment;
import com.scorpio.foodestimationsystem.fragments.HowToUseFragment;
import com.scorpio.foodestimationsystem.fragments.RecipesFragment;
import com.scorpio.foodestimationsystem.fragments.StoreFragment;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Ingredients;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private ActivityMainBinding binding = null;
    private FragmentManager fragmentManager = null;
    public FirebaseFirestore database;
    public static ArrayList<Ingredients> ingredientsList = new ArrayList();
    public static ArrayList<Dishes> dishesList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        changeFragment(new RecipesFragment(), "Recipes");
        database = FirebaseFirestore.getInstance();

    }

    /**
     * This method is called when activity is created successfully.
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initClickListeners();
        initDrawerClicks();
    }

    /**
     * Call this method when you want to change fragment in main activity.
     *
     * @param fragment: Object of fragment to be called.
     * @param heading:  Heading/TAG/Name of fragment.
     */
    private void changeFragment(Fragment fragment, String heading) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(binding.mainFrame.getId(), fragment, heading);
        transaction.addToBackStack(heading);
        transaction.commit();
        initAppbar(heading);
    }

    /**
     * Initialize appbar for different screens.
     *
     * @param heading: Heading to show in appbar.
     */
    private void initAppbar(String heading) {
        binding.appbar.textHeading.setText(heading);
    }

    /**
     * Initialize Click Listeners of main activity.
     */
    private void initClickListeners() {
        binding.appbar.btnMenu.setOnClickListener(v -> binding.mainDrawerLayout.openDrawer(GravityCompat.START));
        binding.appbar.btnSearch.setOnClickListener(v -> {
        });
    }

    /**
     * Initialize Click Listeners of main drawer.
     */
    private void initDrawerClicks() {
        binding.drawerMain.llRecipe.setOnClickListener(v -> {
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
            changeFragment(new RecipesFragment(), "Recipes");
        });
        binding.drawerMain.llDiscount.setOnClickListener(v -> {
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
            changeFragment(new DiscountFragment(), "Discount");
        });
        binding.drawerMain.llEvents.setOnClickListener(v -> {
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
            changeFragment(new EventsFragment(), "Events");
        });
        binding.drawerMain.llExpenses.setOnClickListener(v -> {
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
            changeFragment(new ExpensesFragment(), "Expenses");
        });
        binding.drawerMain.llStore.setOnClickListener(v -> {
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
            changeFragment(new StoreFragment(), "Store");
        });
        binding.drawerMain.llHowToUse.setOnClickListener(v -> {
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
            changeFragment(new HowToUseFragment(), "How to use");
        });
    }

    @Override
    public void onBackStackChanged() {

    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1) {
            Toast.makeText(this, "exit application", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}