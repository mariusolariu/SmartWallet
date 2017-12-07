package com.upt.cti.smartwallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Payment;

/**
 * Created by molariu on 11/9/2017.
 */

public class AppState {
    private static AppState singletonObject;
    private SharedPreferences sharedPreferences;
    // static int currentMonth;
    // reference to Firebase used for reading and writing data
    private DatabaseReference databaseReference;
    // current payment to be edited or deleted
    private Payment currentPayment;

    // this should be private, otherwise the Singleton Pattern is not really functional
    private AppState() {
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

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

    public static String getCurrentTimeDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
        Date date = new Date();

        return simpleDateFormat.format(date);
    }

    /**
     *
     * @param context
     * @param payment
     * @param toAdd - when false the payment is deleted
     */
    public void updateLocalBackup(Context context, Payment payment, boolean toAdd) {
        String fileName = payment.timestamp;
        try {
            if (toAdd) {
                FileOutputStream fos = new FileOutputStream(fileName);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(payment);
                oos.close();
                fos.close();
            } else {
                context.deleteFile(fileName);
            }
        } catch (IOException e) {
            Toast.makeText(context, "Cannot access local data.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean hasLocalStorage(Context context) {
        return context.getFilesDir().listFiles().length > 0;
    }

    public List<Payment> loadFromLocalBackup(Context context, String month) {
        try {
            List<Payment> payments = new ArrayList<>();
            int monthIndex = Integer.parseInt(month);
            for (File file : context.getFilesDir().listFiles()) {
                String timeStamp = file.getName().substring(0, 19);

                //TODO : maybe there's a better check for this part
                if (checkFileRepPayment(timeStamp)) {
                    FileInputStream fis = context.openFileInput(file.getName());
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    Payment payment = (Payment) ois.readObject();
                    int paymentsCurrentMonth = Month.monthFromTimestamp(payment.timestamp);

                    if (monthIndex == paymentsCurrentMonth){
                        payments.add(payment);
                    }

                    ois.close();
                    fis.close();
                }
            }


            return payments;
        } catch (IOException e) {
            Toast.makeText(context, "Cannot access local data.", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } return null;
    }


    //if the timestamp is valid it means that the file containts a payment
    private boolean checkFileRepPayment(String timeStamp) {
        int month = Month.monthFromTimestamp(timeStamp);

        return  (month >= 0) && (month <= 11);
    }

}