package com.example.myversion_proj;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class StudentPanelActivity extends AppCompatActivity {

    Button btnSchedule, btnMarks, btnAssignments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_panel);

        btnSchedule = findViewById(R.id.btnSchedule);
        btnMarks = findViewById(R.id.btnMarks);
        btnAssignments = findViewById(R.id.btnAssignments);

        btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScheduleActivity.class);
            startActivity(intent);
        });

        btnMarks.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewMarksActivity.class);
            startActivity(intent);
        });
//
        btnAssignments.setOnClickListener(v -> {
            Intent intent = new Intent(this, AssignmentsActivity.class);
            startActivity(intent);
        });
    }
}