package com.codextech.ibtisam.lepak_app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codextech.ibtisam.lepak_app.R;

public class HomeActivity extends Activity {
    public static String EXTRA_MESSAGE = "haye";

    private Button Ok, next;
    private EditText enternumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Ok = (Button) findViewById(R.id.carButton);
        next = (Button) findViewById(R.id.next);
        enternumber = (EditText) findViewById(R.id.enterNum);
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String B;

                B = enternumber.getText().toString();
                Intent intent = new Intent(getApplicationContext(), OldMainActivity.class);
                intent.putExtra(EXTRA_MESSAGE, B);
                startActivity(intent);


            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String B;
                B = enternumber.getText().toString();
                Intent intent = new Intent(getApplicationContext(), AllTicketsActivity.class);
                intent.putExtra(EXTRA_MESSAGE, B);
                startActivity(intent);


            }
        });

    }
}