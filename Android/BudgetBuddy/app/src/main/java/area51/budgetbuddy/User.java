package area51.budgetbuddy;


import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by paige on 4/16/17.
 */

public class User {

    // keeping it simple with a username and password for now
    private String username;
    private String password;

    // Not stored in firebase - do not add a getter method for group
    private Group group;

    // Maps all the user's budgets to the budget name. To get the user's group budgets,
    // use the `getGroupBudgets` helper function
    public Map<String, Budget> personalBudgets = new HashMap<String,Budget>();


    // Initializer for a user class
    public User(String username, String password, Map<String, Budget> personalBudgets, Group group) {
        this.username = username;
        this.password = password;
        this.group = group;
        this.personalBudgets = personalBudgets;
    }

    // Returns a list of group budgets for the current user
    public Map<String, Budget> userGroupBudgets() {
        // TODO: search database for the user's group, and get the corresponding budgets
        return group.getGroupBudgets();
    }

    // Returns a list of group budgets names the current user
    public ArrayList<String> userGroupBudgetStrings() {
        // TODO: search database for the user's group, and get the corresponding budgets
        return group.groupBudgetStrings();
    }

    // Returns a list of personal budgets names for the current user
    public ArrayList<String> userPersonalBudgetStrings() {
        return new ArrayList(personalBudgets.keySet());
    }

    public Budget getUserBudgetFromName(String name, boolean isGroupBudget) {
        // If its a group budget, iterate through this user's group's budgets
        if (isGroupBudget) {
            for (Budget budget : this.userGroupBudgets().values()) {
                String budgetName = budget.name;
                if (budgetName.equals(name)) {
                    return budget;
                }
            }
        }
        // else, the budget is in the user's personal budget list
        else {
            for (Budget budget : this.personalBudgets.values()) {
                String budgetName = budget.name;
                if (budgetName.equals(name)) {
                    return budget;
                }
            }
        }
        // We should never get to this point, so print out an error
        Log.e("ERROR", "Couldn't find a budget with name " + name);
        return new Budget("error", new ArrayList<Payment>(), false, 0.0, 0.0);
    }

    public void addPaymentToBudget(Payment payment, Budget budget) {
        budget.addUserPayment(payment);
    }

    public void addBudgetToUserBudgetList(Budget budget) {
        if (budget.isGroupBudget()) {
            this.group.addGroupBudget(budget);
        }
        else {
            this.personalBudgets.put(budget.getName(), budget);
        }
    }

    public void addUserBudgetsFromDictionary(Map<String, Budget> budgets) {
        for (Budget budget : budgets.values()) {
            this.personalBudgets.put(budget.getName(), budget);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Budget> getPersonalBudgets() {
        return personalBudgets;
    }

    public Group getGroup() {
        return group;
    }
}