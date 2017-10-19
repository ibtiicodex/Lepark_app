package com.codextech.ibtisam.lepak_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.activity.TicketFormatActivity;
import com.codextech.ibtisam.lepak_app.service.ScanService;

/**
 * Created by HP on 10/18/2017.
 */

public class PrintTicketFragment extends Fragment {
    public static final String TAG = "test";
    private Button bcarButton;
    private EditText edenternumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: PrintTicketFragment");
//        startService();
        View view = inflater.inflate(R.layout.print_ticket_fragment, container, false);
        edenternumber = (EditText) view.findViewById(R.id.enterNum);
        bcarButton = (Button) view.findViewById(R.id.carButton);
        bcarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carNum = edenternumber.getText().toString();
                if (carNum.trim().length() >= 3 && carNum != null) {
                    Intent intent = new Intent(getActivity(), TicketFormatActivity.class);
                    intent.putExtra(TicketFormatActivity.CAR_NUMBER, carNum);
                    startActivity(intent);
                } else {
                    edenternumber.setError("tvNumber cannot be empty");
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void startService() {
        Log.d(TAG, "startService(): PrintTicketFragment");
        Intent newIntent = new Intent(getActivity(), ScanService.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startService(newIntent);
    }


//    public void onClick(View v) {
//        Intent intent =new Intent(getContext(), TicketFormatActivity.class);
//        startActivity(intent);
//    }
}


