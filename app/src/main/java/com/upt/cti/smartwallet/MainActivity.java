package com.upt.cti.smartwallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import model.MonthlyExpenses;

import static android.R.layout.simple_spinner_item;

public class MainActivity extends AppCompatActivity {
    private TextView tStatus;
    private EditText eSearch, eIncome, eExpenses;

    private ValueEventListener databaseListener;
    private ChildEventListener childEventListener;
    private SharedPreferences prefsApp;
    private Spinner spinnerMonths;
    private ArrayAdapter<String> adapter;
    private List<String> months;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsApp = getPreferences(Context.MODE_PRIVATE);

        tStatus = (TextView) findViewById(R.id.tStatus);

        eIncome = (EditText) findViewById(R.id.eIncome);
        eExpenses = (EditText) findViewById(R.id.eExpenses);
        spinnerMonths = (Spinner) findViewById(R.id.spinnerMonths);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        populateSpinner();
        listenForEvents();
    }

    private void populateSpinner() {
        months = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, simple_spinner_item, months);
        spinnerMonths.setAdapter(adapter);
        //databaseReference.child("calendar").addListenerForSingleValueEvent(new MyListenerForSingleEvent(adapter, months));
        MySpinnerListener spinnerListener = new MySpinnerListener(databaseReference.child("calendar"), eIncome, eExpenses);
        spinnerMonths.setOnItemSelectedListener(spinnerListener);
    }

    private void listenForEvents() {
        databaseReference.child("calendar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MonthlyExpenses newMonth = dataSnapshot.getValue(MonthlyExpenses.class);
                months.add(newMonth.getMonth());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void clicked(View view) {
        switch (view.getId()) {

            case R.id.bUpdate:
                float newIncome = Float.valueOf(eIncome.getText().toString());
                float newExpenses = Float.valueOf(eExpenses.getText().toString());
                String currentMonth = spinnerMonths.getSelectedItem().toString();

                databaseReference.child("calendar").child(currentMonth).child("expenses").setValue(newExpenses);
                databaseReference.child("calendar").child(currentMonth).child("income").setValue(newIncome);

                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();


    }
}
