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
    RequestQueue queue;


    public TicketSenderAsync(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
        Log.d(TAG, "TicketSenderAsync: TOKEN: " + sessionManager.getLoginToken());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "TicketSenderAsync: doInBackground TOKEN: " + sessionManager.getLoginToken());
        addTicketToServer();
        editTicketToServer();
        return null;
    }


    private void addTicketToServer() {

        realm = Realm.getDefaultInstance();
//        RealmConfiguration config = new RealmConfiguration.Builder(context).build();
//
//        realm = Realm.getInstance(config);
        Log.d(TAG, "Site Id For Check " + sessionManager.getKeySiteId());

        RealmQuery<LPTicket> query = realm.where(LPTicket.class);

        query.equalTo("syncStatus", SyncStatus.SYNC_STATUS_TICKET_ADD_NOT_SYNCED);

        RealmResults<LPTicket> manyLPTicket = query.findAll();

//        Log.d(TAG, "addTicketToServer: manyLPTicket: " + manyLPTicket.toString());
        Log.d(TAG, "addTicketToServer: count " + manyLPTicket.size());
        for (LPTicket oneLPTicket : manyLPTicket) {
            Log.d(TAG, "addTicketToServer: oneLPTicket " + oneLPTicket);
            Log.d(TAG, "addTicketToServer: oneLPTicket Number " + oneLPTicket.getNumber());
            addTicketToServerSync(oneLPTicket.getNumber(), oneLPTicket.getVehicleType(), oneLPTicket.getPrice(), oneLPTicket.getTimeIn());
        }
        realm.close();
    }

    private void addTicketToServerSync(final String veh_num, final String veh_type, final String fee, final String time_in) {
        queue = Volley.newRequestQueue(context, new HurlStack());
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
                                vehicle_no = uniObject.getString("vehicle_no");
                                String time_in = uniObject.getString("time_in");
                                realm = Realm.getDefaultInstance();
//                                RealmConfiguration config = new RealmConfiguration.Builder(context).build();
//                                realm = Realm.getInstance(config);
                                RealmQuery<LPTicket> query = realm.where(LPTicket.class);
                                query.equalTo("timeIn", time_in);
                                RealmResults<LPTicket> manyLPTicket = query.findAll();
                                realm.beginTransaction();
                                manyLPTicket.first().setSyncStatus(SyncStatus.SYNC_STATUS_TICKET_ADD_SYNCED);
                                manyLPTicket.first().setServer_id(serverid);
                                realm.commitTransaction();
                                realm.close();
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
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode != 0) {
                                Log.e(TAG, "onErrorResponse:  " + error.networkResponse.statusCode);
                                if (error.networkResponse.statusCode == 500) {
                                    Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
                                } else if (error.networkResponse.statusCode == 409) {
                                    Log.d(TAG, "onErrorResponse: CHANGING TICKET STATUS " + veh_num);
                                    // ticket already exists on server change its status to SYNCED
                                    realm = Realm.getDefaultInstance();
//                                RealmConfiguration config = new RealmConfiguration.Builder(context).build();
//                                realm = Realm.getInstance(config);
                                    RealmQuery<LPTicket> query = realm.where(LPTicket.class);
                                    query.equalTo("timeIn", time_in);
                                    RealmResults<LPTicket> manyLPTicket = query.findAll();
                                    realm.beginTransaction();
                                    manyLPTicket.first().setSyncStatus(SyncStatus.SYNC_STATUS_TICKET_ADD_SYNCED);
//                            manyLPTicket.first().setServer_id(serverid);
                                    realm.commitTransaction();
                                    realm.close();
                                }
                            }
                        } else {
                            Toast.makeText(context, "check internet connection", Toast.LENGTH_SHORT).show();
                        }
                        Log.e(TAG, "onErrorResponse: addTicketToServerSync" + error);
                        Log.e(TAG, "onErrorResponse: Vehicle Number: " + veh_num);
//                        Toast.makeText(context, "Error Syncing Ticket", Toast.LENGTH_SHORT).show();
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
                params.put("time_out", "");
                params.put("token", sessionManager.getLoginToken());

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void editTicketToServer() {

        RealmConfiguration config = new RealmConfiguration.Builder(context).build();

        realm = Realm.getInstance(config);

        RealmQuery<LPTicket> query = realm.where(LPTicket.class);

        query.equalTo("syncStatus", SyncStatus.SYNC_STATUS_TICKET_EDIT_NOT_SYNCED);

        RealmResults<LPTicket> manyLPTicket = query.findAll();

//        Log.d(TAG, "editTicketToServer: manyLPTicket: " + manyLPTicket.toString());
        Log.d(TAG, "editTicketToServer: count " + manyLPTicket.size());
        for (LPTicket oneLPTicket : manyLPTicket) {
            Log.d(TAG, "editTicketToServer: oneLPTicket " + oneLPTicket);
            Log.d(TAG, "editTicketToServer: oneLPTicket Number " + oneLPTicket.getNumber());
            editTicketToServerSync(oneLPTicket.getNumber(), oneLPTicket.getVehicleType(), oneLPTicket.getPrice(), oneLPTicket.getTimeIn(), oneLPTicket.getTimeOut());
        }

    }

    private void editTicketToServerSync(String number, String vehicleType, String price, String timeIn, String timeOut) {

        RequestQueue queue = Volley.newRequestQueue(context, new HurlStack());
        StringRequest putRequest = new StringRequest(Request.Method.PUT, "http://34.215.56.25/apiLepak/public/api/sites/ticket/timeout/" + timeOut,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, response.toString());
                        Toast.makeText(context, "Successfully Edited to server", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d(TAG, error.toString());
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<String, String>();
////                headers.put("Content-Type", "application/json");
////                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//                return headers;
//            }


            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //                params.put("id", 5 + "");
                params.put("time_out", "2017-11-03 01:42:06");

                return params;
            }
        };

        queue.add(putRequest);
    }


}
