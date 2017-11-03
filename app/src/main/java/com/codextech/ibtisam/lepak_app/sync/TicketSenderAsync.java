package com.codextech.ibtisam.lepak_app.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.lepak_app.SessionManager;
import com.codextech.ibtisam.lepak_app.model.LPTicket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by ibtisam on 10/23/2017.
 */

public class TicketSenderAsync extends AsyncTask<Void, Void, Void> {
    public static final String TAG = "TicketSenderAsync";
    Context context;
    SessionManager sessionManager;
    private Realm realm;
    private String vehicle_no;
    private String serverid;

    public TicketSenderAsync(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        addTicketToServer();
        //TODO
        //editTicketToServer();

        return null;
    }


    private void addTicketToServer() {

        RealmConfiguration config = new RealmConfiguration.Builder(context).build();

        realm = Realm.getInstance(config);

        RealmQuery<LPTicket> query = realm.where(LPTicket.class);

        query.equalTo("syncStatus", SyncStatus.SYNC_STATUS_TICKET_ADD_NOT_SYNCED);

        RealmResults<LPTicket> manyLPTicket = query.findAll();

//        Log.d(TAG, "addTicketToServer: manyLPTicket: " + manyLPTicket.toString());
        Log.d(TAG, "addTicketToServer: count " + manyLPTicket.size());
        for (LPTicket oneLPTicket : manyLPTicket) {
            query.equalTo("syncStatus", SyncStatus.SYNC_STATUS_TICKET_ADD_NOT_SYNCED);
            Log.d(TAG, "addTicketToServer: oneLPTicket " + oneLPTicket);
            Log.d(TAG, "addTicketToServer: oneLPTicket Number " + oneLPTicket.getNumber());
            addTicketToServerSync(oneLPTicket.getNumber(), oneLPTicket.getVehicleType(), oneLPTicket.getPrice(), oneLPTicket.getTimeIn());
        }
    }

