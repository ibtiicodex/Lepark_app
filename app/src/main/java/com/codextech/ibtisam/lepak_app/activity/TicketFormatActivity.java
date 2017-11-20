package com.codextech.ibtisam.lepak_app.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.posapi.PosApi;
import android.posapi.PrintQueue;
import android.posapi.PrintQueue.OnPrintListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.SessionManager;
import com.codextech.ibtisam.lepak_app.model.LPTicket;
import com.codextech.ibtisam.lepak_app.service.ScanService;
import com.codextech.ibtisam.lepak_app.sync.DataSenderAsync;
import com.codextech.ibtisam.lepak_app.sync.SyncStatus;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.Realm;

public class TicketFormatActivity extends AppCompatActivity {
    public static final String TAG = "test";
    public static final String KEY_VEHICLE_TYPE = "key_vehicle_type";
    public static final String VEHICLE_TYPE_CAR = "vehicle_type_car";
    public static final String VEHICLE_TYPE_BIKE = "vehicle_type_bike";
    public static final String VEHICLE_TYPE_VAN = "vehicle_type_van";
    public static final String VEHICLE_TYPE_TRUCK = "vehicle_type_truck";
    public static final String KEY_VEHICLE_NUMBER = "key_vehicle_num";
    private Button btnPrintMix;
    private Bitmap mBitmap = null;
    private PrintQueue mPrintQueue = null;
    private byte mGpioPower = 0x1E;// PB14
    private int mCurSerialNo = 3; // usart3
    private int mBaudrate = 4; // 9600
    MediaPlayer player;
    boolean isCanPrint = true;
    private TextView tvSiteName;
    private TextView tvTimeIn;
    private TextView tvNumber;
    private TextView tvVehicleType;
    private TextView tvPrice;
    private TextView tvLocation;
    String site_name = "";
    String ticket_time_in = "";
    String ticket_time_out = "";
    String veh_number = "";
    String veh_type = "";
    String fee = "";
    String device_location = "31.51,74.34";
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(TicketFormatActivity.this);
        Log.d(TAG, "onCreate: TicketFormatActivity");
        setContentView(R.layout.activity_ticket);
        tvSiteName = (TextView) findViewById(R.id.tvSiteName);
        tvTimeIn = (TextView) findViewById(R.id.Dtime);
        tvNumber = (TextView) findViewById(R.id.Dnumber);
        tvVehicleType = (TextView) findViewById(R.id.tvVehicleType);
        tvPrice = (TextView) findViewById(R.id.Dprice);
        tvLocation = (TextView) findViewById(R.id.Dlocation);
        btnPrintMix = (Button) this.findViewById(R.id.btnPrintMix);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(PosApi.ACTION_POS_COMM_STATUS);
        registerReceiver(receiver, mFilter);
        player = MediaPlayer.create(getApplicationContext(), R.raw.beep);

