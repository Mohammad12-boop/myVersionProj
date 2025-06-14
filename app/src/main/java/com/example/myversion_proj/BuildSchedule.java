package com.example.myversion_proj;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class BuildSchedule extends AppCompatActivity {

    private Spinner spnSubjects;
    private RequestQueue requestQueue;
    private HorizontalScrollView sclChoose;
    private CheckBox chSaturday, chSunday, chMonday, chTuesday, chWednesday, chThursday;
    private RadioGroup radioGroupSaturday, radioGroupSunday, radioGroupMonday, radioGroupTuesday, radioGroupWednesday, radioGroupThursday;
    private EditText edtRoom;
    private Button btAddSubjectToSchedule;
    String subject= "", room = "";
    ArrayList<String> day= new ArrayList<>();
    ArrayList<String> startTime= new ArrayList<>();
    ArrayList<String> endTime= new ArrayList<>();
    String valSaturdayStart= "", valSundayStart= "", valMondayStart= "", valTuesdayStart= "", valWednesdayStart= "", valThursdayStart= "";
    String valSaturdayEnd= "", valSundayEnd= "", valMondayEnd= "", valTuesdayEnd= "", valWednesdayEnd= "", valThursdayEnd= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_build_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setUpViews();
        spnsSetDefault();

    }

    private void setUpViews() {

        spnSubjects= findViewById(R.id.spnSubjects2);
        sclChoose= findViewById(R.id.sclChoose);

        chSaturday= findViewById(R.id.chSaturday);
        chSunday= findViewById(R.id.chSunday);
        chMonday= findViewById(R.id.chMonday);
        chTuesday= findViewById(R.id.chTuesday);
        chWednesday= findViewById(R.id.chWednesday);
        chThursday= findViewById(R.id.chThursday);

        radioGroupSaturday= findViewById(R.id.radioGroupSaturday);
        radioGroupSunday= findViewById(R.id.radioGroupSunday);
        radioGroupMonday= findViewById(R.id.radioGroupMonday);
        radioGroupTuesday= findViewById(R.id.radioGroupTuesday);
        radioGroupWednesday= findViewById(R.id.radioGroupWednesday);
        radioGroupThursday= findViewById(R.id.radioGroupThursday);

        edtRoom= findViewById(R.id.edtRoom);

        btAddSubjectToSchedule= findViewById(R.id.btAddSubjectToSchedule);
    }


    private void spnsSetDefault() {

        ArrayList<String> subjectList = new ArrayList<>();
        subjectList.add("Select Subject");

        putSpnSubjects(subjectList);

        spnSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont = ResourcesCompat.getFont(BuildSchedule.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                    sclChoose.setVisibility(View.GONE);
                    edtRoom.setVisibility(View.GONE);
                    subject= "";
                } else {
                    TextView textView = (TextView) view;
                    textView.setTextColor(Color.parseColor("#4840A3"));
                    Typeface customFont = ResourcesCompat.getFont(BuildSchedule.this, R.font.capriola);
                    textView.setTypeface(customFont, Typeface.BOLD);

                    subject= spnSubjects.getSelectedItem().toString();
                    sclChoose.setVisibility(View.VISIBLE);
                    edtRoom.setVisibility(View.VISIBLE);
                    setOnActionsOnView();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setOnActionsOnView() {

        chSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    day.add(chSaturday.getText().toString());
                    radioGroupSaturday.setVisibility(View.VISIBLE);
                    checkConditions();
                    radioGroupSaturday.setOnCheckedChangeListener((group, checkedId) -> checkConditions());

                } else {
                    day.remove(chSaturday.getText().toString());
                    radioGroupSaturday.setVisibility(View.GONE);
                    checkConditions();
                    radioGroupSaturday.clearCheck();
                }

            }
        });

        chSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    day.add(chSunday.getText().toString());
                    radioGroupSunday.setVisibility(View.VISIBLE);
                    checkConditions();
                    radioGroupSunday.setOnCheckedChangeListener((group, checkedId) -> checkConditions());

                } else {
                    day.remove(chSunday.getText().toString());
                    radioGroupSunday.setVisibility(View.GONE);
                    checkConditions();
                    radioGroupSunday.clearCheck();
                }

            }
        });

        chMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    day.add(chMonday.getText().toString());
                    radioGroupMonday.setVisibility(View.VISIBLE);
                    checkConditions();
                    radioGroupMonday.setOnCheckedChangeListener((group, checkedId) -> checkConditions());

                } else {

                    day.remove(chMonday.getText().toString());
                    radioGroupMonday.setVisibility(View.GONE);
                    checkConditions();
                    radioGroupMonday.clearCheck();
                }

            }
        });

        chTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    day.add(chTuesday.getText().toString());
                    radioGroupTuesday.setVisibility(View.VISIBLE);
                    checkConditions();
                    radioGroupTuesday.setOnCheckedChangeListener((group, checkedId) -> checkConditions());

                } else {
                    day.remove(chTuesday.getText().toString());
                    radioGroupTuesday.setVisibility(View.GONE);
                    checkConditions();
                    radioGroupTuesday.clearCheck();
                }

            }
        });

        chWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    day.add(chWednesday.getText().toString());
                    radioGroupWednesday.setVisibility(View.VISIBLE);
                    checkConditions();
                    radioGroupWednesday.setOnCheckedChangeListener((group, checkedId) -> checkConditions());

                } else {
                    day.remove(chWednesday.getText().toString());
                    radioGroupWednesday.setVisibility(View.GONE);
                    checkConditions();
                    radioGroupWednesday.clearCheck();
                }

            }
        });

        chThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    day.add(chThursday.getText().toString());
                    radioGroupThursday.setVisibility(View.VISIBLE);
                    checkConditions();
                    radioGroupThursday.setOnCheckedChangeListener((group, checkedId) -> checkConditions());

                } else {
                    day.remove(chThursday.getText().toString());
                    radioGroupThursday.setVisibility(View.GONE);
                    checkConditions();
                    radioGroupThursday.clearCheck();
                }

            }
        });

        edtRoom.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { checkConditions(); }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    // Method to check all conditions
    private void checkConditions() {

        boolean isChSaturdayChecked = chSaturday.isChecked();
        boolean isChSundayChecked = chSunday.isChecked();
        boolean isChMondayChecked = chMonday.isChecked();
        boolean isChTuesdayChecked = chTuesday.isChecked();
        boolean isChWednesdayChecked = chWednesday.isChecked();
        boolean isChThursdayChecked = chThursday.isChecked();

        boolean isRadioGroupSaturdaySelected = radioGroupSaturday.getCheckedRadioButtonId() != -1;
        if (isRadioGroupSaturdaySelected){

            int selectedId = radioGroupSaturday.getCheckedRadioButtonId();
            RadioButton selectedRadio = findViewById(selectedId);
            String choice = selectedRadio.getText().toString();

            String inf[]= choice.split("-");
            startTime.add(inf[0]);
            valSaturdayStart=inf[0];
            endTime.add(inf[1]);
            valSaturdayEnd= inf[1];
        }
        boolean isRadioGroupSundaySelected = radioGroupSunday.getCheckedRadioButtonId() != -1;
        if (isRadioGroupSundaySelected){

            int selectedId = radioGroupSunday.getCheckedRadioButtonId();
            RadioButton selectedRadio = findViewById(selectedId);
            String choice = selectedRadio.getText().toString();

            String inf[]= choice.split("-");
            startTime.add(inf[0]);
            valSundayStart=inf[0];
            endTime.add(inf[1]);
            valSundayEnd= inf[1];
        }
        boolean isRadioGroupMondaySelected = radioGroupMonday.getCheckedRadioButtonId() != -1;
        if (isRadioGroupMondaySelected){

            int selectedId = radioGroupMonday.getCheckedRadioButtonId();
            RadioButton selectedRadio = findViewById(selectedId);
            String choice = selectedRadio.getText().toString();

            String inf[]= choice.split("-");
            startTime.add(inf[0]);
            valMondayStart=inf[0];
            endTime.add(inf[1]);
            valMondayEnd= inf[1];
        }
        boolean isRadioGroupTuesdaySelected = radioGroupTuesday.getCheckedRadioButtonId() != -1;
        if (isRadioGroupTuesdaySelected){

            int selectedId = radioGroupTuesday.getCheckedRadioButtonId();
            RadioButton selectedRadio = findViewById(selectedId);
            String choice = selectedRadio.getText().toString();

            String inf[]= choice.split("-");
            startTime.add(inf[0]);
            valTuesdayStart=inf[0];
            endTime.add(inf[1]);
            valTuesdayEnd= inf[1];
        }
        boolean isRadioGroupWednesdaySelected = radioGroupWednesday.getCheckedRadioButtonId() != -1;
        if (isRadioGroupWednesdaySelected){

            int selectedId = radioGroupWednesday.getCheckedRadioButtonId();
            RadioButton selectedRadio = findViewById(selectedId);
            String choice = selectedRadio.getText().toString();

            String inf[]= choice.split("-");
            startTime.add(inf[0]);
            valWednesdayStart=inf[0];
            endTime.add(inf[1]);
            valWednesdayEnd= inf[1];
        }
        boolean isRadioGroupThursdaySelected = radioGroupThursday.getCheckedRadioButtonId() != -1;
        if (isRadioGroupThursdaySelected){

            int selectedId = radioGroupThursday.getCheckedRadioButtonId();
            RadioButton selectedRadio = findViewById(selectedId);
            String choice = selectedRadio.getText().toString();

            String inf[]= choice.split("-");
            startTime.add(inf[0]);
            valThursdayStart=inf[0];
            endTime.add(inf[1]);
            valThursdayEnd= inf[1];
        }

        boolean isEditTextFilled = !edtRoom.getText().toString().trim().isEmpty();
        if (isEditTextFilled) {

            room= edtRoom.getText().toString();
        }else {
            room= "";
        }

        Log.d("ConditionsDebug", "CheckBoxes: " + (isChSaturdayChecked || isChSundayChecked || isChMondayChecked
                || isChTuesdayChecked || isChWednesdayChecked || isChThursdayChecked));
        Log.d("ConditionsDebug", "RadioButtons: " + (isRadioGroupSaturdaySelected || isRadioGroupSundaySelected
                || isRadioGroupMondaySelected || isRadioGroupTuesdaySelected
                || isRadioGroupWednesdaySelected || isRadioGroupThursdaySelected));
        Log.d("ConditionsDebug", "EditTextFilled: " + isEditTextFilled);

        // Enable submit button only if all are valid
        if ((isChSaturdayChecked || isChSundayChecked || isChMondayChecked
                || isChTuesdayChecked || isChWednesdayChecked || isChThursdayChecked)
                && (isRadioGroupSaturdaySelected || isRadioGroupSundaySelected
                || isRadioGroupMondaySelected || isRadioGroupTuesdaySelected
                || isRadioGroupWednesdaySelected || isRadioGroupThursdaySelected)
                && isEditTextFilled) {

            Log.d("ConditionsDebug", "All conditions met, showing button.");
            btAddSubjectToSchedule.setVisibility(View.VISIBLE);
        }else {
            Log.d("ConditionsDebug", "Conditions not met, hiding button.");
            btAddSubjectToSchedule.setVisibility(View.GONE);
        }
    }

    private void putSpnSubjects(ArrayList<String> subjectList) {

        requestQueue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2/school/getAllSubjects.php";

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

                    Typeface customFont= ResourcesCompat.getFont(BuildSchedule.this, R.font.capriola);
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

    public void btBackOnClick(View view) {

        Intent intent = new Intent(BuildSchedule.this, RegistrarActivity.class);
        startActivity(intent);
    }

    public void btAddOnClick(View view) {

        for (int i=0; i<startTime.size(); i++){

            Log.d("Error", startTime.get(i));
        }
        String url= "http://10.0.2.2/school/addSubjectToSchedule.php";
        StringRequest request= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("Tag", "RESPONSE IS " + response);
                //on  below line we are displaying a success toast message.
                Toast.makeText(BuildSchedule.this, " " + response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // method to handle errors.
                Toast.makeText(BuildSchedule.this, " " + error.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("subject_name", subject);
                params.put("room", room);

                JSONArray scheduleArray = new JSONArray();
                for (int i = 0; i < day.size(); i++) {
                    try {
                        JSONObject dayObj = new JSONObject();
                        dayObj.put("day", day.get(i));
                        dayObj.put("start_time", startTime.get(i)); // مثال: ["08:00", "10:00", ...]
                        dayObj.put("end_time", endTime.get(i));     // تأكد أنهم بنفس الطول
                        scheduleArray.put(dayObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                params.put("schedule", scheduleArray.toString());

                // at last we are returning our params.
                return params;
            }
        };

        // below line is to make
        // a json object request.
        requestQueue.add(request);
    }

}