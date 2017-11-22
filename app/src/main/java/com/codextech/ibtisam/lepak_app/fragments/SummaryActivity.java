package com.codextech.ibtisam.lepak_app.fragments;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.SessionManager;
import com.codextech.ibtisam.lepak_app.model.LPTicket;
import com.codextech.ibtisam.lepak_app.realm.RealmController;
import com.codextech.ibtisam.lepak_app.service.ScanService;
import com.codextech.ibtisam.lepak_app.util.DateAndTimeUtils;

import java.io.UnsupportedEncodingException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * Created by HP on 10/18/2017.
 */
public class SummaryActivity extends AppCompatActivity {
    public static final String TAG = "SummaryActivity";
    private Realm realm;
    private TextView tvTicketCount;
    private TextView tvTotalTruck;
    public static final String CAR_NUMBER = "car_number";
    private Button bPrintSummaryPa;
    boolean isCanPrint = true;
    private PosApi mPosSDK;
    private Bitmap mBitmap = null;
    private PrintQueue mPrintQueue = null;
    private byte mGpioPower = 0x1E;// PB14
    private int mCurSerialNo = 3; // usart3
    private int mBaudrate = 4; // 9600
    MediaPlayer player;
    long count = 0;
    private int sum = 0;
    private int sumCar = 0;
    private int sumBike = 0;
    private int sumVan = 0;
    private int sumTruck = 0;
    private int countCar = 0;
    private int countBike = 0;
    private int countVan = 0;
    private int countTruck = 0;
    private SessionManager sessionManager;
    private TextView tvTotalCar;
    private TextView tvTotalBike;
    private TextView tvTotalVan;
    private TextView tvTickettruck;
    private TextView tvCountCar;
    private TextView tvCountBike;
    private TextView tvCountVan;
    private TextView tvCountTruck;
    private TextView tvFairCar;
    private TextView tvFairBike;
    private TextView tvFairVan;
    private TextView tvFairTruck;
    private TextView tvTotal;
    private TextView tvCountTotal;
    private long loginTimeStampLong;
    private Button btPrintSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summaryclass);
        btPrintSummary = (Button) findViewById(R.id.btPrintSummary);
        sessionManager = new SessionManager(this);

        loginTimeStampLong = (long) sessionManager.getLoginTimestamp();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  bPrintSummaryPa = (Button) this.findViewById(bPrintSummaryp);
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

        ///////////////////Total prices////////////////////////
        tvTotalCar = (TextView) findViewById(R.id.tvTotalCar);
        tvTotalBike = (TextView) findViewById(R.id.tvTotalBike);
        tvTotalVan = (TextView) findViewById(R.id.tvTotalVan);
        tvTotalTruck = (TextView) findViewById(R.id.tvTotalTruck);


        //////////////////counts vehichles//////////////////
        tvCountCar = (TextView) findViewById(R.id.tvCountCar);
        tvCountBike = (TextView) findViewById(R.id.tvCountBike);
        tvCountVan = (TextView) findViewById(R.id.tvCountVan);
        tvCountTruck = (TextView) findViewById(R.id.tvCountTruck);

        //////////////////Fairs///////////////////////////


        tvFairCar = (TextView) findViewById(R.id.tvFairCar);
        tvFairBike = (TextView) findViewById(R.id.tvFairBike);
        tvFairVan = (TextView) findViewById(R.id.tvFairVan);
        tvFairTruck = (TextView) findViewById(R.id.tvFairTruck);

        /////////////////////////////////////////////////////

        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvCountTotal = (TextView) findViewById(R.id.tvCountTotal);

        this.realm = RealmController.with(this).getRealm();
        count = realm.where(LPTicket.class).count();
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(config);
        RealmQuery<LPTicket> query = realm.where(LPTicket.class);
        query.equalTo("siteName", sessionManager.getKeySiteName());
        RealmResults<LPTicket> manyLPTicket = query.greaterThan("timeIn", loginTimeStampLong).findAll();
        for (LPTicket oneLPTicket : manyLPTicket) {
            if (oneLPTicket.getPrice().equals("0")) {
                sum = sum + 0;
            } else {
                sum = sum + Integer.parseInt(oneLPTicket.getPrice());
            }
        }
        RealmQuery<LPTicket> querycar = realm.where(LPTicket.class);
        querycar.equalTo("siteName", sessionManager.getKeySiteName());
        querycar.equalTo("vehicleType", "Car");
