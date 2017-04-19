package area51.budgetbuddy;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by paige on 4/17/17.
 */

// TODO: will want to add more to this class
public class Payment {

    private double amountSpent;

    // TODO: we will probably want to convert this to a Date
    private Date purchaseDate;

    // optional
    private String notes;

    // Initializer for a Payment class
    public Payment(double amountSpent, String purchaseDateString, String notes) {
        this.amountSpent = amountSpent;
        this.notes = notes;
        this.purchaseDate = convertStringToDate(purchaseDateString);
    }

    // Helper method for converting a string to a date
    private static Date convertStringToDate(String dateString) {
        // TODO: update this to take in more formats
        Date date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
//        try {
//            date = sdf.parse(dateString);
//        } catch (ParseException ex) {
//            Log.e("ERROR", "could not parse date string: " + dateString);
//        }
        return date;
    }

    public double getAmountSpent() {
        return amountSpent;
    }
}
