package com.codextech.ibtisam.lepak_app.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.posapi.PosApi;
import android.util.Log;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.wiget.App;

import java.io.UnsupportedEncodingException;

public class ScanService extends Service {
    public static final String TAG = "test";

    private boolean isOpen = false;
    private int mComFd = -1;
    private final static int SHOW_RECV_DATA = 1;
    // public static ServiceBeepManager beepManager;

    public static PosApi mApi = null;

    private static byte mGpioPower = 0x1E;// PB14
    private static byte mGpioTrig = 0x29;// PC9

    private static int mCurSerialNo = 3; // usart3
    private static int mBaudrate = 4; // 9600


    MediaPlayer player;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ScanService");

        init();

//        initGPIO();

//        IntentFilter mFilter = new IntentFilter();
//        mFilter.addAction(PosApi.ACTION_POS_COMM_STATUS);
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("ismart.intent.scandown");

        player = MediaPlayer.create(getApplicationContext(), R.raw.beep);

        super.onCreate();

    }

    public static void init() {
        Log.d(TAG, "init: ");
        mApi = App.getInstance().getPosApi();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openDevice();
            }
        }, 500);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    public String changeCharset(String str, String newCharset)
            throws UnsupportedEncodingException {
        if (str != null) {
            // Decode the string with the default character encoding.
            byte[] bs = str.getBytes(newCharset);
            // Generate a string with a new character encoding
            return new String(bs, newCharset);
        }
        return null;
    }

    static boolean isIscanScan = false;

    public static void openScan() {
        ScanService.mApi.gpioControl(mGpioTrig, 0, 0);
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
        ScanService.mApi.gpioControl(mGpioTrig, 0, 1);
    }

    private static void openDevice() {
        // open power
        mApi.gpioControl(mGpioPower, 0, 1);

        mApi.extendSerialInit(mCurSerialNo, mBaudrate, 1, 1, 1, 1);
    }

    private void initGPIO() {
        Toast.makeText(getApplicationContext(), "The scan service is initialized", Toast.LENGTH_SHORT).show();

        if (mComFd > 0) {
            isOpen = true;
        } else {
            isOpen = false;
        }
    }

//    @Override
    @Deprecated
//    public void onStart(Intent intent, int startId) {
//        // TODO Auto-generated method stub
//        super.onStart(intent, startId);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                openDevice();
//            }
//        }, 1000);
//    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        mApi.closeDev();
        super.onDestroy();
    }

    private void stopPlaying() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

}
