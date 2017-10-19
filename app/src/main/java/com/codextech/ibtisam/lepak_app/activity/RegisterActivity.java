package com.codextech.ibtisam.lepak_app.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.codextech.ibtisam.lepak_app.R;

public class RegisterActivity extends AppCompatActivity {
    Button btsignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btsignup = (Button) findViewById(R.id.btsignup);
        btsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent location = new Intent(getApplicationContext(), LocationActivity.class);
                startActivity(location);
            }
        });
    }
}
