package area51.budgetbuddy;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by paige on 4/16/17.
 */

public class User {

    // keeping it simple with a username and password for now
    private String username;
    private String password;

    private List<Budget> personalBudgets;
    private List<Budget> groupBudgets;

    // Initializer for a user class
    // This class is just used for now to create some static testing users
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        personalBudgets = new ArrayList<Budget>();


    }

    // Returns a list of group budgets for the current user
    public List<Budget> getGroupBudgets() {
        // TODO: search database for the user's group, and get the corresponding budgets
        return new ArrayList<Budget>();

    }
}