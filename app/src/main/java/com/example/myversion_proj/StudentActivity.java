package com.example.myversion_proj;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
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

public class StudentActivity extends AppCompatActivity {
    private ListView studentListView;
    private StudentAdapter adapter;
    private List<Student> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Initialize views
        studentListView = findViewById(R.id.studentListView);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnAddStudent = findViewById(R.id.btnAddStudent);

        // Set up adapter
        adapter = new StudentAdapter(this, students, this::onEditStudentClick);
        studentListView.setAdapter(adapter);

        // Set click listeners
        btnBack.setOnClickListener(v -> onBackPressed());
        btnAddStudent.setOnClickListener(v -> {
            // Launch AddStudentActivity when button is clicked
            Intent intent = new Intent(StudentActivity.this, AddStudentActivity.class);
            startActivity(intent);
        });
        // Load student data
        loadStudents();
    }


    private void onEditStudentClick(Student student) {
        Intent intent = new Intent(this, EditStudentActivity.class);
        intent.putExtra("STUDENT_ID", student.getId());
        intent.putExtra("STUDENT_NAME", student.getName());
        intent.putExtra("STUDENT_EMAIL", student.getEmail());
        intent.putExtra("STUDENT_PASSWORD", student.getPassword());
        intent.putExtra("STUDENT_CLASS_ID", student.getClassId());
        startActivity(intent);
        loadStudents();
    }

    private void loadStudents() {
        String url = "http://10.0.2.2/school/get_students.php";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            students.clear();
                            JSONArray studentsArray = response.getJSONArray("students");
                            for (int i = 0; i < studentsArray.length(); i++) {
                                JSONObject studentObj = studentsArray.getJSONObject(i);
                                students.add(new Student(
                                        studentObj.getInt("id"),
                                        studentObj.getString("name"),
                                        studentObj.getString("email"),
                                        studentObj.getString("password"),
                                        studentObj.getInt("class_id")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            showError(response.optString("message", "Failed to load students"));
                        }
                    } catch (JSONException e) {
                        showError("Error parsing student data");
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

    private void showAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Student");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_student, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etName);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);
        Spinner spinnerClass = view.findViewById(R.id.spinnerClass);

        // Load classes from database
        loadClasses(spinnerClass, -1);

        // Auto-generate email
        etName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = s.toString().trim().toLowerCase();
                if (!name.isEmpty()) {
                    etEmail.setText(name.replaceAll("\\s+", ".") + "@student.com");
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Add", (DialogInterface.OnClickListener) null);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (d, which) -> d.dismiss());

        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                SchoolClass selectedClass = (SchoolClass) spinnerClass.getSelectedItem();
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validation
                if (name.isEmpty()) {
                    etName.setError("Name is required");
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

                addStudent(name, email, password, selectedClass.getId(), dialog);
            });
        });

        dialog.show();
    }

    private void addStudent(String name, String email, String password, int classId, AlertDialog dialog) {
        String url = "http://10.0.2.2/school/add_student.php";

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding student...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("class_id", classId);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, jsonBody,
                    response -> {
                        progressDialog.dismiss();
                        try {
                            if (response.getString("status").equals("success")) {
                                Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                loadStudents();
                            } else {
                                String errorMsg = response.optString("message", "Failed to add student");
                                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_LONG).show();
                            Log.e("ADD_STUDENT", "JSON error", e);
                        }
                    },
                    error -> {
                        progressDialog.dismiss();
                        String errorMsg = "Network error";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                errorMsg = new String(error.networkResponse.data, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                        Log.e("ADD_STUDENT", "Network error", error);
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
            Log.e("ADD_STUDENT", "JSON creation error", e);
        }
    }

    private void loadClasses(Spinner spinner, int currentClassId) {
        String url = "http://10.0.2.2/school/get_classes.php";

        List<SchoolClass> classes = new ArrayList<>();
        classes.add(new SchoolClass(-1, "Select Class"));

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        int selectedPosition = 0;

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject classObj = response.getJSONObject(i);
                            int classId = classObj.getInt("class_id");
                            String className = classObj.getString("name");

                            classes.add(new SchoolClass(classId, className));

                            if (classId == currentClassId) {
                                selectedPosition = classes.size() - 1;
                            }
                        }

                        int finalSelectedPosition = selectedPosition;
                        runOnUiThread(() -> {
                            ArrayAdapter<SchoolClass> adapter = new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    classes
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                            spinner.setSelection(finalSelectedPosition);
                        });
                    } catch (JSONException e) {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Error parsing class data", Toast.LENGTH_SHORT).show()
                        );
                        Log.e("LOAD_CLASSES", "JSON error", e);
                    }
                },
                error -> {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Error loading classes", Toast.LENGTH_SHORT).show()
                    );
                    Log.e("LOAD_CLASSES", "Network error", error);
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void showError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }
}