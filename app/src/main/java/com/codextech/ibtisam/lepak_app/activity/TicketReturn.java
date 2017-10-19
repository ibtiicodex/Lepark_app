package com.codextech.ibtisam.lepak_app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.model.Ticket;
import com.codextech.ibtisam.lepak_app.realm.RealmController;
import com.codextech.ibtisam.lepak_app.util.DateAndTimeUtils;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class TicketReturn extends Activity {
    private static final String TAG = "TicketReturn";
    TextView tvAgentName, tvTimeOut, tvNumber, tvPrice, tvLocation;
    private EditText etCarNumber;
    Button btnPrintMix;
    private TextView tvTimeIn;
    private TextView tvTimeDifference;
    private long timeNowMillis;
    String ticket_time_out;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_screen);
        this.realm = RealmController.with(this).getRealm();
        RealmController.with(this).refresh();

        timeNowMillis = Calendar.getInstance().getTimeInMillis();
        ticket_time_out = DateAndTimeUtils.getDateTimeStringFromMiliseconds(timeNowMillis, "dd/MM/yyyy hh:mm:ss");
        etCarNumber = (EditText) findViewById(R.id.etCarNumber);

        btnPrintMix = (Button) findViewById(R.id.btnPrintMix);

        tvAgentName = (TextView) findViewById(R.id.tvAgentName);

        tvTimeIn = (TextView) findViewById(R.id.IDtime);

        tvTimeOut = (TextView) findViewById(R.id.tvTimeOut);

        tvNumber = (TextView) findViewById(R.id.tvNumber);

        tvPrice = (TextView) findViewById(R.id.tvPrice);

        tvLocation = (TextView) findViewById(R.id.tvLocation);

        tvTimeDifference = (TextView) findViewById(R.id.tvTimeDifference);

        realm = Realm.getInstance(TicketReturn.this);

        btnPrintMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RealmQuery<Ticket> query = realm.where(Ticket.class);

                query.equalTo("number", etCarNumber.getText().toString());

                RealmResults<Ticket> manyTicket = query.findAll();

                Log.e(TAG, "onCreate: " + manyTicket.toString());

                tvAgentName.setText(manyTicket.first().getAgentName());

                tvTimeIn.setText(manyTicket.first().getTimeIn());

                tvTimeOut.setText(ticket_time_out);

                tvNumber.setText(manyTicket.first().getNumber());

                tvPrice.setText(manyTicket.first().getPrice());

                tvLocation.setText(manyTicket.first().getLocation());

                realm.beginTransaction();

                manyTicket.first().setTimeOut(ticket_time_out);
                realm.commitTransaction();


            }
        });
    }
}
