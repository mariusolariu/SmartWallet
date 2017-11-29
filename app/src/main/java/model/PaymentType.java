package model;

import android.graphics.Color;

/**
 * Created by molariu on 11/5/2017.
 */

public class PaymentType {
    public static int getColorFromPaymentType(String type) {
        type = type.toLowerCase();
        int color;


        //Since Java 8 you can do switch on strings...really nice feature!
        switch (type) {
            case "entertainment":
                color =  Color.rgb(200, 50, 50);
            break;

            case "food":
                color = Color.rgb(50, 150, 50);
            break;

            case "taxes":
                color = Color.rgb(20, 20, 150);
            break;

            case "travel":
                color =  Color.rgb(230, 140, 0);
            break;

            default :
                color = Color.rgb(20, 20, 150);
            break;
        }

        return color;
    }
}