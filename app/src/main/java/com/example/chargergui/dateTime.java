package com.example.chargergui;

import java.text.SimpleDateFormat;
import java.util.Date;

public class dateTime {
    public static java.util.Date parseRFC3339Date(String datestring)
            throws java.text.ParseException, IndexOutOfBoundsException {
        Date d = new Date();

        // if there is no time zone, we don't need to do any special parsing.
        if (datestring.endsWith("Z")) {
            try {
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // spec for RFC3339
                d = s.parse(datestring);
            } catch (java.text.ParseException pe) {// try again with optional decimals
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");// spec for RFC3339 (with fractional seconds)
                s.setLenient(true);
                d = s.parse(datestring);
            }
            return d;
        }

        // step one, split off the timezone.
        String firstpart = datestring.substring(0, datestring.lastIndexOf('-'));
        String secondpart = datestring.substring(datestring.lastIndexOf('-'));

        // step two, remove the colon from the timezone offset
        secondpart = secondpart.substring(0, secondpart.indexOf(':'))
                + secondpart.substring(secondpart.indexOf(':') + 1);
        datestring = firstpart + secondpart;
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");// spec for RFC3339

        try {
            d = s.parse(datestring);
        } catch (java.text.ParseException pe) {// try again with optional decimals
            s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");// spec for RFC3339 (with fractional seconds)
            s.setLenient(true);
            d = s.parse(datestring);
        }
        return d;
    }
}
