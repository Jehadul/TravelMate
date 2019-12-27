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
import com.jihad.app.travelmate.fragments.GalleryFragment;
import com.jihad.app.travelmate.models.Moment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.MomentViewHolder> {

    private static List<Moment> mMomentList = new ArrayList<>();
    private LayoutInflater mInflater;
    protected Context context;

    public MomentAdapter(Context context, List<Moment> eventList) {
        mMomentList = eventList;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public MomentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.card_view_gallery_item, viewGroup, false);

        return new MomentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MomentViewHolder momentViewHolder, int position) {

        Moment moment = mMomentList.get(position);
        momentViewHolder.setData(moment, position);
    }

    @Override
    public int getItemCount() {
        return mMomentList.size();
    }

    class MomentViewHolder extends RecyclerView.ViewHolder {

        Moment currentMoment;
        ImageView imgMoment;
        TextView eventName, remarkView, dateView;

        MomentViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMoment = itemView.findViewById(R.id.gallery_item_img);
            eventName = itemView.findViewById(R.id.gallery_item_event_name);
            remarkView = itemView.findViewById(R.id.gallery_item_remark);
            dateView = itemView.findViewById(R.id.gallery_item_post_date);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new GalleryFragment().deleteGalleryItem(currentMoment, context);
                    return false;
                }
            });
        }

        void setData(Moment moment, int position) {

            currentMoment = moment;
            Picasso.get().load(moment.getImgUrl()).placeholder(R.drawable.ic_photo).into(imgMoment);
            eventName.setText(moment.getEventName());
            remarkView.setText(moment.getRemark());
            dateView.setText("Posted at: "+moment.getCreatedTime()+" "+moment.getCreatedDate());
        }
    }
}
