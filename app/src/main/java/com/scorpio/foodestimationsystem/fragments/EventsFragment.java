package com.scorpio.foodestimationsystem.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.SetOptions;
import com.scorpio.foodestimationsystem.MainActivity;
import com.scorpio.foodestimationsystem.Util.CustomDateTimePicker;
import com.scorpio.foodestimationsystem.adapter.EventAdapter;
import com.scorpio.foodestimationsystem.adapter.EventDishesAdapter;
import com.scorpio.foodestimationsystem.databinding.DialogAddEventsBinding;
import com.scorpio.foodestimationsystem.databinding.FragmentEventsBinding;
import com.scorpio.foodestimationsystem.interfaces.BackPressListener;
import com.scorpio.foodestimationsystem.model.Dishes;
import com.scorpio.foodestimationsystem.model.Events;
import com.scorpio.foodestimationsystem.model.Ingredients;
import com.scorpio.foodestimationsystem.service.NotificationUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.scorpio.foodestimationsystem.MainActivity.eventsList;

public class EventsFragment extends Fragment implements EventAdapter.EventClickListener, BackPressListener {

    private FragmentEventsBinding binding = null;

    private long selectedTime = 0;
    private EventAdapter eventAdapter;
    public static ArrayList<Events> selectedList = new ArrayList<>();
    private CustomDateTimePicker customDateTimePicker;

    public boolean isSelectedLayout = false;

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
        ((MainActivity) requireActivity()).binding.appbar.btnDone.setVisibility(View.INVISIBLE);
    }

    private void initFragment() {
        populateEventsRV();
    }

    private void initClickListeners() {
        binding.emptyLayout.btnAddEmpty.setOnClickListener(v -> {
            showAddEventDialog();
        });
        binding.deleteFab.setOnClickListener(v -> {
            ProgressDialog dialog = new ProgressDialog(requireContext());
            dialog.setTitle("Deleting Events");
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);

            for (Events events : selectedList) {
                dialog.show();
                deleteEvents(events, () -> {
                    eventsList.remove(events);
                    eventAdapter.notifyDataSetChanged();
                    setDeleteLayout(false);
                    hideLoading(dialog::dismiss);
                });
            }
        });
    }

    private void populateEventsRV() {
        showHideEmptyLayout();

        eventAdapter = new EventAdapter(eventsList, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
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
            selected.clear();
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

            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "Select Dishes for this event", Toast.LENGTH_SHORT).show();
                return;
            }

            final Map<String, Object> ingredient = new HashMap<>();
            ingredient.put("user_id", ((MainActivity) requireActivity()).currentUser.getUid());
            ingredient.put("name", name);
            ingredient.put("participants", Integer.parseInt(participants));
            ingredient.put("date", new Timestamp(selectedTime));
            ingredient.put("dishes", selected);

            String id = UUID.randomUUID().toString();
            ((MainActivity) requireActivity()).database.collection("Events").document(id).set(ingredient, SetOptions.merge()).addOnCompleteListener(task -> {
                Toast.makeText(requireContext(), "Event Added!", Toast.LENGTH_SHORT).show();

                NotificationUtils notificationUtils = new NotificationUtils(requireContext());
                notificationUtils.setReminder(selectedTime, dBinding.edEventName.getText().toString(), "You have a event coming.");

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
        customDateTimePicker = new CustomDateTimePicker(requireActivity(), new CustomDateTimePicker.ICustomDateTimeListener() {
            @Override
            public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int day, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
                selectedTime = calendarSelected.getTimeInMillis();
                callback.run();
            }

            @Override
            public void onCancel() {
                Toast.makeText(requireContext(), "You need to select event date and time", Toast.LENGTH_SHORT).show();
            }
        });

        customDateTimePicker.set24HourFormat(false);
        customDateTimePicker.setTimeIn12HourFormat(7, 1, true);
        customDateTimePicker.showDialog();
    }

    private String getFormattedDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("hh:mm a dd MMM,yyyy", cal).toString();
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

    private void hideLoading(Runnable callback) {
        if (selectedList.isEmpty()) {
            callback.run();
        }
    }

    private void deleteEvents(Events events, Runnable callback) {
        ((MainActivity) requireActivity()).database.collection("Events").document(events.getId()).delete().addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                callback.run();
            }
        });
    }

    @Override
    public void onEventClickListener(int position) {

        if (isSelectedLayout) {
            if (selectedList.contains(eventsList.get(position))) {
                selectedList.remove(eventsList.get(position));
                if (selectedList.isEmpty()) {
                    setDeleteLayout(false);
                }
            } else {
                selectedList.add(eventsList.get(position));
            }
            eventAdapter.notifyItemChanged(position);
        } else {
            showHideEventLayout(true);

            binding.eventHeading.setText(eventsList.get(position).getName());
            binding.txtEventParticipants.setText(eventsList.get(position).getParticipants() + "");
            binding.txtEventDate.setText(getFormattedDate(eventsList.get(position).getDate() * 1000));

            ArrayList<Dishes> dishes = new ArrayList<>();
            ArrayList<String> dishIds = eventsList.get(position).getDishes();

            for (String id : dishIds) {
                Log.i("TAG", "onEventClickListener: " + id);
                for (Dishes dish : MainActivity.dishesList) {
                    Log.i("TAG", "onEventClickListener 222: " + dish.getId());
                    if (dish.getId().equalsIgnoreCase(id)) {
                        dishes.add(dish);
                        break;
                    }
                }
            }

            EventDishesAdapter adapter = new EventDishesAdapter(dishes);
            binding.eventDishesRv.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.eventDishesRv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        MainActivity.backPressListener = this;
    }

    @Override
    public void onEventLongClickListener(int position) {
        if (!isSelectedLayout) {
            setDeleteLayout(true);
            selectedList.add(eventsList.get(position));
            eventAdapter.notifyItemChanged(position);
        }
    }

    private void setDeleteLayout(Boolean show) {
        if (show) {
            MainActivity.callBackListener = true;
            MainActivity.backPressListener = this;
            binding.deleteFab.setVisibility(View.VISIBLE);
            binding.emptyLayout.btnAddEmpty.setVisibility(View.INVISIBLE);
            isSelectedLayout = true;
        } else {
            MainActivity.callBackListener = false;
            isSelectedLayout = false;
            selectedList.clear();
            binding.deleteFab.setVisibility(View.GONE);
            binding.emptyLayout.btnAddEmpty.setVisibility(View.VISIBLE);
            eventAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressListener() {

        if (isSelectedLayout) {
            setDeleteLayout(false);
        } else {
            showHideEventLayout(false);
        }
    }
}