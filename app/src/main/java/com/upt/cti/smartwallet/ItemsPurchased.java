package com.upt.cti.smartwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ItemsPurchased extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_purchased);


        // data to be displayed
        List<String> payments = new ArrayList<>();
        payments.add("45.25");
        payments.add("5.50");
        payments.add("15.60");


        // initialize list view from activity layout
        ListView listPayments = (ListView) findViewById(R.id.listView);

        // create adapter for payments list
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, payments);

        // set adapter to list view
        listPayments.setAdapter(listAdapter);
    }
}
