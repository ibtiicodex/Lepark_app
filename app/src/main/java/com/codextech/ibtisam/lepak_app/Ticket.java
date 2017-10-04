package com.codextech.ibtisam.lepak_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.codextech.ibtisam.lepak_app.adapters.BooksAdapter;
import com.codextech.ibtisam.lepak_app.model.Book;

import java.text.DateFormat;
import java.util.Date;

import io.realm.Realm;

public class Ticket extends AppCompatActivity {
    TextView agent, time, number, price, location;
    private RecyclerView recycler;
    public static String EXTRA_MESSAGE1 = "haye";
    private Realm realm;
    private BooksAdapter adapter;
    Button Main;
    String name = "Ali";
    RequestQueue queue;
    String pr = "20";
    String Loocation = "Liberty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Book book = new Book();
        Intent intent = getIntent();
        final String mess = intent.getStringExtra(Home.EXTRA_MESSAGE);
        agent = (TextView) findViewById(R.id.Dname);
        //next=(Button)findViewById(R.id.gonext);

        time = (TextView) findViewById(R.id.Dtime);
        number = (TextView) findViewById(R.id.Dnumber);
        price = (TextView) findViewById(R.id.Dprice);
        location = (TextView) findViewById(R.id.Dlocation);
        agent.setText("Ali");
        time.setText(currentDateTimeString);
        number.setText(mess);
        price.setText("20");
        location.setText("Liberty Market");

       // TicketRequest(name, currentDateTimeString, mess, pr, Loocation);

    }

//    void TicketRequest(final String name, final String t, final String num, final String pr, final String lo) {
//
//        String url = "http://httpbin.org/post";
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // response
//                        Log.d("Response", response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // error
//                        //  Log.d("Error.Response", response);
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("name", name);
//                params.put("time", t);
//                params.put("number", num);
//                params.put("price", pr);
//                params.put("location", lo);
//
//
//                return params;
//            }
//        };
//
//        queue.add(postRequest);
//
//
//    }


}
