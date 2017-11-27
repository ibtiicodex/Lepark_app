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
import com.codextech.ibtisam.lepak_app.sync.DataSenderAsync;
import com.codextech.ibtisam.lepak_app.sync.SyncStatus;
import com.codextech.ibtisam.lepak_app.util.DateAndTimeUtils;

import java.text.DateFormat;
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
    long ticket_time_out;
    private Realm realm;
    private TextView tvTotallPrice;
//    private EditText edAlpha;
//    private EditText edYear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.retrun_ticket_fragment, container, false);
        realm = Realm.getInstance(getContext());
        RealmController.with(this).refresh();
        etCarNumber = (EditText) view.findViewById(R.id.edNum);
//        edAlpha = (EditText) view.findViewById(R.id.edAlpha);
//        edYear = (EditText) view.findViewById(R.id.edYear);
        btnPrintMix = (Button) view.findViewById(R.id.btnPrintMix);
        tvAgentName = (TextView) view.findViewById(R.id.tvAgentName);
        tvTimeIn = (TextView) view.findViewById(R.id.tvTimeIn);
        tvTimeOut = (TextView) view.findViewById(R.id.tvTimeOut);
        tvNumber = (TextView) view.findViewById(R.id.tvNumber);
        tvPrice = (TextView) view.findViewById(R.id.tvPrice);
        tvTimeDifference = (TextView) view.findViewById(R.id.tvTotalHours);
        tvTotallPrice = (TextView) view.findViewById(R.id.tvTotallPrice);
        etCarNumber.setText("");

        btnPrintMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carNum;

                    carNum =  etCarNumber.getText().toString().toUpperCase();

                if (isValidCarNumber(carNum)) {
                    RealmQuery<LPTicket> query = realm.where(LPTicket.class);
                    query.equalTo("number", carNum);
                    RealmResults<LPTicket> manyLPTicket = query.findAll();
                    Log.e(TAG, "onCreate: " + manyLPTicket.toString());
                    if (manyLPTicket.size() > 0) {
                        Log.d(TAG, "onClick: in   __________________________________________________________________________" + manyLPTicket.first().getTimeOut());
                        if (manyLPTicket.first().getTimeOut() == 00L) {
                            tvAgentName.setText(manyLPTicket.first().getSiteName());
                            tvTimeIn.setText(DateAndTimeUtils.getDateTimeStringFromMiliseconds(manyLPTicket.first().getTimeIn(), "yyyy-MM-dd kk:mm:ss"));
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                            Calendar cal = Calendar.getInstance();
                            ticket_time_out = Calendar.getInstance().getTimeInMillis();
//                            ticket_time_out = dateFormat.format(cal.getTime());
                            tvTimeOut.setText(DateAndTimeUtils.getDateTimeStringFromMiliseconds(ticket_time_out, "yyyy-MM-dd kk:mm:ss"));
                            tvNumber.setText(manyLPTicket.first().getNumber());
                            tvPrice.setText(manyLPTicket.first().getPrice());
                            realm.beginTransaction();
                            manyLPTicket.first().setTimeOut(ticket_time_out);
                            if (manyLPTicket.first().getSyncStatus() != null) {
                                if (manyLPTicket.first().getSyncStatus().equals(SyncStatus.SYNC_STATUS_TICKET_ADD_SYNCED)) {
                                    manyLPTicket.first().setSyncStatus(SyncStatus.SYNC_STATUS_TICKET_EDIT_NOT_SYNCED);
                                }
                            } else {
                                manyLPTicket.first().setSyncStatus(SyncStatus.SYNC_STATUS_TICKET_EDIT_NOT_SYNCED);
                            }
                            realm.commitTransaction();
                            timeDifference(DateAndTimeUtils.getDateTimeStringFromMiliseconds(manyLPTicket.first().getTimeIn(), "yyyy-MM-dd kk:mm:ss"), DateAndTimeUtils.getDateTimeStringFromMiliseconds(manyLPTicket.first().getTimeOut(), "yyyy-MM-dd kk:mm:ss"), manyLPTicket.first().getPrice());
                            DataSenderAsync dataSenderAsync = new DataSenderAsync(getActivity());
                            dataSenderAsync.execute();
                            etCarNumber.setText("");
                        } else {
                            Toast.makeText(getActivity(), "Already Exit ", Toast.LENGTH_SHORT).show();
                            etCarNumber.setText("");
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
    }

    public void printDifference(Date startDate, Date endDate, String fee) {
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
        if (elapsedHours == 0 && elapsedHours <= 1) {
            tvTotallPrice.setText(price + "");
        } else {
            tvTotallPrice.setText("" + elapsedHours * price);
        }
        tvTimeDifference.setText(elapsedHours + " h - " + elapsedMinutes + " m");
    }

}
