package com.upt.cti.smartwallet;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import model.MonthlyExpenses;

/**
 * Created by molariu on 11/4/2017.
 */

public class MySpinnerListener extends Activity implements AdapterView.OnItemSelectedListener {
    private DatabaseReference calendarRef;
    private EditText  eIncome, eExpenses;




    public MySpinnerListener(DatabaseReference calendarRef, EditText eIncome, EditText eExpenses) {
        this.calendarRef = calendarRef;
        this.eIncome = eIncome;
        this.eExpenses = eExpenses;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            final String selectedMonth = adapterView.getItemAtPosition(i).toString();


            //read the data for the selected month then remove the listener
            ValueEventListener  databaseListener = calendarRef.child(selectedMonth).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MonthlyExpenses monthlyExpenses = dataSnapshot.getValue(MonthlyExpenses.class);
                    eIncome.setText(String.valueOf(monthlyExpenses.getIncome()));
                    eExpenses.setText(String.valueOf(monthlyExpenses.getExpenses()));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // calendarRef.child(currentMonth).removeEventListener(databaseListener);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
