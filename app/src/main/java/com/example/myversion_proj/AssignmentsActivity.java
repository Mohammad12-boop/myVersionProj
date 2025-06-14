package com.example.myversion_proj;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AssignmentsActivity extends AppCompatActivity {

    LinearLayout container;
    Map<String, LinearLayout> subjectSections = new HashMap<>(); // لتجميع واجبات نفس المادة

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);

        container = findViewById(R.id.assignmentsContainer);
        loadAssignments();
    }

    private void loadAssignments() {
        int studentId = getSharedPreferences("student_prefs", MODE_PRIVATE)
                .getInt("student_id", 1); // مؤقتاً ثابت للتجربة

        String url = getString(R.string.server_ip)+"get_assignments_by_student.php?student_id=" + studentId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            String subject = obj.getString("subject");
                            String title = obj.getString("title");
                            String fileUrl = obj.getString("file_url");
                            int assignmentId = obj.getInt("assignment_id");
                            boolean submitted = obj.getBoolean("submitted");

                            addAssignmentView(subject, title, fileUrl, assignmentId, submitted);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        Log.e("json_error", e.toString());
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to load assignments", Toast.LENGTH_SHORT).show();
                    Log.e("volley_error", error.toString());
                });

        queue.add(request);
    }

    private void addAssignmentView(String subject, String title, String fileUrl, int assignmentId, boolean alreadySubmitted) {
        LinearLayout subjectLayout;

        if (!subjectSections.containsKey(subject)) {
            // عنوان المادة
            TextView subjectTitle = new TextView(this);
            subjectTitle.setText(subject);
            subjectTitle.setTextSize(24);
            subjectTitle.setTypeface(null, Typeface.BOLD);
            subjectTitle.setTextColor(Color.parseColor("#4840A3"));
            subjectTitle.setPadding(0, 24, 0, 8);
            container.addView(subjectTitle);

            // قسم خاص بالواجبات الخاصة بالمادة
            subjectLayout = new LinearLayout(this);
            subjectLayout.setOrientation(LinearLayout.VERTICAL);
            container.addView(subjectLayout);
            subjectSections.put(subject, subjectLayout);
        } else {
            subjectLayout = subjectSections.get(subject);
        }

        // صف الواجب
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setWeightSum(2);
        row.setPadding(0, 8, 0, 8);

        // عنوان الواجب
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(16);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        titleView.setTextColor(Color.parseColor("#4840A3"));
        titleView.setPaintFlags(titleView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        titleView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
            startActivity(intent);
        });

        // زر submit
        Context context = new ContextThemeWrapper(AssignmentsActivity.this, R.style.CustomButton);
        Button submitBtn = new Button(context);
        submitBtn.setText(alreadySubmitted ? "Submitted" : "Submit");
        submitBtn.setEnabled(!alreadySubmitted);
        submitBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        if (!alreadySubmitted) {
            submitBtn.setOnClickListener(v -> {
                Intent intent = new Intent(this, SubmitAssignmentActivity.class);
                intent.putExtra("assignment_id", assignmentId);
                startActivity(intent);
            });
        }

        row.addView(titleView);
        row.addView(submitBtn);
        subjectLayout.addView(row);
    }
}


