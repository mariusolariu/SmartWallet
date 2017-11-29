package com.upt.cti.smartwallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upt.cti.smartwallet.internet_connection.ConnectionStatus;

import java.util.ArrayList;
import java.util.List;

import model.Payment;
import ui.PaymentAdapter;

public class ItemsPurchased extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private List<Payment> payments = new ArrayList<>();
    private Button bPrevious, bNext;
    private TextView tStatus;
    private FloatingActionButton fabAdd;
    private ListView listPayments;
    private PaymentAdapter adapter;
    private SharedPreferences sharedPreferences;
    private int currentMonth;
    private MyListenerForSingleEvent readDataListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_purchased);

        tStatus = (TextView) findViewById(R.id.tStatus);
        bPrevious = (Button) findViewById(R.id.bPrevious);
        bNext = (Button) findViewById(R.id.bNext);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        listPayments = (ListView) findViewById(R.id.listPayments);
        adapter = new PaymentAdapter(this, R.layout.row_layout, payments);
        listPayments.setAdapter(adapter);

        // setup firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        AppState.get().setDatabaseReference(databaseReference);

        String currentTimeDate = AppState.getCurrentTimeDate();
        currentMonth = Month.monthFromTimestamp(currentTimeDate);

        checkInternetConnection();
        readDataListener = new MyListenerForSingleEvent(adapter, payments, currentMonth);
        addListeners();
    }

    private void addListeners() {
        databaseReference.child("wallet").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    if (currentMonth == Month.monthFromTimestamp(dataSnapshot.getKey())) {
                        Payment payment = dataSnapshot.getValue(Payment.class);
                        payment.timestamp = dataSnapshot.getKey();
                        payments.add(payment);
                        adapter.notifyDataSetChanged();
                        tStatus.setText("Found " + payments.size() + " payments for " + Month.intToMonthName(currentMonth));
                    }
                } catch (Exception e) {
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i("info", "onChildChanged()");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Payment removedPayment = dataSnapshot.getValue(Payment.class);

                Log.i("info", "onChildRemoved()");
                //it doesn't work
                payments.remove(removedPayment);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i("info", "onChildMoved()");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listPayments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Payment currentPayment = payments.get(i);
                AppState.get().setCurrentPayment(currentPayment);
                startActivity(new Intent(getApplicationContext(), NewPayment.class));
            }
        });
    }

    public void checkInternetConnection() {
        // Check if Internet present
        ConnectionStatus internetConnection = new ConnectionStatus(this);
        if (!internetConnection.isConnectionAvailable()) {
            // Internet Connection is not present
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Connect to wifi or quit")
                    .setCancelable(false)
                    .setPositiveButton("Connect to WIFI", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void clicked(View view) {

        switch (view.getId()) {
            case R.id.fabAdd:
                    Intent intent = new Intent(this, NewPayment.class);
                    //if a payment was edited previously the NewPayment activity views will be filled with those values
                    //and we don't want this for the current flow/path
                    AppState.get().setCurrentPayment(null);
                    startActivity(intent);
                break;

            case R.id.bNext:
                    currentMonth = ++currentMonth % 12;

                    payments.clear();
                    Payment test = new Payment("2017-11-16 12:31:42", "pizzaTest", 12, "food");

                    payments.add(test);
                    adapter.notifyDataSetChanged();

                    //databaseReference.child("wallet").addListenerForSingleValueEvent(readDataListener);
                    recreate();
                break;

            case R.id.bPrevious:
                    if (currentMonth == 0) {
                        currentMonth = 11;
                    } else {
                        currentMonth--;
                    }

                    MyListenerForSingleEvent readDataListener1 = new MyListenerForSingleEvent(adapter, payments, currentMonth);
                    databaseReference.child("wallet").addListenerForSingleValueEvent(readDataListener1);

                    recreate();
                break;
        }

    }
}
