package com.codextech.ibtisam.lepak_app.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.service.ScanService;

public class HomeActivity extends Activity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    public static String EXTRA_MESSAGE = "haye";

    private Button carButton, btAllTickets;
    private EditText enternumber;
    //    private PosApi mPosSDK;
    public static String EXTRA_MESSAGEy = "haye";
    String mess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        carButton = (Button) findViewById(R.id.carButton);
        btAllTickets = (Button) findViewById(R.id.btAllTickets);
        enternumber = (EditText) findViewById(R.id.enterNum);
        Toast.makeText(this, "  " + mess + "  ", Toast.LENGTH_SHORT).show();
//        mPosSDK = App.getInstance().getPosApi();
        // TODO isServiceRunning if service is already running do not restart it again.
        if (!isServiceRunning()) {
            Log.d(TAG, "onCreate: Service is NOT already running");
            Intent newIntent = new Intent(HomeActivity.this, ScanService.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(newIntent);
        } else {
            Log.d(TAG, "onCreate: Service is already running");
        }
        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String B;
                B = enternumber.getText().toString();
                if (B.trim().length() >= 3 && B != null) {


                    Intent intent = new Intent(getApplicationContext(), TicketActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, B);
                    startActivity(intent);
                } else {

                    enternumber.setError("number cannot be empty");
                }
            }
        });
        btAllTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String B;
//                B = enternumber.getText().toString();
                Intent intent = new Intent(getApplicationContext(), AllTicketsActivity.class);
//                intent.putExtra(EXTRA_MESSAGE, B);
                startActivity(intent);
            }
        });

    }

    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.codextech.ibtisma.ScanService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isServiceRunning()) {
            Log.d(TAG, "onCreate: Service is NOT already running");
            Intent newIntent = new Intent(HomeActivity.this, ScanService.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(newIntent);
        } else {
            Log.d(TAG, "onCreate: Service is already running");
        }
    }
}