package com.codextech.ibtisam.lepak_app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.SessionManager;
import com.codextech.ibtisam.lepak_app.app.MixpanelConfig;
import com.codextech.ibtisam.lepak_app.model.AllSites;
import com.codextech.ibtisam.lepak_app.receiver.NetworkStateReceiver;
import com.codextech.ibtisam.lepak_app.sync.MyUrls;
import com.codextech.ibtisam.lepak_app.sync.SyncStatus;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.codextech.ibtisam.lepak_app.R.id.edSiteName;
import static com.codextech.ibtisam.lepak_app.activity.RegisterActivity.areaName;
import static com.codextech.ibtisam.lepak_app.sync.MyUrls.AllSitesNames;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String LOGIN_EMAIL = "login_email";
    public static final String LOGIN_PASSWORD = "login_password";
    private EditText emailEditText;
    private EditText passEditText;
    private static String TAGA = "LoginActivity";
    Button btRegister;
    TextView tvsignup;
    ProgressDialog pdLoading;
    private Button btLogin;
    private Button btok;
    private SessionManager sessionManager;
    Realm realm;
    private String jsonResponse;
    private RequestQueue queue;
    String macAddtress = "";
    private Spinner spAllSites;
    private ArrayList<String> all_location;
    private String site_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(LoginActivity.this, new HurlStack());
        all_location = new ArrayList<String>();
        macAddtress = SyncStatus.getMacAddr();

        if (sessionManager.isSiteSignedIn()) {
            startActivity(new Intent(getApplicationContext(), NavigationDrawerActivity.class));
            finish();
        }

        if (NetworkStateReceiver.isNetworkAvailable(getApplicationContext())) {
            // Log.d(TAG, "DataSenderAsync: doInBackground TOKEN: " + sessionManager.getLoginToken());
            getAllLocationsFromServer();

        } else {
            Toast.makeText(this, "No InterNet", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "doInBackground: " + "************************ NO INTERNET CONNECTIVITY****************************");
        }

//        getAllLocationsFromServer();
        RegisterActivity obj = new RegisterActivity();
        spAllSites = (Spinner) findViewById(R.id.spAllSites);
        String email = getIntent().getStringExtra(LoginActivity.LOGIN_EMAIL);
        String password = getIntent().getStringExtra(LoginActivity.LOGIN_PASSWORD);
        btLogin = (Button) findViewById(R.id.btLogin);
        //btRegister = (Button) findViewById(R.id.btRegister);
        btok = (Button) findViewById(R.id.btok);
        emailEditText = (EditText) findViewById(edSiteName);
        //emailEditText.setText(email);
        passEditText = (EditText) findViewById(R.id.password);
        passEditText.setText(password);
        //  tvsignup = (TextView) findViewById(R.id.tvsignup);
        pdLoading = new ProgressDialog(this);
        pdLoading.setTitle("Loading data");
        pdLoading.setMessage("Please Wait...");
        sessionManager = new SessionManager(LoginActivity.this);
        queue = Volley.newRequestQueue(LoginActivity.this, new HurlStack());
