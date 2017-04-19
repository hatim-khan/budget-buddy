package area51.budgetbuddy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paige on 4/16/17.
 */

public class Budget {

    // The name of the budget (e.g. "Groceries")
    public String name;
    private ArrayList<Payment> payments;
    private boolean isGroupBudget;
    private Double budgetLimit;
    private Double amountSpentInBudget;

    // temporary initializer for a group
    // we'll need to do some database stuff here in the future
    public Budget(String name, Double budgetLimit, boolean isGroupBudget) {
        this.name = name;
        this.budgetLimit = budgetLimit;
        this.isGroupBudget = isGroupBudget;
        // since we are initializing the budget, nothing has been spent yet
        // we'll need to replace this later when we use a database
        this.amountSpentInBudget = 0.0;
        this.payments = new ArrayList<Payment>();
    }

    public String getName() {
        return name;
    }

    // Adds the payment to list of budget's payments, and adds the payment amount
    // to the current amount spent in the budget
    public void addUserPayment(Payment payment) {
        payments.add(payment);
        amountSpentInBudget = amountSpentInBudget + payment.getAmountSpent();
    }

    public boolean isGroupBudget() {
        return isGroupBudget;
    }

    // returns the amount spent in the budget. This should be used in the Overview Screen
    public Double getAmountSpentInBudget() {
        return amountSpentInBudget;
    }

    // returns the amount left in the budget. This should be used in the Overview Screen
    public Double getAmountLeftInBudget() {
        return budgetLimit - amountSpentInBudget;
    }

    public Double getBudgetLimit() {
        return budgetLimit;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }
}
