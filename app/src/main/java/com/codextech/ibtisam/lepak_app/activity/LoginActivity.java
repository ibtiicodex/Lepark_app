package com.codextech.ibtisam.lepak_app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.SessionManager;
import com.codextech.ibtisam.lepak_app.sync.MyUrls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String LOGIN_EMAIL = "login_email";
    public static final String LOGIN_PASSWORD = "login_password";
    private  EditText emailEditText;
    private  EditText passEditText;
    private static String TAGA="LoginActivity";
    Button btnNext;
    TextView tvsignup;
    ProgressDialog pdLoading;
    private Button btLogin;
    private SessionManager sessionManager;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String email = getIntent().getStringExtra(LoginActivity.LOGIN_EMAIL);
        String password = getIntent().getStringExtra(LoginActivity.LOGIN_PASSWORD);
        btLogin = (Button) findViewById(R.id.btLogin);
        emailEditText = (EditText) findViewById(R.id.edSiteName);
        emailEditText.setText(email);
        passEditText = (EditText) findViewById(R.id.password);
        passEditText.setText(password);
        btnNext = (Button) findViewById(R.id.btnext);
        tvsignup = (TextView) findViewById(R.id.tvsignup);
        pdLoading = new ProgressDialog(this);
        pdLoading.setTitle("Loading data");
        pdLoading.setMessage("Please Wait...");

        sessionManager = new SessionManager(LoginActivity.this);
        queue = Volley.newRequestQueue(LoginActivity.this, new HurlStack());
        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                //  btnNext.setVisibility(View.GONE);
            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailEditText.getText().toString();
                if (!isValidEmail(email)) {
                    //Set error message for email field
                    emailEditText.setError("Invalid Email");
                }
                final String pass = passEditText.getText().toString();
                if (!isValidPassword(pass)) {
                    //Set error message for password field
                    passEditText.setError("Password cannot be empty");
                }
                if (isValidEmail(email) && isValidPassword(pass)) {
                    loginRequest(email, pass);
                    pdLoading.show();
//                    Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
//                    startActivity(intent);
//                    finish();
                }
            }
        });
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

    void loginRequest(final String email, final String password) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, MyUrls.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            int responseCode = obj.getInt("responseCode");
                            if (responseCode == 200) {
                                JSONObject uniObject = obj.getJSONObject("response");
                                String site_id = uniObject.getString("site_id");
                                String site_name = uniObject.getString("site_name");
                                String token = uniObject.getString("token");
                                Log.d(TAG, "onResponse: site_id  :" + site_id);
                                Log.d(TAG, "onResponse: site_name  :" + site_name);
                                Log.d(TAG, "onResponse: token  :" + token);
                                sessionManager.loginSite(site_id, site_name, token, Calendar.getInstance().getTimeInMillis());
                                Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(LoginActivity.this, "User Successfully Login ", Toast.LENGTH_SHORT).show();
                                Toast.makeText(LoginActivity.this, sessionManager.getKeySiteId(), Toast.LENGTH_SHORT).show();
                                if (pdLoading != null && pdLoading.isShowing()) {
                                    pdLoading.dismiss();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        pdLoading.show();
                        Toast.makeText(LoginActivity.this, "Error login ", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdLoading != null && pdLoading.isShowing()) {
            pdLoading.dismiss();
        }
    }
}
