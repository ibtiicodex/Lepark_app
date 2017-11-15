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
import com.codextech.ibtisam.lepak_app.model.LPLocation;
import com.codextech.ibtisam.lepak_app.realm.RealmController;
import com.codextech.ibtisam.lepak_app.sync.MyUrls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.codextech.ibtisam.lepak_app.sync.MyUrls.LocationUrl;
import static java.lang.String.valueOf;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private static final String TAGI = "RegisterActivity";
    EditText edSiteName;
    Button btsignup;
    private Spinner spLocationNames, spCityId;
    private Button btnSubmit;
    private RequestQueue queue;
    ProgressDialog pdLoading;
    private TextView txtResponse;
    private String jsonResponse;
    List<String> listId;
    List<String> listLocations;
    String cityId;
    String locationName;
    public static String areaName;
    private int count = 0;
    private Realm realm;
    private EditText etCarAmount;
    private EditText etBikeAmount;
    private EditText etVanAmount;
    private EditText etTruckAmount;
    private EditText edSitePass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        queue = Volley.newRequestQueue(RegisterActivity.this, new HurlStack());
        btsignup = (Button) findViewById(R.id.btsignup);
        edSiteName = (EditText) findViewById(R.id.edSiteName);
        etCarAmount = (EditText) findViewById(R.id.etCarAmount);
        etBikeAmount = (EditText) findViewById(R.id.etBikeAmount);
        etVanAmount = (EditText) findViewById(R.id.etVanAmount);
        etTruckAmount = (EditText) findViewById(R.id.etTruckAmount);
        edSitePass = (EditText) findViewById(R.id.edSitePass);
        spCityId = (Spinner) findViewById(R.id.spLocation);
        spLocationNames = (Spinner) findViewById(R.id.spCityId);

        getAllLocationsFromServer();
        pdLoading = new ProgressDialog(this);
        pdLoading.setTitle("Loading data");
        pdLoading.setMessage("Please Wait...");
        listId = new ArrayList<String>();
        listLocations = new ArrayList<String>();
    }

    public void addItemsOnSpinner2(String id, String location) {
        Log.d(TAG, "addItemsOnSpinner2: Adding Items in Spinner");
        listId.add(id);
        listLocations.add(location);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listId);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCityId.setAdapter(dataAdapter);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listLocations);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocationNames.setAdapter(dataAdapter2);

        btsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityId = valueOf(spCityId.getSelectedItem());
                locationName = String.valueOf(spLocationNames.getSelectedItem());
                String locationId = RealmController.with(RegisterActivity.this).getLocationFromLocationName(locationName).getId();
                Log.d(TAG, "onClick: locationId: " + locationId);
                boolean isValid = true;
                String siteName = edSiteName.getText().toString();
                String carAmount = etCarAmount.getText().toString();
                String bikeAmount = etBikeAmount.getText().toString();
                String vanAmount = etVanAmount.getText().toString();
                String truckAmount = etTruckAmount.getText().toString();
                String password=edSitePass.getText().toString();

                if (siteName.trim().length() < 3) {
                    isValid = false;
                    edSiteName.setError("Empty Field!");
                }
                if (carAmount.trim().length() < 1) {
                    isValid = false;
                    etCarAmount.setError("Empty Field!");
                }
                if (bikeAmount.trim().length() < 1) {
                    isValid = false;
                    etBikeAmount.setError("Empty Field!");
                }
                if (vanAmount.trim().length() < 1) {
                    isValid = false;
                    etVanAmount.setError("Empty Field!");
                }

                if (truckAmount.trim().length() < 1) {
                    isValid = false;
                    etTruckAmount.setError("Empty Field!");
                }
                if (isValid) {
                    makeSignupRequest(siteName,password ,locationId, cityId, carAmount, bikeAmount, vanAmount, truckAmount);

                     areaName=valueOf(spLocationNames.getSelectedItem());
                    Toast.makeText(RegisterActivity.this,
                            "OnClickListener : "
                                    + "\nSpinner 2 : " + valueOf(spCityId.getSelectedItem())
                                    + "\nSpinner 1 : " + valueOf(spLocationNames.getSelectedItem())+"  "+areaName,
                            Toast.LENGTH_SHORT).show();
                }
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

    private void getAllLocationsFromServer() {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        JsonArrayRequest req = new JsonArrayRequest(LocationUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v(TAG, response.toString());
                        try {
                            RealmConfiguration config = new RealmConfiguration.Builder(RegisterActivity.this).build();
                            realm = Realm.getInstance(config);
                            jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = (JSONObject) response.get(i);
                                String id = obj.getString("id");
                                String location = obj.getString("location");
                                String city_id = obj.getString("city_id");
                                jsonResponse += "id: " + id + "\n\n";
                                jsonResponse += "location: " + location + "\n\n";
                                jsonResponse += "city_id: " + city_id + "\n\n";
                                RealmQuery<LPLocation> query = realm.where(LPLocation.class);
                                query.equalTo("id", id);
                                RealmResults<LPLocation> allLocations = query.findAll();
                                Log.d(TAG, "allLocations: " + allLocations.toString());
                                // Duplication avoidance check
                                if (allLocations.isEmpty()) {
                                    Log.d(TAG, "Location doesn't exist adding it.");
                                    LPLocation lpLocation = new LPLocation();
                                    lpLocation.setId(id);
                                    lpLocation.setLocationName(location);
                                    lpLocation.setCityId(city_id);
                                    realm.beginTransaction();
                                    realm.copyToRealm(lpLocation);
                                    realm.commitTransaction();
                                } else {
                                    Log.d(TAG, "Already Exists");
                                }
                            }
                            Log.d(TAGI, jsonResponse.toString());
                            RealmQuery<LPLocation> query = realm.where(LPLocation.class);
                            query.equalTo("cityId", "1");
                            RealmResults<LPLocation> allLocations = query.findAll();
                            Log.d(TAG, "allLocationsFromDB: " + allLocations.toString());
                            Log.d(TAG, "allLocationsFromDB: SIZE: " + allLocations.size());
                            for (LPLocation oneLocation : allLocations) {
                                addItemsOnSpinner2(oneLocation.getCityId(), oneLocation.getLocationName());
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

    public void makeSignupRequest(final String sitename,final String password , final String locations, final String id, final String carAmount, final String bikeAmount, final String vanAmount, final String truckAmount) {
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
                                String site_email = uniObject.getString("username");
                                String site_password = uniObject.getString("site_password");
                                Log.d(TAG, "onResponse: site_name  :" + site_name);
                                Log.d(TAG, "onResponse: username  :" + site_email);
                                Log.d(TAG, "onResponse: site_password  :" + site_password);
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.putExtra(LoginActivity.LOGIN_EMAIL, site_email);
                                intent.putExtra(LoginActivity.LOGIN_PASSWORD, site_password);
                                startActivity(intent);
                                Toast.makeText(RegisterActivity.this, "User Successfully Registered ", Toast.LENGTH_SHORT).show();
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
                params.put("site_name", sitename);
                params.put("location_id", locations);
                params.put("city_id", id);
                params.put("car", carAmount);
                params.put("bike", bikeAmount);
                params.put("truck", vanAmount);
                params.put("van", truckAmount);
                params.put("password", password);
                // Log.d(TAGX, "getParams: " +siteName+"  "+cityId+"  "+locationName+"  ");
                return params;
            }
        };
        queue.add(postRequest);
    }
}
