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
import com.codextech.ibtisam.lepak_app.model.LPTicket;
import com.codextech.ibtisam.lepak_app.realm.RealmController;
import com.codextech.ibtisam.lepak_app.util.DateAndTimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by HP on 10/18/2017.
 */
public class ReturnTicketFragment extends Fragment {
    private static final String TAG = "TicketReturn";
    TextView tvAgentName, tvTimeOut, tvNumber, tvPrice, tvTotalHure;
    private EditText etCarNumber;
    Button btnPrintMix;
    private TextView tvTimeIn;
    private TextView tvTimeDifference;
    private long timeNowMillis;
    String ticket_time_out;
    private Realm realm;
    private TextView tvTotallPrice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.retrun_ticket_fragment, container, false);
//        this.realm = RealmController.with(this).getRealm();
        realm = Realm.getInstance(getContext());
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


//
        tvTimeDifference = (TextView) view.findViewById(R.id.tvTotalHours);
        tvTotallPrice = (TextView) view.findViewById(R.id.tvTotallPrice);


        btnPrintMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String carNum = etCarNumber.getText().toString();

                if (isValidCarNumber(carNum)) {

                    RealmQuery<LPTicket> query = realm.where(LPTicket.class);

                    query.equalTo("number", carNum);

                    RealmResults<LPTicket> manyLPTicket = query.findAll();

                    Log.e(TAG, "onCreate: " + manyLPTicket.toString());


                    if (manyLPTicket.size() > 0) {

//                         if(manyLPTicket.first().getTimeOut()=="") {
                        Log.d(TAG, "onClick: in   __________________________________________________________________________" + manyLPTicket.first().getTimeOut());

                          if (manyLPTicket.first().getTimeOut().equals("")) {
                        tvAgentName.setText(manyLPTicket.first().getSiteName());

                        tvTimeIn.setText(manyLPTicket.first().getTimeIn());

                        tvTimeOut.setText(ticket_time_out);

                        tvNumber.setText(manyLPTicket.first().getNumber());

                        tvPrice.setText(manyLPTicket.first().getPrice());


                        realm.beginTransaction();

                        manyLPTicket.first().setTimeOut(ticket_time_out);

                        timeDifference(manyLPTicket.first().getTimeIn(), manyLPTicket.first().getTimeOut(), manyLPTicket.first().getPrice());


                        realm.commitTransaction();

                        } else {

                            Toast.makeText(getActivity(), "Already Exit ", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(getActivity(), "vehicle doesn't exist", Toast.LENGTH_SHORT).show();

                    }

                } else {
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

    @Override
    public void onResume() {
        super.onResume();
        etCarNumber.setText("");
    }

    public void timeDifference(String in, String out, String fee) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

        try {
            Date date1 = simpleDateFormat.parse(in);
            Date date2 = simpleDateFormat.parse(out);

            printDifference(date1, date2, fee);

        } catch (ParseException e) {
            e.printStackTrace();
        }

//1 minute = 60 seconds
//1 hour = 60 x 60 = 3600
//1 day = 3600 x 24 = 86400


    }

    public void printDifference(Date startDate, Date endDate, String fee) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
        long elapsedSeconds = different / secondsInMilli;
        long price = Long.parseLong(fee);
        tvTotallPrice.setText("" + elapsedHours * price);
        tvTimeDifference.setText(elapsedHours +" h - " + elapsedMinutes +" m");

    }
}
