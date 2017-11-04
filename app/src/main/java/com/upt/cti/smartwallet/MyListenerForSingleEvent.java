package com.upt.cti.smartwallet;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import model.MonthlyExpenses;

/**
 * Created by molariu on 11/2/2017.
 */

public class MyListenerForSingleEvent implements ValueEventListener {
    private ArrayAdapter<String> adapter;
    private List<String> months;


    public MyListenerForSingleEvent(ArrayAdapter<String> adapter, List<String> months) {
        this.adapter = adapter;
        this.months = months;

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // called only once, when the listener is attached

        for (DataSnapshot entrySnapshot : dataSnapshot.getChildren()) {
            MonthlyExpenses currMonth = entrySnapshot.getValue(MonthlyExpenses.class);

            String month = currMonth.getMonth();
            months.add(month);
            Log.d("month found:", month);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
