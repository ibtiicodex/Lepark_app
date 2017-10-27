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

    public TicketSenderAsync(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        sendTicketToServer();

        return null;
    }

    private void sendTicketToServer() {

        RealmConfiguration config = new RealmConfiguration.Builder(context).build();

        realm = Realm.getInstance(config);

        RealmQuery<LPTicket> query = realm.where(LPTicket.class);

        query.equalTo("syncStatus", "ticket_not_synced");

        RealmResults<LPTicket> manyLPTicket = query.findAll();

//        Log.d(TAG, "sendTicketToServer: manyLPTicket: " + manyLPTicket.toString());

        for (LPTicket oneLPTicket : manyLPTicket) {
            Log.d(TAG, "sendTicketToServer: oneLPTicket " + oneLPTicket);
            Log.d(TAG, "sendTicketToServer: oneLPTicket Number " + oneLPTicket.getNumber());
            syncTicket(oneLPTicket.getNumber(), oneLPTicket.getVehicleType(), oneLPTicket.getPrice(), oneLPTicket.getTimeIn());
        }
    }

    private void syncTicket(final String veh_num, final String veh_type, final String fee, final String time_in) {
        RequestQueue queue = Volley.newRequestQueue(context, new HurlStack());

        StringRequest postRequest = new StringRequest(Request.Method.POST, MyUrls.TICKET_SEND,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(context, "Ticket Synced", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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
                params.put("ticket_time", time_in);
                params.put("token", sessionManager.getLoginToken());

                return params;
            }
        };
        queue.add(postRequest);

    }
}
