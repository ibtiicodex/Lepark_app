package com.codextech.ibtisam.lepak_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.posapi.PosApi;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.activity.TicketFormatActivity;
import com.codextech.ibtisam.lepak_app.service.ScanService;
import com.codextech.ibtisam.lepak_app.wiget.App;

/**
 * Created by HP on 10/18/2017.
 */

public class PrintTicketFragment extends Fragment {
    public static final String TAG = "test";
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    private Button carButton;
    private EditText enternumber;
    FragmentManager manager;
    private PosApi mPosSDK;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: PrintTicketFragment");
        View view = inflater.inflate(R.layout.print_ticket_fragment, container, false);
        enternumber = (EditText) view.findViewById(R.id.enterNum);
        carButton = (Button) view.findViewById(R.id.carButton);
        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carNum = enternumber.getText().toString();
                if (carNum.trim().length() >= 3 && carNum != null) {
                    Intent intent = new Intent(getActivity(), TicketFormatActivity.class);
                    intent.putExtra(TicketFormatActivity.CAR_NUMBER, carNum);
                    startActivity(intent);
                } else {
                    enternumber.setError("tvNumber cannot be empty");
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPosSDK = App.getInstance().getPosApi();
        startService();
    }

    private void startService() {
        Intent newIntent = new Intent(getActivity(), ScanService.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startService(newIntent);
    }


//    public void onClick(View v) {
//        Intent intent =new Intent(getContext(), TicketFormatActivity.class);
//        startActivity(intent);
//    }
}


