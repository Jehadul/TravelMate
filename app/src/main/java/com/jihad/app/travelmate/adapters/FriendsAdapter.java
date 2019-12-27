package com.jihad.app.travelmate.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    private static List<User> mFriendsList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;

    private MessagingListener mMessagingListener;

    public FriendsAdapter(Context context, List<User> friendsList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        mFriendsList = friendsList;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.card_view_user_item, viewGroup, false);

        this.mMessagingListener = (MessagingListener) context;

        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder friendsViewHolder, int position) {

        User user = mFriendsList.get(position);

        friendsViewHolder.setData(user);
    }

    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        User currentUser;
        private TextView userName, userEmail;
        private ImageView imgGenderMale, imgGenderFemale;
        private CircleImageView imgUserProfile;

        FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_item_name);
            userEmail = itemView.findViewById(R.id.user_item_email);

            imgUserProfile = itemView.findViewById(R.id.user_item_circle_image);
            imgGenderMale = itemView.findViewById(R.id.user_item_gender_male);
            imgGenderFemale = itemView.findViewById(R.id.user_item_gender_female);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goMessagingFragment(currentUser);
                }
            });
        }

        public void setData(User user) {

            this.currentUser = user;
            userName.setText(user.getName());
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
    }

    private void goMessagingFragment(User user) {
        if (mMessagingListener != null) {
            mMessagingListener.onMessagingFragmentShow(user);
        }
    }

    public interface MessagingListener {
        void onMessagingFragmentShow(User user);
    }
}
