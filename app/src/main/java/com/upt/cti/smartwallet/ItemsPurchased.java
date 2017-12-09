package com.upt.cti.smartwallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import model.Payment;
import ui.PaymentAdapter;

public class ItemsPurchased extends AppCompatActivity {
    private static final String TAG_MONTH = "current_month";
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
        databaseReference = AppState.get().getDatabaseReference();

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        currentMonth = sharedPreferences.getInt(TAG_MONTH, -1);

        //first time we  launch the app we don't have anything stored in preferences
        if (currentMonth == -1) {
            String currentTimeDate = AppState.getCurrentTimeDate();
            currentMonth = Month.monthFromTimestamp(currentTimeDate);
            sharedPreferences.edit().putInt(TAG_MONTH, currentMonth).apply();
        }

        //checkInternetConnection();
        if (!AppState.isNetworkAvailable(getApplicationContext())) {
            // checks to see if there are any payments on local storage, if not the user is informed that for the very first time he/she
            //  launches the app internet is needed
            if (AppState.get().hasLocalStorage(getApplicationContext())) {
                AppState.get().loadFromLocalBackup(getApplicationContext(), String.valueOf(currentMonth), payments);
                adapter.notifyDataSetChanged();
                tStatus.setText("Found " + payments.size() + " payments for " + Month.intToMonthName(currentMonth));
            } else {
                tStatus.setText("No payment history found on local storage! Turn internet on!");
                Toast.makeText(this, "This app needs an internet connection!", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            addWalletListener();
        }

        listPayments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Payment currentPayment = payments.get(i);
                AppState.get().setCurrentPayment(currentPayment);
                startActivity(new Intent(getApplicationContext(), NewPayment.class));
            }
        });
    }

    private void addWalletListener() {
        databaseReference.child("wallet").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Log.i("info", "onChildAdded()");
                //In my implementation only this method is called since when I edit/delete an item
                //i get back to this activity by creating a new one from NewPayment activity
                try {
                    Payment payment = dataSnapshot.getValue(Payment.class);

                    //add the new child in local storge
                    AppState.get().updateLocalBackup(getApplicationContext(), payment, true);

                    if (currentMonth == Month.monthFromTimestamp(dataSnapshot.getKey())) {

                        payments.add(payment);
                        adapter.notifyDataSetChanged();
                    }
                        tStatus.setText("Found " + payments.size() + " payments for " + Month.intToMonthName(currentMonth));

                } catch (Exception e) {
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Payment payment = dataSnapshot.getValue(Payment.class);
                int modifiedPaymentMonth = Month.monthFromTimestamp(payment.timestamp);

                //update the child in local storge
                AppState.get().updateLocalBackup(getApplicationContext(), payment, true);

                if (currentMonth == modifiedPaymentMonth) {
                    recreate();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Payment payment = dataSnapshot.getValue(Payment.class);
                int modifiedPaymentMonth = Month.monthFromTimestamp(payment.timestamp);

                //delete local storge
                AppState.get().updateLocalBackup(getApplicationContext(), payment, false);


                if (currentMonth == modifiedPaymentMonth) {
                    recreate();
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i("info", "onChildMoved()");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //used before to force the user to force the user to connect to internet
/*    public void checkInternetConnection() {
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

    }*/

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
                currentMonth = (currentMonth + 1) % 12;
                sharedPreferences.edit().putInt(TAG_MONTH, currentMonth).apply();
                recreate();
                break;

            case R.id.bPrevious:
                currentMonth = (currentMonth - 1 == -1) ? 11 : currentMonth - 1;
                sharedPreferences.edit().putInt(TAG_MONTH, currentMonth).apply();
                recreate();
                break;
        }

    }
}
