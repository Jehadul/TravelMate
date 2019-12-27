package com.jihad.app.travelmate.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.activities.LoginActivity;
import com.jihad.app.travelmate.adapters.EventAdapter;
import com.jihad.app.travelmate.adapters.FriendsAdapter;
import com.jihad.app.travelmate.adapters.UserAdapter;
import com.jihad.app.travelmate.common.Functions;
import com.jihad.app.travelmate.fragments.EventDetailsFragment;
import com.jihad.app.travelmate.fragments.EventsFragment;
import com.jihad.app.travelmate.fragments.FriendsFragment;
import com.jihad.app.travelmate.fragments.GalleryFragment;
import com.jihad.app.travelmate.fragments.HomeFragment;
import com.jihad.app.travelmate.fragments.LocationFragment;
import com.jihad.app.travelmate.fragments.MessageFragment;
import com.jihad.app.travelmate.fragments.MessagingFragment;
import com.jihad.app.travelmate.fragments.SettingFragment;
import com.jihad.app.travelmate.fragments.UserProfileFragment;
import com.jihad.app.travelmate.fragments.WeatherFragment;
import com.jihad.app.travelmate.models.Event;
import com.jihad.app.travelmate.models.User;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;


import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements EventAdapter.EventDetailsListener,
        FriendsAdapter.MessagingListener,
        UserAdapter.UserProfileListener,
        UserProfileFragment.MessagingListenerFromProfile {

    private static final int GAL_PIC_CODE = 999;
    /*------for firebase----------*/
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private StorageReference profileStorageRef;

    /*------custom toolbar-----------*/
    Toolbar toolbar;
    private ProgressDialog loadingBar;

    /*------for Navigation Drawer and Fragments--------------------*/
    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Fragment mFragment = null;
    private Class fragmentClass;
    private FragmentTransaction fragmentTransaction;

    /*------Navigation Drawer Header--------------------*/
    private TextView userProfileName, userEmail;
    private ImageView imgGenderMale, imgGenderFemale;
    private CircleImageView imgUserProfile;
    private Uri cropImageUri;

    //public GoogleMap googleMap;


    //-------------google addMob--------------
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (firebaseAuthentication()){
            initFunction();
        }
    }

    private void initFunction() {

        toolbar = findViewById(R.id.app_tool_bar);
        setSupportActionBar(toolbar);

        //------------google addMob-----------------
        MobileAds.initialize(this, "ca-app-pub-6069934421651847~3664092277");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6069934421651847/9080070487");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });


        /*----------navigation drawer open and close------------*/
        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                                                  R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*-------for select home fragment at first time--------*/
        selectHomeMenuAtRunTime();

        loadingBar  = new ProgressDialog(this);

        /*-------navigation item on click call it's related fragment--------*/
        navigationView  = findViewById(R.id.navigation_view);
        setupDrawerContent(navigationView);

        /*----------initial here all navigation----------*/
        View navHeader  = navigationView.getHeaderView(0);
        userProfileName = navHeader.findViewById(R.id.nav_user_profile_name);
        userEmail       = navHeader.findViewById(R.id.nav_user_profile_email);
        imgGenderMale   = navHeader.findViewById(R.id.nav_icon_gender_male);
        imgGenderFemale = navHeader.findViewById(R.id.nav_icon_gender_female);
        imgUserProfile  = navHeader.findViewById(R.id.nav_user_profile_image);

        getUserDataAndSetNavigationHeader();
        imgUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUserProfileImage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            Functions.sendToActivityAsNew(MainActivity.this, LoginActivity.class);
        }
    }

    /*--------set NavigationItemSelectedListener ------------*/
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        selectNavDrawerItem(item);

                        return true;
                    }
                });
    }

    /*----------on click a navigation item return it's Fragment-----------*/
    private void selectNavDrawerItem(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_item_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_item_events:
                fragmentClass = EventsFragment.class;
                break;
            case R.id.nav_item_gallery:
                fragmentClass = GalleryFragment.class;
                break;
            case R.id.nav_item_friends:
                fragmentClass = FriendsFragment.class;
                break;
            case R.id.nav_item_message:
                fragmentClass = MessageFragment.class;
                break;
            case R.id.nav_item_weather:
                fragmentClass = WeatherFragment.class;
                break;
            case R.id.nav_item_location:
                fragmentClass = LocationFragment.class;
                break;
            case R.id.nav_item_setting:
                fragmentClass = SettingFragment.class;
                break;
            case R.id.nav_item_logout:
                userLogout();
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            mFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e){
            e.printStackTrace();
        }

        fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment);
        fragmentTransaction.addToBackStack(item.getTitle().toString());
        fragmentTransaction.commit();

        toolbar.setTitle(item.getTitle());
        mDrawerLayout.closeDrawers();

        hideSoftKeyboard();

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    /*---------on click menu icon Drawer will be open -----------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /*------------Home Navigation Item will be selected and load it's Fragment ---------------*/
    private void selectHomeMenuAtRunTime(){

        fragmentClass = HomeFragment.class;
        try {
            mFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e){
            e.printStackTrace();
        }
        fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment);
        fragmentTransaction.commit();
    }

    private void userLogout() {
        mAuth.signOut();
        Functions.sendToActivityAsNew(MainActivity.this, LoginActivity.class);
    }

    private boolean firebaseAuthentication(){

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Functions.sendToActivityAsNew(MainActivity.this, LoginActivity.class);
            return false;
        }else {
            rootRef = FirebaseDatabase.getInstance().getReference();
            profileStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImg");
            return true;
        }
    }

    private void getUserDataAndSetNavigationHeader() {

        rootRef.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userProfileName.setText(user.getName());
                userEmail.setText(user.getEmail());

                if(user.getGender().equals("Male")){
                    imgGenderMale.setVisibility(View.VISIBLE);
                } else if(user.getGender().equals("Female")) {
                    imgGenderFemale.setVisibility(View.VISIBLE);
                }
                if (!user.getImgUrl().isEmpty()) {
                    Picasso.get().load(user.getImgUrl()).placeholder(R.drawable.profile_img).into(imgUserProfile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUserProfileImage() {
        //-----runtime permission for device android 6.0 and above
        CropImage.activity().setAspectRatio(1, 1)
                .setMinCropResultSize(250, 250)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                cropImageUri = result.getUri();
                saveProfileImageToFirebase();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void saveProfileImageToFirebase() {
        if (cropImageUri !=null){

            StorageReference filePath = profileStorageRef.child(currentUser.getUid()+".jpg");

            loadingBar.setTitle("Upload Profile Image");
            loadingBar.setMessage("Please wait, your Profile Image is uploading...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            final StorageReference storageRef = profileStorageRef.child(currentUser.getUid()+".jpg");
            storageRef.putFile(cropImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).placeholder(R.drawable.profile_img).into(imgUserProfile);

                            rootRef.child("Users").child(currentUser.getUid()).child("imgUrl").setValue(uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        loadingBar.dismiss();
                                        Toast.makeText(MainActivity.this, "Profile Image uploaded Successfully", Toast.LENGTH_SHORT).show();

                                    } else {
                                        loadingBar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(MainActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@androidx.annotation.NonNull Exception e) {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

            /*filePath.putFile(cropImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){

                        String downloadedUrl = task.getResult().getDownloadUrl().toString();

                        Picasso.get().load(downloadedUrl).placeholder(R.drawable.profile_img).into(imgUserProfile);

                        rootRef.child("Users").child(currentUser.getUid()).child("imgUrl").setValue(downloadedUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    loadingBar.dismiss();
                                    Toast.makeText(MainActivity.this, "Profile Image uploaded Successfully", Toast.LENGTH_SHORT).show();

                                } else {
                                    loadingBar.dismiss();
                                    String message = task.getException().toString();
                                    Toast.makeText(MainActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        loadingBar.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(MainActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
        }
    }

    //----------Override from EventAdapter.EventDetailsListener to go Event Details Fragment----------------------
    @Override
    public void onEventDetailsFragmentShow(Event event) {

        fragmentClass = EventDetailsFragment.class;

        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);

        try {
            mFragment = (Fragment) fragmentClass.newInstance();
            mFragment.setArguments(bundle);
        } catch (Exception e){
            e.printStackTrace();
        }
        fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment);
        fragmentTransaction.addToBackStack("event_details");
        fragmentTransaction.commit();
        toolbar.setTitle(event.getName());
    }

    @Override
    public void onMessagingFragmentShow(User user) {
        fragmentClass = MessagingFragment.class;

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        try {
            mFragment = (Fragment) fragmentClass.newInstance();
            mFragment.setArguments(bundle);
        } catch (Exception e){
            e.printStackTrace();
        }
        fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment);
        fragmentTransaction.addToBackStack("messaging");
        fragmentTransaction.commit();
        toolbar.setTitle(user.getName());
    }

    @Override
    public void onUserProfileFragmentShow(User user) {
        fragmentClass = UserProfileFragment.class;

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        try {
            mFragment = (Fragment) fragmentClass.newInstance();
            mFragment.setArguments(bundle);
        } catch (Exception e){
            e.printStackTrace();
        }
        fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment);
        fragmentTransaction.addToBackStack("user_profile");
        fragmentTransaction.commit();
        toolbar.setTitle(user.getName());
    }

    @Override
    public void onMessagingFragmentShowFromProfile(User user) {
        fragmentClass = MessagingFragment.class;

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        try {
            mFragment = (Fragment) fragmentClass.newInstance();
            mFragment.setArguments(bundle);
        } catch (Exception e){
            e.printStackTrace();
        }
        fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment);
        fragmentTransaction.addToBackStack("messaging");
        fragmentTransaction.commit();
        toolbar.setTitle(user.getName());
    }

    private void hideSoftKeyboard(){

        if(getCurrentFocus()!= null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
