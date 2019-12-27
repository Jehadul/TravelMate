package com.jihad.app.travelmate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.adapters.PrivateMessageAdapter;
import com.jihad.app.travelmate.app.MainActivity;
import com.jihad.app.travelmate.models.PrivateMessage;
import com.jihad.app.travelmate.models.User;

import java.util.ArrayList;
import java.util.List;


public class MessagingFragment extends Fragment {

    /*-------------------------------------------*/
    private MainActivity mainActivity;
    private Context context;
    private RecyclerView recyclerPrivateMessageView;
    private EditText etMessageText;
    private Button btnMessageSend;

    /*------for firebase----------*/
    private FirebaseUser currentUser;
    protected FirebaseAuth mAuth;
    private DatabaseReference privateMessageRef;

    private PrivateMessageAdapter privateMessageAdapter;
    protected LinearLayoutManager linearLayoutManager;
    private List<PrivateMessage> privateMessageList = new ArrayList<>();

    private User userFriend;

    public MessagingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MessagingFragment newInstance(String param1, String param2) {
        MessagingFragment fragment = new MessagingFragment();
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

        View view = inflater.inflate(R.layout.fragment_messaging, container, false);
        if (getArguments() != null){
            userFriend = (User) getArguments().getSerializable("user");
        }
        recyclerPrivateMessageView = view.findViewById(R.id.recycler_private_message_list);
        etMessageText = view.findViewById(R.id.tv_private_message_text);
        btnMessageSend = view.findViewById(R.id.btn_private_message_send);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (userFriend != null){
            mainActivity.getSupportActionBar().setTitle(userFriend.getName());
        }

        if (initFirebaseRef()){
            setMessageDataIntoMessageList();
            setUpRecyclerEventView();
            saveMessageToFirebase();

            etMessageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerPrivateMessageView.scrollToPosition(privateMessageAdapter.getItemCount()-1);
                }
            });
        }
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

    private boolean initFirebaseRef(){

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "You lost your connection", Toast.LENGTH_SHORT).show();
            return false;
        }
        privateMessageRef = FirebaseDatabase.getInstance().getReference().child("PrivateMessage");
        return true;
    }

    private void setMessageDataIntoMessageList() {

        privateMessageRef.child(currentUser.getUid()).child(userFriend.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    privateMessageList.clear();
                    for (DataSnapshot message : dataSnapshot.getChildren()){
                        privateMessageList.add(message.getValue(PrivateMessage.class));
                    }
                    privateMessageAdapter.notifyDataSetChanged();
                    recyclerPrivateMessageView.scrollToPosition(privateMessageAdapter.getItemCount()-1);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpRecyclerEventView() {

        privateMessageAdapter = new PrivateMessageAdapter(context, privateMessageList, currentUser.getUid(), userFriend);
        recyclerPrivateMessageView.setAdapter(privateMessageAdapter);

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerPrivateMessageView.setLayoutManager(linearLayoutManager);
        recyclerPrivateMessageView.setItemAnimator(new DefaultItemAnimator());
    }

    private void saveMessageToFirebase() {
        btnMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!etMessageText.getText().toString().isEmpty()){

                    String textSms = etMessageText.getText().toString();
                    final String key = privateMessageRef.child(currentUser.getUid()).child(userFriend.getUid()).push().getKey();
                    final PrivateMessage message = new PrivateMessage(key, "Text", currentUser.getUid(), textSms);
                    etMessageText.setText("");

                    privateMessageRef.child(currentUser.getUid()).child(userFriend.getUid()).child(key).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                privateMessageRef.child(userFriend.getUid()).child(currentUser.getUid()).child(key).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        /*if (task.isSuccessful()){

                                        }*/
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }


}
