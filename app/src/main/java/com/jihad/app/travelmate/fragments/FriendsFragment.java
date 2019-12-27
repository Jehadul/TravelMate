package com.jihad.app.travelmate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.adapters.UserAdapter;
import com.jihad.app.travelmate.app.MainActivity;
import com.jihad.app.travelmate.models.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private MainActivity mainActivity;
    private Context context;
    private RecyclerView recyclerUserView;
    private TextView tvError;

    /*------for firebase----------*/
    protected FirebaseUser currentUser;
    protected FirebaseAuth mAuth;
    private DatabaseReference userRef;

    private UserAdapter userAdapter;
    protected LinearLayoutManager linearLayoutManager;
    private List<User> mUserList = new ArrayList<>();
    private User user;

    public FriendsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerUserView = view.findViewById(R.id.recycler_user_view_list);
        tvError = view.findViewById(R.id.tv_friends_error);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mainActivity.getSupportActionBar().setTitle("Friends");

        if(initFirebaseRef()){
            setUserDataIntoUserList();
            setUpRecyclerUserView();
        }
    }

    private boolean initFirebaseRef(){

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "You lost your connection", Toast.LENGTH_SHORT).show();
            return false;
        }
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return true;
    }

    private void setUserDataIntoUserList() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    mUserList.clear();
                    for (DataSnapshot event : dataSnapshot.getChildren()){
                        mUserList.add(event.getValue(User.class));
                    }
                    userAdapter.notifyDataSetChanged();

                    if (mUserList.size()<1){
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

    private void setUpRecyclerUserView() {

        userAdapter = new UserAdapter(context, mUserList);
        recyclerUserView.setAdapter(userAdapter);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerUserView.setLayoutManager(linearLayoutManager);
        recyclerUserView.setItemAnimator(new DefaultItemAnimator());
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

}
