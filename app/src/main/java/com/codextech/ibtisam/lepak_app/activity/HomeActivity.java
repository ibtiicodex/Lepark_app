package com.codextech.ibtisam.lepak_app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.posapi.PosApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.service.ScanService;
import com.codextech.ibtisam.lepak_app.wiget.App;

public class HomeActivity extends Activity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private Button carButton, btAllTickets, Exit;
    private EditText enternumber;
    private PosApi mPosSDK;
    String mess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        carButton = (Button) findViewById(R.id.carButton);

        //Exit = (Button) findViewById(R.id.exit);

       // btAllTickets = (Button) findViewById(R.id.btAllTickets);

        enternumber = (EditText) findViewById(R.id.enterNum);



        mPosSDK = App.getInstance().getPosApi();

        // TODO isServiceRunning if service is already running do not restart it again.

        Log.d(TAG, "onCreate: Service is NOT already running");

        Intent newIntent = new Intent(HomeActivity.this, ScanService.class);

        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startService(newIntent);

        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String carNum;

                carNum = enternumber.getText().toString();

                if (carNum.trim().length() >= 3 && carNum != null) {

                    Intent intent = new Intent(getApplicationContext(), TicketPrintActivity.class);

                    intent.putExtra(TicketPrintActivity.CAR_NUMBER, carNum);

                    startActivity(intent);

                } else {

                    enternumber.setError("tvNumber cannot be empty");
                }
            }
        });
//        btAllTickets.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(getApplicationContext(), AllTicketsActivity.class);
//
//                startActivity(intent);
//            }
//        });
//        Exit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(getApplicationContext(), TicketReturn.class);
//
//                startActivity(intent);
//            }
//        });

    }

}