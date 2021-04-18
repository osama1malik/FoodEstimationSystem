package com.scorpio.foodestimationsystem.fragments;

import android.app.Dialog;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.SetOptions;
import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.R;
import com.scorpio.foodestimationsystem.adapter.DishAdapter;
import com.scorpio.foodestimationsystem.adapter.EventAdapter;
import com.scorpio.foodestimationsystem.databinding.DialogAddDishesBinding;
import com.scorpio.foodestimationsystem.databinding.DialogAddEventsBinding;
import com.scorpio.foodestimationsystem.databinding.FragmentEventsBinding;
import com.scorpio.foodestimationsystem.interfaces.BackPressListener;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Events;
import com.scorpio.foodestimationsystem.model.Ingredients;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class EventsFragment extends Fragment implements EventAdapter.EventClickListener, BackPressListener {

    private FragmentEventsBinding binding = null;
    private ArrayList<Events> eventsList = new ArrayList<>();
    private long selectedTime = 0;
    private EventAdapter eventAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventsBinding.inflate(getLayoutInflater());
        initFragment();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initClickListeners();
    }

    private void initFragment() {
        populateEventsRV();
    }

    private void initClickListeners() {
        binding.emptyLayout.btnAddEmpty.setOnClickListener(v -> {
            showAddEventDialog();
        });
    }

    private void populateEventsRV() {
        eventsList.clear();
        ((MainActivity) requireActivity()).database.collection("Events").addSnapshotListener((value, error) -> {
            if (value != null && value.size() > 0) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Map<String, Object> data = dc.getDocument().getData();
                            String name = Objects.requireNonNull(data.get("name")).toString();
                            int participants = Integer.parseInt(data.get("participants").toString());
                            ArrayList<String> dishes = (ArrayList<String>) data.get("dishes");
                            long date = ((com.google.firebase.Timestamp) data.get("date")).getSeconds();
                            eventsList.add(new Events(name, participants, date, dishes));

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
                eventAdapter.notifyDataSetChanged();
            }
        });
        eventAdapter = new EventAdapter(eventsList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.eventsRv.setLayoutManager(layoutManager);
        binding.eventsRv.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();
        showHideEmptyLayout();
    }

    private void showAddEventDialog() {
        Dialog dialog = new Dialog(requireContext());
        DialogAddEventsBinding dBinding = DialogAddEventsBinding.inflate(getLayoutInflater());
        dialog.setContentView(dBinding.getRoot());

        final ArrayList<String> selected = new ArrayList<>();
        ArrayList<KeyPairBoolData> data = new ArrayList<>();
        for (Dishes dishes : MainActivity.dishesList) {
            KeyPairBoolData data1 = new KeyPairBoolData(dishes.getName(), false);
            data1.setObject(dishes);
            data.add(data1);
        }

        dBinding.edEventDate.setOnClickListener(v -> {
            showDatePicker(() -> dBinding.edEventDate.setText(getFormattedDate(selectedTime)));
        });

        dBinding.edEventDishes.setSearchEnabled(true);
        dBinding.edEventDishes.setSearchHint("Search Dishes");
        dBinding.edEventDishes.setEmptyTitle("No Dishes Found!");
        dBinding.edEventDishes.setShowSelectAllButton(true);
        dBinding.edEventDishes.setClearText("Close & Clear");
        dBinding.edEventDishes.setItems(data, items -> {
            for (KeyPairBoolData item : items) {
                selected.add(((Dishes) item.getObject()).getId());
            }
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).isSelected()) {
                    Log.i("TAG", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                }
            }
        });

        dBinding.btnCancel.setOnClickListener(view -> dialog.dismiss());
        dBinding.btnSave.setOnClickListener(view -> {
            String name = dBinding.edEventName.getText().toString();
            String participants = dBinding.edEventParticipants.getText().toString();
            if (name.isEmpty()) {
                dBinding.edEventName.setError("Enter Event Name");
                dBinding.edEventName.requestFocus();
                return;
            }
            if (participants.isEmpty()) {
                dBinding.edEventParticipants.setError("Enter Event Participants");
                dBinding.edEventParticipants.requestFocus();
                return;
            }
            if (selectedTime == 0) {
                dBinding.edEventDate.setError("Select Event Date");
                dBinding.edEventDate.requestFocus();
                return;
            }

            final Map<String, Object> ingredient = new HashMap<>();
            ingredient.put("name", name);
            ingredient.put("participants", Integer.parseInt(participants));
            ingredient.put("date", new Timestamp(selectedTime));
            ingredient.put("dishes", selected);

            String id = UUID.randomUUID().toString();
            ((MainActivity) requireActivity()).database.collection("Events").document(id).set(ingredient, SetOptions.merge()).addOnCompleteListener(task -> {
                Toast.makeText(requireContext(), "Event Added!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to add Event, Please try again!", Toast.LENGTH_SHORT).show());

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

    private void showDatePicker(Runnable callback) {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Event Date").build();
        datePicker.show(getChildFragmentManager(), "tag");

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                selectedTime = (long) selection;
                callback.run();
            }
        });
    }

    private String getFormattedDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(selectedTime);
        return DateFormat.format("dd-MMM-yyyy", cal).toString();
    }

    /**
     * Method to show/hide empty layout.
     */
    private void showHideEmptyLayout() {
        if (MainActivity.dishesList.size() > 0) {
            binding.eventsRv.setVisibility(View.VISIBLE);
            binding.emptyLayout.emptyText.setVisibility(View.GONE);
        } else {
            binding.eventsRv.setVisibility(View.GONE);
            binding.emptyLayout.emptyText.setVisibility(View.VISIBLE);
            binding.emptyLayout.emptyText.setText("No Events Added");
        }
    }

    private void showHideEventLayout(Boolean showEvent) {
        if (showEvent) {
            MainActivity.callBackListener = true;
            binding.listLayout.setVisibility(View.GONE);
            binding.singleEventLayout.setVisibility(View.VISIBLE);
        } else {
            MainActivity.callBackListener = false;
            binding.listLayout.setVisibility(View.VISIBLE);
            binding.singleEventLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEventClickListener(int position) {
        showHideEventLayout(true);
        MainActivity.backPressListener = this;
    }

    @Override
    public void onBackPressListener() {
        showHideEventLayout(false);
    }
}