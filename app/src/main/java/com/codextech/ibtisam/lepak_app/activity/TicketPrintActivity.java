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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.model.Ticket;
import com.codextech.ibtisam.lepak_app.realm.RealmController;
import com.codextech.ibtisam.lepak_app.service.ScanService;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import io.realm.Realm;
public class TicketPrintActivity extends Activity {
    public static final String CAR_NUMBER = "car_number";
    private Button btnPrintMix;
    private Bitmap mBitmap = null;
    private PrintQueue mPrintQueue = null;
    private byte mGpioPower = 0x1E;// PB14
    private int mCurSerialNo = 3; // usart3
    private int mBaudrate = 4; // 9600
    MediaPlayer player;
    boolean isCanPrint = true;
    TextView tvagent,
             tvtime,
             tvnumber,
             tvprice,
             tvlocation;
    private Realm realm;
    String ticket_time = "";
    String ticket_time_Out = "";
    String veh_number = "";
    String agent_name = "Ali";
    String veh_type = "car";
    String fee = "20";
    String device_location = "Liberty";
    private long timeNowMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        this.realm = RealmController.with(this).getRealm();
        RealmController.with(this).refresh();

        Intent intenti = getIntent();
        veh_number = intenti.getStringExtra(TicketPrintActivity.CAR_NUMBER);
        timeNowMillis = Calendar.getInstance().getTimeInMillis();
        //ticket_time = DateAndTimeUtils.getDateTimeStringFromMiliseconds(timeNowMillis, "dd/MM/yyyy hh:mm:ss");
        ticket_time = DateFormat.getDateTimeInstance().format(new Date());

        tvagent = (TextView) findViewById(R.id.Dname);
        tvtime = (TextView) findViewById(R.id.Dtime);
        tvnumber = (TextView) findViewById(R.id.Dnumber);
        tvprice = (TextView) findViewById(R.id.Dprice);
        tvlocation = (TextView) findViewById(R.id.Dlocation);
        tvagent.setText(agent_name);
        tvtime.setText(ticket_time);
        tvnumber.setText(veh_number);
        tvprice.setText(fee);
        tvlocation.setText(device_location);
        Toast.makeText(this, "  " + veh_number + " ", Toast.LENGTH_SHORT).show();

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
//                Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
//                startActivity(intent);
                isCanPrint = true;

                finish();
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
                        Toast.makeText(TicketPrintActivity.this, "Continued with paper", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(TicketPrintActivity.this, "Out of paper", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(TicketPrintActivity.this, "Black mark is detected", Toast.LENGTH_SHORT).show();
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
            sb.append(agent_name);
            sb.append("\n");
            sb.append("Time:  " + ticket_time);
            sb.append("\n");
            sb.append("Veh Reg No:  " + veh_number);
            sb.append("\n");
            sb.append("Veh Type:  Car");
            sb.append("\n");
            sb.append("Parking Fee:  20");
            sb.append("\n");
            sb.append("Location:  " + device_location);
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
            saveTicket(agent_name, "" + ticket_time, ticket_time_Out, veh_number, veh_type, fee, device_location);
            mPrintQueue.printStart();
            //TODO if ticket is printed successfull then do this
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void saveTicket(String agent_name, String ticket_time_in, String ticket_time_out, String veh_number, String veh_type, String fee, String device_location) {

        if (agent_name != null && ticket_time_in != null && veh_number != null && veh_type != null && fee != null && device_location != null) {

            Ticket ticket = new Ticket();
            ticket.setId(RealmController.getInstance().getBooks().size() + System.currentTimeMillis());
            ticket.setAgentName(agent_name);
            ticket.setTimeIn(ticket_time_in);
            ticket.setTimeOut(ticket_time_out);
            ticket.setNumber(veh_number);
            ticket.setPrice(fee);
            ticket.setLocation(device_location);
            realm.beginTransaction();
            realm.copyToRealm(ticket);
            realm.commitTransaction();
            Toast.makeText(TicketPrintActivity.this, "Entry Saved" + RealmController.getInstance().getBooks().size() + System.currentTimeMillis(), Toast.LENGTH_SHORT).show();
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
