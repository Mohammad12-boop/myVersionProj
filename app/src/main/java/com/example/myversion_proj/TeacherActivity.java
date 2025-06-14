package com.example.myversion_proj;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class TeacherActivity extends AppCompatActivity {
    private ListView teacherListView;
    private TeacherAdapter adapter;
    private List<Teacher> teachers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        // Initialize views
        teacherListView = findViewById(R.id.teacherListView);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Set up adapter
        adapter = new TeacherAdapter(this, teachers);
        teacherListView.setAdapter(adapter);

        // Set click listeners
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button btnAddTeacher = findViewById(R.id.btnAddTeacher);
        btnAddTeacher.setOnClickListener(v -> showAddTeacherDialog());

        // Load student data
        loadTeachers();
    }

    private void loadTeachers() {
        String url = "http://10.0.2.2/school/get_teachers.php";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            teachers.clear();
                            JSONArray teachersArray = response.getJSONArray("teachers");
                            for (int i = 0; i < teachersArray.length(); i++) {
                                JSONObject teacherObj = teachersArray.getJSONObject(i);
                                teachers.add(new Teacher(
                                        teacherObj.getInt("id"),
                                        teacherObj.getString("name"),
                                        teacherObj.getString("email"),
                                        teacherObj.getString("password")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            showError(response.optString("message", "Failed to load teachers"));
                        }
                    } catch (JSONException e) {
                        showError("Error parsing teacher data");
                        Log.e("API", "JSON parsing error", e);
                    }
                },
                error -> {
                    String errorMsg = "Network error";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMsg = new String(error.networkResponse.data);
                    }
                    showError(errorMsg);
                    Log.e("API", "Network error", error);
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }

    private void showAddTeacherDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Teacher");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_teacher, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etName);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);

        // Auto-generate email when name changes
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = s.toString().trim().toLowerCase();
                if (!name.isEmpty()) {
                    // Generate email: firstname.lastname@teacher.com
                    String email = name.replaceAll("\\s+", ".") + "@teacher.com";
                    etEmail.setText(email);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Name and password are required", Toast.LENGTH_SHORT).show();
                return;
            }

            addTeacher(name, email, password);
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void addTeacher(String name, String email, String password) {
        String url = "http://10.0.2.2/school/add_teacher.php";

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding teacher...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("password", password);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        progressDialog.dismiss();
                        try {
                            if (response.getString("status").equals("success")) {
                                Toast.makeText(this, "Teacher added successfully", Toast.LENGTH_SHORT).show();
                                loadTeachers(); // Refresh the list
                            } else {
                                Toast.makeText(this, "Failed to add teacher: " +
                                        response.optString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Network error: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Error creating request", Toast.LENGTH_LONG).show();
        }
    }

    public void showEditDialog(Teacher teacher) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Teacher");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_teacher, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etName);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);

        etName.setText(teacher.getName());
        etEmail.setText(teacher.getEmail());
        etPassword.setText(teacher.getPassword());

        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedName = etName.getText().toString().trim();
            String updatedEmail = etEmail.getText().toString().trim();
            String updatedPassword = etPassword.getText().toString().trim();

            if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            updateTeacher(teacher.getId(), updatedName, updatedEmail, updatedPassword);
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void updateTeacher(int teacherId, String name, String email, String password) {
        String url = "http://10.0.2.2/school/edit_teacher.php";

        // Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating teacher...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            // Create request body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("teacher_id", teacherId);
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("password", password);

            // Debug log
            Log.d("UPDATE_TEACHER", "Sending: " + jsonBody.toString());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        progressDialog.dismiss();
                        try {
                            Log.d("UPDATE_RESPONSE", response.toString());
                            if (response.getString("status").equals("success")) {
                                Toast.makeText(this, "Teacher updated successfully", Toast.LENGTH_SHORT).show();
                                loadTeachers(); // Refresh the list
                            } else {
                                Toast.makeText(this, "Update failed: " + response.optString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_LONG).show();
                            Log.e("UPDATE_ERROR", "JSON error", e);
                        }
                    },
                    error -> {
                        progressDialog.dismiss();
                        String errorMsg = "Network error";
                        if (error.networkResponse != null) {
                            errorMsg += " (Status: " + error.networkResponse.statusCode + ")";
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                errorMsg += ": " + responseBody;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                        Log.e("UPDATE_ERROR", errorMsg, error);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public byte[] getBody() {
                    try {
                        return jsonBody.toString().getBytes("utf-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e("UPDATE_ERROR", "Encoding error", e);
                        return null;
                    }
                }
            };

            // Set timeout and retry policy
            request.setRetryPolicy(new DefaultRetryPolicy(
                    15000, // 15 seconds timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error creating request", Toast.LENGTH_LONG).show();
            Log.e("UPDATE_ERROR", "JSON creation error", e);
        }
    }

//    public void deleteTeacher(int teacherId) {
//        String url = "http://10.0.2.2/school/delete_teacher.php";
//
//        // Show loading indicator
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Deleting teacher...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        try {
//            // Create JSON payload
//            JSONObject jsonBody = new JSONObject();
//            jsonBody.put("teacher_id", teacherId); // Must match PHP expected parameter
//
//            // Log the request for debugging
//            Log.d("DELETE_REQUEST", "Endpoint: " + url);
//            Log.d("DELETE_REQUEST", "Teacher ID: " + teacherId);
//            Log.d("DELETE_REQUEST", "Request Body: " + jsonBody.toString());
//
//            JsonObjectRequest request = new JsonObjectRequest(
//                    Request.Method.POST,
//                    url,
//                    jsonBody,
//                    response -> {
//                        progressDialog.dismiss();
//                        try {
//                            Log.d("DELETE_RESPONSE", "Raw Response: " + response.toString());
//
//                            if (response.has("status") && response.getString("status").equals("success")) {
//                                // Success case
//                                runOnUiThread(() -> {
//                                    Toast.makeText(this, "teacher deleted successfully", Toast.LENGTH_SHORT).show();
//                                    loadTeachers(); // Refresh the list
//                                });
//                            } else {
//                                // Server returned error
//                                String errorMsg = response.optString("message", "Unknown server error");
//                                Log.e("DELETE_ERROR", "Server error: " + errorMsg);
//                                runOnUiThread(() ->
//                                        Toast.makeText(this, "Delete failed: " + errorMsg, Toast.LENGTH_LONG).show()
//                                );
//                            }
//                        } catch (JSONException e) {
//                            Log.e("DELETE_ERROR", "JSON parsing error", e);
//                            runOnUiThread(() ->
//                                    Toast.makeText(this, "Error parsing server response", Toast.LENGTH_LONG).show()
//                            );
//                        }
//                    },
//                    error -> {
//                        progressDialog.dismiss();
//                        String errorMsg = "Network error";
//
//                        // Detailed error logging
//                        if (error.networkResponse != null) {
//                            errorMsg = "HTTP " + error.networkResponse.statusCode;
//                            try {
//                                String responseBody = new String(error.networkResponse.data, "UTF-8");
//                                errorMsg += " - " + responseBody;
//                                Log.e("DELETE_ERROR", "Full error response: " + responseBody);
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
//                        } else if (error.getMessage() != null) {
//                            errorMsg += ": " + error.getMessage();
//                        }
//
//                        Log.e("DELETE_ERROR", errorMsg, error);
//                        String finalErrorMsg = errorMsg;
//                        runOnUiThread(() ->
//                                Toast.makeText(this, "Delete failed: " + finalErrorMsg, Toast.LENGTH_LONG).show()
//                        );
//                    }
//            ) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Content-Type", "application/json");
//                    headers.put("Accept", "application/json");
//                    return headers;
//                }
//
//                @Override
//                public byte[] getBody() {
//                    try {
//                        return jsonBody.toString().getBytes("utf-8");
//                    } catch (UnsupportedEncodingException e) {
//                        Log.e("DELETE_ERROR", "Encoding error", e);
//                        return null;
//                    }
//                }
//
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//            };
//
//            // Set retry policy
//            request.setRetryPolicy(new DefaultRetryPolicy(
//                    15000, // 15 seconds timeout
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//            ));
//
//            // Add to request queue
//            Volley.newRequestQueue(this).add(request);
//
//        } catch (JSONException e) {
//            progressDialog.dismiss();
//            Log.e("DELETE_ERROR", "JSON creation error", e);
//            Toast.makeText(this, "Error creating delete request", Toast.LENGTH_LONG).show();
//        }
//    }

    private void showError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }
}