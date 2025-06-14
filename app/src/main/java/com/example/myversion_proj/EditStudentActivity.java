package com.example.myversion_proj;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditStudentActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Spinner spinnerClass;
    private int studentId;
    private List<SchoolClass> classes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword); // New password field
        spinnerClass = findViewById(R.id.spinnerClass);
        Button btnSave = findViewById(R.id.btnSave);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Get student data from intent
        try {
            studentId = getIntent().getIntExtra("STUDENT_ID", -1);
            String name = getIntent().getStringExtra("STUDENT_NAME");
            String email = getIntent().getStringExtra("STUDENT_EMAIL");
            String password = getIntent().getStringExtra("STUDENT_PASSWORD"); // Get password
            int classId = getIntent().getIntExtra("STUDENT_CLASS_ID", -1);

            if (studentId == -1) {
                throw new Exception("Invalid student ID");
            }

            // Set all fields including password
            etName.setText(name);
            etEmail.setText(email);
            etPassword.setText(password);

        } catch (Exception e) {
            Toast.makeText(this, "Error loading student data", Toast.LENGTH_SHORT).show();
            Log.e("EDIT_ACTIVITY", "Intent data error", e);
            finish();
            return;
        }

        // Set listeners
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> updateStudent());

        // Load classes
        loadClasses();
    }

    private void loadClasses() {
        String url = "http://10.0.2.2/school/get_classes.php";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            classes.clear();
                            JSONArray classesArray = response.getJSONArray("classes");
                            for (int i = 0; i < classesArray.length(); i++) {
                                JSONObject classObj = classesArray.getJSONObject(i);
                                classes.add(new SchoolClass(
                                        classObj.getInt("id"),
                                        classObj.getString("name")
                                ));
                            }
                            setupClassSpinner();
                        } else {
                            showError(response.optString("message", "Failed to load classes"));
                        }
                    } catch (JSONException e) {
                        showError("Error parsing class data");
                        Log.e("LOAD_CLASSES", "JSON error", e);
                    }
                },
                error -> {
                    String errorMsg = "Network error";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMsg = new String(error.networkResponse.data);
                    }
                    showError(errorMsg);
                    Log.e("LOAD_CLASSES", "Network error", error);
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }

    private void setupClassSpinner() {
        ArrayAdapter<SchoolClass> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                classes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);

        // Select the current class if available
        int classId = getIntent().getIntExtra("STUDENT_CLASS_ID", -1);
        if (classId != -1) {
            for (int i = 0; i < classes.size(); i++) {
                if (classes.get(i).getId() == classId) {
                    spinnerClass.setSelection(i);
                    break;
                }
            }
        }
    }

    private void updateStudent() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim(); // Get password
        SchoolClass selectedClass = (SchoolClass) spinnerClass.getSelectedItem();

        // Validation
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return;
        }
        if (selectedClass == null || selectedClass.getId() == -1) {
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/school/edit_student.php";

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating student...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("student_id", studentId);
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("password", password); // Include password
            jsonBody.put("class_id", selectedClass.getId());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        progressDialog.dismiss();
                        try {
                            if (response.getString("status").equals("success")) {
                                Toast.makeText(this, "Student updated successfully", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            Log.e("UPDATE_STUDENT", "JSON error", e);
                        }
                    },
                    error -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                        Log.e("UPDATE_STUDENT", "Network error", error);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
            Log.e("UPDATE_STUDENT", "JSON creation error", e);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }
}