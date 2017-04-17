package area51.budgetbuddy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by paige on 4/17/17.
 */

// TODO: will want to add more to this class
public class Payment {
    private String name;
    // True if group payment, false if personal
    private boolean isGroupPayment;

    private double amountSpent;

    private Budget budget;

    private Date purchaseDate;

    // optional
    private String notes;

    // Initializer for a Payment class
    public Payment(String name, boolean isGroupPayment, double amountSpent, Budget budget, Date purchaseDate, String notes) {
        this.name = name;
        this.isGroupPayment = isGroupPayment;
        this.amountSpent = amountSpent;
        this.budget = budget;
        this.purchaseDate = purchaseDate;
        this.notes = notes;
    }

    public String getName() {
        return name;
    }
}
