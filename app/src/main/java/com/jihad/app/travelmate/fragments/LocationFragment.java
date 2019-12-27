package com.jihad.app.travelmate.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.adapters.PlaceAutocompleteAdapter;
import com.jihad.app.travelmate.app.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class LocationFragment extends Fragment implements OnMapReadyCallback , GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "LocationPermission";

    private MainActivity mainActivity;
    private Context context;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 991;

    private boolean mLocationPermissionGranted = false;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private LocationCallback locationCallback;
    protected FusedLocationProviderClient mLocationProvider;
    private static final float DEFAULT_ZOOM = 15f;

    private AutoCompleteTextView etSearchText;
    private ImageView imSearchBtn, imNearbyPlacesBtn;
    protected PlaceAutocompleteAdapter placeAutocompleteAdapter;
    protected GeoDataClient geoDataClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(20, 92), new LatLng(26, 88));


    //---------------------------
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_PICKER_REQUEST = 1;

    public LocationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
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
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        mapFragment     = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.google_map);
        etSearchText    = view.findViewById(R.id.google_map_search_text);
        imSearchBtn     = view.findViewById(R.id.google_map_search_btn);
        imNearbyPlacesBtn     = view.findViewById(R.id.google_map_nearby_search_btn);

        return view;
    }

    //-----------from GoogleAutoCompleteAdapter-------------
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initSearch(){

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("BD").build();

        geoDataClient = Places.getGeoDataClient(context, null);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(context, geoDataClient, LAT_LNG_BOUNDS, typeFilter);

        etSearchText.setAdapter(placeAutocompleteAdapter);

        etSearchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                geoLocationSearch();
            }
        });
        imSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //---------execute our method for searching ----------
                geoLocationSearch();
            }
        });
    }


    private void geoLocationSearch() {
        String searchText   = etSearchText.getText().toString();
        Geocoder geocoder   = new Geocoder(context);
        List<Address> list  = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchText, 1);
        }catch (Exception e){
            Log.w(TAG, "getDeviceLocation: ", e);
        }
        if (list.size() > 0){
            Address address = list.get(0);

            if (address.getFeatureName() != null & address.getAddressLine(0) !=null){
                moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                        DEFAULT_ZOOM, address.getFeatureName(), address.getAddressLine(0));
            } else {
                moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                        DEFAULT_ZOOM, searchText, searchText);
            }

        } else {
            Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show();
        }
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

        mainActivity.getSupportActionBar().setTitle("Location");

        getLocationPermission();
        initSearch();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /*------------------------------------------------------------*/
    private void getLocationPermission() {

        String[] permission = {FINE_LOCATION, COURSE_LOCATION};

        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(context.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(mainActivity, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(mainActivity, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        switch (requestCode) {

            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted) {

            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            getDeviceLocation();
        }

        imNearbyPlacesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNearbyPlacesDialog();
            }
        });
    }

    private void getDeviceLocation(){
        mLocationProvider = LocationServices.getFusedLocationProviderClient(context);

        try {
            if(mLocationPermissionGranted) {
                Task<Location> location = mLocationProvider.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener<Location>(){

                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location", "");
                            } else {
                                moveCamera(new LatLng(23.750854, 90.393527), DEFAULT_ZOOM, "My Location", "");
                            }
                        }
                    }
                });
            }

        }catch (SecurityException se){
            Log.w(TAG, "getDeviceLocation: ", se);
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title, String address){

        hideSoftKeyboard();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("My Location")){
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title).snippet(address);
            mMap.addMarker(markerOptions);
        }
    }

    private void hideSoftKeyboard(){

        final InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null & getView()!= null){
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    public void showNearbyPlacesDialog() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(mainActivity), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

}
