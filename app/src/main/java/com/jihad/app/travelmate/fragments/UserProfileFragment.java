package com.jihad.app.travelmate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.app.MainActivity;
import com.jihad.app.travelmate.models.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {


    private MessagingListenerFromProfile mMessagingListener;

    private TextView userProfileName, userProfileEmail;
    private ImageView imgGenderMale, imgGenderFemale;
    private CircleImageView imgUserProfile;
    protected Button btnSendMessage;

    /*-------------------------------------------*/
    private MainActivity mainActivity;
    private Context context;
    private User currentUser;

    public UserProfileFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        currentUser         = (User) getArguments().getSerializable("user");
        this.mMessagingListener = (MessagingListenerFromProfile) context;

        imgUserProfile      = view.findViewById(R.id.civ_user_profile_image);
        userProfileName     = view.findViewById(R.id.tv_user_profile_name);
        userProfileEmail    = view.findViewById(R.id.tv_user_profile_email);
        imgGenderMale       = view.findViewById(R.id.iv_user_profile_icon_gender_male);
        imgGenderFemale     = view.findViewById(R.id.iv_user_profile_icon_gender_female);
        btnSendMessage      = view.findViewById(R.id.btn_user_profile_send_message);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressedGoMessagingFragment(currentUser);
            }
        });

        return view;
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

        mainActivity.getSupportActionBar().setTitle(currentUser.getName());
        if (!currentUser.getImgUrl().isEmpty()) {
            Picasso.get().load(currentUser.getImgUrl()).placeholder(R.drawable.profile_img).into(imgUserProfile);
        }
        userProfileName.setText(currentUser.getName());
        userProfileEmail.setText(currentUser.getEmail());
        userProfileName.setText(currentUser.getName());
        if(currentUser.getGender().equals("Male")){
            imgGenderMale.setVisibility(View.VISIBLE);
        } else if(currentUser.getGender().equals("Female")) {
            imgGenderFemale.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMessagingListener = null;
    }

    public void onButtonPressedGoMessagingFragment(User user) {
        if (mMessagingListener != null) {
            mMessagingListener.onMessagingFragmentShowFromProfile(user);
        }
    }

    public interface MessagingListenerFromProfile {
        void onMessagingFragmentShowFromProfile(User user);
    }
}