//        btRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
//                startActivity(intent);
//                //  btnNext.setVisibility(View.GONE);
//            }
//        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String site = emailEditText.getText().toString();

                final String pass = passEditText.getText().toString();
                if (!isValidPassword(pass)) {
                    //Set error message for password field
                    passEditText.setError("Password cannot be empty");
                }


                if (NetworkStateReceiver.isNetworkAvailable(getApplicationContext())) {
                    // Log.d(TAG, "DataSenderAsync: doInBackground TOKEN: " + sessionManager.getLoginToken());

                    if (isValidPassword(pass)) {
                        loginRequest(site, pass);
                        pdLoading.show();

                    }

                } else {
                    Toast.makeText(LoginActivity.this, "No InterNet", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "doInBackground: " + "************************ NO INTERNET CONNECTIVITY****************************");
                }


            }
        });
    }

    // validating password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 4) {
            return true;
        }
        return false;
    }

    void loginRequest(final String site, final String password) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, MyUrls.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            int responseCode = obj.getInt("responseCode");
                            if (responseCode == 200) {
                                if (pdLoading != null && pdLoading.isShowing()) {
                                    pdLoading.dismiss();
                                }
                                JSONObject uniObject = obj.getJSONObject("response");
                                String site_id = uniObject.getString("site_id");
                                site_name = uniObject.getString("site_name");
                                String location = uniObject.getString("location");
                                String token = uniObject.getString("token");
                                String car_fare = uniObject.getString("car_fare");
                                String bike_fare = uniObject.getString("bike_fare");
                                String van_fare = uniObject.getString("van_fare");
                                String truck_fare = uniObject.getString("truck_fare");
                                String image_url = uniObject.getString("image_url");
                                Log.d(TAG, "onResponse: site_id  :" + site_id);
                                Log.d(TAG, "onResponse: site_name  :" + site_name);
                                Log.d(TAG, "onResponse: token  :" + token);
                                Log.d(TAG, "onResponse: image_url  :" + image_url);
                                areaName = location;
                                Log.d(TAG, "onResponse: ///////////////////////////////////////////////////////////" + areaName);
                                sessionManager.loginSite(site_id, site_name, token, Calendar.getInstance().getTimeInMillis(), car_fare, bike_fare, van_fare, truck_fare, areaName, macAddtress, image_url);
                                Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                                intent.putExtra("hello", site_name);
                                startActivity(intent);
                                finish();
                                Toast.makeText(LoginActivity.this, "User Successfully Login ", Toast.LENGTH_SHORT).show();
                                Toast.makeText(LoginActivity.this, sessionManager.getKeySiteId(), Toast.LENGTH_SHORT).show();

                                String projectToken = MixpanelConfig.projectToken;
                                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                                MixpanelAPI.People people = mixpanel.getPeople();
                                people.identify(site_id);
//                                people.initPushHandling("44843550731");
//                                mixpanel.getPeople().identify(site_id);

                                JSONObject props = new JSONObject();

                                props.put("$site_name", "" + site);
                                props.put("$area_name", "" + areaName);
                                props.put("car_fare", "" + car_fare);
                                props.put("bike_fare", "" + bike_fare);
                                props.put("van_fare", "" + van_fare);
                                props.put("truck_fare", "" + truck_fare);
//                                props.put("activated", "yes");

                                mixpanel.getPeople().set(props);

                                mixpanel.track("User Logged in", props);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: JSONException: ");
                            pdLoading.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        try {
                            pdLoading.dismiss();
                            Log.e(TAG, "onErrorResponse: " + error);
                            if (error.networkResponse != null) {
                                if (error.networkResponse.statusCode == 401) {
                                    JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                                    int responseCode = jObj.getInt("responseCode");
                                    if (responseCode == 17) {
                                        Toast.makeText(LoginActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                                    } else if (responseCode == 5) {
                                        Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Error login Check Internet", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", site);
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


    private void getAllLocationsFromServer() {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        JsonArrayRequest req = new JsonArrayRequest(AllSitesNames,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v(TAG, response.toString());
                        try {
                            RealmConfiguration config = new RealmConfiguration.Builder(LoginActivity.this).build();
                            realm = Realm.getInstance(config);
                            jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = (JSONObject) response.get(i);
                                int id = obj.getInt("id");
                                String site_name = obj.getString("site_name");
                                jsonResponse += "id: " + id + "\n\n";
                                jsonResponse += "site_name: " + site_name + "\n\n";
                                RealmQuery<AllSites> query = realm.where(AllSites.class);
                                query.equalTo("id", id);
                                RealmResults<AllSites> allSitesNames = query.findAll();
                                Log.d(TAG, "allSitesNames: " + allSitesNames.toString());
                                // Duplication avoidance check
                                if (allSitesNames.isEmpty()) {
                                    Log.d(TAG, "Location doesn't exist adding it.");
                                    AllSites allSitesFor = new AllSites();
                                    allSitesFor.setId(id);
                                    allSitesFor.setSite_name(site_name);
                                    realm.beginTransaction();
                                    realm.copyToRealm(allSitesFor);
                                    realm.commitTransaction();
                                } else {
                                    Log.d(TAG, "Already Exists");
                                }
                            }
                            Log.d(TAG, jsonResponse.toString());
                            RealmQuery<AllSites> query = realm.where(AllSites.class);
                            // query.equalTo("id",1);
                            RealmResults<AllSites> allSitesNames = query.findAll();
                            Log.d(TAG, "allLocationsFromDB: " + allSitesNames.toString());
                            Log.d(TAG, "allLocationsFromDB: SIZE: " + allSitesNames.size());
                            for (AllSites oneLocation : allSitesNames) {
                                showAllSitesInSpinner(oneLocation.getSite_name());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onResponse: JSONException");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    public void showAllSitesInSpinner(String siteName) {
        Log.d(TAG, "addItemsOnSpinner2: Adding Items in Spinner");
        all_location.add(siteName);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, all_location);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAllSites.setAdapter(dataAdapter);
//        if (!site_name.equals(null)) {
//            int spinnerPosition = dataAdapter.getPosition(site_name);
//            spAllSites.setSelection(spinnerPosition);
//        }

        btok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                site_name = String.valueOf(spAllSites.getSelectedItem());
                emailEditText.setText(site_name);
            }
        });
//        site_name=String.valueOf(spAllSites.getSelectedItem());
//        emailEditText.setText(site_name);

    }


}
