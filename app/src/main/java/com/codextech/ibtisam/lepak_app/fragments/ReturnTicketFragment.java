package com.codextech.ibtisam.lepak_app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codextech.ibtisam.lepak_app.R;

/**
 * Created by HP on 10/18/2017.
 */

public class ReturnTicketFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.retrun_ticket_fragment,null);
    }
}
