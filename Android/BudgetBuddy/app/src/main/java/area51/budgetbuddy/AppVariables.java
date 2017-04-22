package area51.budgetbuddy;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by paige on 4/16/17.
 * Contains the global variables needed for the "wizard of oz"-ing of our app
 */


// Should've named this DataModel or something, but renaming files may cause some merge conflicts so oh well
public class AppVariables extends Application {
    private static AppVariables singleton;

    public static DatabaseReference mDatabase;

    // The user currently logged in
    // use this variable to access group and personal budgets
    public static User currentUser;

    // TODO: we'll eventually want to map something like groupID's to group objects (rather than name)
    public static Map<String, Group> allGroups = new HashMap<String,Group>();


    // If the user has an account, returns the User object for the user
    public static boolean signInUser(String username, String password, Group group) {
        if (allGroups.containsKey(group.getName())) {
            // TODO: will want to check based off email or something
            if (group.getGroupMembers().containsKey(username)) {
                User userInGroup = group.getGroupMembers().get(username);
                if (userInGroup.getPassword().equals(password)) {
                    // If the username and password are valid, sign the user in by setting
                    // the current user equal to the user found in the group
                    currentUser = userInGroup;
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public static boolean groupWithNameExists(String name) {
        return allGroups.containsKey(name);
    }

    // TODO: we'll want to replace this with some sort of ID (rather than checking by name)
    public static Group getGroupWithName(String name) {
        return allGroups.get(name);
    }

    public static Map<String, Budget> getBudgetsForGroupWithName(String name) {
        return currentUser.userGroupBudgets();
    }

    // TODO: actually make the name make sense. Right now the `database` is the `allGroups` dictionary
    public static void addGroupToAllGroupsDictionary(Group group) {
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


    // TODO: make this helper function return a list of all payments
    // (both group and personal) ordered by date to be used in the
    // 'Payments' screen.
    public static ArrayList<Payment> getAllPaymentsSorted(User user) {
        // TODO: replace
        ArrayList<Payment> allPayments = new ArrayList<>();

        // adds all the group budget payments to the array
        for (Budget budget : user.userGroupBudgets().values()) {
            allPayments.addAll(budget.getPayments());
        }

        // add all the personal budget payments to the array
        for (Budget budget : user.getPersonalBudgets().values()) {
            allPayments.addAll(budget.getPayments());
        }

        Collections.sort(allPayments, new Comparator<Payment>() {
            public int compare(Payment m1, Payment m2) {
                return m1.paymentPurchaseDate().compareTo(m2.paymentPurchaseDate());
            }
        });

        return allPayments;
    }



}
