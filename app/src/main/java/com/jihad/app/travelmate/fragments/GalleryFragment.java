package com.jihad.app.travelmate.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.jihad.app.travelmate.adapters.MomentAdapter;
import com.jihad.app.travelmate.app.MainActivity;
import com.jihad.app.travelmate.common.Functions;
import com.jihad.app.travelmate.models.Event;
import com.jihad.app.travelmate.models.Moment;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class GalleryFragment extends Fragment implements View.OnClickListener{

    private MainActivity mainActivity;
    private Context context;
    protected FloatingActionButton fabAddMoment;
    private RecyclerView recyclerMomentView;

    /*------for firebase----------*/
    protected FirebaseUser currentUser;
    protected FirebaseAuth mAuth;
    private DatabaseReference momentRef, eventRef;
    private StorageReference momentStorageRef;

    private List<String> eventNameList = new ArrayList<>();

    /*-----modal dialog finds------*/
    private TextView tvModalTitle, tvError;
    private ImageView imgMoment;
    private Spinner spinnerEvents;
    private EditText etMomentRemark;
    protected Button btnMomentSave, btnMomentUpdate, btnMomentDelete;
    private Uri cropImageUri;

    private Dialog momentModalDialog;
    private ProgressDialog loadingBar;

    private MomentAdapter momentAdapter;
    protected LinearLayoutManager linearLayoutManager;
    private List<Moment> mMomentList = new ArrayList<>();
    private Moment currentMoment;

    private final int GAL_PIC_CODE = 111;

    public GalleryFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(String param1, String param2) {
        GalleryFragment fragment = new GalleryFragment();
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
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        fabAddMoment = view.findViewById(R.id.fab_app_moment);
        recyclerMomentView = view.findViewById(R.id.recycler_moment_view_list);
        tvError = view.findViewById(R.id.tv_gallery_error);


        fabAddMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //Toast.makeText(getContext(), "Add Moment", Toast.LENGTH_SHORT).show();
                momentModalDialog = new Dialog(getContext());

                initAllDialogModalElements(context);
                //------------Check if we're running on android 5.0 or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //--------call some material design APIs here
                    tvModalTitle.setText("Add Moment");
                } else { // for Below API 21
                    //---------Implement this feature without material design
                    tvModalTitle.setVisibility(View.GONE);
                    momentModalDialog.setTitle("Add Moment");
                }
                if (eventNameList.size()<1){
                    eventNameList.add("No Event");
                }
                ArrayAdapter<String> eventListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, eventNameList);
                spinnerEvents.setAdapter(eventListAdapter);

                momentModalDialog.show();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mainActivity.getSupportActionBar().setTitle("Gallery");

        if (initFirebaseRef()){
            setEventNameIntoList();
            setMomentDataIntoEventListFromFirebase();
            setUpRecyclerMomentView();
        }
    }

    private void setUpRecyclerMomentView() {

        momentAdapter = new MomentAdapter(getContext(), mMomentList);
        recyclerMomentView.setAdapter(momentAdapter);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerMomentView.setLayoutManager(linearLayoutManager);
        recyclerMomentView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setMomentDataIntoEventListFromFirebase() {
        momentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    mMomentList.clear();
                    for (DataSnapshot moment : dataSnapshot.getChildren()){
                        mMomentList.add(moment.getValue(Moment.class));
                    }
                    momentAdapter.notifyDataSetChanged();

                    if (mMomentList.size()<1){
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

    /*-------initialise all modal elements------*/
    private void initAllDialogModalElements(Context context) {

        loadingBar          = new ProgressDialog(getContext());

        momentModalDialog.setContentView(R.layout.model_moment_add);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels*0.70);
        momentModalDialog.getWindow().setLayout(width, height);

        tvModalTitle    = momentModalDialog.findViewById(R.id.tv_moment_modal_title);
        imgMoment       = momentModalDialog.findViewById(R.id.img_moment_pic);
        spinnerEvents   = momentModalDialog.findViewById(R.id.spin_moment_events);
        etMomentRemark  = momentModalDialog.findViewById(R.id.et_moment_remark);
        btnMomentSave   = momentModalDialog.findViewById(R.id.btn_moment_save);
        btnMomentUpdate = momentModalDialog.findViewById(R.id.btn_moment_update);
        btnMomentDelete = momentModalDialog.findViewById(R.id.btn_moment_delete);

        imgMoment.setOnClickListener(this);
        btnMomentSave.setOnClickListener(this);
        btnMomentUpdate.setOnClickListener(this);
        btnMomentDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_moment_pic:
                addMomentImage();
                break;
            case R.id.btn_moment_save:
                saveMomentDataToFirebase();
                break;
            case R.id.btn_moment_update:
                //updateEventDataToFirebase(currentEvent);
                break;
            case R.id.btn_moment_delete:
                //deleteEventDataToFirebase(currentEvent);
                break;
        }
    }

    private void addMomentImage() {
        //-----runtime permission for device android 6.0 and above
        ActivityCompat.requestPermissions( mainActivity, new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE }, GAL_PIC_CODE );
        CropImage.activity().setAspectRatio(1, 1)
                .setMinCropResultSize(250, 250)
                .start(context, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GAL_PIC_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GAL_PIC_CODE);
            } else {
                Toast.makeText(context, "Don't have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                cropImageUri = result.getUri();
                imgMoment.setImageURI(cropImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
               // Exception error = result.getError();
            }
        }
    }

    private boolean initFirebaseRef(){

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "You lost your connection", Toast.LENGTH_SHORT).show();
            return false;
        }
        momentStorageRef = FirebaseStorage.getInstance().getReference().child("Moments").child(currentUser.getUid());
        momentRef = FirebaseDatabase.getInstance().getReference().child("Moments").child(currentUser.getUid());
        eventRef = FirebaseDatabase.getInstance().getReference().child("Events").child(currentUser.getUid());
        return true;
    }

    private void setEventNameIntoList(){

        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    eventNameList.clear();
                    for (DataSnapshot event : dataSnapshot.getChildren()){
                        eventNameList.add(event.getValue(Event.class).getName());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveMomentDataToFirebase() {

        if (cropImageUri == null){
            Toast.makeText(mainActivity, "Please, Select an Image", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isEmpty = Functions.isEmptySetError(etMomentRemark);
        if (!isEmpty && cropImageUri !=null){

            final String eventName = spinnerEvents.getSelectedItem().toString();
            final String momentRemark = etMomentRemark.getText().toString();

            final String momentUid = momentRef.push().getKey();
            final StorageReference filePath = momentStorageRef.child(momentUid +".jpg");

            loadingBar.setTitle("Upload Moment");
            loadingBar.setMessage("Please wait, your Moment is uploading...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            filePath.putFile(cropImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Moment moment = new Moment(momentUid, uri.toString(), eventName, momentRemark);

                            momentRef.child(momentUid).setValue(moment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        loadingBar.dismiss();
                                        Toast.makeText(context, "Moment uploaded Successfully", Toast.LENGTH_SHORT).show();

                                    } else {
                                        loadingBar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(context, "Error : "+message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@androidx.annotation.NonNull Exception e) {
                    loadingBar.dismiss();
                    String message = e.getMessage();
                    Toast.makeText(context, "Error : "+message, Toast.LENGTH_SHORT).show();
                }
            });

            /*filePath.putFile(cropImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){

                        String downloadedUrl = task.getResult().getDownloadUrl().toString();

                        Moment moment = new Moment(momentUid, downloadedUrl, eventName, momentRemark);

                        momentRef.child(momentUid).setValue(moment).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    loadingBar.dismiss();
                                    Toast.makeText(context, "Moment uploaded Successfully", Toast.LENGTH_SHORT).show();

                                } else {
                                    loadingBar.dismiss();
                                    String message = task.getException().toString();
                                    Toast.makeText(context, "Error : "+message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        loadingBar.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(context, "Error : "+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
            momentModalDialog.dismiss();
        }
    }

    public void deleteGalleryItem(final Moment currentMoment, Context context) {

        initFirebaseRef();

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
        deleteDialog.setTitle("Warning !!!");
        deleteDialog.setMessage("Are you sure to delete?");

        deleteDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                StorageReference desertRef = momentStorageRef.child(currentMoment.getUid() +".jpg");

                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        momentRef.child(currentMoment.getUid()).getRef().removeValue();
                    }
                });
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
}
