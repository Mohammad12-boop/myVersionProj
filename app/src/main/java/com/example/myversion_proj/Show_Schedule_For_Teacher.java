package com.example.myversion_proj;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Show_Schedule_For_Teacher extends AppCompatActivity {

    private RecyclerView recycler_schedule;
    private RequestQueue requestQueue;
    private int teacher_id= 1;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_schedule_for_teacher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recycler_schedule= findViewById(R.id.recycler_schedule);
        recycler_schedule.setLayoutManager(new LinearLayoutManager(Show_Schedule_For_Teacher.this));

        getSchedule();
    }

    private void getSchedule() {

        requestQueue = Volley.newRequestQueue(Show_Schedule_For_Teacher.this);
        String url = "http://10.0.2.2/school/getScheduleForTeacher.php?teacher_id=" + teacher_id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                ArrayList<ScheduleModel> scheduleList= new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {

                    ArrayList<ScheduleItems> scheduleItemsList= new ArrayList<>();
                    try {

                        JSONObject obj = response.getJSONObject(i);

                        String day= obj.getString("day_of_week");
                        ScheduleItems scheduleItems= new ScheduleItems(
                                obj.getString("start_time"),
                                obj.getString("end_time"),
                                obj.getString("subject_name"),
                                obj.getString("class_name"),
                                obj.getString("room"));

                        scheduleItemsList.add(scheduleItems);

                        boolean exist= false;
                        for (int j=0; j<scheduleList.size(); j++){

                            if (scheduleList.get(j).getDay().equalsIgnoreCase(day)) {

                                scheduleList.get(j).getItems().add(scheduleItems);
                                exist= true;
                                break;
                            }
                        }

                        if (!exist){

                            scheduleList.add(new ScheduleModel(obj.getString("day_of_week"), scheduleItemsList));
                        }

                        Log.d("Error", scheduleItemsList.get(0).getSubject());
                    } catch (JSONException e) {
                        Log.d("Error", e.toString());
                    }

                }

                DaysAdapter adapter = new DaysAdapter(Show_Schedule_For_Teacher.this,
                        scheduleList);
                recycler_schedule.setAdapter(adapter);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error_json", error.toString());
            }
        });

        requestQueue.add(request);
    }

    public void btBackOnClick(View view) {

        Intent intent = new Intent(Show_Schedule_For_Teacher.this, Teacher_Interfaces.class);
        startActivity(intent);
    }
}