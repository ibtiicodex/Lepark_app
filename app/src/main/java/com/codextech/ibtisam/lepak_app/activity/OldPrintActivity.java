package com.codextech.ibtisam.lepak_app.activity;

import android.app.Activity;
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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.adapters.BooksAdapter;
import com.codextech.ibtisam.lepak_app.model.Book;
import com.codextech.ibtisam.lepak_app.service.ScanService;
import com.codextech.ibtisam.lepak_app.util.BarcodeCreater;
import com.codextech.ibtisam.lepak_app.util.BitmapTools;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;

import io.realm.Realm;

public class OldPrintActivity extends Activity {
    private Button btnPrintMix;
    private Bitmap mBitmap = null;
    private PrintQueue mPrintQueue = null;
    private byte mGpioPower = 0x1E;// PB14
    private int mCurSerialNo = 3; // usart3
    private int mBaudrate = 4; // 9600
    private static final int REQUEST_EX = 1;
    MediaPlayer player;
    boolean isCanPrint = true;

    TextView agent, time, number, price, location;
    private RecyclerView recycler;
    public static String EXTRA_MESSAGE1 = "haye";
    private Realm realm;
    private BooksAdapter adapter;
    Button Main;
    String name = "Ali";
    RequestQueue queue;
    String pr = "20";
    String Loocation = "Liberty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//        Intent intent = getIntent();
//        String mess = intent.getStringExtra(HomeActivity.EXTRA_MESSAGE);
        agent = (TextView) findViewById(R.id.Dname);
        time = (TextView) findViewById(R.id.Dtime);
        number = (TextView) findViewById(R.id.Dnumber);
        price = (TextView) findViewById(R.id.Dprice);
        location = (TextView) findViewById(R.id.Dlocation);
        agent.setText("Ali");
        time.setText(currentDateTimeString);
//        number.setText(mess);
        price.setText("20");
        location.setText("Liberty Market");

        btnPrintMix = (Button) this.findViewById(R.id.btnPrintMix);
        btnPrintMix.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                printMix();
            }
        });
        mPrintQueue = new PrintQueue(this, ScanService.mApi);
        mPrintQueue.init();
        mPrintQueue.setOnPrintListener(new OnPrintListener() {
            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), getString(R.string.print_complete), Toast.LENGTH_SHORT).show();
                isCanPrint = true;
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
            }

            @Override
            public void onPrinterSetting(int state) {
                isCanPrint = true;
                switch (state) {
                    case 0:
                        Toast.makeText(OldPrintActivity.this, "Continued with paper", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(OldPrintActivity.this, "Out of paper", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(OldPrintActivity.this, "Black mark is detected", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(PosApi.ACTION_POS_COMM_STATUS);
        registerReceiver(receiver, mFilter);
        player = MediaPlayer.create(getApplicationContext(), R.raw.beep);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
                        } else {
                            // ed_str.setText("open fail\n");
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
            sb.append("Agent Name: ");
            sb.append("Ali");
            sb.append("\n");
            sb.append("Time:  2:22:37pm");
            sb.append("\n");
            sb.append("Veh Reg No:  1234567890");
            sb.append("\n");
            sb.append("Veh Type:  Car");
            sb.append("\n");
            sb.append("Parking Fee:  20");
            sb.append("\n");
            sb.append("Location:  Liberty Market");
            sb.append("\n");
            sb.append("--------------------------------");
            sb.append("   Parking at your own risk");
            sb.append("\n");
            sb.append("Parking company is not liable for any loss");
            sb.append("\n");
            byte[] text = null;
            text = sb.toString().getBytes("GBK");
            addPrintTextWithSize(1, concentration, text);

            sb = new StringBuilder();
            sb.append("\n");
            text = sb.toString().getBytes("GBK");
            addPrintTextWithSize(1, concentration, text);

            int mWidth = 300;
            int mHeight = 60;
            mBitmap = BarcodeCreater.creatBarcode(getApplicationContext(), "1234567890", mWidth, mHeight, true, 1);
            byte[] printData = BitmapTools.bitmap2PrinterBytes(mBitmap);
            mPrintQueue.addBmp(concentration, 30, mBitmap.getWidth(), mBitmap.getHeight(), printData);

            mWidth = 150;
            mHeight = 150;

            mBitmap = BarcodeCreater.encode2dAsBitmap("1234567890", mWidth, mHeight, 2);
            printData = BitmapTools.bitmap2PrinterBytes(mBitmap);
            mPrintQueue.addBmp(concentration, 100, mBitmap.getWidth(), mBitmap.getHeight(), printData);


            mPrintQueue.printStart();

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
