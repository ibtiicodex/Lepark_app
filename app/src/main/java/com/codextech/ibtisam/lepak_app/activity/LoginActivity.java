package com.codextech.ibtisam.lepak_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.lepak_app.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Address the email and password field
        emailEditText = (EditText) findViewById(R.id.username);
        passEditText = (EditText) findViewById(R.id.password);
    }

    void api(final String a, final String b) {
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this, new HurlStack());
        String url = "http://192.168.10.100/apiLepak/public/api/employees/authenticate";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(LoginActivity.this, "agyaa ha", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //  Log.d("Error.Response", response);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", a);
                params.put("domain", b);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void checkLogin(View arg0) {

        final String email = emailEditText.getText().toString();
//        if (!isValidEmail(email)) {
//            //Set error message for email field
//            emailEditText.setError("Invalid Email");
//        }

        final String pass = passEditText.getText().toString();
//        if (!isValidPassword(pass)) {
//            //Set error message for password field
//            passEditText.setError("Password cannot be empty");
//        }
//
//        if (isValidEmail(email) && isValidPassword(pass)) {
        api(email, pass);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);

        // Validation Completed
//        }

    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 4) {
            return true;
        }
        return false;
    }
}
