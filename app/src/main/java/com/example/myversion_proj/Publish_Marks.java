package com.example.myversion_proj;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Publish_Marks extends AppCompatActivity {

    private Spinner spnSubjects, spnClasses, spnAssessmentType;
    private ScrollView sclTable;
    private LinearLayout baseLin;
    private RequestQueue requestQueue;
    private EditText edtAssessment_type;
    private TextView txtAssessment_type;
    private RadioGroup radioGroup;
    private RadioButton radio_option1, radio_option2;
    private Button btPublish;
    private int teacher_id= 1;
    private ArrayList<String> grade= new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_publish_marks);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spnSubjects= findViewById(R.id.spnSubjects);
        spnClasses= findViewById(R.id.spnClasses);
        sclTable= findViewById(R.id.sclTable);
        baseLin= findViewById(R.id.baselin);
        edtAssessment_type= findViewById(R.id.edtAssessment_type);
        txtAssessment_type= findViewById(R.id.txtAssessment_type);
        radioGroup= findViewById(R.id.radioGroup);
        radio_option1= findViewById(R.id.radio_option1);
        radio_option2= findViewById(R.id.radio_option2);
        spnAssessmentType= findViewById(R.id.spnAssessmentType);
        btPublish= findViewById(R.id.btPublish);

        spnsSetDefault();

    }

    private void spnsSetDefault() {

        ArrayList<String> subjectList= new ArrayList<>();
        subjectList.add("Select Subject");
        ArrayList<String> classList= new ArrayList<>();
        classList.add("Select Class");
        ArrayList<String> assessmentList= new ArrayList<>();
        classList.add("Select Assessment Type");

        putSpnSubjects(subjectList);

        spnSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont= ResourcesCompat.getFont(Publish_Marks.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                    classList.clear();
                    classList.add("Select Class");
                    spnClasses.setVisibility(View.GONE);

                } else {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont= ResourcesCompat.getFont(Publish_Marks.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                    classList.clear();
                    classList.add("Select Class");
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    putSpnClasses(classList, selectedItem);

                    if (radio_option1.isChecked() || radio_option2.isChecked()) {

                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        RadioButton selectedRadio = findViewById(selectedId);
                        String choice = selectedRadio.getText().toString();
                        if (choice.equalsIgnoreCase("Show_Marks")) {

                            assessmentList.clear();
                            assessmentList.add("Select AssessmentType");
                            putSpnAssessmentTypes(assessmentList, selectedItem);
                        }

                        spnClasses.setVisibility(View.VISIBLE);
                        sclTable.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont= ResourcesCompat.getFont(Publish_Marks.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                    edtAssessment_type.setVisibility(View.GONE);
                    spnAssessmentType.setVisibility(View.GONE);

                } else {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont= ResourcesCompat.getFont(Publish_Marks.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                    if (radio_option1.isChecked() || radio_option2.isChecked()) {

                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        RadioButton selectedRadio = findViewById(selectedId);
                        String choice = selectedRadio.getText().toString();
                        if (choice.equalsIgnoreCase("Publish_Marks")) {

                            edtAssessment_type.setVisibility(View.VISIBLE);
                        }else if (choice.equalsIgnoreCase("Show_Marks")) {

                            spnAssessmentType.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnAssessmentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont= ResourcesCompat.getFont(Publish_Marks.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                } else {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont= ResourcesCompat.getFont(Publish_Marks.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    private void putSpnSubjects(ArrayList<String> subjectList) {

        requestQueue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2/school/getSubjects.php?teacher_id=" + teacher_id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {

                    try {

                        JSONObject obj = response.getJSONObject(i);
                        subjectList.add(obj.getString("name"));

                    } catch (JSONException e) {
                        Log.d("Error", e.toString());
                    }

                }

                setAdapterSubjects(subjectList);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error_json", error.toString());
            }
        });

        requestQueue.add(request);
    }

    private void putSpnClasses(ArrayList<String> classList, String subjectSelected) {

        String url = "http://10.0.2.2/school/getClassesBySubject.php?subject_name="+subjectSelected+"&teacher_id="+teacher_id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {

                    try {

                        JSONObject obj = response.getJSONObject(i);
                        classList.add(obj.getString("class_name"));

                    } catch (JSONException e) {
                        Log.d("Error", e.toString());
                    }

                }

                setAdapterClasses(classList);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error_json", error.toString());
            }
        });

        requestQueue.add(request);
    }

    private void putSpnAssessmentTypes(ArrayList<String> assessmentList, String selectedItem) {

        String url = "http://10.0.2.2/school/getAssessment_Types.php?subject_name="+selectedItem;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {

                    try {

                        JSONObject obj = response.getJSONObject(i);
                        assessmentList.add(obj.getString("assessment_type"));

                    } catch (JSONException e) {
                        Log.d("Error", e.toString());
                    }

                }

                setAdapterAssessmentTypes(assessmentList);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error_json", error.toString());
            }
        });

        requestQueue.add(request);
    }

    private void setAdapterSubjects(ArrayList<String> subjectList) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectList) {

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

                    Typeface customFont= ResourcesCompat.getFont(Publish_Marks.this, R.font.capriola);
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
        spnSubjects.setAdapter(adapter);

    }

    private void setAdapterClasses(ArrayList<String> classList) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classList) {

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

                    Typeface customFont= ResourcesCompat.getFont(Publish_Marks.this, R.font.capriola);
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
        spnClasses.setAdapter(adapter);
    }

    private void setAdapterAssessmentTypes(ArrayList<String> assessmentList) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, assessmentList) {

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

                    Typeface customFont= ResourcesCompat.getFont(Publish_Marks.this, R.font.capriola);
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
        spnAssessmentType.setAdapter(adapter);
    }

    public void btLoadOnClick(View view) {

        if (!(edtAssessment_type.getText().toString().trim().isEmpty()) || spnAssessmentType.getSelectedItemPosition()!=0) {

            if (baseLin.getChildCount() > 3) {
                baseLin.removeViews(2, baseLin.getChildCount() - 3);
            }

            if (radio_option1.isChecked()) {

                txtAssessment_type.setText(edtAssessment_type.getText());
                btPublish.setVisibility(View.VISIBLE);

            }else if (radio_option2.isChecked()) {

                txtAssessment_type.setText(spnAssessmentType.getSelectedItem().toString());
                btPublish.setVisibility(View.GONE);
            }

            String subjectName= spnSubjects.getSelectedItem().toString();
            String classesName = spnClasses.getSelectedItem().toString();

            putStudents(subjectName, classesName);

            Toast.makeText(Publish_Marks.this, "Loading..", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    sclTable.setVisibility(View.VISIBLE);
                }
            }, 3500);

        }else {

            sclTable.setVisibility(View.GONE);
        }

        spnAssessmentType.setVisibility(View.GONE);
        spnClasses.setVisibility(View.GONE);
        spnSubjects.setSelection(0);
        edtAssessment_type.setVisibility(View.GONE);
    }

    private void putStudents(String subjectName, String classesName) {

        String url = "http://10.0.2.2/school/getStudentsInClass.php?class_name=" + classesName + "&subject_name=" + subjectName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                ArrayList<String> studentList= new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {

                    try {

                        JSONObject obj = response.getJSONObject(i);
                        studentList.add(obj.getString("student_name"));

                    } catch (JSONException e) {
                        Log.d("Error", e.toString());
                    }

                }

                studentTable(studentList, subjectName);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error_json", error.toString());
            }
        });

        requestQueue.add(request);
    }

    private void studentTable(ArrayList<String> studentList, String subjectName) {

        for (int i=0; i<studentList.size(); i++) {

            LinearLayout lin= new LinearLayout(Publish_Marks.this);
            lin.setOrientation(LinearLayout.HORIZONTAL);
            lin.setPadding(15, 15, 15, 15);
            lin.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            lin.setLayoutParams(params);

            int widthInDp = 400; // القيمة التي تريدها بوحدة dp
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    widthInDp,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            params2.setMargins(0,15,80,15);


            TextView txtName= new TextView(Publish_Marks.this);
            txtName.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            txtName.setTextColor(Color.parseColor("#4840A3"));
            txtName.setTextSize(15);
            Typeface customFont= ResourcesCompat.getFont(Publish_Marks.this, R.font.capriola);
            txtName.setTypeface(customFont, Typeface.BOLD);
            txtName.setText(studentList.get(i));
            txtName.setLayoutParams(params2);

            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );


            params3.setMargins(200,15,130,15);

            EditText edtMark = new EditText(Publish_Marks.this);
            edtMark.setWidth(130);
            edtMark.setHeight(130);
            edtMark.setBackgroundResource(R.drawable.edittext_background);
            edtMark.setLayoutParams(params3);
            edtMark.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            edtMark.setTextSize(15);
            edtMark.setTypeface(customFont, Typeface.BOLD);
            edtMark.setTextColor(Color.parseColor("#4840A3"));
            edtMark.setInputType(InputType.TYPE_CLASS_NUMBER);

            if (radio_option2.isChecked()){

                edtMark.setEnabled(false);
                showGrade(studentList.get(i), subjectName , spnAssessmentType.getSelectedItem().toString());
            }

            lin.addView(txtName);
            lin.addView(edtMark);

            baseLin.addView(lin, 2);

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (radio_option2.isChecked()) {

                    int j= 0;
                    for (int i = baseLin.getChildCount() - 2; i >= 2; i--) {

                        LinearLayout lin = (LinearLayout) baseLin.getChildAt(i);
                        ((EditText) lin.getChildAt(1)).setText(grade.get(j));
                        j++;
                    }

                }
            }
        }, 3500);
    }

    private void showGrade(String student_name, String subject_name, String assessment_type) {

        String url = "http://10.0.2.2/school/getGradeForStudents.php?student_name=" + student_name + "&subject_name=" + subject_name + "&assessment_type=" +assessment_type;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("Tag", "RESPONSE IS " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    grade.add(jsonObject.getString("grade"));

                } catch (JSONException exception) {

                    Log.d("Error", exception.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Error_json", error.toString());
            }
        });

        requestQueue.add(request);

    }

    public void btPublishOnClick(View view) {

        int count= baseLin.getChildCount();

        String assessment_type= ((TextView)baseLin.getChildAt(0)).getText().toString();
        String subject_name= spnSubjects.getSelectedItem().toString();

        Handler handler = new Handler();
        int delay = 0;

        for (int i=2; i<count-1; i++) {

            View child= baseLin.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) child;

                TextView textView = (TextView) rowLayout.getChildAt(0);
                EditText editText = (EditText) rowLayout.getChildAt(1);
                if (editText.getText().toString().trim().isEmpty()) {

                    editText.setText("0");
                }

                String student_name = textView.getText().toString();
                String mark = editText.getText().toString();

                AddMark(student_name, subject_name, mark, assessment_type);

            }

        }

        sclTable.setVisibility(View.GONE);
        spnSubjects.setSelection(0);
        spnClasses.setVisibility(View.GONE);
        edtAssessment_type.setText("");
        edtAssessment_type.setVisibility(View.GONE);

    }

    private void AddMark(String student_name, String subject_name, String mark, String assessment_type) {

        String url= "http://10.0.2.2/school/addMark.php";
        StringRequest request= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("Tag", "RESPONSE IS " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //on  below line we are displaying a success toast message.
                    String msg = jsonObject.getString("message");
                    Toast.makeText(Publish_Marks.this, msg, Toast.LENGTH_SHORT).show();

                } catch (JSONException exception) {

                    exception.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // method to handle errors.
                Toast.makeText(Publish_Marks.this, "Fail to get response = " +error, Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            public String getBodyContentType() {
                // as we are passing data in the form of url encoded
                // so we are passing the content type below
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams(){

                // below line we are creating a map for storing
                // our values in key and value pair.
                Map<String, String> params= new HashMap<>();

                // on below line we are passing our
                // key and value pair to our parameters.
                params.put("student_name", student_name);
                params.put("subject_name", subject_name);
                params.put("mark", mark.trim());
                params.put("assessment_type", assessment_type);

                // at last we are returning our params.
                return params;
            }
        };

        // below line is to make
        // a json object request.
        requestQueue.add(request);
    }

    public void btBackOnClick(View view) {

        Intent intent = new Intent(Publish_Marks.this, MainActivity.class);
        startActivity(intent);
    }

}