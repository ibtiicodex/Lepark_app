package com.codextech.ibtisam.lepak_app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.sync.MyUrls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "Register";
    Button btsignup;
    private Spinner spCityId, spLocation;
    private Button btnSubmit;
    private RequestQueue queue;
    ProgressDialog pdLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        queue = Volley.newRequestQueue(RegisterActivity.this, new HurlStack());
        btsignup = (Button) findViewById(R.id.btsignup);
        spLocation = (Spinner) findViewById(R.id.spLocation);
        spCityId = (Spinner) findViewById(R.id.spCityId);
        pdLoading = new ProgressDialog(this);
        pdLoading.setTitle("Loading data");
        pdLoading.setMessage("Please Wait...");
        PostRegister();
        addItemsOnSpinner2();
        addListenerOnButton();


    }

    public void PostRegister() {


        StringRequest postRequest = new StringRequest(Request.Method.POST, MyUrls.Register,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            int responseCode = obj.getInt("responseCode");
                            if (responseCode == 200) {
                                JSONObject uniObject = obj.getJSONObject("response");
                                String site_name = uniObject.getString("site_name");
                                String site_email = uniObject.getString("site_email");
                                String site_password = uniObject.getString("site_password");
                                Log.d(TAG, "onResponse: site_name  :" + site_name);
                                Log.d(TAG, "onResponse: site_email  :" + site_email);
                                Log.d(TAG, "onResponse: site_password  :" + site_password);
                                //  sessionManager.loginSite(site_id, site_name, token, Calendar.getInstance().getTimeInMillis());
                                Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(RegisterActivity.this, "User Successfully Login ", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(RegisterActivity.this, "Error login ", Toast.LENGTH_SHORT).show();
                    }
                }
        )

        {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("site_name", "butt Chowk");
                params.put("location_id",2+"");
                params.put("city_id",1+"");
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void addItemsOnSpinner2() {

        spLocation = (Spinner) findViewById(R.id.spLocation);
        List<String> list = new ArrayList<String>();
        list.add("Town Ship");
        list.add("Gulberg");
        list.add("Green Town");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocation.setAdapter(dataAdapter);

        ;
        List<String> list2 = new ArrayList<String>();
        list2.add("1");
        list2.add("2");
        list2.add("3");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCityId.setAdapter(dataAdapter2);
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {


        spLocation = (Spinner) findViewById(R.id.spLocation);


        btsignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                Toast.makeText(RegisterActivity.this,
                        "OnClickListener : "
                                + "\nSpinner 2 : " + String.valueOf(spLocation.getSelectedItem())
                                + "\nSpinner 1 : " + String.valueOf(spCityId.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
                Intent location = new Intent(getApplicationContext(), LocationActivity.class);
                startActivity(location);

            }


        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdLoading != null && pdLoading.isShowing()) {
            pdLoading.dismiss();
        }
    }


}
