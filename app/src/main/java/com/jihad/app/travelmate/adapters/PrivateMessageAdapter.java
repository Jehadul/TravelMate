package com.jihad.app.travelmate.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.models.PrivateMessage;
import com.jihad.app.travelmate.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.MessageViewHolder> {

    private static List<PrivateMessage> privateMessageArrayList = new ArrayList<>();
    private LayoutInflater mInflater;
    protected Context context;

    private String currentUserUid;
    private User friend;

    public PrivateMessageAdapter(Context context, List<PrivateMessage> messageArrayList, String currentUserUid, User friend) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        privateMessageArrayList = messageArrayList;
        this.currentUserUid = currentUserUid;
        this.friend = friend;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.card_view_messaging_item, viewGroup, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int position) {

        PrivateMessage message = privateMessageArrayList.get(position);

        messageViewHolder.setData(message);
    }

    @Override
    public int getItemCount() {
        return privateMessageArrayList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        PrivateMessage message;
        private TextView sendText, receiveText, sendTextTime, receiveTextTime;
        private CircleImageView imgUserProfile;

        private int showDate = 0;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            sendText = itemView.findViewById(R.id.private_message_item_send);
            receiveText = itemView.findViewById(R.id.private_message_item_receive);
            imgUserProfile = itemView.findViewById(R.id.private_message_item_receive_img);

            sendTextTime = itemView.findViewById(R.id.private_message_item_send_time);
            receiveTextTime = itemView.findViewById(R.id.private_message_item_receive_time);

            //-----------on click show send or received Date with Time
            sendText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (showDate == 0){
                        showDate = 1;
                        sendTextTime.setVisibility(View.VISIBLE);
                    } else {
                        showDate = 0;
                        sendTextTime.setVisibility(View.GONE);
                    }
                }
            });
            receiveText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (showDate == 0){
                        showDate = 1;
                        receiveTextTime.setVisibility(View.VISIBLE);
                    } else {
                        showDate = 0;
                        receiveTextTime.setVisibility(View.GONE);
                    }
                }
            });
        }

        public void setData(PrivateMessage message) {
            this.message = message;

            if (message.getType().equals("Text")){

                if (currentUserUid.equals(message.getSenderUid())){

                    imgUserProfile.setVisibility(View.GONE);
                    sendText.setVisibility(View.VISIBLE);
                    receiveText.setVisibility(View.GONE);

                    sendText.setText(message.getMessage());
                } else {
                    imgUserProfile.setVisibility(View.VISIBLE);
                    sendText.setVisibility(View.GONE);
                    receiveText.setVisibility(View.VISIBLE);

                    receiveText.setText(message.getMessage());
                    try {
                        if(friend.getImgUrl() !=null){
                            Picasso.get().load(friend.getImgUrl()).placeholder(R.drawable.profile_img).into(imgUserProfile);
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }

                }
                sendTextTime.setText(message.getCreatedDate()+" "+message.getCreatedTime());
                receiveTextTime.setText(message.getCreatedDate()+" "+message.getCreatedTime());
            }
        }

    }


}
