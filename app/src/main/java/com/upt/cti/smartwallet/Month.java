package com.upt.cti.smartwallet;

/**
 * Created by molariu on 11/16/2017.
 */

public enum Month {
    January, February, March, April, May, June, July, August, September, October, November, December;

    public static int monthNameToInt(Month month) {
        return month.ordinal();
    }

    public static Month intToMonthName(int index) {
        return Month.values()[index];
    }

    public static int monthFromTimestamp(String timestamp) {
        int month = Integer.parseInt(timestamp.substring(5, 7));
        return month - 1;
    }
}