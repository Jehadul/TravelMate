package com.jihad.app.travelmate.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.adapters.EventDetailsAdapter;
import com.jihad.app.travelmate.app.MainActivity;
import com.jihad.app.travelmate.common.Functions;
import com.jihad.app.travelmate.models.Event;
import com.jihad.app.travelmate.models.EventExpense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventDetailsFragment extends Fragment implements View.OnClickListener{

    private MainActivity mainActivity;
    private Context context;
    protected FloatingActionButton fabAddExpenseDetails;
    private RecyclerView recyclerExpenseDetailsView;
    private ProgressBar progressBarExpenseStatus;
    private TextView tvEventDestination, tvEventStartDate, tvEventEndDate, tvBudgetStatus, tvProgressPercentage, tvError;
    private double totalExpense;

    private EventDetailsAdapter eventDetailsAdapter;
    protected LinearLayoutManager linearLayoutManager;
    private List<EventExpense> mEventExpenseList = new ArrayList<>();
    private Event currentEvent;
    private EventExpense currentEventExpense;

    /*-----Date Picker from Calender*/
    private int todayYear=2020;
    private int todayMonth=6;
    private int todayDay=15;
    DatePickerDialog datePickerDialog;

    /*----------custom dialog----------*/
    private Dialog eventDetailsModalDialog;
    /*-----modal dialog finds------*/
    private TextView tvModalTitle, tvExpenseDate;
    private EditText etExpenseAmount, etExpenseType, etExpenseDescription;
    private Button btnExpenseSave, btnExpenseUpdate, btnExpenseDelete;

    /*------for firebase----------*/
    protected FirebaseUser currentUser;
    protected FirebaseAuth mAuth;
    private DatabaseReference eventExpenseRef;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EventDetailsFragment newInstance(String param1, String param2) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        fabAddExpenseDetails = view.findViewById(R.id.fab_add_event_expense_details);
        recyclerExpenseDetailsView = view.findViewById(R.id.recycler_event_expense_details_list);
        progressBarExpenseStatus = view.findViewById(R.id.pb_event_detail_progress_bar);

        tvEventDestination = view.findViewById(R.id.tv_event_detail_destination);
        tvEventStartDate = view.findViewById(R.id.tv_event_detail_start_date);
        tvEventEndDate = view.findViewById(R.id.tv_event_detail_end_date);
        tvBudgetStatus = view.findViewById(R.id.tv_event_detail_budget_status);
        tvProgressPercentage = view.findViewById(R.id.tv_event_detail_progress_percentage);
        tvError = view.findViewById(R.id.tv_event_details_error);
        fabAddExpenseDetails.setOnClickListener(this);

        if (getArguments() != null){
            currentEvent = (Event) getArguments().getSerializable("event");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mainActivity.getSupportActionBar().setTitle(currentEvent.getName());

        if(initFirebaseRef()){
            setEventExpenseDataIntoList();
            setUpRecyclerEventView();
        }
    }

    private void setEventExpenseDataIntoList() {

        eventExpenseRef.child(currentEvent.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    mEventExpenseList.clear();
                    totalExpense = 0.0;

                    for (DataSnapshot expense : dataSnapshot.getChildren()){
                        mEventExpenseList.add(expense.getValue(EventExpense.class));
                        totalExpense += expense.getValue(EventExpense.class).getAmount();
                    }
                    eventDetailsAdapter.notifyDataSetChanged();

                    tvBudgetStatus.setText(String.valueOf(totalExpense)+"/"+currentEvent.getBudget());
                    double progress = (totalExpense/currentEvent.getBudget())*100;
                    progressBarExpenseStatus.setProgress((int) progress);
                    tvProgressPercentage.setText(String.valueOf((int)progress)+"%");

                    if (mEventExpenseList.size()<1){
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

        eventDetailsAdapter = new EventDetailsAdapter(getContext(), mEventExpenseList, currentEvent);
        recyclerExpenseDetailsView.setAdapter(eventDetailsAdapter);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerExpenseDetailsView.setLayoutManager(linearLayoutManager);
        recyclerExpenseDetailsView.setItemAnimator(new DefaultItemAnimator());

        tvEventDestination.setText(currentEvent.getDestination());
        tvEventStartDate.setText("Start "+currentEvent.getStartDate());
        tvEventEndDate.setText("End "+currentEvent.getEndDate());
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
            case R.id.fab_add_event_expense_details:
                onClickFloatingButtonShowModal();
                break;
            case R.id.et_expense_details_date:
                getDateFromDatePicker();
                break;
            case R.id.btn_expense_details_save:
                saveExpenseDataIntoFirebase();
                break;
            case R.id.btn_expense_details_update:
                updateExpenseDataIntoFirebase();
                break;
            case R.id.btn_expense_details_delete:
                deleteExpenseDataFromFirebase();
                break;

        }
    }

    private void onClickFloatingButtonShowModal() {

        eventDetailsModalDialog = new Dialog(context);

        /*-------initialise all modal elements------*/
        initAllDialogModalElements(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //--------call some material design APIs here
            tvModalTitle.setText("Add Expense");

        } else { // for Below API 21
            //---------Implement this feature without material design
            tvModalTitle.setVisibility(View.GONE);
            eventDetailsModalDialog.setTitle("Add Expense");
        }

        tvExpenseDate.setText(Functions.currentDateFormat());
        eventDetailsModalDialog.show();
    }

    private void getDateFromDatePicker() {

        datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        todayYear    = year;
                        todayMonth   = month;
                        todayDay     = day;

                        tvExpenseDate.setText(Functions.printDateFormat(day, month, year));
                    }
                }, todayYear, todayMonth, todayDay);

        datePickerDialog.show();
    }


    private void saveExpenseDataIntoFirebase() {

        boolean isEmpty = Functions.isEmptySetError(etExpenseAmount, etExpenseType, etExpenseDescription);

        if (!isEmpty){

            double amount = Double.parseDouble(etExpenseAmount.getText().toString());

            if ((totalExpense+amount)>currentEvent.getBudget()){
                Toast.makeText(context, "Expense can't cross the Total Budget", Toast.LENGTH_SHORT).show();
                return;
            }
            String date = tvExpenseDate.getText().toString();
            String type = etExpenseType.getText().toString();
            String desc = etExpenseDescription.getText().toString();

            String key = eventExpenseRef.child(currentEvent.getUid()).push().getKey();

            EventExpense expense = new EventExpense(key, amount, date, type, desc);

            eventExpenseRef.child(currentEvent.getUid()).child(key).setValue(expense);

            eventDetailsModalDialog.dismiss();
        }
    }

    private void updateExpenseDataIntoFirebase() {

        boolean isEmpty = Functions.isEmptySetError(etExpenseAmount, etExpenseType, etExpenseDescription);

        if (!isEmpty){

            double amount = Double.parseDouble(etExpenseAmount.getText().toString());

            if ((totalExpense+amount-currentEventExpense.getAmount())>currentEvent.getBudget()){
                Toast.makeText(context, "Expense can't cross the Total Budget", Toast.LENGTH_SHORT).show();
                return;
            }
            String date = tvExpenseDate.getText().toString();
            String type = etExpenseType.getText().toString();
            String desc = etExpenseDescription.getText().toString();

            EventExpense eventExpense = new EventExpense(currentEventExpense.getUid(), amount, date, type, desc, currentEventExpense.getCreatedDate(), currentEventExpense.getCreatedTime());

            eventExpenseRef.child(currentEvent.getUid()).child(currentEventExpense.getUid()).setValue(eventExpense);

            eventDetailsModalDialog.dismiss();
        }
    }

    private void deleteExpenseDataFromFirebase() {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
        deleteDialog.setTitle("Warning !!!");
        deleteDialog.setMessage("Are you sure to delete?");

        deleteDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                eventExpenseRef.child(currentEvent.getUid()).child(currentEventExpense.getUid()).getRef().removeValue();
                eventDetailsModalDialog.dismiss();
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

    private void initAllDialogModalElements(Context context) {

        eventDetailsModalDialog.setContentView(R.layout.modal_event_expense_details);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels*0.60);
        eventDetailsModalDialog.getWindow().setLayout(width, height);

        tvModalTitle = eventDetailsModalDialog.findViewById(R.id.tv_expense_details_modal_title);

        etExpenseAmount = eventDetailsModalDialog.findViewById(R.id.et_expense_details_amount);
        tvExpenseDate = eventDetailsModalDialog.findViewById(R.id.et_expense_details_date);
        etExpenseType = eventDetailsModalDialog.findViewById(R.id.et_expense_details_type);
        etExpenseDescription = eventDetailsModalDialog.findViewById(R.id.et_expense_details_description);

        btnExpenseSave = eventDetailsModalDialog.findViewById(R.id.btn_expense_details_save);
        btnExpenseUpdate = eventDetailsModalDialog.findViewById(R.id.btn_expense_details_update);
        btnExpenseDelete = eventDetailsModalDialog.findViewById(R.id.btn_expense_details_delete);

        Calendar start  = Calendar.getInstance();
        todayYear       = start.get(Calendar.YEAR);
        todayMonth      = start.get(Calendar.MONTH);
        todayDay        = start.get(Calendar.DAY_OF_MONTH);

        tvExpenseDate.setOnClickListener(this);
        btnExpenseSave.setOnClickListener(this);
        btnExpenseUpdate.setOnClickListener(this);
        btnExpenseDelete.setOnClickListener(this);

    }

    private boolean initFirebaseRef(){

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "You lost your connection", Toast.LENGTH_SHORT).show();
            return false;
        }
        eventExpenseRef = FirebaseDatabase.getInstance().getReference().child("EventExpense").child(currentUser.getUid());
        return true;
    }

    public void openModalDialogForEdit(Event currentEvent, EventExpense eventExpense, Context context) {

        this.currentEvent = currentEvent;
        this.currentEventExpense = eventExpense;
        this.context = context;

        initFirebaseRef();

        eventDetailsModalDialog = new Dialog(context);

        /*-------initialise all modal elements------*/
        initAllDialogModalElements(context);

        //------------Check if we're running on android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //--------call some material design APIs here
            tvModalTitle.setText("Edit Expense");

        } else { // for Below API 21
            //---------Implement this feature without material design
            tvModalTitle.setVisibility(View.GONE);
            eventDetailsModalDialog.setTitle("Edit Expense");
        }
        etExpenseAmount.setText(String.valueOf(eventExpense.getAmount()));
        tvExpenseDate.setText(eventExpense.getExpenseDate());
        etExpenseType.setText(eventExpense.getType());
        etExpenseDescription.setText(eventExpense.getDescription());

        btnExpenseSave.setVisibility(View.GONE);
        btnExpenseUpdate.setVisibility(View.VISIBLE);
        btnExpenseDelete.setVisibility(View.VISIBLE);

        eventDetailsModalDialog.show();
    }

}
