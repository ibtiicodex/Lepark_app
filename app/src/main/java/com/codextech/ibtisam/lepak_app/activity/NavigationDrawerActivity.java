package com.codextech.ibtisam.lepak_app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.SessionManager;
import com.codextech.ibtisam.lepak_app.app.MixpanelConfig;
import com.codextech.ibtisam.lepak_app.fragments.TabFragment;
import com.codextech.ibtisam.lepak_app.receiver.NetworkStateReceiver;
import com.codextech.ibtisam.lepak_app.service.ScanService;
import com.codextech.ibtisam.lepak_app.sync.DataSenderAsync;
import com.codextech.ibtisam.lepak_app.sync.MyUrls;
import com.codextech.ibtisam.lepak_app.sync.SyncStatus;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//import com.codextech.ibtisam.lepak_app.service.ScanService;
public class NavigationDrawerActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentTransaction mFragmentTransaction;
    FragmentManager mFragmentManager;
    private ImageView ivProfileImgNavBar;
    SessionManager sessionManager;

    private RequestQueue queue;
    TextView setOnProfile;
    private String TAG = "NavigationDrawerActivity";
    private String siteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(NavigationDrawerActivity.this, new HurlStack());


        sessionManager = new SessionManager(NavigationDrawerActivity.this);
        siteId = sessionManager.getKeySiteId();
        if (!sessionManager.isSiteSignedIn()) {
            finish();
            startActivity(new Intent(NavigationDrawerActivity.this, LoginActivity.class));
            finish();
        }
        Intent newIntent = new Intent(NavigationDrawerActivity.this, ScanService.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        NavigationDrawerActivity.this.startService(newIntent);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        View header = mNavigationView.getHeaderView(0);
/*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        setOnProfile = (TextView) header.findViewById(R.id.tvSite);
        setOnProfile.setText(sessionManager.getKeySiteName());


        LinearLayout headerLayout = (LinearLayout) mNavigationView.getHeaderView(0);
        ivProfileImgNavBar = (ImageView) headerLayout.findViewById(R.id.ivProfileImgNavBar);
        Glide.with(NavigationDrawerActivity.this)
                .load(sessionManager.getKeyImage())
                .into(ivProfileImgNavBar);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();



                TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                int simState = telMgr.getSimState();


                if (menuItem.getItemId() == R.id.showallnav) {
                    switch (simState) {
                        case TelephonyManager.SIM_STATE_ABSENT:
                            Toast.makeText(NavigationDrawerActivity.this, "Sim Is Absent", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_NETWORK_LOCKED  ", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_PIN_REQUIRED ", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_PUK_REQUIRED", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_READY:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_READY", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_UNKNOWN:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_UNKNOWN", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    Intent intent = new Intent(getApplicationContext(), AllTicketsActivity.class);
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.summarynav) {
                    switch (simState) {
                        case TelephonyManager.SIM_STATE_ABSENT:
                            Toast.makeText(NavigationDrawerActivity.this, "Sim Is Absent", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_NETWORK_LOCKED  ", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_PIN_REQUIRED ", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_PUK_REQUIRED", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_READY:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_READY", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_UNKNOWN:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_UNKNOWN", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    Intent intent = new Intent(getApplicationContext(), SummaryActivity.class);
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.logoutnav) {
                    switch (simState) {
                        case TelephonyManager.SIM_STATE_ABSENT:
                            Toast.makeText(NavigationDrawerActivity.this, "Sim Is Absent", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_NETWORK_LOCKED  ", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_PIN_REQUIRED ", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_PUK_REQUIRED", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_READY:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_READY", Toast.LENGTH_SHORT).show();
                            break;
                        case TelephonyManager.SIM_STATE_UNKNOWN:
                            // do something
                            Toast.makeText(NavigationDrawerActivity.this, "SIM_STATE_UNKNOWN", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    logoutManager();
                }
                if (menuItem.getItemId() == R.id.nav_NfcActvity) {
                    Intent intent = new Intent(getApplicationContext(), NfcGetAllCoinsActivity.class);
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.nav_refresh) {
                    DataSenderAsync dataSenderAsync = new DataSenderAsync(NavigationDrawerActivity.this);
                    dataSenderAsync.execute();
                    String projectToken = MixpanelConfig.projectToken;
                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(NavigationDrawerActivity.this, projectToken);
                    mixpanel.track("Refreshed");
                    Toast.makeText(NavigationDrawerActivity.this, " Refreshed ", Toast.LENGTH_SHORT).show();
                }

//                if (menuItem.getItemId() == R.id.nav_BlockUser) {
//                    Intent intent = new Intent(getApplicationContext(), BlackList.class);
//                    startActivity(intent);
//                }

                return false;
            }

        });
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    private void logoutManager() {


        if (NetworkStateReceiver.isNetworkAvailable(getApplicationContext())) {
            // Log.d(TAG, "DataSenderAsync: doInBackground TOKEN: " + sessionManager.getLoginToken());
            StringRequest postRequest = new StringRequest(Request.Method.POST, MyUrls.LOGOUT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //  Log.d(TAG, "****onResponse:" + response);
                                JSONObject obj = new JSONObject(response);
                                int responseCode = obj.getInt("responseCode");
                                if (responseCode == 200) {

                                    sessionManager.logoutSite();
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(NavigationDrawerActivity.this, "Okay Send Data ", Toast.LENGTH_SHORT).show();

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
                            try {
                                //Log.d(TAG, "**onResponse:" + error);
                                if (error.networkResponse != null) {
                                    if (error.networkResponse.statusCode == 401) {
                                        JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                                        int responseCode = jObj.getInt("responseCode");

                                    }
                                } else {

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
                    params.put("site_id", siteId);
                    params.put("mac", SyncStatus.getMacAddr());
                    return params;
                }
            };
            queue.add(postRequest);


        } else {
            Toast.makeText(NavigationDrawerActivity.this, "No Internet", Toast.LENGTH_LONG).show();
            //  Log.d(TAG, "doInBackground: " + "************************ NO INTERNET CONNECTIVITY****************************");
        }
    }


}
