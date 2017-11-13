package com.codextech.ibtisam.lepak_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.SessionManager;
import com.codextech.ibtisam.lepak_app.fragments.SummaryActivity;
import com.codextech.ibtisam.lepak_app.fragments.TabFragment;
import com.codextech.ibtisam.lepak_app.service.ScanService;
import com.codextech.ibtisam.lepak_app.sync.DataSenderAsync;

//import com.codextech.ibtisam.lepak_app.service.ScanService;
public class NavigationDrawerActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentTransaction mFragmentTransaction;
    FragmentManager mFragmentManager;
    private ImageView ivProfileImgNavBar;
    SessionManager sessionManager;
    TextView datasetonheadedr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datasetonheadedr = (TextView) findViewById(R.id.tvSite);
        sessionManager = new SessionManager(NavigationDrawerActivity.this);
        if (!sessionManager.isSiteSignedIn()) {
            finish();
            startActivity(new Intent(NavigationDrawerActivity.this, LoginActivity.class));
        }
        Intent newIntent = new Intent(NavigationDrawerActivity.this, ScanService.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        NavigationDrawerActivity.this.startService(newIntent);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);
        LinearLayout headerLayout = (LinearLayout) mNavigationView.getHeaderView(0);
        ivProfileImgNavBar = (ImageView) headerLayout.findViewById(R.id.ivProfileImgNavBar);
        Glide.with(NavigationDrawerActivity.this)
                .load("https://scontent.fkhi10-1.fna.fbcdn.net/v/t31.0-8/18518369_1478734302201566_2146160655778083392_o.jpg?oh=8a7cbc129e4cc3bc87d13f6200882df5&oe=5A6F9CC4")
                .into(ivProfileImgNavBar);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                if (menuItem.getItemId() == R.id.showallnav) {
                    Intent intent = new Intent(getApplicationContext(), AllTicketsActivity.class);
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.summarynav) {
                    Intent intent = new Intent(getApplicationContext(), SummaryActivity.class);
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.logoutnav) {
                    sessionManager.logoutSite();
                    finish();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.nav_NfcActvity) {
                    Intent intent = new Intent(getApplicationContext(), NfcGetAllCoinsActivity.class);
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.nav_refresh) {
                    DataSenderAsync dataSenderAsync = new DataSenderAsync(NavigationDrawerActivity.this);
                    dataSenderAsync.execute();
                    Toast.makeText(NavigationDrawerActivity.this, " Refreshed ", Toast.LENGTH_SHORT).show();
                }

                return false;
            }

        });
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

}
