package com.example.myversion_proj;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>  {

  private Context context; // for activity
  List<ReplyCard> replayCardList; // for all students replies

    public ReplyAdapter(Context context, List<ReplyCard> replayCardList) {
        this.context = context;
        this.replayCardList = replayCardList;
    }

    @NonNull
    @Override
    //convert card_replies to real card replies view
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =   LayoutInflater.from(parent.getContext()).inflate(R.layout.card_replies,parent,false);
        return new ReplyViewHolder(v) ;
    }
    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        ReplyCard replyCard = replayCardList.get(position);

        //holder.studentName.setText("ID: " + replyCard.getStudentId() + " - " + replyCard.getStudentName());
        holder.studentName.setText("Name: " + replyCard.getStudentName());
        holder.studentId.setText("ID: " + replyCard.getStudentId());
        holder.submittedTime.setText("" + replyCard.getSubmittedTime());
        holder.UploadBtn.setOnClickListener(v -> {
            Uri uri = Uri.parse("http://10.0.2.2/school_app/" + replyCard.getFilePath());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return replayCardList.size();
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, submittedTime , studentId;
        Button UploadBtn;
        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            studentId=itemView.findViewById(R.id.studentId);
            submittedTime = itemView.findViewById(R.id.SubmitTime);
            UploadBtn = itemView.findViewById(R.id.fileBtn);

        }
    }
}