    private void addTicketToServerSync(final String veh_num, final String veh_type, final String fee, final String time_in) {
        RequestQueue queue = Volley.newRequestQueue(context, new HurlStack());

        StringRequest postRequest = new StringRequest(Request.Method.POST, MyUrls.TICKET_SEND,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "onResponse: " + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int responseCode = obj.getInt("responseCode");
                            if (responseCode == 200) {
                                JSONObject uniObject = obj.getJSONObject("response");
                                //TODO  Save server_id of ticket in local db
                                serverid = uniObject.getString("id");
//                                String site_id = uniObject.getString("site_id");
                                vehicle_no = uniObject.getString("vehicle_no");
//                                String vehicle_type = uniObject.getString("vehicle_type");
//                                String fee = uniObject.getString("fee");
                                String time_in = uniObject.getString("time_in");
//                                Log.d(TAG, "onResponse: site_id  :" + site_id);
//                                Log.d(TAG, "onResponse: site_name  :" + site_name);
//                                Log.d(TAG, "onResponse: token  :" + token);

                                // Toast.makeText(LoginActivity.this, "User Successfully Login ", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(LoginActivity.this, sessionManager.getKeySiteId(), Toast.LENGTH_SHORT).show();
//                                if (pdLoading != null && pdLoading.isShowing()) {
//                                    pdLoading.dismiss();
//                                }


                                RealmConfiguration config = new RealmConfiguration.Builder(context).build();

                                realm = Realm.getInstance(config);

                                RealmQuery<LPTicket> query = realm.where(LPTicket.class);

                                query.equalTo("timeIn", time_in);
                                RealmResults<LPTicket> manyLPTicket = query.findAll();
                                realm.beginTransaction();

                                manyLPTicket.first().setSyncStatus(SyncStatus.SYNC_STATUS_TICKET_ADD_SYNCED);
                                manyLPTicket.first().setServer_id(serverid);
                                realm.commitTransaction();
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
                        Log.e(TAG, "onErrorResponse: addTicketToServerSync" + error);
                        Log.e(TAG, "onErrorResponse: VehNum" + veh_num);
                        // error
                        Toast.makeText(context, "Error Syncing Ticket", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("site_id", sessionManager.getKeySiteId());
                params.put("vehicle_no", veh_num);
                params.put("vehicle_type", veh_type);
                params.put("fee", fee);
                params.put("time_in", time_in);
                params.put("token", sessionManager.getLoginToken());

                return params;
            }
        };
        queue.add(postRequest);


    }

//    private void editTicketToServer() {
//
//        RealmConfiguration config = new RealmConfiguration.Builder(context).build();
//
//        realm = Realm.getInstance(config);
//
//        RealmQuery<LPTicket> query = realm.where(LPTicket.class);
//
//        query.equalTo("syncStatus", SyncStatus.SYNC_STATUS_TICKET_EDIT_NOT_SYNCED);
//
//        RealmResults<LPTicket> manyLPTicket = query.findAll();
//
////        Log.d(TAG, "addTicketToServer: manyLPTicket: " + manyLPTicket.toString());
//
//        for (LPTicket oneLPTicket : manyLPTicket) {
//            Log.d(TAG, "addTicketToServer: oneLPTicket " + oneLPTicket);
//            Log.d(TAG, "addTicketToServer: oneLPTicket Number " + oneLPTicket.getNumber());
//            editTicketToServerSync(oneLPTicket.getNumber(), oneLPTicket.getVehicleType(), oneLPTicket.getPrice(), oneLPTicket.getTimeIn());
//        }
//
//    }

    //    private void editTicketToServerSync(String number, String vehicleType, String price, String timeIn) {
//
//    }
//    public void add() {
//        RequestQueue queue = Volley.newRequestQueue(context, new HurlStack());
//        StringRequest putRequest = new StringRequest(Request.Method.PUT, "http://34.215.56.25/apiLepak/public/api/sites/ticket/timeout/5/2017-11-03 01:48:06",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // response
//                        Log.d("Response           ::: ", response.toString());
//                        Toast.makeText(context, "" +response.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // error
//                        Log.d("Error.Response   ::::: ", error.toString());
//                        Toast.makeText(context, "" +error.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
//                //or try with this:
//                //headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//                return headers;
//            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("id",5+"");
//                params.put("time_out", "2017-11-03 01:42:06");
//
//                return params;
//            }
//        };
//
//        queue.add(putRequest);
//    }


//    private void updateFirebaseIdAndInitConfigMakeRequest(Activity activity, @Nullable JSONObject returnInitJson) {
//
//        final int MY_SOCKET_TIMEOUT_MS = 60000;
//        final String BASE_URL = MyURLs.UPDATE_AGENT;
//        Uri builtUri;
//        if (returnInitJson != null) {
//            builtUri = Uri.parse(BASE_URL)
//                    .buildUpon()
//                    .appendQueryParameter("config", "" + returnInitJson)
//                    .appendQueryParameter("device_id", "" + sessionManager.getKeyLoginFirebaseRegId())
//                    .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
//                    .build();
//
//        }else {
//            builtUri = Uri.parse(BASE_URL)
//                    .buildUpon()
//                    .appendQueryParameter("device_id", "" + sessionManager.getKeyLoginFirebaseRegId())
//                    .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
//                    .build();
//        }
//        final String myUrl = builtUri.toString();
//        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "onResponse() updateFirebaseIdAndInitConfigMakeRequest: response = [" + response + "]");
//                try {
//                    if (pdLoading != null && pdLoading.isShowing()) {
//                        pdLoading.dismiss();
//                    }
//                    JSONObject jObj = new JSONObject(response);
//                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 200) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
//                        Log.d(TAG, "onResponse : FirebaseLocalRegID : " + sessionManager.getKeyLoginFirebaseRegId());
//                        Log.d(TAG, "onResponse : FirebaseServerRegID : " + responseObject.getString("device_id"));
//
//                        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
//                        theCallLogEngine.execute();
//                        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
//                        dataSenderAsync.run();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (pdLoading != null && pdLoading.isShowing()) {
//                    pdLoading.dismiss();
//                }
//                error.printStackTrace();
//                Log.d(TAG, "onErrorResponse: CouldNotUpdateInitConfigMakeRequest OR CouldNotSyncAgentFirebaseRegId");
//
////                RecordingManager recordingManager = new RecordingManager();
////                recordingManager.execute();
//                TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
//                theCallLogEngine.execute();
//                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
//                dataSenderAsync.run();
//            }
//        }) {
//        };
//        sr.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(sr);
//
//    }
//


}
