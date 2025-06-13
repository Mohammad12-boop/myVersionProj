package com.example.myversion_proj;

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

public class MainActivityVolley extends AppCompatActivity {

    LinearLayout verticalScrollView;

//       TextView AssignmentName;
    Button createAssignment;
    RequestQueue requestQueue;
    int teacherId = 1; // change after sara give me the ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_volley);

//        AssignmentName = findViewById(R.id.AssignmentName);
        verticalScrollView = findViewById(R.id.verticalScrollView);
        createAssignment = findViewById(R.id.createAssignment);
        requestQueue = Volley.newRequestQueue(this);


//        createAssignment.setOnClickListener(view -> {
//            Intent intent = new Intent(this, createAssignment.class);
//            startActivity(intent);
//        });

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

                        String AssignmentTitle = obj.getString("title");
                        int countStudent = obj.getInt("submission_student_count");
                        String createdAssignmentTime = obj.getString("created_at");

                        Log.d("hiiii", AssignmentTitle);

//                      AssignmentName.setText(AssignmentTitle);
                        addAssignments(AssignmentTitle,  createdAssignmentTime, countStudent);

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

    public void addAssignments(String title,String Time, int countStudent) {

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setPadding(2, 15, 0, 15);
        LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linearLayout.setLayoutParams(paramss);


        TextView txt1 = new TextView(this);
        txt1.setText(title+" | ");
        Typeface customFont = ResourcesCompat.getFont(this, R.font.capriola);
        txt1.setTypeface(customFont, Typeface.BOLD);
        txt1.setTextColor(Color.parseColor("#4840A3"));


        TextView txt2 = new TextView(this);
//      txt2.setText(countStudent);
        txt2.setText(String.valueOf(countStudent+" | "));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 0, 0, 0);
        txt2.setLayoutParams(params);
        txt2.setTypeface(customFont, Typeface.BOLD);
        txt2.setTextColor(Color.parseColor("#4840A3"));



        TextView txt3 = new TextView(this);
        txt3.setText(Time+" | ");
//       ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) txt3.getLayoutParams();
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params2.setMargins(10, 0, 0, 0);
        txt3.setLayoutParams(params2);

        txt3.setTypeface(customFont, Typeface.BOLD);
        txt3.setTextColor(Color.parseColor("#4840A3"));

        Button replayBtn = new Button(this);
        replayBtn.setText("Replies");
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params3.setMargins(7, 0, 0, 0);
        replayBtn.setLayoutParams(params3);

//        replayBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(this, RepliesAssignment.class);
//            startActivity(intent);
//        });

        linearLayout.addView(txt1);
        linearLayout.addView(txt2);
        linearLayout.addView(txt3);
        linearLayout.addView(replayBtn);

        verticalScrollView.addView(linearLayout);
    }

}