package com.example.myversion_proj;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder>{

    private Context context;
    private List<ScheduleItems> scheduleItemsList;

    public SubjectsAdapter(Context context, List<ScheduleItems> scheduleItemsList){
        this.context = context;
        this.scheduleItemsList = scheduleItemsList;
    }

    @Override
    public SubjectsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_card_subjects,
                parent,
                false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SubjectsAdapter.ViewHolder holder, int position) {

        final ScheduleItems scheduleItem = scheduleItemsList.get(position);
        CardView cardView = holder.cardView;

        Log.d("Error", scheduleItem.getSubject());
        TextView txt1 = (TextView)cardView.findViewById(R.id.txtSubject);
        txt1.setText(scheduleItem.getSubject());

        TextView txt2 = (TextView)cardView.findViewById(R.id.txtClass);
        txt2.setText(scheduleItem.getClassName());

        TextView txt3 = (TextView)cardView.findViewById(R.id.txtRoom);
        txt3.setText(scheduleItem.getRoom());

        TextView txt4 = (TextView)cardView.findViewById(R.id.txtStartEndTime);
        txt4.setText(scheduleItem.getStartTime()+"\n-\n"+scheduleItem.getEndTime());


        cardView.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //
            }
        });

    }

    @Override
    public int getItemCount() {
        return scheduleItemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView cardView){
            super(cardView);
            this.cardView = cardView;
        }

    }
}
