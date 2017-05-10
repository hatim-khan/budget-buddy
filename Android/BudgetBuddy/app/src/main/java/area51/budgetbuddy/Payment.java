package area51.budgetbuddy;

import android.util.Log;

import java.text.DecimalFormat;
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

    private boolean isGroupPayment;

    // Initializer for a Payment class
    public Payment(double amountSpent, String purchaseDateString, String notes, String username, boolean isGroupPayment) {
        this.amountSpent = amountSpent;
        this.notes = notes;
        this.purchaseDate = purchaseDateString; // look for how this is defined 10/30/2017
        this.username = username;
        this.isGroupPayment = isGroupPayment;
    }

    public double getAmountSpent() {
        return amountSpent;
    }

    public Date paymentPurchaseDate() {
        return new Date();
    } // might have to modify

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

    public boolean isGroupPayment() {
        return isGroupPayment;
    }

    // Used for the PaymentsFragment. Returns number that should be displayed in the alert cell
    public Double amountDueForPayment() {
        DecimalFormat df = new DecimalFormat("#.##");
        int numGroupMembers = AppVariables.currentUser.getGroup().getGroupMembers().size();

        // if payment made by current user, should be amount owed
        // which is the payment amount - (payment amount) / #groupmembers
        if (username.equals(AppVariables.currentUser.getUsername())) {
            return Double.valueOf(df.format(amountSpent- amountSpent/numGroupMembers));
        }

        return Double.valueOf(df.format(amountSpent/numGroupMembers));


    }

}
