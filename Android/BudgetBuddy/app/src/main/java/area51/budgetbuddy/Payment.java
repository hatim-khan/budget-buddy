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
        Date date = new Date();
        String[] formatStrings = {"M/y", "M/d/y", "M-d-y"};
        for (String formatString : formatStrings) {
            try {
                return new SimpleDateFormat(formatString).parse(dateString);
            }
            catch (ParseException e) {
                Log.e("ERROR", "could not parse date string: " + dateString);
            }
        }
        return date;
    }

    public double getAmountSpent() {
        return amountSpent;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }
}
