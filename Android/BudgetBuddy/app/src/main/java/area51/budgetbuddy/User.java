package area51.budgetbuddy;


import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paige on 4/16/17.
 */

public class User {

    // keeping it simple with a username and password for now
    private String username;
    private String password;
    private Group group;

    // List of all the user's budgets. To get the user's group budgets,
    // use the `getGroupBudgets` helper function
    private ArrayList<Budget> personalBudgets;


    // Initializer for a user class
    // This class is just used for now to create some static testing users
    public User(String username, String password, Group group) {
        this.username = username;
        this.password = password;
        this.group = group;
        personalBudgets = new ArrayList<Budget>();
    }

    // Returns a list of group budgets for the current user
    public ArrayList<Budget> getUserGroupBudgets() {
        // TODO: search database for the user's group, and get the corresponding budgets
        return group.getGroupBudgets();
    }

    // Returns a list of group budgets for the current user
    public ArrayList<String> getUserGroupBudgetStrings() {
        // TODO: search database for the user's group, and get the corresponding budgets
        return group.getGroupBudgetStrings();
    }

    public Budget getUserBudgetFromName(String name, boolean isGroupBudget) {
        // If its a group budget, iterate through this user's group's budgets
        if (isGroupBudget) {
            for (Budget budget : this.getUserGroupBudgets()) {
                if (budget.getName().equals(name)) {
                    return budget;
                }
            }
        }
        // else, the budget is in the user's personal budget list
        else {
            for (Budget budget : this.personalBudgets) {
                if (budget.getName().equals(name)) {
                    return budget;
                }
            }
        }
        // We should never get to this point, so print out an error
        Log.e("ERROR", "Couldn't find a budget with name " + name);
        return new Budget("error", 0.0, false);
    }

    public void addPaymentToBudget(Payment payment, Budget budget) {
        budget.addUserPayment(payment);
    }

    public void addBudgetToUserBudgetList(Budget budget) {
        if (budget.isGroupBudget()) {
            this.group.addGroupBudget(budget);
        }
        else {
            this.personalBudgets.add(budget);
        }
    }

    // TODO: make this helper function return a list of all payments
    // (both group and personal) ordered by date to be used in the
    // 'Payments' screen.
    public ArrayList<Payment> getAllPayments() {
        // TODO: replace
        return new ArrayList<Payment>();
    }

}