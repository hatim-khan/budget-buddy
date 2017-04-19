package area51.budgetbuddy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paige on 4/16/17.
 */

public class Group {

    private ArrayList<User> groupMembers;
    private String name;
    private ArrayList<Budget> groupBudgets;

    // temporary initializer for a group
    // we'll need to do some database stuff here in the future
    public Group(String groupName) {
        this.name = groupName;
        this.groupMembers = new ArrayList<>();
        this.groupBudgets = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addUserToGroup(User newUser) {
        groupMembers.add(newUser);
    }

    public ArrayList<Budget> getGroupBudgets() {
        return groupBudgets;
    }

    public ArrayList<String> getGroupBudgetStrings() {
        ArrayList<String> arrayOfStrings = new ArrayList<>();
        for (Budget budget : groupBudgets) {
            arrayOfStrings.add(budget.getName());
        }
        return arrayOfStrings;
    }

    public void addGroupBudget(Budget budget) {
        // TODO: make sure budget with same name doesn't already exists
        groupBudgets.add(budget);
    }
}