//        RealmResults<LPTicket> manyLPTicketcar = querycar.findAll();
        RealmResults<LPTicket> manyLPTicketcar = querycar.greaterThan("timeIn", loginTimeStampLong).findAll();
        // RealmQuery<LPTicket> query = realm.where(LPTicket.class).greaterThan("timeIn",loginTimeStamp).lessThan("timeIn", currentTime).findAll();

        Log.d("", "////////////////////////////////sdada///////////////////////////////");

        for (LPTicket oneLPTicket : manyLPTicketcar) {

            if (oneLPTicket.getPrice().equals("0")) {
                sumCar = sumCar + 0;
                countCar++;
            } else {
                sumCar = sumCar + Integer.parseInt(oneLPTicket.getPrice());
                Log.d("  ", "____________________________________________________________________________________Car " + oneLPTicket.getPrice());
                countCar++;
            }
        }
        RealmQuery<LPTicket> querybike = realm.where(LPTicket.class);
        querybike.equalTo("siteName", sessionManager.getKeySiteName());
        querybike.equalTo("vehicleType", "Bike");
        RealmResults<LPTicket> manyLPTicketbike = querybike.greaterThan("timeIn", loginTimeStampLong).findAll();
        Log.d("", "////////////////////////////////sdada///////////////////////////////");

        for (LPTicket oneLPTicket : manyLPTicketbike) {
            if (oneLPTicket.getPrice().equals("0")) {
                sumBike = sumBike + 0;
                Log.d("  ", "____________________________________________________________________________________editTicketToServer: oneLPTicket " + oneLPTicket.getPrice());
                countBike++;
            } else {
                sumBike = sumBike + Integer.parseInt(oneLPTicket.getPrice());
                Log.d("  ", "____________________________________________________________________________________bike " + oneLPTicket.getPrice());
                countBike++;
            }
        }
        RealmQuery<LPTicket> queryvan = realm.where(LPTicket.class);
        queryvan.equalTo("siteName", sessionManager.getKeySiteName());
        queryvan.equalTo("vehicleType", "Van");
        RealmResults<LPTicket> manyLPTicketvan = queryvan.greaterThan("timeIn", loginTimeStampLong).findAll();
        Log.d("", "////////////////////////////////sdada///////////////////////////////");
        for (LPTicket oneLPTicket : manyLPTicketvan) {
            if (oneLPTicket.getPrice().equals("0")) {
                sumVan = sumVan + 0;
                Log.d("  ", "____________________________________________________________________________________editTicketToServer: oneLPTicket " + oneLPTicket.getPrice());
                countVan++;
            } else {
                sumVan = sumVan + Integer.parseInt(oneLPTicket.getPrice());
                Log.d("  ", "____________________________________________________________________________________bike " + oneLPTicket.getPrice());
                countVan++;
            }
        }
        RealmQuery<LPTicket> querytruck = realm.where(LPTicket.class);
        querytruck.equalTo("siteName", sessionManager.getKeySiteName());
        querytruck.equalTo("vehicleType", "Truck");
        RealmResults<LPTicket> manyLPTickettruck = querytruck.greaterThan("timeIn", loginTimeStampLong).findAll();
        Log.d("", "////////////////////////////////sdada///////////////////////////////");
        for (LPTicket oneLPTicket : manyLPTickettruck) {
            if (oneLPTicket.getPrice().equals("0")) {
                sumTruck = sumTruck + 0;
                Log.d("  ", "____________________________________________________________________________________editTicketToServer: oneLPTicket " + oneLPTicket.getPrice());
                countTruck++;
            } else {
                sumTruck = sumTruck + Integer.parseInt(oneLPTicket.getPrice());
                Log.d("  ", "____________________________________________________________________________________bike " + oneLPTicket.getPrice());
                countTruck++;
            }
        }
        Log.d("              ", "totall:           " + sum);
        Log.d("              ", "totall  CAR:           " + sumCar);
        Log.d("              ", "totall  BIKE:           " + sumBike);
        Log.d("              ", "totall  VAN:           " + sumVan);
        Log.d(TAG, "saveTicket: COUNT: " + count);

        count = countCar + countBike + countTruck + countVan;

        tvCountTotal.setText(count + " ");
        tvTotal.setText(sum + "");
        ///////////////////////// set totals/////////////////////////
        tvTotalCar.setText(sumCar + "");
        tvTotalBike.setText(sumBike + "");
        tvTotalVan.setText(sumVan + "");
        tvTotalTruck.setText(sumTruck + "");
        //////////////////////set counts////////////////////////////
        tvCountCar.setText(countCar + "");
        tvCountBike.setText(countBike + "");
        tvCountVan.setText(countVan + "");
        tvCountTruck.setText(countTruck + "");
        ///////////////////////set fairs///////////////////////////
        tvFairCar.setText(sessionManager.getKeyCarAmount() + "");
        tvFairBike.setText(sessionManager.getKeyBikeAmount() + "");
        tvFairVan.setText(sessionManager.getKeyVanAmount() + "");
        tvFairTruck.setText(sessionManager.getKeyTruckAmount() + "");
        /////////////////////////////////////////////////////////////////////
        btPrintSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPrintSummary.setVisibility(View.GONE);
                printMix();
