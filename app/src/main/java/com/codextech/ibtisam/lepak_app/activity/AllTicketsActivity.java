package com.codextech.ibtisam.lepak_app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.adapters.RealmTicketsAdapter;
import com.codextech.ibtisam.lepak_app.adapters.TicketsAdapter;
import com.codextech.ibtisam.lepak_app.app.Prefs;
import com.codextech.ibtisam.lepak_app.model.LPTicket;
import com.codextech.ibtisam.lepak_app.realm.RealmController;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class AllTicketsActivity extends AppCompatActivity {
    private final static String TAG = "AllTicketsActivity";
    public TicketsAdapter adapter;
    private Realm realm;
    public RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tickets);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setSupportActionBar(toolbar);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        this.realm = RealmController.with(this).getRealm();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketsAdapter(this);
        recycler.setAdapter(adapter);
        if (!Prefs.with(this).getPreLoad()) {
            setRealmData();
        }
        // refresh the realm instance
        RealmController.with(this).refresh();
        // get all persisted objects
        // create the helper adapter and notify data set changes
        // changes will be reflected automatically
        setRealmAdapter(RealmController.with(this).getTickets());
        //  Toast.makeText(this, "Press card item for edit, long press to remove item", Toast.LENGTH_LONG).show();
    }

    public void setRealmAdapter(RealmResults<LPTicket> LPTickets) {
        RealmTicketsAdapter realmAdapter = new RealmTicketsAdapter(this.getApplicationContext(), LPTickets, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    private void setRealmData() {
        ArrayList<LPTicket> LPTickets = new ArrayList<>();
        for (LPTicket b : LPTickets) {
            // Persist your data easily
            realm.beginTransaction();
            realm.copyToRealm(b);
            realm.commitTransaction();
        }

        Prefs.with(this).setPreLoad(true);

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