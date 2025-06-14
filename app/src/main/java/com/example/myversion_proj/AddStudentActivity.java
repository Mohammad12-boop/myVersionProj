package com.example.myversion_proj;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {
    private EditText etFullName, etEmail, etPassword;
    private Spinner spinnerProgram;
    private Button btnSubmit;
    private List<SchoolClass> classes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPass);
        spinnerProgram = findViewById(R.id.spinnerProgram);
        btnSubmit = findViewById(R.id.btnSubmit);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Auto-generate email when name changes
        etFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                generateEmailFromName();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnBack.setOnClickListener(v -> onBackPressed());
        loadClasses();
        btnSubmit.setOnClickListener(v -> addStudent());
    }

    private void generateEmailFromName() {
        String name = etFullName.getText().toString().trim();
        if (!name.isEmpty()) {
            // Generate email from name
            String email = name.toLowerCase()
                    .replaceAll("\\s+", ".")
                    .replaceAll("[^a-z.]", "") + "@student.edu";
            etEmail.setText(email);
        }
    }

    private void addStudent() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInputs(name, email, password)) {
            return;
        }

        SchoolClass selectedProgram = (SchoolClass) spinnerProgram.getSelectedItem();
        if (selectedProgram == null || selectedProgram.getId() == -1) {
            Toast.makeText(this, "Please select a program", Toast.LENGTH_SHORT).show();
            return;
        }

        addStudentToDatabase(name, email, password, selectedProgram.getId());
    }

    private boolean validateInputs(String name, String email, String password) {
        boolean isValid = true;

        if (name.isEmpty()) {
            etFullName.setError("Name is required");
            isValid = false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            isValid = false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            isValid = false;
        }
        return isValid;
    }

    private void addStudentToDatabase(String name, String email, String password, int programId) {
        btnSubmit.setEnabled(false);

        String url = "http://10.0.2.2/school/add_student.php";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("class_id", programId);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        btnSubmit.setEnabled(true);
                        try {
                            if (response.getString("status").equals("success")) {
                                Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
                                finish(); // Close activity after successful addition
                            } else {
                                showError(response.optString("message", "Failed to add student"));
                            }
                        } catch (JSONException e) {
                            showError("Error parsing server response");
                        }
                    },
                    error -> {
                        btnSubmit.setEnabled(true);
                        showError("Network error: " + error.getMessage());
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
            btnSubmit.setEnabled(true);
            showError("Error creating request");
        }
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
                            JSONArray classesArray = response.getJSONArray("classes");
                            for (int i = 0; i < classesArray.length(); i++) {
                                JSONObject classObj = classesArray.getJSONObject(i);
                                classes.add(new SchoolClass(
                                        classObj.getInt("id"),
                                        classObj.getString("name")
                                ));
                            }
                            setupProgramSpinner();
                        } else {
                            showError(response.optString("message", "Failed to load programs"));
                        }
                    } catch (JSONException e) {
                        showError("Error parsing program data");
                    }
                },
                error -> {
                    String errorMsg = "Network error";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMsg = new String(error.networkResponse.data);
                    }
                    showError(errorMsg);
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }

    private void setupProgramSpinner() {
        ArrayAdapter<SchoolClass> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                classes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProgram.setAdapter(adapter);
    }

    private boolean validateInputs(String name, String email) {
        name = name.trim();
        email = email.trim();

        if (name.isEmpty() || name.length() > 100) {
            etFullName.setError("Name must be 1-100 characters");
            return false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.length() > 255) {
            etEmail.setError("Valid email required (max 255 chars)");
            return false;
        }

        return true;
    }

    private void addStudentToDatabase(String name, String email, int programId) {
        btnSubmit.setEnabled(false);

        // 1. Verify URL is correct
        String url = "http://10.0.2.2/school/add_student.php";
        Log.d("API", "Attempting to POST to: " + url);

        try {
            // 2. Create proper JSON payload
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("class_id", programId);

            Log.d("API", "Request payload: " + jsonBody.toString());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        btnSubmit.setEnabled(true);
                        try {
                            Log.d("API", "Response: " + response.toString());
                            if (response.getString("status").equals("success")) {
                                etFullName.setText("");
                                etEmail.setText("");
                                Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = response.optString("message", "Unknown server error");
                                Log.e("API", "Server error: " + errorMsg);
                                showError(errorMsg);
                            }
                        } catch (JSONException e) {
                            Log.e("API", "JSON parsing error", e);
                            showError("Invalid server response");
                        }
                    },
                    error -> {
                        btnSubmit.setEnabled(true);
                        String errorMsg = "Network error";

                        if (error.networkResponse != null) {
                            errorMsg = "HTTP " + error.networkResponse.statusCode;
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                Log.e("API", "Error response: " + responseBody);
                                errorMsg += " - " + responseBody;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else if (error.getMessage() != null) {
                            errorMsg += ": " + error.getMessage();
                        }

                        Log.e("API", errorMsg, error);
                        showError(errorMsg);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            // 3. Set proper timeout and retry policy
            request.setRetryPolicy(new DefaultRetryPolicy(
                    15000, // 15 seconds timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            // 4. Add to request queue
            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) {
            btnSubmit.setEnabled(true);
            showError("Error creating request data");
            Log.e("API", "JSON creation error", e);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }
}