        Intent intent = getIntent();
        if (intent != null) {
            veh_number = intent.getStringExtra(TicketFormatActivity.KEY_VEHICLE_NUMBER);
            veh_type = intent.getStringExtra(TicketFormatActivity.KEY_VEHICLE_TYPE);
            if (veh_type != null) {
                if (veh_type.equals(TicketFormatActivity.VEHICLE_TYPE_CAR)) {
                    veh_type = "Car";

                    fee = sessionManager.getKeyCarAmount();
                    if (fee.equals("0") || fee.equals("00")) {
                        fee = "0";
                    }

                } else if (veh_type.equals(TicketFormatActivity.VEHICLE_TYPE_BIKE)) {
                    veh_type = "Bike";
                    fee = sessionManager.getKeyBikeAmount();
                    if (fee.equals("0") || fee.equals("00")) {
                        fee = "0";
                    }

                } else if (veh_type.equals(TicketFormatActivity.VEHICLE_TYPE_VAN)) {
                    veh_type = "Van";
                    fee = sessionManager.getKeyVanAmount();
                    if (fee.equals("0") || fee.equals("00")) {
                        fee = "0";
                    }

                } else if (veh_type.equals(TicketFormatActivity.VEHICLE_TYPE_TRUCK)) {
                    veh_type = "Truck";
                    fee = sessionManager.getKeyTruckAmount();
                    if (fee.equals("0") || fee.equals("00")) {
                        fee = "0";
                    }

                }
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Calendar cal = Calendar.getInstance();
        ticket_time_in = dateFormat.format(cal.getTime());
        //  return dateFormat.format(cal.getTime());
        site_name = sessionManager.getKeySiteName();
        tvSiteName.setText(site_name);
        tvTimeIn.setText(ticket_time_in);
        tvNumber.setText(veh_number);
        tvVehicleType.setText(veh_type);
        tvPrice.setText(fee);
        tvLocation.setText(sessionManager.getKeyAreanmae());
        Toast.makeText(this, "  " + veh_number + " ", Toast.LENGTH_SHORT).show();

        btnPrintMix.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPrintMix.setVisibility(View.GONE);
                printMix();
//                storeTicketInRealm();
            }
        });
        mPrintQueue = new PrintQueue(this, ScanService.mApi);
        mPrintQueue.init();
        mPrintQueue.setOnPrintListener(new OnPrintListener() {
            @Override
            public void onFinish() {
                isCanPrint = true;
                storeTicketInRealm();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnPrintMix.setVisibility(View.VISIBLE);
                    }
                });
                finish();
                DataSenderAsync dataSenderAsync = new DataSenderAsync(TicketFormatActivity.this);
                dataSenderAsync.execute();
                Toast.makeText(getApplicationContext(), getString(R.string.print_complete), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int state) {
                isCanPrint = true;
                switch (state) {
                    case PosApi.ERR_POS_PRINT_NO_PAPER:
                        showTip(getString(R.string.print_no_paper));
                        break;
                    case PosApi.ERR_POS_PRINT_FAILED:
                        showTip(getString(R.string.print_failed));
                        break;
                    case PosApi.ERR_POS_PRINT_VOLTAGE_LOW:
                        showTip(getString(R.string.print_voltate_low));
                        break;
                    case PosApi.ERR_POS_PRINT_VOLTAGE_HIGH:
                        showTip(getString(R.string.print_voltate_high));
                        break;
                }
            }

            @Override
            public void onGetState(int arg0) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onGetState: ");
            }

            @Override
            public void onPrinterSetting(int state) {
                isCanPrint = true;
                switch (state) {
                    case 0:
                        Toast.makeText(TicketFormatActivity.this, "Continued with paper", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(TicketFormatActivity.this, "Out of paper", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(TicketFormatActivity.this, "Black mark is detected", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void storeTicketInRealm() {

        Realm realm = Realm.getDefaultInstance();
        LPTicket LPTicket = new LPTicket();
//      LPTicket.setId(RealmController.getInstance().getTickets().size() + System.currentTimeMillis());
        LPTicket.setId(System.currentTimeMillis());
        LPTicket.setSiteName(sessionManager.getKeySiteName());
        LPTicket.setTimeIn(ticket_time_in);
        LPTicket.setTimeOut("");
        LPTicket.setNumber(veh_number);
        LPTicket.setVehicleType(veh_type);
        LPTicket.setPrice(fee);
        LPTicket.setLocation(sessionManager.getKeyAreanmae());
        LPTicket.setSyncStatus(SyncStatus.SYNC_STATUS_TICKET_ADD_NOT_SYNCED);
        realm.beginTransaction();
        realm.copyToRealm(LPTicket);
        realm.commitTransaction();
        Log.d(TAG, "onFinish: mPrintQueue.setOnPrintListener");
        realm.close();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Print Request Received");
            String action = intent.getAction();
            if (action.equalsIgnoreCase(PosApi.ACTION_POS_COMM_STATUS)) {
                int cmdFlag = intent.getIntExtra(PosApi.KEY_CMD_FLAG, -1);
                int status = intent.getIntExtra(PosApi.KEY_CMD_STATUS, -1);
                int bufferLen = intent.getIntExtra(PosApi.KEY_CMD_DATA_LENGTH,
                        0);
                byte[] buffer = intent
                        .getByteArrayExtra(PosApi.KEY_CMD_DATA_BUFFER);

                switch (cmdFlag) {
                    case PosApi.POS_EXPAND_SERIAL_INIT:
                        if (status == PosApi.COMM_STATUS_SUCCESS) {
                            // ed_str.setText("open success\n ");
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            // ed_str.setText("open fail\n");
                            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case PosApi.POS_EXPAND_SERIAL3:
                        if (buffer == null)
                            return;

                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < buffer.length; i++) {
                            if (buffer[i] == 0x0D) {
                                // sb.append("\n");
                            } else {
                                sb.append((char) buffer[i]);
                            }
                        }
                        player.start();
                        try {
                            String str = new String(buffer, "GBK");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                buffer = null;
            }
        }

    };

    private void openDevice() {
        // open power
        ScanService.mApi.gpioControl(mGpioPower, 0, 1);
        ScanService.mApi.extendSerialInit(mCurSerialNo, mBaudrate, 1, 1, 1, 1);
    }

    private void closeDevice() {
        // close power
        ScanService.mApi.gpioControl(mGpioPower, 0, 0);
        ScanService.mApi.extendSerialClose(mCurSerialNo);
    }

    private void addPrintTextWithSize(int size, int concentration, byte[] data) {
        if (data == null)
            return;
        byte[] _2x = new byte[]{0x1b, 0x57, 0x02};
        byte[] _1x = new byte[]{0x1b, 0x57, 0x01};
        byte[] mData = null;
        if (size == 1) {
            mData = new byte[3 + data.length];
            System.arraycopy(_1x, 0, mData, 0, _1x.length);
            System.arraycopy(data, 0, mData, _1x.length, data.length);
            mPrintQueue.addText(concentration, mData);
        } else if (size == 2) {
            mData = new byte[3 + data.length];
            System.arraycopy(_2x, 0, mData, 0, _2x.length);
            System.arraycopy(data, 0, mData, _2x.length, data.length);
            mPrintQueue.addText(concentration, mData);

        }
    }

    private void printMix() {
        try {
            int concentration = 44;
            StringBuilder sb = new StringBuilder();
            sb.append("   LEPARK Lahore Parking Company     ");
            sb.append("\n");
            sb.append("        PARKING TICKET     ");
            sb.append("\n");
            sb.append("Site Name:  " + site_name);
            sb.append("\n");
            sb.append("Time:       " + ticket_time_in);
            sb.append("\n");
            sb.append("No. Plate:  " + veh_number);
            sb.append("\n");
            sb.append("Type:       " + veh_type);
            sb.append("\n");
            if(fee.equals("0")) {
                sb.append("Fee:        " + "Free Parking");
            }
            else
            {
                sb.append("Fee:        " +fee);

            }
            sb.append("\n");
            sb.append("Zone:       " + sessionManager.getKeyAreanmae());
            sb.append("\n");
            sb.append("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            sb.append("   Parking at your own risk");
            sb.append("\n");
            sb.append("Parking company is not liable");
            sb.append("\n");
            sb.append("for any loss");
            sb.append("\n");
            sb.append("Complaint No:    " + "042-35116657");
            sb.append("\n");
            sb.append("\n");
            sb.append("----POWERED BY OUTSTART TECH----");
            //sb.append("\n");
            sb.append("**************END***************");
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");

            byte[] text = null;
            text = sb.toString().getBytes("GBK");
            addPrintTextWithSize(1, concentration, text);
            sb = new StringBuilder();
            sb.append("\n");
            text = sb.toString().getBytes("GBK");
            addPrintTextWithSize(1, concentration, text);
            mPrintQueue.printStart();
            //TODO if ticket is printed successfull then do this
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void showTip(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.tips))
                .setMessage(msg)
                .setNegativeButton(getString(R.string.close),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        openDevice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDevice();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBitmap != null) {
            mBitmap.recycle();
        }

        if (mPrintQueue != null) {
            mPrintQueue.close();
        }
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
