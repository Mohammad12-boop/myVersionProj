package com.example.myversion_proj;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNewAssignment extends AppCompatActivity {
    private EditText AssTitle;
    private Spinner classSpinner , subjectSpinner;
    private Button uploadFile , submit;
    private TextView fileName;
    RequestQueue requestQueue;
    Uri selectedFileUri = null;
    private Button backBtn;
    List<String> subjectNames = new ArrayList<>();
    List<String> className = new ArrayList<>();
    List<String> classIds = new ArrayList<>();

     int teacher = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_assignment);

        AssTitle = findViewById(R.id.AssTitle);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        classSpinner = findViewById(R.id.classSpinner);
        uploadFile = findViewById(R.id.uploadFile);
        submit = findViewById(R.id.submit);
        fileName = findViewById(R.id.fileName);
        backBtn=findViewById(R.id.backBtn);


        requestQueue = Volley.newRequestQueue(this);

        spnsSetDefault();


        uploadFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, 100);
        });

        submit.setOnClickListener(v -> {
            String title = AssTitle.getText().toString().trim();
            int selectedIndex = classSpinner.getSelectedItemPosition();

            if (classIds.isEmpty()) {
                Toast.makeText(this, "Classes not loaded yet", Toast.LENGTH_SHORT).show();
                return;
            }

            if (title.isEmpty() || selectedFileUri == null || selectedIndex == -1 ||classIds.isEmpty()) {
                Toast.makeText(this, "fill the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedIndex >= classIds.size()) {
                Toast.makeText(this, "Invalid class selection", Toast.LENGTH_SHORT).show();
                return;
            }

            String filePath = selectedFileUri.toString();
            String classId = classIds.get(selectedIndex);

            uploadAssignment(title, filePath, teacher, classId);
        });

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

    }
    private void getSubjects() {
        String url = "http://10.0.2.2/school/getSubjects.php?teacher_id=" + teacher;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            subjectNames.add(obj.getString("name"));
                        }

                        setAdapterSubjects();

                    } catch (JSONException e) {
                        Log.d("SUBJECT_ERR", e.toString());
                    }
                }, error -> Log.d("SUBJECT_ERR", error.toString()));

        requestQueue.add(request);
    }

    private void  getClasses(String selectSubject){
        String url = "http://10.0.2.2/school/getClassesBySubject.php?subject_name=" +selectSubject +"&teacher_id=" +teacher;

        StringRequest request = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>()  {
            @Override
            public void onResponse(String response) {
                    try {
                        JSONArray array = new JSONArray(response);
//                        className.clear();
//                        classIds.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            classIds.add(obj.getString("class_id"));
                            className.add(obj.getString("class_name"));
                          }
                         setAdapterClasses();

                        } catch (JSONException e) {
                        Log.d("Error", e.toString());
                    }
                }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error_json", error.toString());
            }
        });

        Volley.newRequestQueue(this).add(request);
    }

    //open file chooser

    protected void onActivityResult(int requestCode , int resultCode,  @Nullable Intent data){
        super.onActivityResult(requestCode , resultCode , data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            fileName.setText(getSimpleFileName(selectedFileUri));
        }
    }

    private String getSimpleFileName(Uri uri) {
        String path = uri.getPath();
        return path != null ? path.substring(path.lastIndexOf('/') + 1) : "unknown";
    }

    private void uploadAssignment(String title, String filePath, int teacherId, String classId) {
        String url = "http://10.0.2.2/school/postAss2.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(CreateNewAssignment.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreateNewAssignment.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("file_path", filePath);
                params.put("teacher_id", String.valueOf(teacherId));
                params.put("class_id", classId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void spnsSetDefault() {

        subjectNames.add("Select Subject");
        className.add("Select Class");

       getSubjects();

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont= ResourcesCompat.getFont(CreateNewAssignment.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                    className.clear();
                    className.add("Select Class");
                    classSpinner.setVisibility(View.GONE);

                } else {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont= ResourcesCompat.getFont(CreateNewAssignment.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                    className.clear();
                    className.add("Select Class");
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    getClasses(selectedItem);
                    classSpinner.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont= ResourcesCompat.getFont(CreateNewAssignment.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                } else {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont= ResourcesCompat.getFont(CreateNewAssignment.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setAdapterSubjects() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectNames) {

            // تنسيق العنصر عند عرض القائمة المنسدلة (Dropdown)
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                if (view instanceof TextView) {
                    TextView textView = (TextView) view;

                    // تغيير لون النص
                    textView.setTextColor(Color.parseColor("#4840A3"));

                    // تغيير حجم الخط
                    textView.setTextSize(20);

                    if (position == 0) {

                        textView.setBackgroundColor(Color.parseColor("#4840A3"));
                        textView.setTextColor(Color.parseColor("#F9CD6A"));

                    }else {

                        textView.setBackgroundColor(Color.parseColor("#F9CD6A"));
                    }

                    Typeface customFont= ResourcesCompat.getFont(CreateNewAssignment.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                    // إضافة padding داخلي
                    int padding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
                    textView.setPadding(padding, padding, padding, padding);
                }

                return view;
            }

            // تنسيق العنصر الظاهر في Spinner عند الإغلاق
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(18);
                }

                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapter);

    }

    private void setAdapterClasses() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, className) {

            // تنسيق العنصر عند عرض القائمة المنسدلة (Dropdown)
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                if (view instanceof TextView) {
                    TextView textView = (TextView) view;

                    // تغيير لون النص
                    textView.setTextColor(Color.parseColor("#4840A3"));

                    // تغيير حجم الخط
                    textView.setTextSize(20);

                    if (position == 0) {

                        textView.setBackgroundColor(Color.parseColor("#4840A3"));
                        textView.setTextColor(Color.parseColor("#F9CD6A"));

                    }else {

                        textView.setBackgroundColor(Color.parseColor("#F9CD6A"));
                    }

                    Typeface customFont= ResourcesCompat.getFont(CreateNewAssignment.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                    // إضافة padding داخلي
                    int padding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
                    textView.setPadding(padding, padding, padding, padding);
                }

                return view;
            }

            // تنسيق العنصر الظاهر في Spinner عند الإغلاق
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(18);
                }

                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(adapter);
    }




}
