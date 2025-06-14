package com.example.myversion_proj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Teacher_Interfaces extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_interfaces);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void btPublishOnClick(View view) {

        Intent intent = new Intent(Teacher_Interfaces.this, Publish_Marks.class);
        startActivity(intent);
    }

    public void btManAssignOnClick(View view) {

        Intent intent = new Intent(Teacher_Interfaces.this, MainActivity.class);
        startActivity(intent);
    }

    public void btViewScheduleOnClick(View view) {

//        Intent intent = new Intent(Teacher_Interfaces.this, BuildSchedule.class);
//        startActivity(intent);

        Intent intent = new Intent(Teacher_Interfaces.this, Show_Schedule_For_Teacher.class);
        startActivity(intent);
    }
}