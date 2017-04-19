package area51.budgetbuddy;


import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    // Returns a list of group budgets names the current user
    public ArrayList<String> getUserGroupBudgetStrings() {
        // TODO: search database for the user's group, and get the corresponding budgets
        return group.getGroupBudgetStrings();
    }

    // Returns a list of personal budgets names for the current user
    public ArrayList<String> getUserPersonalBudgetStrings() {
        ArrayList<String> arrayOfStrings = new ArrayList<>();
        for (Budget budget : personalBudgets) {
            arrayOfStrings.add(budget.getName());
        }
        return arrayOfStrings;
    }

    public Budget getUserBudgetFromName(String name, boolean isGroupBudget) {
        // If its a group budget, iterate through this user's group's budgets

        if (isGroupBudget) {
            for (Budget budget : this.getUserGroupBudgets()) {
                String budgetName = budget.name;
                if (budgetName.equals(name)) {

                    return budget;
                }
            }
        }
        // else, the budget is in the user's personal budget list
        else {
            for (Budget budget : this.personalBudgets) {
                String budgetName = budget.name;
                if (budgetName.equals(name)) {
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
    public ArrayList<Payment> getAllPaymentsSorted() {
        // TODO: replace
        ArrayList<Payment> allPayments = new ArrayList<>();

        // adds all the group budget payments to the array
        for (Budget budget : this.getUserGroupBudgets()) {
            allPayments.addAll(budget.getPayments());
        }

        // add all the personal budget payments to the array
        for (Budget budget : this.personalBudgets) {
            allPayments.addAll(budget.getPayments());
        }

        Collections.sort(allPayments, new Comparator<Payment>() {
            public int compare(Payment m1, Payment m2) {
                return m1.getPurchaseDate().compareTo(m2.getPurchaseDate());
            }
        });

        return allPayments;
    }

    public String getUsername() {
        return username;
    }
}