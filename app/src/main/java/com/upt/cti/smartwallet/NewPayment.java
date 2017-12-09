package com.upt.cti.smartwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.Payment;

public class NewPayment extends AppCompatActivity {
    private Button saveBtn, deleteBtn;
    private EditText paymentET, costET, typeET;
    private DatabaseReference databaseReference;
    private Payment currentPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_payment_activity);
        setTitle("New Payment");

        saveBtn = (Button) findViewById(R.id.saveBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        paymentET = (EditText) findViewById(R.id.paymentNameV);
        costET = (EditText) findViewById(R.id.costV);
        typeET = (EditText) findViewById(R.id.typeV);

        AppState appState = AppState.get();
        databaseReference = appState.getDatabaseReference();
        currentPayment = appState.getCurrentPayment();

        addBtnListeners();
        populateFields();
    }

    private void populateFields() {
        if (currentPayment != null) {
            paymentET.setText(currentPayment.getName());
            costET.setText(currentPayment.getCost() + "");
            typeET.setText(currentPayment.getType());
        }
    }

    private void addBtnListeners() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = createMap();

                Payment p = createPayment();

                //save
                if (currentPayment == null) {
                    //create new payment mode
                    String currentTimeDate = getCurrentTimeDate();
                    map.put("timestamp", currentTimeDate);
                    p.setTimestamp(currentTimeDate);

                    //create child in the db; if network connectivity isn't available data will be saved
                    databaseReference.child("wallet").child(currentTimeDate).updateChildren(map);

                    //create child locally, here's some duplicate code because

                    //update
                } else {
                    String paymentTimestamp = currentPayment.getTimestamp();
                    map.put("timestamp", paymentTimestamp);
                    p.setTimestamp(paymentTimestamp);

                    databaseReference.child("wallet").child(paymentTimestamp).updateChildren(map);


                }

                //create/update payment locally
                AppState.get().updateLocalBackup(getApplicationContext(),p,true);
                startActivity(new Intent(getApplicationContext(), ItemsPurchased.class));
            }

            private Payment createPayment() {
                Payment p = new Payment();
                String paymentName = paymentET.getText().toString();
                String paymentCost = costET.getText().toString();
                String paymentType = typeET.getText().toString();
                float cost = Float.parseFloat(paymentCost);

                p.setName(paymentName);
                p.setCost(cost);
                p.setType(paymentType);

                return p;
            }

            private Map<String,Object> createMap() {
                Map<String, Object> map = new HashMap<>();
                String paymentName = paymentET.getText().toString();
                String paymentCost = costET.getText().toString();
                String paymentType = typeET.getText().toString();
                Double cost = Double.parseDouble(paymentCost);

                map.put("name", paymentName);
                map.put("cost", cost);
                map.put("type", paymentType);

                return map;
            }
        });


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Payment currentPayment = AppState.get().getCurrentPayment();
                String currentPaymentTimestamp = currentPayment.getTimestamp();

                databaseReference.child("wallet").child(currentPaymentTimestamp).removeValue();
                AppState.get().updateLocalBackup(getApplicationContext(), currentPayment, false);
                startActivity(new Intent(getApplicationContext(), ItemsPurchased.class));
            }
        });
    }

    public static String getCurrentTimeDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }
}
