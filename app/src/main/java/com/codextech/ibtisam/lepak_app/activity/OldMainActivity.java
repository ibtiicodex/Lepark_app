package com.codextech.ibtisam.lepak_app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.posapi.PosApi;
import android.posapi.PosApi.OnCommEventListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.service.ScanService;
import com.codextech.ibtisam.lepak_app.util.DialogUtils;
import com.codextech.ibtisam.lepak_app.util.ProgressDialogUtils;
import com.codextech.ibtisam.lepak_app.wiget.App;

public class OldMainActivity extends Activity {
    private PosApi mPosSDK;
    private Button btPrintActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btPrintActivity = (Button) this.findViewById(R.id.btPrintActivity);
        mPosSDK = App.getInstance().getPosApi();
        mPosSDK.setOnComEventListener(mCommEventListener);
        mPosSDK.setOnDeviceStateListener(onDeviceStateListener);

        Intent newIntent = new Intent(OldMainActivity.this, ScanService.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(newIntent);

        btPrintActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OldMainActivity.this, TicketActivity.class));
            }
        });

    }

    OnCommEventListener mCommEventListener = new PosApi.OnCommEventListener() {
        @Override
        public void onCommState(int cmdFlag, int state, byte[] resp, int respLen) {
            switch (cmdFlag) {
                case PosApi.POS_INIT:
                    if (state == PosApi.COMM_STATUS_SUCCESS) {
                        Toast.makeText(getApplicationContext(), "The device was initialized successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Device initialization failed", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private PosApi.OnDeviceStateListener onDeviceStateListener = new PosApi.OnDeviceStateListener() {
        public void OnGetState(int state, String version, String serialNo, int psam1, int psam2, int ic, int swipcard, int printer) {
            ProgressDialogUtils.dismissProgressDialog();
            if (state == PosApi.COMM_STATUS_SUCCESS) {

                StringBuilder sb = new StringBuilder();
                String mPsam1 = null;
                switch (psam1) {
                    case 0:
                        mPsam1 = getString(R.string.state_normal);
                        break;
                    case 1:
                        mPsam1 = getString(R.string.state_no_card);
                        break;
                    case 2:
                        mPsam1 = getString(R.string.state_card_error);
                        break;
                }

                String mPsam2 = null;
                switch (psam2) {
                    case 0:
                        mPsam2 = getString(R.string.state_normal);
                        break;
                    case 1:
                        mPsam2 = getString(R.string.state_no_card);
                        break;
                    case 2:
                        mPsam2 = getString(R.string.state_card_error);
                        break;
                }

                String mIc = null;
                switch (ic) {
                    case 0:
                        mIc = getString(R.string.state_normal);
                        break;
                    case 1:
                        mIc = getString(R.string.state_no_card);
                        break;
                    case 2:
                        mIc = getString(R.string.state_card_error);
                        break;
                }

                String magnetic_card = null;
                switch (swipcard) {
                    case 0:
                        magnetic_card = getString(R.string.state_normal);
                        break;
                    case 1:
                        magnetic_card = getString(R.string.state_fault);
                        break;

                }

                String mPrinter = null;
                switch (printer) {
                    case 0:
                        mPrinter = getString(R.string.state_normal);
                        break;
                    case 1:
                        mPrinter = getString(R.string.state_no_paper);
                        break;

                }

                sb.append(/*getString(R.string.pos_status)+"\n "
                            +*/
                        getString(R.string.psam1_) + mPsam1 + "\n" //pasm1
                                + getString(R.string.psam2) + mPsam2 + "\n" //pasm2
                                + getString(R.string.ic_card) + mIc + "\n" //card
                                + getString(R.string.magnetic_card) + magnetic_card + "\n" //Magnetic stripe cards
                                + getString(R.string.printer) + mPrinter + "\n" //printer
                );

                sb.append(getString(R.string.pos_serial_no) + serialNo + "\n");
                sb.append(getString(R.string.pos_firmware_version) + version);
                DialogUtils.showTipDialog(OldMainActivity.this, sb.toString());

            } else {
                DialogUtils.showTipDialog(OldMainActivity.this, getString(R.string.get_pos_status_failed));
            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //这Must do a double judgment, or there will be two dialog box
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            this.finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProgressDialogUtils.dismissProgressDialog();
    }
}

