package com.example.myversion_proj;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleActivity extends AppCompatActivity {

    TableLayout tableLayout;
    List<String> days = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday");
    List<String> hours = Arrays.asList("08:00", "09:00", "10:00", "11:00", "12:00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        tableLayout = findViewById(R.id.tableLayout);
        loadSchedule();
    }

    private void loadSchedule() {
        int studentId = getSharedPreferences("student_prefs", MODE_PRIVATE)
                .getInt("student_id", 1);

        String url = getString(R.string.server_ip) + "get_schedule_by_student.php?student_id=" + studentId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Map<String, Map<String, JSONObject>> scheduleMap = new HashMap<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String day = obj.getString("day");
                            String start = obj.getString("time_start");

                            scheduleMap.computeIfAbsent(day, k -> new HashMap<>());
                            scheduleMap.get(day).put(start, obj);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    buildTable(scheduleMap);
                },
                error -> {
                    Toast.makeText(this, "Failed to load schedule", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void buildTable(Map<String, Map<String, JSONObject>> scheduleMap) {
        // Header row
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.parseColor("#D0E9FF"));
        headerRow.addView(makeHeaderCell("Day", true));
        for (String hour : hours) {
            headerRow.addView(makeHeaderCell(hour, false));
        }
        tableLayout.addView(headerRow);

        for (String day : days) {
            TableRow row = new TableRow(this);
            row.setPadding(0, 24, 0, 24);

            TextView dayCell = makeHeaderCell(day, true);
            TableRow.LayoutParams dayParams = new TableRow.LayoutParams(340, 160);
            dayParams.setMargins(0, 0, 12, 0);
            dayCell.setLayoutParams(dayParams);
            row.addView(dayCell);

            int hourIndex = 0;
            while (hourIndex < hours.size()) {
                String currentHour = hours.get(hourIndex);
                JSONObject cellData = scheduleMap.getOrDefault(day, new HashMap<>()).get(currentHour);

                if (cellData != null) {
                    String subject = cellData.optString("subject_name", "...");
                    String teacher = cellData.optString("teacher_name", "");
                    String start = cellData.optString("time_start");
                    String end = cellData.optString("time_end");

                    int span = getHourSpan(start, end);
                    TextView cell = makeCell(subject + "\n" + teacher);

                    GradientDrawable bg = new GradientDrawable();
                    bg.setCornerRadius(16);
                    bg.setColor(getColorForSubject(subject));
                    cell.setBackground(bg);

                    TableRow.LayoutParams params = new TableRow.LayoutParams(250 * span, 160);
                    params.span = span;
                    cell.setLayoutParams(params);

                    row.addView(cell);
                    hourIndex += span;
                } else {
                    TextView breakCell = makeCell("Break");
                    breakCell.setTextColor(Color.GRAY);
                    breakCell.setBackgroundColor(Color.parseColor("#FAFAFA"));
                    row.addView(breakCell);
                    hourIndex++;
                }
            }

            tableLayout.addView(row);

            // Divider row
            View divider = new View(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, 3);
            params.setMargins(0, 16, 0, 16);
            divider.setLayoutParams(params);
            divider.setBackgroundColor(Color.GRAY);
            tableLayout.addView(divider);
        }
    }

    private int getHourSpan(String start, String end) {
        try {
            int startHour = Integer.parseInt(start.split(":" )[0]);
            int endHour = Integer.parseInt(end.split(":" )[0]);
            return Math.max(1, endHour - startHour);
        } catch (Exception e) {
            return 1;
        }
    }

    private TextView makeHeaderCell(String text, boolean isDayColumn) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(24, 24, 24, 24);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(18);
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundColor(Color.parseColor("#D0E9FF"));
        tv.setLayoutParams(new TableRow.LayoutParams(
                isDayColumn ? 340 : 250,
                160
        ));
        return tv;
    }

    private TextView makeCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(24, 24, 24, 24);
        tv.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        tv.setTextSize(15);
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundColor(Color.WHITE);
        tv.setLayoutParams(new TableRow.LayoutParams(250, 160));
        return tv;
    }

    private int getColorForSubject(String subject) {
        switch (subject.toLowerCase()) {
            case "math": return Color.parseColor("#90CAF9");
            case "english": return Color.parseColor("#A5D6A7");
            case "arabic": return Color.parseColor("#FFCC80");
            case "science": return Color.parseColor("#CE93D8");
            case "history": return Color.parseColor("#B0BEC5");
            default: return Color.parseColor("#E1F5FE");
        }
    }
}
