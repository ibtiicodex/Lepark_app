package com.codextech.ibtisam.lepak_app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.model.Ticket;
import com.codextech.ibtisam.lepak_app.realm.RealmController;
import com.codextech.ibtisam.lepak_app.util.DateAndTimeUtils;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by HP on 10/18/2017.
 */

public class ReturnTicketFragment extends Fragment {
    private static final String TAG = "TicketReturn";
    TextView tvAgentName, tvTimeOut, tvNumber, tvPrice, tvLocation;
    private EditText etCarNumber;
    Button btnPrintMix;
    private TextView tvTimeIn;
    private TextView tvTimeDifference;
    private long timeNowMillis;
    String ticket_time_out;
    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.retrun_ticket_fragment, container, false);
        this.realm = RealmController.with(this).getRealm();
        RealmController.with(this).refresh();
        timeNowMillis = Calendar.getInstance().getTimeInMillis();
        ticket_time_out = DateAndTimeUtils.getDateTimeStringFromMiliseconds(timeNowMillis, "yyyy-MM-dd kk:mm:ss");
        etCarNumber = (EditText) view.findViewById(R.id.etCarNumber);

        btnPrintMix = (Button) view.findViewById(R.id.btnPrintMix);

        tvAgentName = (TextView) view.findViewById(R.id.tvAgentName);

        tvTimeIn = (TextView) view.findViewById(R.id.IDtime);

        tvTimeOut = (TextView) view.findViewById(R.id.tvTimeOut);

        tvNumber = (TextView) view.findViewById(R.id.tvNumber);

        tvPrice = (TextView) view.findViewById(R.id.tvPrice);

        tvLocation = (TextView) view.findViewById(R.id.tvLocation);
        realm = Realm.getInstance(getContext());
//
        tvTimeDifference = (TextView) view.findViewById(R.id.tvTimeDifference);


        btnPrintMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String carNum = etCarNumber.getText().toString();

                if (isValidCarNumber(carNum)) {

                    RealmQuery<Ticket> query = realm.where(Ticket.class);

                    query.equalTo("number", carNum );

                    RealmResults<Ticket> manyTicket = query.findAll();

                    Log.e(TAG, "onCreate: " + manyTicket.toString());

                        if (manyTicket.size() > 0) {

                            tvAgentName.setText(manyTicket.first().getAgentName());

                            tvTimeIn.setText(manyTicket.first().getTimeIn());

                            tvTimeOut.setText(ticket_time_out);

                            tvNumber.setText(manyTicket.first().getNumber());

                            tvPrice.setText(manyTicket.first().getPrice());

                            tvLocation.setText(manyTicket.first().getLocation());

                            realm.beginTransaction();

                            manyTicket.first().setTimeOut(ticket_time_out);
                            realm.commitTransaction();


                        } else {

                            Toast.makeText(getActivity(), "vehicle doesn't exist", Toast.LENGTH_SHORT).show();

                        }

                }else {
                    etCarNumber.setError("empty field!");
                }
            }
        });

        return view;
    }

    private boolean isValidCarNumber(String pass) {
        if (pass != null && pass.length() >= 1) {
            return true;
        }
        return false;
    }

}
