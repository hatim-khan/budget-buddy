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

public class Payment {

    // The amount of money spent by the user
    private double amountSpent;

    // The day, month, and year of the purchase
    // TODO: may want to support time of day at some point
     private String purchaseDate;

    // (Optional) Any notes included about the payment
    private String notes;

    // The username of the user who made the payment
    private String username;

    // Initializer for a Payment class
    public Payment(double amountSpent, String purchaseDateString, String notes, String username) {
        this.amountSpent = amountSpent;
        this.notes = notes;
        this.purchaseDate = purchaseDateString;
        this.username = username;
    }

    public double getAmountSpent() {
        return amountSpent;
    }

    public Date paymentPurchaseDate() {
        return new Date();
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getNotes() {
        return notes;
    }

    public String getUsername() {
        return username;
    }

    public Date dateForPayment() {
        return AppVariables.convertStringToDate(purchaseDate);
    }
}
