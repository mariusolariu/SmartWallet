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

        saveBtn = (Button)findViewById(R.id.saveBtn);
        deleteBtn =(Button) findViewById(R.id.deleteBtn);
        paymentET = (EditText) findViewById(R.id.paymentNameV);
        costET = (EditText)findViewById(R.id.costV);
        typeET =(EditText) findViewById(R.id.typeV);

        AppState appState = AppState.get();
        databaseReference = appState.getDatabaseReference();
        currentPayment = appState.getCurrentPayment();

        addBtnListeners();
        populateFields();
    }

    private void populateFields() {
        if (currentPayment != null){
            paymentET.setText(currentPayment.getName());
            costET.setText(currentPayment.getCost() + "");
            typeET.setText(currentPayment.getType());
        }
    }

    private void addBtnListeners() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String paymentName = paymentET.getText().toString();
                String paymentCost = costET.getText().toString();
                String paymentType = typeET.getText().toString();
                String currentTimeDate = getCurrentTimeDate();

                Map<String, Object> map = new HashMap<>();
                map.put("name", paymentName);
                map.put("cost", Double.parseDouble(paymentCost));
                map.put("type",paymentType);

                if (currentPayment == null){
                    //create new payment mode
                    databaseReference.child("wallet").child(currentTimeDate).updateChildren(map);
                }else{
                    databaseReference.child("wallet").child(currentPayment.getTimestamp()).updateChildren(map);

                }

                startActivity(new Intent(getApplicationContext(), ItemsPurchased.class));
            }
        });


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Payment currentPayment = AppState.get().getCurrentPayment();
                String currentPaymentTimestamp = currentPayment.getTimestamp();

                databaseReference.child("wallet").child(currentPaymentTimestamp).removeValue();
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
