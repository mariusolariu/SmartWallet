package com.upt.cti.smartwallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.MonthlyExpenses;

public class MainActivity extends AppCompatActivity {
    private TextView tStatus;
    private EditText eSearch, eIncome, eExpenses;
    private String currentMonth;
    private ValueEventListener databaseListener;
    private SharedPreferences prefsApp;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsApp = getPreferences(Context.MODE_PRIVATE);

        tStatus = (TextView) findViewById(R.id.tStatus);
        eSearch = (EditText) findViewById(R.id.eSearch);
        eIncome = (EditText) findViewById(R.id.eIncome);
        eExpenses = (EditText) findViewById(R.id.eExpenses);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.bSearch:
                if (!eSearch.getText().toString().isEmpty()) {
                    // save text to lower case (all our months are stored online in lower case)
                    currentMonth = eSearch.getText().toString().toLowerCase();

                    tStatus.setText("Searching ...");
                    createNewDBListener();

                    //store the last searched month in prefs
                    SharedPreferences.Editor editor = prefsApp.edit();
                    editor.putString("lastM", currentMonth);
                    editor.commit();
                } else {
                    Toast.makeText(this, "Search field may not be empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bUpdate:

                if (!eSearch.getText().toString().isEmpty()) {
                    float newIncome = Float.valueOf(eIncome.getText().toString());
                    float newExpenses = Float.valueOf(eExpenses.getText().toString());

                    databaseReference.child("calendar").child(currentMonth).child("expenses").setValue(newExpenses);
                    databaseReference.child("calendar").child(currentMonth).child("income").setValue(newIncome);
                }else{
                    Toast.makeText(this, "You first have to search for a month", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void createNewDBListener() {
        // you basically have only one active listener at a time for the current month
        // remove previous databaseListener
        if (databaseReference != null && currentMonth != null && databaseListener != null)
            databaseReference.child("calendar").child(currentMonth).removeEventListener(databaseListener);

        databaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MonthlyExpenses monthlyExpense = dataSnapshot.getValue(MonthlyExpenses.class);
                // explicit mapping of month name from entry key

                monthlyExpense.month = dataSnapshot.getKey();

                eIncome.setText(String.valueOf(monthlyExpense.getIncome()));
                eExpenses.setText(String.valueOf(monthlyExpense.getExpenses()));
                tStatus.setText("Found entry for " + currentMonth);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        // set new databaseListener
        databaseReference.child("calendar").child(currentMonth).addValueEventListener(databaseListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String lastSearchedMonth = prefsApp.getString("lastM", "april");
        eSearch.setText(lastSearchedMonth);
    }
}
