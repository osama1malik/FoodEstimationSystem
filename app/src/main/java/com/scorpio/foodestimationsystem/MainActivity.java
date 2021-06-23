package com.scorpio.foodestimationsystem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scorpio.foodestimationsystem.authentication.LoginActivity;
import com.scorpio.foodestimationsystem.databinding.ActivityMainBinding;
import com.scorpio.foodestimationsystem.fragments.DiscountFragment;
import com.scorpio.foodestimationsystem.fragments.EventsFragment;
import com.scorpio.foodestimationsystem.fragments.ExpensesFragment;
import com.scorpio.foodestimationsystem.fragments.HowToUseFragment;
import com.scorpio.foodestimationsystem.fragments.RecipesFragment;
import com.scorpio.foodestimationsystem.fragments.StoreFragment;
import com.scorpio.foodestimationsystem.interfaces.BackPressListener;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Events;
import com.scorpio.foodestimationsystem.model.Ingredients;
import com.scorpio.foodestimationsystem.model.Store;
import com.scorpio.foodestimationsystem.service.NotificationUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    public ActivityMainBinding binding = null;
    private FragmentManager fragmentManager = null;
    public FirebaseFirestore database;
    public static ArrayList<Ingredients> ingredientsList = new ArrayList();
    public static ArrayList<Dishes> dishesList = new ArrayList();
    public static ArrayList<Events> eventsList = new ArrayList<>();
    public static Boolean callBackListener = false;
    public static BackPressListener backPressListener;
    public static String currentDishName = "";
    public static Dishes currentDish = new Dishes();
    public static AtomicInteger seed = new AtomicInteger();

    public FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        changeFragment(new RecipesFragment(), "Recipes");
        database = FirebaseFirestore.getInstance();

//        reminderNotification(10, "10 sec", "this is body from paramaters");
//        reminderNotification(15, "15 sec", "this is body from paramaters");
//        reminderNotification(20, "20 sec", "this is body from paramaters");
//        reminderNotification(25, "25 sec", "this is body from paramaters");
    }

    /**
     * This method is called when activity is created successfully.
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initClickListeners();
        initDrawerClicks();

        populateEventsList();
    }

    /**
     * Call this method when you want to change fragment in main activity.
     *
     * @param fragment: Object of fragment to be called.
     * @param heading:  Heading/TAG/Name of fragment.
     */
    public void changeFragment(Fragment fragment, String heading) {
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
        binding.drawerMain.headerName.setText(currentUser.getDisplayName());
        binding.drawerMain.headerEmail.setText(currentUser.getEmail());


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
        binding.drawerMain.llLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Initialize the events list in background thread.
     */
    private void populateEventsList() {
        new Thread(() -> {
            eventsList.clear();
            database.collection("Events").addSnapshotListener((value, error) -> {
                if (value != null && value.size() > 0) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Map<String, Object> data = dc.getDocument().getData();
                                String userid = Objects.requireNonNull(data.get("user_id")).toString();
                                String name = Objects.requireNonNull(data.get("name")).toString();
                                int participants = Integer.parseInt(data.get("participants").toString());
                                ArrayList<String> dishes = (ArrayList<String>) data.get("dishes");
                                long date = ((com.google.firebase.Timestamp) data.get("date")).getSeconds();
                                if (userid.equalsIgnoreCase(currentUser.getUid())) {
                                    eventsList.add(new Events(dc.getDocument().getId(), currentUser.getUid(), name, participants, date, dishes));
                                }

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
                }
            });

        }).start();
    }

    @Override
    public void onBackStackChanged() {

    }

    @Override
    public void onBackPressed() {

        if (callBackListener) {
            if (backPressListener != null) {
                backPressListener.onBackPressListener();
            } else {
                if (fragmentManager.getBackStackEntryCount() == 1) {
                    Toast.makeText(this, "exit application", Toast.LENGTH_SHORT).show();
                } else {
                    super.onBackPressed();
                }
            }
        } else {
            if (fragmentManager.getBackStackEntryCount() == 1) {
                Toast.makeText(this, "exit application", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        }


    }
}