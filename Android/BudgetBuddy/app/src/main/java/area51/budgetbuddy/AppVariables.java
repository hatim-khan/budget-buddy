package area51.budgetbuddy;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paige on 4/16/17.
 * Contains the global variables needed for the "wizard of oz"-ing of our app
 */

public class AppVariables extends Application {
    private static AppVariables singleton;

    // The user currently logged in
    // use this variable to access group and personal budgets
    public static User currentUser;

    // TODO: we'll eventually want to map something like groupID's to group objects (rather than name)
    public static Map<String, Group> allGroups = new HashMap<String,Group>();


    public static boolean groupWithNameExists(String name) {
        return allGroups.containsKey(name);
    }

    // TODO: we'll want to replace this with some sort of ID (rather than checking by name)
    public static Group getGroupWithName(String name) {
        return allGroups.get(name);
    }

    public static ArrayList<Budget> getBudgetsForGroupWithName(String name) {
        return currentUser.getUserGroupBudgets();
    }

    // TODO: actually make the name make sense. Right now the `database` is the `allGroups` dictionary
    public static void addGroupToDatabase(Group group) {
        allGroups.put(group.getName(), group);
    }

    public static AppVariables getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

}
