package com.example.myversion_proj;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    private Context context;
    private List<ScheduleModel> scheduleList;

    public TestAdapter(Context context, List<ScheduleModel> scheduleList){
        this.context = context;
        this.scheduleList = scheduleList;
    }

    @Override
    public TestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.test,
                    parent,
                    false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TestAdapter.ViewHolder holder, int position) {

        final ScheduleModel scheduleModel = scheduleList.get(position);
        List<ScheduleItems> scheduleItems = scheduleList.get(position).getItems();
        CardView cardView = holder.cardView;

        TextView txt = (TextView) cardView.findViewById(R.id.txtDay);
        txt.setText(scheduleModel.getDay());

        RecyclerView rv= (RecyclerView) cardView.findViewById(R.id.rvPeriods);
        rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        SubjectsAdapter adapter = new SubjectsAdapter(context, scheduleItems);
        rv.setAdapter(adapter);

        cardView.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //
            }
        });

    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(CardView cardView){
            super(cardView);
            this.cardView = cardView;
        }

    }
}
