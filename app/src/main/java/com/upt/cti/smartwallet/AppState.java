package com.upt.cti.smartwallet;

import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;

import model.Payment;

/**
 * Created by molariu on 11/9/2017.
 */

public class AppState {
    private static AppState singletonObject;
    // reference to Firebase used for reading and writing data
    private DatabaseReference databaseReference;
    // current payment to be edited or deleted
    private Payment currentPayment;

    private AppState(){} // this should be private, otherwise the Singleton Pattern is not really functional

    public static synchronized AppState get() {
        if (singletonObject == null) {
            singletonObject = new AppState();
        }
        return singletonObject;
    }
    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public Payment getCurrentPayment() {
        return currentPayment;
    }

    public void setCurrentPayment(Payment currentPayment) {
        this.currentPayment = currentPayment;
    }

    public static String getCurrentTimeDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
        Date date = new Date();

        return simpleDateFormat.format(date);
    }
}