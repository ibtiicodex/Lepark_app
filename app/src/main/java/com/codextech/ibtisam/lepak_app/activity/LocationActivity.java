package com.codextech.ibtisam.lepak_app.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.codextech.ibtisam.lepak_app.R;

public class LocationActivity extends AppCompatActivity {
Button btnext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);

        btnext=(Button)findViewById(R.id.btnext);

        btnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent next = new Intent(getApplicationContext(),LoginActivity.class);

                startActivity(next);

            }
        });
    }
}
