package area51.budgetbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

// Initial view presented to the user (Sign In Page)
public class SignInActivity extends AppCompatActivity {

    Group group;
    DatabaseReference dataBaseRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference groupRef = dataBaseRef.child("Group").child("Area 51");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }


    // Attach a listener to read the data at our posts reference. Probably shouldn't do this here
    // since this means this view will have to stay in memory, but its the easiest place to put
    // this for now
    ValueEventListener valueEventListener = groupRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Map<String, Object> newGroupDict = (Map<String, Object>) dataSnapshot.getValue();
            Log.d("Group", newGroupDict.toString());

            // Create the new group
            group = new Group(newGroupDict.get("name").toString());
            if (group == null) throw new AssertionError("GroupRef cannot be null");

            // Get the group budgets from the database
            Map<String, Object> groupBudgetDict = (Map<String, Object>) newGroupDict.get("groupBudgets");
            group.addGroupBudgets(parseBudgets(groupBudgetDict));

            // Get the group users and their payments from the db
            Map<String, Object> groupMembersDict = (Map<String, Object>) newGroupDict.get("groupMembers");
            Map<String, User> groupMembersParsed = parseUsers(groupMembersDict, group);
            group.setGroupMembers(groupMembersParsed);
            AppVariables.addGroupToAllGroupsDictionary(group);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d("ERROR", "The read failed");
        }
    });


    private Map<String, Budget> parseBudgets(Map<String, Object> budgetDictionary) {
        Map<String, Budget> parsedBudgets = new HashMap<>();
        for (String budgetName : budgetDictionary.keySet()) {
            Map<String, Object> budget =  (Map<String, Object>) budgetDictionary.get(budgetName);
            // public Budget(String name, ArrayList<Payment> payments, boolean isGroupBudget, Double budgetLimit, Double amountSpentInBudget) {
            Boolean isGroupBudget = (Boolean) budget.get("groupBudget");
            Double budgetLimit = new Double(budget.get("budgetLimit").toString());
            Double amountSpentInBudget = new Double(budget.get("amountSpentInBudget").toString());

            ArrayList<Payment> payments = new ArrayList<Payment>();
            ArrayList<Object> paymentsArray = (ArrayList<Object>) budget.get("payments");
            // There may be no payments made, so need to check if it isn't null
            if (paymentsArray != null) {
                for (Object paymentObject : paymentsArray) {
                    Map<String, Object> paymentDict = (Map<String, Object>) paymentObject;
                    Double amountSpent = new Double(paymentDict.get("amountSpent").toString());
                    String purchaseDateString = paymentDict.get("purchaseDate").toString();
                    String notes = paymentDict.get("notes").toString();
                    Payment newPayment = new Payment(amountSpent, purchaseDateString, notes);
                    payments.add(newPayment);
                }
            }
            Budget newBudget = new Budget(budgetName, payments, isGroupBudget, budgetLimit, amountSpentInBudget);
            parsedBudgets.put(newBudget.getName(), newBudget);
        }
        return parsedBudgets;
    }

    private Map<String, User> parseUsers(Map<String, Object> userDictionary, Group group) {
        Map<String, User> parsedUsers = new HashMap<>();
        for (String username : userDictionary.keySet()) {
            Map<String, Object> user =  (Map<String, Object>) userDictionary.get(username);
            String password = user.get("password").toString();
            Map<String, Object> personalBudgets = (Map<String, Object>) user.get("personalBudgets");
            Map<String, Budget> parsedPersonalBudgets = parseBudgets(personalBudgets);
            User newUser = new User(username, password, parsedPersonalBudgets, group);
            parsedUsers.put(username, newUser);
        }
        return parsedUsers;
    }




    // This method is called every time the user taps down on the sign in button
    // Listener is attached in the activity_sign_in.xml file
    public void signInButtonWasPressed(View view) {
        EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        EditText usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        EditText groupNameEditText = (EditText) findViewById(R.id.group_name_edit_text);


        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String groupName = groupNameEditText.getText().toString();

        if (username.isEmpty()||password.isEmpty()||groupName.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
            // TODO: update this - not sure how we want to format the login screen so not putting much time into it now
            builder.setTitle("Username, Password, or Group Name not specified").setMessage("Please provide a username (i.e. your name), password, and group name ");
            builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).show();
        }

        else {
            // if all the edit texts have values, create a new user with the given username and password
            // TODO: right now this just supports signing in, not logging in
            if (signInUser(username, password, group)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                // TODO: update this - not sure how we want to format the login screen so not putting much time into it now
                builder.setTitle("The group '" + groupName + "' does not exist").setMessage("Please check that the group name provided is valid");
                builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).show();
            }
        }
    }

    // If the user has an account, returns the User object for the user
    private boolean signInUser(String username, String password, Group group) {
        if (AppVariables.allGroups.containsKey(group.getName())) {
            // TODO: will want to check based off email or something
            boolean groupContainsUser = group.getGroupMembers().containsKey(username);
            if (groupContainsUser) {
                User userInGroup = group.getGroupMembers().get(username);

                    // If the username and password are valid, sign the user in by setting
                    // the current user equal to the user found in the group
                AppVariables.currentUser = userInGroup;
                return true;

            }
        }
        return false;
    }



}
