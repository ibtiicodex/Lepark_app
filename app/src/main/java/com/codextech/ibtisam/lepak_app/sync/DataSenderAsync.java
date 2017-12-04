package com.codextech.ibtisam.lepak_app.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.lepak_app.SessionManager;
import com.codextech.ibtisam.lepak_app.activity.NavigationDrawerActivity;
import com.codextech.ibtisam.lepak_app.model.LPNfc;
import com.codextech.ibtisam.lepak_app.model.LPTicket;
import com.codextech.ibtisam.lepak_app.receiver.NetworkStateReceiver;
import com.codextech.ibtisam.lepak_app.util.DateAndTimeUtils;
import com.google.android.gms.iid.InstanceID;

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

public class DataSenderAsync {
    public static final String TAG = "DataSenderAsync";
    private static DataSenderAsync instance = null;
    private static int currentState = 1;
    private static final int IDLE = 1;
    private static final int PENDING = 2;
    private static boolean firstThreadIsRunning = false;
    private final int MY_MAX_RETRIES = 0;
    Context mContext;
    SessionManager sessionManager;
    RequestQueue queue;

    public static DataSenderAsync getInstance(Context applicationContext) {
        final Context appContext = applicationContext.getApplicationContext();
        if (instance == null) {
//            synchronized ((Object) firstThreadIsRunning) { //TODO make it thread safe
            if (!firstThreadIsRunning) {
                firstThreadIsRunning = true;
                instance = new DataSenderAsync(appContext);
            }
//            }
        }
        return instance;
    }

    public DataSenderAsync(Context context) {
        mContext = context;
        queue = Volley.newRequestQueue(context, new HurlStack());
        firstThreadIsRunning = false;
    }

    public void run() {
        if (currentState == IDLE) {
            currentState = PENDING;
            Log.d(TAG, "run: InsideRUNING" + this.toString());
            queue.setmAllFinishedListener(new RequestQueue.AllFinishedListener() {
                @Override
                public void onAllFinished() {
                    currentState = IDLE;
                    Log.d(TAG, "onRequestFinished: EVERYTHING COMPLETED");
                }
            });
            new AsyncTask<Object, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    sessionManager = new SessionManager(mContext);
                    Log.d(TAG, "DataSenderAsync ++++++++++++++onPreExecute:+++++++++++++ ");
                }

                @Override
                protected Void doInBackground(Object... objects) {
                    try {
                        if (NetworkStateReceiver.isNetworkAvailable(mContext)) {
                            Log.d(TAG, "DataSenderAsync: run TOKEN: " + sessionManager.getLoginToken());
                            addTicketToServer();
//                            editTicketToServer();
//                            editCoinsToServer();
//                            if (NetworkStateReceiver.isWifiConnected(mContext)) {
//                                // Do big syncing on WIFI only.
//                            }
                        } else {
                            Log.d(TAG, "run: " + "************************ NO INTERNET CONNECTIVITY****************************");
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "SyncingException: " + e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    queue.isIdle();
//                    if (currentState != PENDING) {
//                        Log.d(TAG, "onPostExecuteListener: currentState: " + currentState);
//                        currentState = IDLE;
//                    }
                    Log.d(TAG, "DataSenderAsync onPostExecute:");
                }
            }.execute();
        }
    }

    private void addTicketToServer() {
        Realm realm;
        realm = Realm.getDefaultInstance();
        RealmQuery<LPTicket> query = realm.where(LPTicket.class);
        query.equalTo("syncStatus", SyncStatus.SYNC_STATUS_TICKET_ADD_NOT_SYNCED);
        RealmResults<LPTicket> manyLPTicket = query.findAll();
        Log.d(TAG, "addTicketToServer: count " + manyLPTicket.size());
        for (LPTicket oneLPTicket : manyLPTicket) {
            Log.d(TAG, "addTicketToServer: oneLPTicket " + oneLPTicket);
            Log.d(TAG, "addTicketToServer: oneLPTicket Number " + oneLPTicket.getNumber());
            addTicketToServerSync(oneLPTicket.getNumber(), oneLPTicket.getVehicleType(), oneLPTicket.getPrice(), oneLPTicket.getTimeIn(), oneLPTicket.getTimeOut());
        }
        realm.close();
    }

