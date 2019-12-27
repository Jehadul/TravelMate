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
import com.jihad.app.travelmate.adapters.FriendsAdapter;
import com.jihad.app.travelmate.app.MainActivity;
import com.jihad.app.travelmate.models.User;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private MainActivity mainActivity;
    private Context context;
    private RecyclerView recyclerUserView;
    private TextView tvError;

    /*------for firebase----------*/
    protected FirebaseUser currentUser;
    protected FirebaseAuth mAuth;
    private DatabaseReference friendsRef;

    private FriendsAdapter friendsAdapter;
    protected LinearLayoutManager linearLayoutManager;
    private List<User> mFriendsList = new ArrayList<>();

    public MessageFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerUserView = view.findViewById(R.id.recycler_friends_view_list);
        tvError = view.findViewById(R.id.tv_message_error);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mainActivity.getSupportActionBar().setTitle("Message");

        boolean init = initFirebaseRef();
        if(init){
            setFriendsDataIntoFriendsList();
            setUpRecyclerFriendsView();
        }
    }

    private boolean initFirebaseRef(){

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "You lost your connection", Toast.LENGTH_SHORT).show();
            return false;
        }
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return true;
    }

    private void setFriendsDataIntoFriendsList() {

        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    mFriendsList.clear();
                    for (DataSnapshot event : dataSnapshot.getChildren()){
                        mFriendsList.add(event.getValue(User.class));
                    }
                    friendsAdapter.notifyDataSetChanged();

                    if (mFriendsList.size()<1){
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

    private void setUpRecyclerFriendsView() {

        friendsAdapter = new FriendsAdapter(context, mFriendsList);
        recyclerUserView.setAdapter(friendsAdapter);

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
