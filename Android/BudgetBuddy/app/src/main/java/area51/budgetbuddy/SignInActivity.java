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
        // TODO: delete this before submitting - just here so we can type less
        EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        EditText usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        EditText groupNameEditText = (EditText) findViewById(R.id.group_name_edit_text);

        usernameEditText.setText("Rupaul Charles");
        passwordEditText.setText("password");
        groupNameEditText.setText("Area 51");

        FirebaseDatabase.getInstance().goOnline();
    }


    // Creates a test group in case the database fails
    private Group createTestGroup() {
        Group testGroup = new Group("Test Group");
        User testUser = new User("Test User", "password", new HashMap<String, Budget>(), testGroup);
        // Create test budgets
        Budget testPersonalBudget1 = new Budget("Test Personal Budget 1", new ArrayList<Payment>(), false, 0.0, 50.0);
        Budget testPersonalBudget2 = new Budget("Test Personal Budget 2", new ArrayList<Payment>(), false, 0.0, 100.0);
        Budget testGroupBudget1 = new Budget("Test Group Budget 1", new ArrayList<Payment>(), true, 50.0, 0.0);
        Budget testGroupBudget2 = new Budget("Test Group Budget 2", new ArrayList<Payment>(), true, 100.0, 0.0);
        // Add budgets to user's saved budgets
        testUser.addBudgetToUserBudgetList(testGroupBudget1);
        testUser.addBudgetToUserBudgetList(testGroupBudget2);
        testUser.addBudgetToUserBudgetList(testPersonalBudget1);
        testUser.addBudgetToUserBudgetList(testPersonalBudget2);
        // Create some test payments
        Payment testPayment0 = new Payment(12.00, "05/12/2017", "A test group purchase", "Test User", true);
        Payment testPayment1 = new Payment(19.00, "12/12/2016", "Another test group purchase", "Test User", true);
        Payment testPayment2 = new Payment(20.00, "20/20/2016", "Yet another test group purchase", "Test User", true);
        // Add the payments to the group's first budget
        testGroupBudget1.addUserPayment(testPayment0);
        testGroupBudget1.addUserPayment(testPayment1);
        testGroupBudget1.addUserPayment(testPayment2);
        AppVariables.currentUser = testUser;

        return testGroup;
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
            if (newGroupDict != null) {
                group.addGroupBudgets(parseBudgets(groupBudgetDict));
            }
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
        if (budgetDictionary == null) {
            return new HashMap<String, Budget>();
        }
        for (String budgetName : budgetDictionary.keySet()) {
            Map<String, Object> budget =  (Map<String, Object>) budgetDictionary.get(budgetName);
                Boolean isGroupBudget = (Boolean) budget.get("groupBudget");
                Double budgetLimit = new Double(budget.get("budgetLimit").toString());
                Double amountSpentInBudget = new Double(budget.get("amountSpentInBudget").toString());
                ArrayList<Payment> payments = parsePaymentsFromBudget(budget, budgetName, isGroupBudget);
                Budget newBudget = new Budget(budgetName, payments, isGroupBudget, budgetLimit, amountSpentInBudget);
                parsedBudgets.put(newBudget.getName(), newBudget);
        }
        return parsedBudgets;
    }


    private ArrayList<Payment> parsePaymentsFromBudget(Map<String, Object> budgetDict, String budgetName, boolean isGroup) {
        ArrayList<Payment> payments = new ArrayList<Payment>();
        if (budgetDict.get("payments") != null) {
            // Make sure we don't error if a dictionary is pushed instead of an ArrayList
            if ((budgetDict.get("payments") != null) && !(budgetDict.get("payments") instanceof ArrayList)) {
                Log.d("ERROR", "Payments should be of type Arraylist ");
                return payments;
            }
            ArrayList<Object> paymentsArray = (ArrayList<Object>) budgetDict.get("payments");

            // There may be no payments made, so need to check if it isn't null
            if (paymentsArray != null) {
                for (Object paymentObject : paymentsArray) {
                    Map<String, Object> paymentDict = (Map<String, Object>) paymentObject;
                    if (paymentDict.keySet().size() == 4) {
                        Double amountSpent = new Double(paymentDict.get("amountSpent").toString());
                        String purchaseDateString = paymentDict.get("purchaseDate").toString();
                        String notes = paymentDict.get("notes").toString();
                        String username = paymentDict.get("username").toString();
                        Payment newPayment = new Payment(amountSpent, purchaseDateString, notes, username, isGroup);
                        payments.add(newPayment);
                    } else {
                        Log.d("ERROR", "Database error with payment for budget " + budgetName);
                    }
                }
            }
        }
        return payments;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);builder.setTitle("Username, Password, or Group Name not specified").setMessage("Please provide a username (i.e. your name), password, and group name ");
            builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).show();
            // TODO: update this - not sure how we want to format the login screen so not putting much time into it now
            builder.setTitle("Username, Password, or Group Name not specified").setMessage("Please provide a username (i.e. your name), password, and group name ");
            builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).show();

        }

        else {
            // If group is null, that means onDataChange was never called, which is not good
            // For now, if this happens, we create a test group so we can still test out UI stuff
            if (group == null) {
                Log.d("FIREBASE ERROR", "Did not fetch group from database");
                group = createTestGroup();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
            if (!AppVariables.allGroups.containsKey(group.getName())) {
                dataBaseRef.child("Test").setValue("Test");
                builder.setTitle("The group '" + groupName + "' does not exist.").setMessage("Please check that you correctly entered your group's name (Hint: try signing in with group name 'Area 51')");
                builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).show();


            }
            else if (!signInUser(username, password, group)) {
                // TODO: update this - not sure how we want to format the login screen so not putting much time into it now
                builder.setTitle("Invalid username and password combindation").setMessage("Please check that the group name provided is valid (Hint: try signing in with username 'Drake' and password 'password')");
                builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).show();
            }
            // this wasnt here before
            else {
                // group name exists and password / username valid
                //AppVariables.getUniquePaymentDates(AppVariables.currentUser); not here
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
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
                if (userInGroup.getPassword().equals(password)) {
                    // If the username and password are valid, sign the user in by setting
                    // the current user equal to the user found in the group
                    AppVariables.currentUser = userInGroup;
                    return true;
                }
            }
        }
        return false;
    }
}
