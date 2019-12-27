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
import com.jihad.app.travelmate.common.Functions;
import com.jihad.app.travelmate.fragments.EventsFragment;
import com.jihad.app.travelmate.models.Event;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{

    private static List<Event> mEventList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;

    private EventDetailsListener mListener;

    public EventAdapter(Context context, List<Event> eventList) {
        mEventList = eventList;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;

        this.mListener = (EventDetailsListener) context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = mInflater.inflate(R.layout.card_view_event_item, viewGroup, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        Event event = mEventList.get(position);
        holder.setData(event);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {

        return mEventList.size();
    }


    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        Event currentEvent;
        TextView name, destination, startDate, budget, createdAt, upcoming, completed, tvCurrent;
        ImageView ivEdit;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.event_item_name);
            destination = itemView.findViewById(R.id.event_item_destination);
            startDate = itemView.findViewById(R.id.event_item_start_date);
            budget = itemView.findViewById(R.id.event_item_budget);
            createdAt = itemView.findViewById(R.id.event_item_create_date);
            upcoming = itemView.findViewById(R.id.event_item_upcoming);
            tvCurrent = itemView.findViewById(R.id.event_item_current);
            completed = itemView.findViewById(R.id.event_item_completed);

            ivEdit = itemView.findViewById(R.id.iv_event_modal_edit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goEventDetailsFragment(currentEvent);
                }
            });
        }

        void setData(Event event) {

            name.setText(event.getName());
            destination.setText(event.getDestination());
            startDate.setText(event.getStartDate());
            budget.setText(String.valueOf(event.getBudget())+" TK");
            createdAt.setText("Created : "+event.getCreatedDate());

            if (event.getStartDateInt() > Functions.getCurrentDateInt()){

                upcoming.setVisibility(View.VISIBLE);
                tvCurrent.setVisibility(View.GONE);
                completed.setVisibility(View.GONE);
            }else {

                if (event.getEndDateInt() >= Functions.getCurrentDateInt()){
                    tvCurrent.setVisibility(View.VISIBLE);
                    upcoming.setVisibility(View.GONE);
                    completed.setVisibility(View.GONE);
                }else {
                    completed.setVisibility(View.VISIBLE);
                    tvCurrent.setVisibility(View.GONE);
                    upcoming.setVisibility(View.GONE);
                }
            }
            this.currentEvent = event;
        }

        void setListeners() {
            ivEdit.setOnClickListener(EventViewHolder.this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_event_modal_edit:
                    new EventsFragment().openModalDialogForEdit(currentEvent, context);
                    break;
            }
        }

    }

    private void goEventDetailsFragment(Event event) {
        if (mListener != null) {
            mListener.onEventDetailsFragmentShow(event);
        }
    }

    public interface EventDetailsListener {
        void onEventDetailsFragmentShow(Event event);
    }
}
