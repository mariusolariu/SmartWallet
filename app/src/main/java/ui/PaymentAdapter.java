package ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.upt.cti.smartwallet.R;

import java.util.List;

import model.Payment;
import model.PaymentType;

/**
 * Created by molariu on 11/5/2017.
 */

public class PaymentAdapter extends ArrayAdapter<Payment> {

    private Context context;
    private List<Payment> payments;
    private int layoutResID;

    public PaymentAdapter(Context context, int layoutResourceID, List<Payment> payments) {
        super(context, layoutResourceID, payments);
        this.context = context;
        this.payments = payments;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.tIndex = (TextView) view.findViewById(R.id.tIndex);
            itemHolder.tName = (TextView) view.findViewById(R.id.tName);
            itemHolder.lHeader = (RelativeLayout) view.findViewById(R.id.lHeader);
            itemHolder.tDate = (TextView) view.findViewById(R.id.tDate);
            itemHolder.tTime = (TextView) view.findViewById(R.id.tTime);
            itemHolder.tCost = (TextView) view.findViewById(R.id.tCost);
            itemHolder.tType = (TextView) view.findViewById(R.id.tType);

            view.setTag(itemHolder);

        } else {
            itemHolder = (ItemHolder) view.getTag();
        }

        final Payment currentItemFromDb = payments.get(position);

        itemHolder.tIndex.setText(String.valueOf(position + 1));
        itemHolder.tName.setText(currentItemFromDb.getName());
        itemHolder.lHeader.setBackgroundColor(PaymentType.getColorFromPaymentType(currentItemFromDb.getType()));
        itemHolder.tCost.setText(currentItemFromDb.getCost() + " LEI");
        itemHolder.tType.setText(currentItemFromDb.getType());
        itemHolder.tDate.setText("Date: " + currentItemFromDb.timestamp.substring(0, 10));
        itemHolder.tTime.setText("Time: " + currentItemFromDb.timestamp.substring(11));

        return view;
    }

    private static class ItemHolder {
        TextView tIndex;
        TextView tName;
        RelativeLayout lHeader;
        TextView tDate, tTime;
        TextView tCost, tType;
    }
}