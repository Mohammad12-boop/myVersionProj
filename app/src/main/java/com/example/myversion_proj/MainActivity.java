package com.example.myversion_proj;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.HORIZONTAL;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    LinearLayout verticalScrollView;
    Button createAssignment;
    RequestQueue requestQueue;
    int teacherId = 1; // change after sara give me the ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_volley);

        verticalScrollView = findViewById(R.id.verticalScrollView);
        createAssignment = findViewById(R.id.createAssignment);
        requestQueue = Volley.newRequestQueue(this);


        createAssignment.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateNewAssignment.class);
            startActivity(intent);
        });

        AllAssignments();
    }

    private void AllAssignments() {

        String url = "http://10.0.2.2/school/getAss.php?teacher_id=" + teacherId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        int assId = obj.getInt("id");
                        String AssignmentTitle = obj.getString("title");
                        int countStudent = obj.getInt("submission_student_count");
                        String createdAssignmentTime = obj.getString("created_at");

                        Log.d("Error", AssignmentTitle);

                        addAssignments(AssignmentTitle,  createdAssignmentTime, countStudent ,assId);

                    } catch (JSONException e) {
                        Log.d("Error", e.toString());
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error_json", error.toString());
            }
        });


        requestQueue.add(request);
    }

    public void addAssignments(String title,String Time, int countStudent, int assId) {

        //assignmnet
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setPadding(7, 15, 0, 15);
        LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(paramss);


        TextView txt1 = new TextView(this);
        txt1.setText(title + "  |  ");
        Typeface customFont = ResourcesCompat.getFont(this, R.font.capriola);
        txt1.setTypeface(customFont, Typeface.BOLD);
        txt1.setTextColor(Color.parseColor("#4840A3"));
//        txt1.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 2));
//        txt1.setPadding(4,0,0,0);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 2f);
        titleParams.setMargins(4, 0, 10, 0);
        txt1.setLayoutParams(titleParams);


        //student count
        TextView txt2 = new TextView(this);
        txt2.setText(String.valueOf(countStudent));
        txt2.setTypeface(customFont, Typeface.BOLD);
        txt2.setTextColor(Color.parseColor("#4840A3"));
        txt2.setTextSize(14);
        LinearLayout.LayoutParams countParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f);
        countParams.setMargins(10, 0, 10, 0);
        txt2.setLayoutParams(countParams);

        // time
        TextView txt3 = new TextView(this);
        txt3.setText(Time);
        txt3.setTypeface(customFont, Typeface.BOLD);
        txt3.setTextColor(Color.parseColor("#4840A3"));
        txt3.setTextSize(13);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 2f);
        timeParams.setMargins(10, 0, 10, 0);
        txt3.setLayoutParams(timeParams);


        Button replayBtn = new Button(this);
        replayBtn.setText("Replies");
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
        );
        params3.setMargins(7, 0, 0, 0);
        replayBtn.setLayoutParams(params3);

        replayBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, RepliesAssignment.class);
            intent.putExtra("assId",assId);
            startActivity(intent);
        });

        linearLayout.addView(txt1);
        linearLayout.addView(txt2);
        linearLayout.addView(txt3);
        linearLayout.addView(replayBtn);

        verticalScrollView.addView(linearLayout);
    }

}