package com.jihad.app.travelmate.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.fragments.EventDetailsFragment;
import com.jihad.app.travelmate.models.Event;
import com.jihad.app.travelmate.models.EventExpense;

import java.util.ArrayList;
import java.util.List;

public class EventDetailsAdapter extends RecyclerView.Adapter<EventDetailsAdapter.DetailsViewHolder> {

    private static List<EventExpense> mEventExpenseList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private Event currentEvent;

    public EventDetailsAdapter(Context context, List<EventExpense> eventExpenseList, Event currentEvent) {

        mEventExpenseList = eventExpenseList;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.currentEvent = currentEvent;
    }

    @NonNull
    @Override
    public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.card_view_event_expense_item, viewGroup, false);

        return new DetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsViewHolder viewHolder, int position) {

        EventExpense eventExpense = mEventExpenseList.get(position);
        viewHolder.setData(eventExpense);
    }

    @Override
    public int getItemCount() {
        return mEventExpenseList.size();
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {

        EventExpense currentEventExpense;

        TextView tvDate, tvType, tvAmount, tvDescription, tvCreatedAt;

        DetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_expense_item_date);
            tvType = itemView.findViewById(R.id.tv_expense_item_type);
            tvAmount = itemView.findViewById(R.id.tv_expense_item_amount);
            tvDescription = itemView.findViewById(R.id.tv_expense_item_description);
            tvCreatedAt = itemView.findViewById(R.id.tv_expense_item_create_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new EventDetailsFragment().openModalDialogForEdit(currentEvent, currentEventExpense, context);
                }
            });
        }

        public void setData(EventExpense eventExpense) {
            this.currentEventExpense = eventExpense;

            tvDate.setText(eventExpense.getExpenseDate());
            tvType.setText(eventExpense.getType());
            tvAmount.setText(String.valueOf(eventExpense.getAmount())+" TK");
            tvDescription.setText(eventExpense.getDescription());
            tvCreatedAt.setText("Created at: "+eventExpense.getCreatedTime()+" "+eventExpense.getCreatedDate());
        }

    }
}