//                storeTicketInRealm();
            }
        });
        mPrintQueue = new PrintQueue(this, ScanService.mApi);
        mPrintQueue.init();
        mPrintQueue.setOnPrintListener(new PrintQueue.OnPrintListener() {
            @Override
            public void onFinish() {
                isCanPrint = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btPrintSummary.setVisibility(View.VISIBLE);
                    }
                });
                finish();
                Toast.makeText(getApplicationContext(), getString(R.string.print_complete), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int state) {
                isCanPrint = true;
                switch (state) {
                    case PosApi.ERR_POS_PRINT_NO_PAPER:
                        showTip(getString(R.string.print_no_paper));
                        player.start();
                        break;
                    case PosApi.ERR_POS_PRINT_FAILED:
                        showTip(getString(R.string.print_failed));
                        player.start();
                        break;
                    case PosApi.ERR_POS_PRINT_VOLTAGE_LOW:
                        showTip(getString(R.string.print_voltate_low));
                        player.start();
                        break;
                    case PosApi.ERR_POS_PRINT_VOLTAGE_HIGH:
                        showTip(getString(R.string.print_voltate_high));
                        player.start();
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
                        Toast.makeText(SummaryActivity.this, "Continued with paper", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(SummaryActivity.this, "Out of paper", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(SummaryActivity.this, "Black mark is detected", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        ////////////////////////////////////////////////////////////////////

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
                            player.start();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

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
            sb.append("\n");
            sb.append("   LEPARK Lahore Parking Company ");
            sb.append("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            sb.append("Zone:-      "+sessionManager.getKeyAreanmae());
            sb.append("\n");
            sb.append("Site Name:- "+sessionManager.getKeySiteName());
            sb.append("\n");
            sb.append("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            sb.append("\n");
            sb.append("   Summary of all tickets     ");
            sb.append("\n");
            sb.append("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            sb.append( "     "+DateAndTimeUtils.getDateTimeStringFromMiliseconds(loginTimeStampLong, "MMM dd,yyyy hh:mm a")+"       ");

            sb.append("\n");
            sb.append("\n");
            sb.append("Veh   Count   Fair   Total");
            sb.append("\n");
            sb.append("Car    "+countCar+"      "+sessionManager.getKeyCarAmount()+"    "+sumCar);
            sb.append("\n");
            sb.append("Bike   "+countBike+"       "+sessionManager.getKeyBikeAmount()+"     "+sumBike);
            sb.append("\n");
            sb.append("Van    "+countVan+"       "+sessionManager.getKeyVanAmount()+"    "+sumVan);
            sb.append("\n");
            sb.append("Truck  "+countTruck+"       "+sessionManager.getKeyTruckAmount()+"     "+sumTruck);
            sb.append("\n");
            sb.append("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            sb.append("Total  "+count+"              "+sum);
            sb.append("\n");
            sb.append("----POWERED BY OUTSTART TECH----");
            sb.append("                                                                                                   ");
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
                                finish();
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

}

