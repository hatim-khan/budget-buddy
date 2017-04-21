package area51.budgetbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Initial view presented to the user (Sign In Page)
public class SignInActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // TODO: adding in some test users now. Replace this in final project with database
        setupTestUsers();
    }

    private void setupTestUsers() {
        Group testGroup = new Group("Area 51");
        Budget testGroupBudget = new Budget("Cleaning Supplies", 50.0,  true);
        Budget testGroupBudget2 = new Budget("Gas and Car Maintenance", 200.0,  true);
        Budget testGroupBudget3 = new Budget("Shared Groceries", 100.0, true);
        testGroup.addGroupBudget(testGroupBudget);
        testGroup.addGroupBudget(testGroupBudget2);
        testGroup.addGroupBudget(testGroupBudget3);

        User testUser1 = new User("Drake", "password", testGroup);
        User testUser2 = new User("Rupaul Charles", "password", testGroup);
        User testUser3 = new User("Joe Biden", "password", testGroup);

        // TODO: make sure just adding the group persists all of the users, budgets, etc. during app lifetime
        AppVariables.addGroupToDatabase(testGroup);
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
            if (AppVariables.groupWithNameExists(groupName)) {
                Group userGroup = AppVariables.getGroupWithName(groupName);
                AppVariables.currentUser = new User(username, password, userGroup);
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
}
