package com.upt.cti.smartwallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //all writes done offline are put in queue to keep data synchronized
        database.setPersistenceEnabled(true);
        databaseReference = database.getReference();

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
     * @param context
     * @param payment
     * @param toAdd   - when false the payment is deleted
     */
    public void updateLocalBackup(Context context, Payment payment, boolean toAdd) {
        String fileName = payment.timestamp;
        try {
            if (toAdd) {
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(payment);
                oos.close();
                fos.close();
            } else {
                context.deleteFile(fileName);
            }
        } catch (IOException e) {
            Log.d("error", "updateLocalBackup: " + e.getMessage());
            Toast.makeText(context, "Cannot access local data.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean hasLocalStorage(Context context) {
        return context.getFilesDir().listFiles().length > 0;
    }

    public void loadFromLocalBackup(Context context, String monthIndex, List<Payment> payments) {
        try {
            payments.clear();
            int month = Integer.parseInt(monthIndex);
            for (File file : context.getFilesDir().listFiles()) {
                String fileName = file.getName();

                if (fileName.length() < 19) continue; //doesn't represent a file that contains a payment

                String timeStamp = fileName.substring(0, 19);

                //TODO : maybe there's a better check for this part
                if (checkFileRepPayment(timeStamp)) {
                    FileInputStream fis = context.openFileInput(fileName);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    Payment payment = (Payment) ois.readObject();
                    int paymentMonth = Month.monthFromTimestamp(payment.timestamp);

                    if (month == paymentMonth) {
                        payments.add(payment);
                    }

                    ois.close();
                    fis.close();
                }
            }


        } catch (IOException e) {
            Log.d("error", "loadFromLocalBackup: " + e.toString());
            Toast.makeText(context, "Cannot access local dm data.", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    //if the timestamp is valid it means that the file containts a payment
    private boolean checkFileRepPayment(String timeStamp) {
        int month = Month.monthFromTimestamp(timeStamp);

        return (month >= 0) && (month <= 11);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}