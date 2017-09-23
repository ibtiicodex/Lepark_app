package com.codextech.ibtisam.lepak_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

        private Button car, bike;
        private EditText numberCar;
        public static final String EXTRA="this";
        public static final String EXTRAl="this";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            car = (Button) findViewById(R.id.carButton);
            bike = (Button) findViewById(R.id.bikeButton);
            numberCar = (EditText) findViewById(R.id.enterNum);
            GoToTicket();
        }

        public void GoToTicket() {
            String Check= numberCar.getText().toString().trim();
            car.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // Intent intent=new Intent(getApplicationContext(),Car.class);
                    String message=numberCar.getText().toString();

                   // intent.putExtra(EXTRA,message);
                   // startActivity(intent);

                }
            });


            bike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // Intent intent=new Intent(getApplicationContext(),Bike.class);
                    String message=numberCar.getText().toString();

                    //intent.putExtra(EXTRA,message);
                   // startActivity(intent);

                }
            });
        }
    }

