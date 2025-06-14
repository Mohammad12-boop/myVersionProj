package com.example.myversion_proj;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewMarksActivity extends AppCompatActivity {

    LinearLayout layoutMarksContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_marks);

        layoutMarksContainer = findViewById(R.id.layoutMarksContainer);

        loadMarks(); // 🟢 ناديناها هون
    }

    private void loadMarks() {
        int studentId = getSharedPreferences("student_prefs", MODE_PRIVATE)
                .getInt("student_id", 1); // أو رقم ثابت للتجربة

        String url = getString(R.string.server_ip)+"get_marks_by_student.php?student_id=" + studentId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject subjectObj = response.getJSONObject(i);

                            // 🟦 اسم المادة
                            TextView subjectTitle = new TextView(this);
                            subjectTitle.setText(subjectObj.getString("subject_name"));
                            subjectTitle.setTextSize(20);
                            subjectTitle.setTypeface(null, Typeface.BOLD);
                            subjectTitle.setTextColor(Color.parseColor("#2196F3"));
                            subjectTitle.setPadding(0, 24, 0, 8);
                            layoutMarksContainer.addView(subjectTitle);

                            // 🟧
                            JSONArray marksArray = subjectObj.getJSONArray("marks");
                            for (int j = 0; j < marksArray.length(); j++) {
                                JSONObject mark = marksArray.getJSONObject(j);

                                LinearLayout row = new LinearLayout(this);
                                row.setOrientation(LinearLayout.HORIZONTAL);
                                row.setWeightSum(2);
                                row.setPadding(0, 8, 0, 8);

                                TextView label = new TextView(this);
                                label.setText(mark.getString("label"));
                                label.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                label.setTextColor(Color.DKGRAY);

                                TextView value = new TextView(this);
                                value.setText(mark.getString("value"));
                                value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                value.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                                value.setTextColor(Color.BLACK);

                                row.addView(label);
                                row.addView(value);
                                layoutMarksContainer.addView(row);
                            }

                            // 🟨 خط فاصل بين المواد
                            View divider = new View(this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, 2);
                            params.setMargins(0, 16, 0, 16);
                            divider.setLayoutParams(params);
                            divider.setBackgroundColor(Color.LTGRAY);
                            layoutMarksContainer.addView(divider);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to load marks", Toast.LENGTH_SHORT).show();
                    Log.e("volley_error", error.toString());
                });

        queue.add(request);
    }
}
