package com.codextech.ibtisam.lepak_app.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import com.codextech.ibtisam.lepak_app.sync.DataSenderAsync;
import com.codextech.ibtisam.lepak_app.sync.SyncStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static android.R.attr.id;

public class NfcGetAllCoinsActivity extends AppCompatActivity {
    // list of NFC technologies detected:

    private RequestQueue queue;
    private String jsonResponse;
    private String TAGI = "NfcGetAllCoinsActivity";
    private String TAG = "NfcGetAllCoinsActivity";
    private Realm realm;
    private String coin_id;
    private String coin_amount;
    private String coin_vehicle;
    private long idserver;
    String coinget;

    TextView tvCoinID;
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
    private Button btDetecta;
    private TextView tvAmountNfc;
    private TextView tvVehicleNfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_get_all_coins);
//        getAllLocationsFromServer();
        tvCoinID = (TextView) findViewById(R.id.text);
        tvAmountNfc = (TextView) findViewById(R.id.tvAmountNfc);
        tvVehicleNfc = (TextView) findViewById(R.id.tvVehicleNfc);
        btDetecta = (Button) findViewById(R.id.btDetecta);

        queue = Volley.newRequestQueue(NfcGetAllCoinsActivity.this, new HurlStack());
        getAllLocationsFromServer();
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        Toast.makeText(this, " Mack Address         " + address, Toast.LENGTH_LONG).show();
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
            tvCoinID.setText(
                    " " +
                            ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
            coinget = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
            RealmQuery<LPNfc> query = realm.where(LPNfc.class);
            query.equalTo("coinId", coinget);
            RealmResults<LPNfc> make = query.findAll();
            tvAmountNfc.setText("Amount  : " + (make.first().getCoinAmount()));
            tvVehicleNfc.setText("Vehicle  no : " + (make.first().getCoinVehicle()));
            btDetecta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RealmQuery<LPNfc> query = realm.where(LPNfc.class);
                    query.equalTo("coinId", coinget);
                    RealmResults<LPNfc> make = query.findAll();
                    tvAmountNfc.setText("Amount  : " + (make.first().getCoinAmount()));
                    tvVehicleNfc.setText("Vehicle  no : " + (make.first().getCoinVehicle()));
                    int minus = Integer.parseInt(make.first().getCoinAmount());
                    int again = minus - 10;
                    realm.beginTransaction();
                    make.first().setCoinAmount(again + "");
                    make.first().setSyncStatus(SyncStatus.SYNC_STATUS_COIN_EDIT_NOT_SYNCED);
                    realm.commitTransaction();
                    tvAmountNfc.setText("Amount  : " + (make.first().getCoinAmount()));
                    tvVehicleNfc.setText("Vehicle  no : " + (make.first().getCoinVehicle()));
                    btDetecta.setVisibility(View.GONE);
                    DataSenderAsync dataSenderAsync = new DataSenderAsync(NfcGetAllCoinsActivity.this);
                    dataSenderAsync.execute();
                }
            });
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
                        RealmConfiguration config = new RealmConfiguration.Builder(NfcGetAllCoinsActivity.this).build();
                        realm = Realm.getInstance(config);
                        try {
                            jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = (JSONObject) response.get(i);
                                idserver = obj.getLong("id");
                                coin_id = obj.getString("coin_id");
                                coin_amount = obj.getString("coin_amount");
                                coin_vehicle = obj.getString("coin_vehicle");
                                jsonResponse += "id: " + id + "\n\n";
                                jsonResponse += "coin_id: " + coin_id + "\n\n";
                                jsonResponse += "coin_amount: " + coin_amount + "\n\n";
                                jsonResponse += "coin_vehicle: " + coin_vehicle + "\n\n";
                                LPNfc lpNfc = new LPNfc();
                                RealmQuery<LPNfc> query = realm.where(LPNfc.class);
                                query.equalTo("serverId", idserver);
                                RealmResults<LPNfc> al = query.findAll();
                                Log.d(TAG, "allLocations: " + al.toString());
                                if (al.isEmpty()) {
                                    lpNfc.setId(RealmController.getInstance().getNfcGet().size() + System.currentTimeMillis());
                                    lpNfc.setServerId(idserver);
                                    lpNfc.setCoinId(coin_id);
                                    lpNfc.setCoinAmount(coin_amount);
                                    lpNfc.setCoinVehicle(coin_vehicle);
                                    realm.beginTransaction();
                                    realm.copyToRealm(lpNfc);
                                    realm.commitTransaction();
                                } else {
                                    Log.d(TAG, "Already Exists");
                                }

//                                RealmQuery<LPLocation> query = realm.where(LPLocation.class);
//                                query.equalTo("id", id);
//                                RealmResults<LPLocation> allLocations = query.findAll();
//                                Log.d(TAG, "allLocations: " + allLocations.toString());
//
//                                // Duplication avoidance check
//                                if (allLocations.isEmpty()) {
//                                    Log.d(TAG, "Location doesn't exist adding it.");
//                                    LPLocation lpLocation = new LPLocation();
//                                    lpLocation.setId(id);
//                                    lpLocation.setLocationName(location);
//                                    lpLocation.setCityId(city_id);
//                                    realm.beginTransaction();
//                                    realm.copyToRealm(lpLocation);
//                                    realm.commitTransaction();
//                                } else {
//                                    Log.d(TAG, "Already Exists");
//                                }
                            }
                            Log.d(TAGI, jsonResponse.toString());
                            RealmQuery<LPNfc> query = realm.where(LPNfc.class);
                            // query.equalTo("coinId", coinget);
                            RealmResults<LPNfc> make = query.findAll();
                            Log.d(TAGI, jsonResponse.toString() + make);


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

        btDetecta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
