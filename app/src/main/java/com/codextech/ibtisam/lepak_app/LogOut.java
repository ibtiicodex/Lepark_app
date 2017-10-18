package com.codextech.ibtisam.lepak_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codextech.ibtisam.lepak_app.activity.LoginActivity;

public class LogOut extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);
        Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);



    }
}
