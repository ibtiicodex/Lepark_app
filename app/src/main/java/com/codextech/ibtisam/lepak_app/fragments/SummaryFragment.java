package com.codextech.ibtisam.lepak_app.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.model.Ticket;
import com.codextech.ibtisam.lepak_app.realm.RealmController;

import io.realm.Realm;

/**
 * Created by HP on 10/18/2017.
 */

public class SummaryFragment extends AppCompatActivity{
    private static final String TAG = "SummaryFragment";
    private Realm realm;
    private TextView tvTicketCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summaryclass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tvTicketCount = (TextView) findViewById(R.id.tvTicketCount);
        this.realm = RealmController.with(this).getRealm();
        long count = realm.where(Ticket.class).count();
        Log.d(TAG, "saveTicket: COUNT: " + count);
        tvTicketCount.setText(count+"");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