    private void addTicketToServerSync(final String veh_num, final String veh_type, final String fee, final long time_in, final long time_out) {
        currentState = PENDING;
        final String timeInString = DateAndTimeUtils.getDateTimeStringFromMiliseconds(time_in, "yyyy-MM-dd kk:mm:ss");
        Log.d(TAG, "addTicketToServerSync: TimeOUT: " + time_out);
        final String timeOutString = DateAndTimeUtils.getDateTimeStringFromMiliseconds(time_out, "yyyy-MM-dd kk:mm:ss");
        Log.d(TAG, "addTicketToServerSync: timeInString: " + timeInString);
        Log.d(TAG, "addTicketToServerSync: timeOutString: " + timeOutString);
        StringRequest sr = new StringRequest(Request.Method.POST, MyUrls.TICKET_SEND,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "onResponse(): " + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int responseCode = obj.getInt("responseCode");
                            if (responseCode == 200) {
                                JSONObject uniObject = obj.getJSONObject("response");
                                //TODO  Save server_id of ticket in local db
                                String serverid = uniObject.getString("id");
                                String vehicle_no = uniObject.getString("vehicle_no");
                                Realm realm;
                                realm = Realm.getDefaultInstance();
                                try {
                                    RealmQuery<LPTicket> query = realm.where(LPTicket.class);
                                    query.equalTo("timeIn", time_in);
                                    RealmResults<LPTicket> manyLPTicket = query.findAll();
                                    realm.beginTransaction();
                                    manyLPTicket.first().setSyncStatus(SyncStatus.SYNC_STATUS_TICKET_ADD_SYNCED);
                                    manyLPTicket.first().setServer_id(serverid);
                                    realm.commitTransaction();
                                    Log.d(TAG, "onSuccessResponse: CHANGING TICKET STATUS " + veh_num);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    realm.close();
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
                        try {
//                            if (error instanceof NetworkError) {
//                                Log.e(TAG, "onErrorResponse: NetworkError + " + error);
//                            } else if (error instanceof ServerError) {
//                                Log.e(TAG, "onErrorResponse: ServerError + " + error);
//                            } else if (error instanceof AuthFailureError) {
//                                Log.e(TAG, "onErrorResponse: AuthFailureError + " + error);
//                            } else if (error instanceof ParseError) {
//                                Log.e(TAG, "onErrorResponse: ParseError + " + error);
//                            } else if (error instanceof NoConnectionError) {
//                                Log.e(TAG, "onErrorResponse: NoConnectionError + " + error);
//                            } else if (error instanceof TimeoutError) {
//                                Log.e(TAG, "onErrorResponse: TimeoutError + " + error);
//                            }

                            Log.e(TAG, "onErrorResponse: addTicketToServerSync" + error);
                            Log.e(TAG, "onErrorResponse: Vehicle Number: " + veh_num);
                            if (error.networkResponse != null) {
                                if (error.networkResponse.statusCode != 0) {
                                    Log.e(TAG, "onErrorResponse:  " + error.networkResponse.statusCode);
                                    if (error.networkResponse.statusCode == 500) {
                                        Toast.makeText(mContext, "Server Error", Toast.LENGTH_SHORT).show();
                                    } else if (error.networkResponse.statusCode == 409) {
                                        Realm realm;
                                        realm = Realm.getDefaultInstance();
                                        try {
                                            RealmQuery<LPTicket> query = realm.where(LPTicket.class);
                                            query.equalTo("timeIn", time_in);
                                            RealmResults<LPTicket> manyLPTicket = query.findAll();
                                            realm.beginTransaction();
                                            manyLPTicket.first().setSyncStatus(SyncStatus.SYNC_STATUS_TICKET_ADD_SYNCED);
                                            realm.commitTransaction();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            realm.close();
                                        }
                                        Log.e(TAG, "onErrorResponse: CHANGING TICKET STATUS " + veh_num);
                                    } else if (error.networkResponse.statusCode == 401) {
                                        Toast.makeText(mContext, "AuthFailureError", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(mContext, "check internet connection", Toast.LENGTH_SHORT).show();
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
                params.put("site_id", sessionManager.getKeySiteId());
                params.put("vehicle_no", veh_num);
                params.put("vehicle_type", veh_type);
                params.put("fee", fee);
                params.put("time_in", timeInString);
                params.put("time_out", timeOutString);
                params.put("token", sessionManager.getLoginToken());
                params.put("mac", sessionManager.getKeyMac());
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

//    private void editTicketToServer() {
//        RealmConfiguration config = new RealmConfiguration.Builder(mContext).build();
//        realm = Realm.getInstance(config);
//        RealmQuery<LPTicket> query = realm.where(LPTicket.class);
//        query.equalTo("syncStatus", SyncStatus.SYNC_STATUS_TICKET_EDIT_NOT_SYNCED);
//        RealmResults<LPTicket> manyLPTicket = query.findAll();
//        Log.d(TAG, "editTicketToServer: count " + manyLPTicket.size());
//        for (LPTicket oneLPTicket : manyLPTicket) {
//            Log.d(TAG, "editTicketToServer: oneLPTicket " + oneLPTicket);
//            Log.d(TAG, "editTicketToServer: oneLPTicket Number " + oneLPTicket.getNumber());
//            editTicketToServerSync(oneLPTicket.getNumber(), oneLPTicket.getVehicleType(), oneLPTicket.getPrice(), oneLPTicket.getTimeIn(), oneLPTicket.getTimeOut(), oneLPTicket.getServer_id());
//        }
//    }
//
//    private void editTicketToServerSync(final String number, final String vehicleType, final String price, final long timeInLong, final long timeOutLong, final String serverId) {
//        currentState = PENDING;
//        final String timeInString = DateAndTimeUtils.getDateTimeStringFromMiliseconds(timeInLong, "yyyy-MM-dd kk:mm:ss");
//        final String timeOutString = DateAndTimeUtils.getDateTimeStringFromMiliseconds(timeOutLong, "yyyy-MM-dd kk:mm:ss");
//        Log.d(TAG, "editTicketToServerSync: timeInString: " + timeInString);
//        Log.d(TAG, "editTicketToServerSync: timeOutString: " + timeOutString);
//        StringRequest sr = new StringRequest(Request.Method.PUT, "http://34.215.56.25/apiLepak/public/api/sites/ticket/timeout/" + serverId,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d(TAG, "editTicketToServerSync onResponse(): " + response.toString());
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            int responseCode = obj.getInt("responseCode");
//                            if (responseCode == 200) {
//                                JSONObject uniObject = obj.getJSONObject("response");
//                                //TODO  Save server_id of ticket in local db
//                                String serverid = uniObject.getString("id");
//                                String vehicle_no = uniObject.getString("vehicle_no");
//                                String vehicle_type = uniObject.getString("vehicle_type");
//                                String fee = uniObject.getString("fee");
//                                String time_in = uniObject.getString("time_in");
//                                String time_out = uniObject.getString("time_out");
//                                Log.d(TAG, "onResponse: serverid: " + serverid);
//                                Log.d(TAG, "onResponse: vehicle_no: " + vehicle_no);
//                                Log.d(TAG, "onResponse: vehicle_type: " + vehicle_type);
//                                Log.d(TAG, "onResponse: fee: " + fee);
//                                Log.d(TAG, "onResponse: time_in: " + time_in);
//                                Log.d(TAG, "onResponse: time_out: " + time_out);
//                                realm = Realm.getDefaultInstance();
//                                RealmQuery<LPTicket> query = realm.where(LPTicket.class);
//                                query.equalTo("timeIn", timeInLong);
//                                RealmResults<LPTicket> manyLPTicket = query.findAll();
//                                realm.beginTransaction();
//                                manyLPTicket.first().setSyncStatus(SyncStatus.SYNC_STATUS_TICKET_EDIT_SYNCED); //TODO crash here on exiting ticket which was synced on adding
//                                realm.commitTransaction();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.d(TAG, "onResponse: JSONException: " + e);
//                        }
//                        Toast.makeText(mContext, "Successfully Edited to server", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        if (error instanceof NetworkError) {
//                            Log.e(TAG, "onErrorResponse: NetworkError + " + error);
//                        } else if (error instanceof ServerError) {
//                            Log.e(TAG, "onErrorResponse: ServerError + " + error);
//                        } else if (error instanceof AuthFailureError) {
//                            Log.e(TAG, "onErrorResponse: AuthFailureError + " + error);
//                        } else if (error instanceof ParseError) {
//                            Log.e(TAG, "onErrorResponse: ParseError + " + error);
//                        } else if (error instanceof NoConnectionError) {
//                            Log.e(TAG, "onErrorResponse: NoConnectionError + " + error);
//                        } else if (error instanceof TimeoutError) {
//                            Log.e(TAG, "onErrorResponse: TimeoutError + " + error);
//                        }
//                        Log.d(TAG, "editTicketToServerSync onErrorResponse: " + error.toString());
//                        Log.d(TAG, "editTicketToServerSync onErrorResponse:  statusCode: " + error.networkResponse.statusCode);
//                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded";
//            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("time_out", timeOutString);
//                params.put("token", sessionManager.getLoginToken());
//                return params;
//            }
//        };
//        sr.setRetryPolicy(new DefaultRetryPolicy(
//                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
//                MY_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(sr);
//    }

    //////////////////////////////////////////////////////////////////////////////coinsupdate
//    private void editCoinsToServer() {
//        RealmConfiguration config = new RealmConfiguration.Builder(mContext).build();
//        realm = Realm.getInstance(config);
//        LPNfc lpNfc = new LPNfc();
//        RealmQuery<LPNfc> query = realm.where(LPNfc.class);
//        query.equalTo("syncStatus", SyncStatus.SYNC_STATUS_COIN_EDIT_NOT_SYNCED);
//        RealmResults<LPNfc> manyLPCoin = query.findAll();
//        Log.d(TAG, "editCoinToServer: count " + manyLPCoin.size());
//        for (LPNfc oneLPCoin : manyLPCoin) {
//            Log.d(TAG, "editCoinToServer: oneLPCoin " + oneLPCoin);
//
//            editCoinToServerSync(oneLPCoin.getCoinId(), oneLPCoin.getCoinVehicle(), oneLPCoin.getCoinAmount());
//        }
//    }
//
//    private void editCoinToServerSync(String coinId, String coinvehicle, final String coinAmount) {
//        currentState = PENDING;
//        StringRequest sr = new StringRequest(Request.Method.PUT, "http://34.215.56.25/apiLepak/public/api/sites/coin/" + coinId,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d(TAG, "onResponse: " + response.toString());
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            int responseCode = obj.getInt("responseCode");
//                            if (responseCode == 200) {
//                                Toast.makeText(mContext, "Coin Synced", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.d(TAG, "onResponse: JSONException: " + e);
//                        }
//
//                        Toast.makeText(mContext, "Successfully Edited to server", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d(TAG, "editCoinToServerSync onErrorResponse: " + error.toString());
//                        Log.d(TAG, "editCoinToServerSync onErrorResponse:  statusCode: " + error.networkResponse.statusCode);
//                        // error
//                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded";
//            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("coin_amount", coinAmount);
//                return params;
//            }
//        };
//        sr.setRetryPolicy(new DefaultRetryPolicy(
//                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
//                MY_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(sr);
//    }
}
