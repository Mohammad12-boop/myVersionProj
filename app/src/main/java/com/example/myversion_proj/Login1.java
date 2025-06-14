package com.example.myversion_proj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login1 extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        requestQueue = Volley.newRequestQueue(this);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            if(email.isEmpty() || pass.isEmpty()){
                Toast.makeText(this, "missing password or email", Toast.LENGTH_SHORT).show();
            }else{
                login(email,pass);
            }
        });


    }

    private void login (String email, String password){

        String url = "http://10.0.2.2/school/login.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String status = obj.getString("status");
                    if(status.equals("success")){
                        String role = obj.getString("role");

                        if(role.equals("teacher")){
                            Intent intent = new Intent(Login1.this, Teacher_Interfaces.class);
                            startActivity(intent);
                        }else if( role.equals("student")){
                            Intent intent = new Intent(Login1.this,StudentPanelActivity.class);
                            startActivity(intent);
                        }else if( role.equals("admin")){
                            Intent intent = new Intent(Login1.this,RegistrarActivity.class);
                            startActivity(intent);
                        }
                        Toast.makeText(Login1.this, "login successful", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(Login1.this, "login fail", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Log.d("error", e.toString());
                }

            }
        },new Response.ErrorListener(){
            public void onErrorResponse(VolleyError error){
                Log.d(error.toString(), "onErrorResponse: error");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailEditText.getText().toString());
                params.put("password", passwordEditText.getText().toString());
                return params;
            }

        };

        requestQueue.add(request);

    }

}