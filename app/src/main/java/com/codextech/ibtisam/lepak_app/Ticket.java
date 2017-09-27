package com.codextech.ibtisam.lepak_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.activity.MainActivity;
import com.codextech.ibtisam.lepak_app.adapters.BooksAdapter;
import com.codextech.ibtisam.lepak_app.model.Book;

import java.text.DateFormat;
import java.util.Date;

import io.realm.Realm;

public class Ticket extends AppCompatActivity {
 TextView agent,time,number,price,location;
    private RecyclerView recycler;
    public static String EXTRA_MESSAGE1 ="haye";
    private Realm realm;
    private BooksAdapter adapter;
    Button next,Main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Book book = new Book();
        Intent intent = getIntent();
        final String mess= intent.getStringExtra(Home.EXTRA_MESSAGE);
        agent=(TextView)findViewById(R.id.Dname);
        next=(Button)findViewById(R.id.gonext);

        time=(TextView)findViewById(R.id.Dtime);
        number=(TextView)findViewById(R.id.Dnumber);
        price=(TextView)findViewById(R.id.Dprice);
        location=(TextView)findViewById(R.id.Dlocation);
        agent.setText("Ali");
        time.setText(currentDateTimeString);
        number.setText(mess);
        price.setText("20");
        location.setText("71.22");

       // book.setId(RealmController.getInstance().getBooks().size() + System.currentTimeMillis());
//        book.setTitle("Ali");
//        book.setAuthor(currentDateTimeString);
//        book.setNumber(mess);
//        book.setPrice("20");
//        book.setLocation("71");
//        realm.beginTransaction();
//        realm.copyToRealm(book);
//        realm.commitTransaction();
//        adapter.notifyDataSetChanged();
//




next.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {






        Intent GoToAll =new Intent(getApplicationContext(), MainActivity.class);
        GoToAll.putExtra(EXTRA_MESSAGE1,mess);
        startActivity(GoToAll);

    }
});
        Toast.makeText(this, ""+currentDateTimeString+"  "+mess , Toast.LENGTH_SHORT).show();




    }
}
