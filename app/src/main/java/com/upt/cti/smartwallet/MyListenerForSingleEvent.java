package com.upt.cti.smartwallet;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import model.Payment;
import ui.PaymentAdapter;

/**
 * Created by molariu on 11/2/2017.
 */

public class MyListenerForSingleEvent implements ValueEventListener {
    private PaymentAdapter adapter;
    private List<Payment> payments;
    private int searchedMonthIndex;


    public MyListenerForSingleEvent(PaymentAdapter adapter, List<Payment> payments, int searchedMonthIndex) {
        payments.clear(); // fresh data will be added to this list
        this.adapter = adapter;
        this.payments = payments;
        this.searchedMonthIndex = searchedMonthIndex;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // called only once, when the listener is attached

        for (DataSnapshot entrySnapshot : dataSnapshot.getChildren()) {
            Payment monthFromDb = entrySnapshot.getValue(Payment.class);
            monthFromDb.timestamp = entrySnapshot.getKey();
            int monthFromDbIndex = Month.monthFromTimestamp(monthFromDb.getTimestamp());
        
            if (searchedMonthIndex == monthFromDbIndex){
                payments.add(monthFromDb);
            }
                
        }

        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
