package com.codextech.ibtisam.lepak_app;

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

import com.bumptech.glide.Glide;
import com.codextech.ibtisam.lepak_app.activity.AllTicketsActivity;
import com.codextech.ibtisam.lepak_app.fragments.SummaryFragment;
import com.codextech.ibtisam.lepak_app.fragments.TabFragment;


public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    private ImageView ivProfileImgNavBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        LinearLayout headerLayout = (LinearLayout) mNavigationView.getHeaderView(0);
        ivProfileImgNavBar = (ImageView) headerLayout.findViewById(R.id.ivProfileImgNavBar);
        Glide.with(MainActivity.this)
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
//                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.containerView,new ReturnTicketFragment()).commit();
                    Intent intent = new Intent(getApplicationContext(), AllTicketsActivity.class);
                    startActivity(intent);

                }

                if (menuItem.getItemId() == R.id.summarynav) {

                    Intent intent = new Intent(getApplicationContext(), SummaryFragment.class);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.logoutnav) {
//                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
//                    xfragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();

                    Intent intent = new Intent(getApplicationContext(), LogOut.class);
                    startActivity(intent);
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
