package com.example.myversion_proj;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SubmitAssignmentActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;
    private Uri fileUri;
    private TextView txtFile;
    private int assignmentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_assignment);

        assignmentId = getIntent().getIntExtra("assignment_id", -1);
        txtFile = findViewById(R.id.txtFileName);

        Button selectBtn = findViewById(R.id.btnSelectFile);
        Button finishBtn = findViewById(R.id.btnFinish);

        selectBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE);
        });

        finishBtn.setOnClickListener(v -> {
            if (fileUri != null && assignmentId != -1) {
                finishBtn.setEnabled(false);
                finishBtn.setText("Uploading...");
                uploadFile(fileUri);
            } else {
                Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            txtFile.setText(fileUri.getLastPathSegment());
        }
    }

    private void uploadFile(Uri uri) {
        int studentId = getSharedPreferences("student_prefs", MODE_PRIVATE)
                .getInt("student_id", 1);

        String url = getString(R.string.server_ip)+"upload_submission_file.php"; // <-- غير IP إذا لزم

        byte[] fileBytes = readBytesFromUri(uri);
        if (fileBytes == null) {
            Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
            return;
        }

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                com.android.volley.Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "File submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    Toast.makeText(this, "File submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("student_id", String.valueOf(studentId));
                params.put("assignment_id", String.valueOf(assignmentId));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file", new DataPart("answer.pdf", fileBytes, "application/pdf"));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(multipartRequest);
    }

    private byte[] readBytesFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
