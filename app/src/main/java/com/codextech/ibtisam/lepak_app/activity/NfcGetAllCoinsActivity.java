package com.codextech.ibtisam.lepak_app.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.model.LPNfc;
import com.codextech.ibtisam.lepak_app.realm.RealmController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static android.R.attr.id;

public class NfcGetAllCoinsActivity extends AppCompatActivity {
    private RequestQueue queue;
    private String jsonResponse;
    private String TAGI = "NfcGetAllCoinsActivity";
    private String TAG = "NfcGetAllCoinsActivity";
    private Realm realm;
    private static String TAGQ = "NfcGetAllCoinsActivity";
    private Button BTsave;
    private String coin_id;
    private String coin_amount;
    private String coin_vehicle;
    String coinget;
    private final String[][] techList = new String[][]{
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_get_all_coins);
        queue = Volley.newRequestQueue(NfcGetAllCoinsActivity.this, new HurlStack());
        BTsave = (Button) findViewById(R.id.BNsave);
        this.realm = RealmController.with(this).getRealm();
        RealmController.with(this).refresh();
        getAllLocationsFromServer();
        dataenter();


    }

    @Override
    protected void onResume() {
        super.onResume();
        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            ((TextView) findViewById(R.id.text)).setText(
                    "NFC Tag\n" +
                            ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
            coinget = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));

            RealmQuery<LPNfc> query = realm.where(LPNfc.class);
            query.equalTo("coinId", coinget);
            RealmResults<LPNfc> make = query.findAll();
            ((TextView) findViewById(R.id.text2)).setText(
                    "Amount  : " + (make.first().getCoinAmount()));
            ((TextView) findViewById(R.id.text3)).setText(
                    "Vehicle  no : " + (make.first().getCoinVehicle()));
            int minus = Integer.parseInt(make.first().getCoinAmount());

            int again = minus - 10;
            realm.beginTransaction();

            make.first().setCoinAmount(again + "");

            realm.commitTransaction();
        }
    }

    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }


    private void getAllLocationsFromServer() {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        JsonArrayRequest req = new JsonArrayRequest("http://34.215.56.25/apiLepak/public/api/sites/coins/data",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

//                        RealmConfiguration config = new RealmConfiguration.Builder(RegisterActivity.this).build();
//                        realm = Realm.getInstance(config);

                        try {
                            jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject obj = (JSONObject) response.get(i);

                                // String id = obj.getString("id");
                                coin_id = obj.getString("coin_id");
                                coin_amount = obj.getString("coin_amount");
                                coin_vehicle = obj.getString("coin_vehicle");
                                jsonResponse += "id: " + id + "\n\n";
                                jsonResponse += "coin_id: " + coin_id + "\n\n";
                                jsonResponse += "coin_amount: " + coin_amount + "\n\n";
                                jsonResponse += "coin_vehicle: " + coin_vehicle + "\n\n";

                                LPNfc lpNfc = new LPNfc();

                                lpNfc.setId(RealmController.getInstance().getNfcGet().size() + System.currentTimeMillis());

                                lpNfc.setCoinId(coin_id);
                                lpNfc.setCoinAmount(coin_amount);
                                lpNfc.setCoinVehicle(coin_vehicle);
                                realm.beginTransaction();
                                realm.copyToRealm(lpNfc);
                                realm.commitTransaction();


                            }
                            Log.d(TAGI, jsonResponse.toString());


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    private void dataenter() {

        BTsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RealmQuery<LPNfc> query = realm.where(LPNfc.class);
                query.equalTo("coinId", coinget);
                RealmResults<LPNfc> make = query.findAll();


            }
        });

    }

}
