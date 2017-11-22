package com.codextech.ibtisam.lepak_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import io.realm.Realm;


public class BlackList extends AppCompatActivity {

    private static final String TAG = "BlackList";
    private Realm realm;
    private Button btBlock;
    private Button btUnBlock;
    private EditText etCarNumber;
    private EditText edAlpha;
    private EditText edYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
//        realm = Realm.getInstance(getApplicationContext());
//        RealmController.with(this).refresh();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        btBlock = (Button) findViewById(R.id.btBlock);
//        btUnBlock = (Button) findViewById(R.id.btUnBlock);
//        etCarNumber = (EditText) findViewById(R.id.edNum);
//        edAlpha = (EditText) findViewById(R.id.edAlpha);
//        edYear = (EditText) findViewById(R.id.edYear);
//
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//
//        btBlock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String carNum;
//                if (edYear.getText().toString().equals("")) {
//
//                    carNum = edAlpha.getText().toString() + " " + etCarNumber.getText().toString();
//
//                } else {
//
//
//                    carNum = edAlpha.getText().toString() + "-" + edYear.getText().toString() + "-" + etCarNumber.getText().toString();
//                }
//                if (isValidCarNumber(carNum)) {
//                    RealmQuery<LPTicket> query = realm.where(LPTicket.class);
//                    query.equalTo("number", carNum);
//                    RealmResults<LPTicket> manyLPTicket = query.findAll();
//                    Log.e(TAG, "onCreate: " + manyLPTicket.toString());
//                    if (manyLPTicket.size() > 0) {
//                        Log.d(TAG, "onClick: in   __________________________________________________________________________" + manyLPTicket.first().getTimeOut());
//                        if (manyLPTicket.first().getTimeOut().equals("")) {
//                            tvAgentName.setText(manyLPTicket.first().getSiteName());
//                            tvTimeIn.setText(manyLPTicket.first().getTimeIn());
//                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
//                            Calendar cal = Calendar.getInstance();
//                            ticket_time_out = dateFormat.format(cal.getTime());
//                            tvTimeOut.setText(ticket_time_out);
//                            tvNumber.setText(manyLPTicket.first().getNumber());
//                            tvPrice.setText(manyLPTicket.first().getPrice());
//                            realm.beginTransaction();
//                            manyLPTicket.first().setTimeOut(ticket_time_out);
//                            if (manyLPTicket.first().getSyncStatus() != null) {
//                                if (manyLPTicket.first().getSyncStatus().equals(SyncStatus.SYNC_STATUS_TICKET_ADD_SYNCED)) {
//                                    manyLPTicket.first().setSyncStatus(SyncStatus.SYNC_STATUS_TICKET_EDIT_NOT_SYNCED);
//                                }
//                            } else {
//                                manyLPTicket.first().setSyncStatus(SyncStatus.SYNC_STATUS_TICKET_EDIT_NOT_SYNCED);
//                            }
//                            realm.commitTransaction();
//                            timeDifference(manyLPTicket.first().getTimeIn(), manyLPTicket.first().getTimeOut(), manyLPTicket.first().getPrice());
//                            DataSenderAsync dataSenderAsync = new DataSenderAsync(getActivity());
//                            dataSenderAsync.execute();
//                        } else {
//                            Toast.makeText(getActivity(), "Already Exit ", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(getActivity(), "vehicle doesn't exist", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    etCarNumber.setError("empty field!");
//                }
//            }
//        });
//
//    }
//
//    private boolean isValidCarNumber(String pass) {
//        if (pass != null && pass.length() >= 1) {
//            return true;
//        }
//        return false;
    }

}
