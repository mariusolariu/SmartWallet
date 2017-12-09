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

    /**
     *
     * @param timestamp
     * @return -1 if the string passed doesn't represent a timestamp
     */
    public static int monthFromTimestamp(String timestamp) {
        int month;

        try{
             month = Integer.parseInt(timestamp.substring(5, 7));
        }catch(NumberFormatException e){
            month = 0;
        }

        return month - 1;
    }
}