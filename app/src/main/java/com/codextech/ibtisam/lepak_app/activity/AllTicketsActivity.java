package com.codextech.ibtisam.lepak_app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.adapters.RealmTicketsAdapter;
import com.codextech.ibtisam.lepak_app.adapters.TicketsAdapter;
import com.codextech.ibtisam.lepak_app.app.Prefs;
import com.codextech.ibtisam.lepak_app.model.Ticket;
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

        recycler = (RecyclerView) findViewById(R.id.recycler);
        //get realm instance
        this.realm = RealmController.with(this).getRealm();
        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        setupRecycler();

        if (!Prefs.with(this).getPreLoad()) {

            setRealmData();
        }
        // refresh the realm instance
        RealmController.with(this).refresh();
        // get all persisted objects
        // create the helper adapter and notify data set changes
        // changes will be reflected automatically
        setRealmAdapter(RealmController.with(this).getBooks());

      //  Toast.makeText(this, "Press card item for edit, long press to remove item", Toast.LENGTH_LONG).show();
    }
    public void setRealmAdapter(RealmResults<Ticket> tickets) {

        RealmTicketsAdapter realmAdapter = new RealmTicketsAdapter(this.getApplicationContext(), tickets, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);

        adapter.notifyDataSetChanged();
    }
    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recycler.setHasFixedSize(true);
        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        layoutManager.setReverseLayout(true);

        recycler.setLayoutManager(layoutManager);
        // create an empty adapter and add it to the recycler view
        adapter = new TicketsAdapter(this);

        recycler.setAdapter(adapter);
    }
    private void setRealmData() {
        ArrayList<Ticket> tickets = new ArrayList<>();

        for (Ticket b : tickets) {
            // Persist your data easily
            realm.beginTransaction();

            realm.copyToRealm(b);

            realm.commitTransaction();
        }

        Prefs.with(this).setPreLoad(true);

    }
}