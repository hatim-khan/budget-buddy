package area51.budgetbuddy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by paige on 4/16/17.
 */

public class Group {

    // maps username to User object for each member of the group
    public static Map<String, User> groupMembers = new HashMap<String,User>();

    // The name of the group
    private String name;

    public static Map<String, Budget> groupBudgets = new HashMap<String,Budget>();

    // temporary initializer for a group
    // we'll need to do some database stuff here in the future
    public Group(String groupName) {
        this.name = groupName;
        this.groupMembers = new HashMap<String, User>();
        this.groupBudgets = new HashMap<String, Budget>();
    }

    public String getName() {
        return name;
    }

    public void addUserToGroup(User newUser) {
        groupMembers.put(newUser.getUsername(), newUser);
    }

    // Add all the users from the given Hashmap (easy format for the database pull)
    public void addUsersToGroup(Map<String, User> usersDict) {
        for (User user : usersDict.values()) {
            addUserToGroup(user);
        }
    }

    public Map<String, Budget> getGroupBudgets() {
        return groupBudgets;
    }

    public Map<String, User> getGroupMembers() {
        return groupMembers;
    }

    public ArrayList<String> groupBudgetStrings() {
        return new ArrayList<String>(groupBudgets.keySet());
    }

    public void addGroupBudget(Budget budget) {
        // TODO: make sure budget with same name doesn't already exists
        groupBudgets.put(budget.getName(), budget);
    }

    // Add all the budgets from the given Hashmap (easy format for the database pull)
    public void addGroupBudgets(Map<String, Budget> budgets) {
        for (Budget budget : budgets.values()) {
            addGroupBudget(budget);
        }
    }
}
