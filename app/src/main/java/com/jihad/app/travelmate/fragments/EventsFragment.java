package com.jihad.app.travelmate.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.adapters.EventAdapter;
import com.jihad.app.travelmate.app.MainActivity;
import com.jihad.app.travelmate.common.Functions;
import com.jihad.app.travelmate.models.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class EventsFragment extends Fragment implements View.OnClickListener{

    private MainActivity mainActivity;
    private Context context;
    protected FloatingActionButton fabAddEvent;
    private RecyclerView recyclerEventView;

    /*-----Date Picker from Calender*/
    private int todayYear=2020;
    private int todayMonth=6;
    private int todayDay=15;
    DatePickerDialog datePickerDialog;
    private int startDateInt = Functions.getCurrentDateInt();
    private int endDateInt = Functions.getCurrentDateInt();

    /*------for firebase----------*/
    protected FirebaseUser currentUser;
    protected FirebaseAuth mAuth;
    private DatabaseReference eventRef, eventExpenseRef;

    /*-----modal dialog finds------*/
    private TextView tvModalTitle, tvStartDate, tvEndDate, tvError;
    private EditText etEventName, etStartLocation, etDestination, etBudget;
    private Button btnEventSave, btnEventUpdate, btnEventDelete;

    private Dialog eventModalDialog;

    private EventAdapter eventAdapter;
    protected LinearLayoutManager linearLayoutManager;
    private List<Event> mEventList = new ArrayList<>();
    private Event currentEvent;

    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events, container, false);
        fabAddEvent = view.findViewById(R.id.fab_add_event);
        tvError = view.findViewById(R.id.tv_events_error);
        recyclerEventView = view.findViewById(R.id.recycler_event_view_list);

        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eventModalDialog = new Dialog(context);
                /*-------initialise all modal elements------*/
                initAllDialogModalElements(context);
                //------------Check if we're running on android 5.0 or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //--------call some material design APIs here
                    tvModalTitle.setText("Add Event");
                } else { // for Below API 21
                    //---------Implement this feature without material design
                    tvModalTitle.setVisibility(View.GONE);
                    eventModalDialog.setTitle("Add Event");
                }
                tvStartDate.setText(Functions.currentDateFormat());
                tvEndDate.setText(Functions.currentDateFormat());
                eventModalDialog.show();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        mainActivity.getSupportActionBar().setTitle("Events");

        if (initFirebaseRef()){
            setEventDataIntoEventList();
            setUpRecyclerEventView();
        }
    }

    private void setEventDataIntoEventList() {

        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    mEventList.clear();
                    for (DataSnapshot event : dataSnapshot.getChildren()){
                        mEventList.add(event.getValue(Event.class));
                    }
                   eventAdapter.notifyDataSetChanged();

                    if (mEventList.size()<1){
                        tvError.setVisibility(View.VISIBLE);
                    } else {
                        tvError.setVisibility(View.INVISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpRecyclerEventView() {

        eventAdapter = new EventAdapter(getContext(), mEventList);
        recyclerEventView.setAdapter(eventAdapter);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerEventView.setLayoutManager(linearLayoutManager);
        recyclerEventView.setItemAnimator(new DefaultItemAnimator());
    }

    /*-------initialise all modal elements------*/
    private void initAllDialogModalElements(Context context) {

        eventModalDialog.setContentView(R.layout.modal_event_add);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels*0.70);
        eventModalDialog.getWindow().setLayout(width, height);

        tvModalTitle = eventModalDialog.findViewById(R.id.tv_event_modal_title);

        etEventName = eventModalDialog.findViewById(R.id.et_event_name);
        etStartLocation = eventModalDialog.findViewById(R.id.et_event_start_location);
        etDestination = eventModalDialog.findViewById(R.id.et_event_destination);
        etBudget = eventModalDialog.findViewById(R.id.et_event_estimated_budget);
        tvStartDate = eventModalDialog.findViewById(R.id.et_event_start_date);
        tvEndDate = eventModalDialog.findViewById(R.id.et_event_end_date);

        btnEventSave = eventModalDialog.findViewById(R.id.btn_event_save);
        btnEventUpdate = eventModalDialog.findViewById(R.id.btn_event_update);
        btnEventDelete = eventModalDialog.findViewById(R.id.btn_event_delete);

        Calendar start  = Calendar.getInstance();
        todayYear       = start.get(Calendar.YEAR);
        todayMonth      = start.get(Calendar.MONTH);
        todayDay        = start.get(Calendar.DAY_OF_MONTH);

        btnEventSave.setOnClickListener(this);
        btnEventUpdate.setOnClickListener(this);
        btnEventDelete.setOnClickListener(this);

        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_event_save:
                saveEventDataToFirebase();
                break;
            case R.id.btn_event_update:
                updateEventDataToFirebase(currentEvent);
                break;
            case R.id.btn_event_delete:
                deleteEventDataToFirebase(currentEvent);
                break;
            case R.id.et_event_start_date:
                getDatePickerCalender(tvStartDate, 1);
                break;
            case R.id.et_event_end_date:
                getDatePickerCalender(tvEndDate, 9);
                break;

        }
    }

    private void deleteEventDataToFirebase(final Event event) {

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
        deleteDialog.setTitle("Warning !!!");
        deleteDialog.setMessage("Are you sure to delete?");

        deleteDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                eventRef.child(event.getUid()).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            eventExpenseRef.child(event.getUid()).getRef().removeValue();
                        }
                    }
                });
                eventModalDialog.dismiss();
            }
        });
        deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        deleteDialog.show();

    }

    private void updateEventDataToFirebase(Event event) {

        boolean isEmpty = Functions.isEmptySetError(etEventName, etStartLocation, etDestination, etBudget);

        if (!isEmpty){

            if (startDateInt>endDateInt){
                Toast.makeText(context, "Start Date can't be after the End Date", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = etEventName.getText().toString();
            String location = etStartLocation.getText().toString();
            String destination = etDestination.getText().toString();
            double budget = Double.parseDouble(etBudget.getText().toString());
            String sDate = tvStartDate.getText().toString();
            String eDate = tvEndDate.getText().toString();

            Event newEvent = new Event(event.getUid(), name, location, destination, budget, sDate, eDate, startDateInt, endDateInt, event.getCreatedDate(), event.getCreatedTime());

            eventRef.child(event.getUid()).setValue(newEvent);

            eventModalDialog.dismiss();
        }
    }

    private void saveEventDataToFirebase() {

        boolean isEmpty = Functions.isEmptySetError(etEventName, etStartLocation, etDestination, etBudget);

        if (!isEmpty){

            if (startDateInt>endDateInt){
                Toast.makeText(context, "Start Date can't be after the End Date", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = etEventName.getText().toString();
            String location = etStartLocation.getText().toString();
            String destination = etDestination.getText().toString();
            double budget = Double.parseDouble(etBudget.getText().toString());
            String sDate = tvStartDate.getText().toString();
            String eDate = tvEndDate.getText().toString();

            String key = eventRef.push().getKey();

            Event event = new Event(key, name, location, destination, budget, sDate, eDate, startDateInt, endDateInt);

            eventRef.child(key).setValue(event);

            eventModalDialog.dismiss();
        }

    }

    public void openModalDialogForEdit(Event event, Context context) {

        this.currentEvent = event;
        this.context = context;

        initFirebaseRef();
        eventModalDialog = new Dialog(context);

        /*-------initialise all modal elements------*/
        initAllDialogModalElements(context);

        //------------Check if we're running on android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //--------call some material design APIs here
            tvModalTitle.setText("Edit Event");

        } else { // for Below API 21
            //---------Implement this feature without material design
            tvModalTitle.setVisibility(View.GONE);
            eventModalDialog.setTitle("Edit Event");
        }
        etEventName.setText(event.getName());
        etStartLocation.setText(event.getStartLocation());
        etDestination.setText(event.getDestination());
        etBudget.setText(String.valueOf(event.getBudget()));
        tvStartDate.setText(event.getStartDate());
        tvEndDate.setText(event.getEndDate());

        startDateInt = event.getStartDateInt();
        endDateInt = event.getEndDateInt();

        btnEventSave.setVisibility(View.GONE);
        btnEventUpdate.setVisibility(View.VISIBLE);
        btnEventDelete.setVisibility(View.VISIBLE);

        eventModalDialog.show();
    }

    private boolean initFirebaseRef(){

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "You lost your connection", Toast.LENGTH_SHORT).show();
            return false;
        }
        eventRef = FirebaseDatabase.getInstance().getReference().child("Events").child(currentUser.getUid());
        eventExpenseRef = FirebaseDatabase.getInstance().getReference().child("EventExpense").child(currentUser.getUid());
        return true;
    }

    /*---------set Today Date from DatePicker and View Result -------------*/
    private void getDatePickerCalender(final TextView textView, final int i){

        datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        todayYear    = year;
                        todayMonth   = month;
                        todayDay     = day;

                        if (i==1){
                            startDateInt = Functions.getDateInt(day, month, year);
                        } else {
                            endDateInt = Functions.getDateInt(day, month, year);
                        }
                        textView.setText(Functions.printDateFormat(day, month, year));
                    }
                }, todayYear, todayMonth, todayDay);

        datePickerDialog.show();
    }

}
