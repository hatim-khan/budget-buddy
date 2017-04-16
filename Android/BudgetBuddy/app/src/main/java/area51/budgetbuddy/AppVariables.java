package area51.budgetbuddy;

import android.app.Application;
import android.util.Log;

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


    public static User testUser1 = new User("Paige", "password");

    public static Map<String, Group> allGroups = new HashMap<String,Group>();

    public static void addUserToGroup(User user, String groupName) {
        if (allGroups.containsKey(groupName)) {
            Group existingGroup = allGroups.get(groupName);
            existingGroup.addUserToGroup(user);
        }
        else {
            // TODO: should have some sort of check here
            Log.d("APPVARIABLES", "creating a new group named '" + groupName + "'");
            Group newGroup = new Group(groupName);
            newGroup.addUserToGroup(user);
            allGroups.put(groupName, newGroup);
        }
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
