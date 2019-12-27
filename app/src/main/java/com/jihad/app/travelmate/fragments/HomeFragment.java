package com.jihad.app.travelmate.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

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
import java.util.List;

public class HomeFragment extends Fragment {

    /*-------------------------------------------*/
    private MainActivity mainActivity;
    private Context context;
    private RecyclerView recyclerEventView;

    /*------for firebase----------*/
    private FirebaseUser currentUser;
    private DatabaseReference eventRef;

    //-------------------------
    private EventAdapter eventAdapter;
    LinearLayoutManager linearLayoutManager;
    private List<Event> mEventList = new ArrayList<>();

    private TextView tvDate, tvUserLocation, tvUserAddress, tvError;

    //----------for Current Location Update--------
    private final int REQ_CODE = 1;
    private FusedLocationProviderClient fusedLocation;
    private Geocoder geocoder;
    private List<Address> addressList = new ArrayList<>();
    private LocationCallback locationCallback;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view   = inflater.inflate(R.layout.fragment_home, container, false);

        tvDate              = view.findViewById(R.id.tv_home_show_date);
        tvUserLocation      = view.findViewById(R.id.tv_home_user_location);
        tvUserAddress       = view.findViewById(R.id.tv_home_user_address);
        recyclerEventView   = view.findViewById(R.id.recycler_upcoming_event_view_list);
        tvError             = view.findViewById(R.id.tv_home_current_event_error);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        setUserHomeInfoData();

        mainActivity.getSupportActionBar().setTitle(R.string.app_name);
        fusedLocation   = new FusedLocationProviderClient(context);
        geocoder        = new Geocoder(context);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location: locationResult.getLocations()){
                    double lLong = location.getLongitude();
                    double lLat = location.getLatitude();
                    try {
                        addressList     = geocoder.getFromLocation(lLat, lLong, 1);
                        String country  = addressList.get(0).getCountryName();
                        String city     = addressList.get(0).getLocality();
                        String address  = addressList.get(0).getAddressLine(0);

                        tvUserLocation.setText(city+", "+country);
                        tvUserAddress.setText(address);
                    }catch (Exception e){
                    }

                }
            }
        };

        if (checkLocationPermission()){
            getLocationUpdate();
        }

        if(initFirebaseRef()){
            setUpRecyclerEventView();
        }
    }

    private void getLocationUpdate() {
        if (checkLocationPermission()){
            fusedLocation.requestLocationUpdates(getLocationRequest(), locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        if (fusedLocation != null){
            fusedLocation.removeLocationUpdates(locationCallback);
        }
    }


    private boolean checkLocationPermission() {

        int selfPermission= ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        if (selfPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(mainActivity, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION }, REQ_CODE);
            return false;
        }
        return true;
    }

    private LocationRequest getLocationRequest(){
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(8000);

        return request;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_CODE) {
            if (requestCode == REQ_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    getLocationUpdate();
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        stopLocationUpdates();
    }

    private void setUpRecyclerEventView() {

        setEventDataIntoEventList();

        eventAdapter = new EventAdapter(getContext(), mEventList);
        recyclerEventView.setAdapter(eventAdapter);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerEventView.setLayoutManager(linearLayoutManager);
        recyclerEventView.setItemAnimator(new DefaultItemAnimator());

    }

    private void setEventDataIntoEventList() {

        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    mEventList.clear();

                    for (DataSnapshot event : dataSnapshot.getChildren()){

                        Event mEvent = event.getValue(Event.class);
                        if (mEvent.getEndDateInt() >= Functions.getCurrentDateInt()){
                            mEventList.add(mEvent);
                        }
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

    //---------------------------------------------
    private void setUserHomeInfoData() {
        tvDate.setText(Functions.getTodayName()+" "+Functions.currentDateFormat());
    }

    private boolean initFirebaseRef(){

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "You lost your connection", Toast.LENGTH_SHORT).show();
            return false;
        }
        eventRef    = FirebaseDatabase.getInstance().getReference().child("Events").child(currentUser.getUid());
        return true;
    }

}
