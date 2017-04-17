package area51.budgetbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

// Initial view presented to the user (Sign In Page)
public class SignInActivity extends AppCompatActivity {

    public static User testUser1 = new User("Paige", "password");
    public static User testUser2 = new User("Susan", "password");
    public static User testUser3 = new User("Erick", "password");
    public static User testUser4 = new User("Hatim", "password");
    public static User testUser5 = new User("Natalie", "password");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        // TODO: adding in some test users now. Replace this in final project with database
        AppVariables.addUserToGroup(testUser1, "Area 51");
        AppVariables.addUserToGroup(testUser2, "Area 51");
        AppVariables.addUserToGroup(testUser3, "Area 51");
        AppVariables.addUserToGroup(testUser4, "Area 51");
        AppVariables.addUserToGroup(testUser5, "Area 51");
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
            // TODO: right now this just supports creating a new user, not signing in as an existing user
            AppVariables.addUserToGroup(new User(username, password), groupName);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }



    }
}
