package com.example.myversion_proj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RepliesAssignment extends AppCompatActivity {

    private List<ReplyCard> replyCardList = new ArrayList<>();
    private RecyclerView recyclerView;

    private Button backBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replies_assignment);

        recyclerView = findViewById(R.id.repliesRecycler);
        backBtn=findViewById(R.id.backBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int assId= getIntent().getIntExtra("assId", -1);

        if(assId != -1){
        studentRepliesView(assId);
        }else{
            Toast.makeText(this, "error in assignment Id", Toast.LENGTH_SHORT).show();
        }

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

    }

    private void studentRepliesView(int assId){
        String url = "http://10.0.2.2/school/getReplies.php?assignment_id=" + assId;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                       int studentId = object.getInt("student_id");
                        String name = object.getString("student_name");
                        String subTime = object.getString("submitted_at");

                        String verticalTime;
                        String [] dateTime = subTime.split(" ");
                        verticalTime = dateTime[0] +"\n"+ dateTime[1];

                        String file = object.getString("file_path");

                        ReplyCard reply = new ReplyCard(studentId,name,verticalTime,file);
                       // ReplyCard reply = new ReplyCard(name,verticalTime,file);

                        replyCardList.add(reply);
                    }


                } catch (JSONException e) {
                    Log.d("error", e.toString());
                }
                ReplyAdapter adapter = new ReplyAdapter(RepliesAssignment.this, replyCardList);
                recyclerView.setAdapter(adapter);
              }
            },new Response.ErrorListener(){
            public void onErrorResponse(VolleyError error){
                Log.d(error.toString(), "onErrorResponse: error");
                }
            }
        );

         Volley.newRequestQueue(this).add(request);

    }

}
