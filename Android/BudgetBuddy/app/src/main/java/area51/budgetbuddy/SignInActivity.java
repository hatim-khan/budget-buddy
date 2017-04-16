package area51.budgetbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

// Initial view presented to the user (Sign In Page)
public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    // This method is called every time the user taps down on the sign in button
    // Listener is attached in the activity_sign_in.xml file
    public void signInButtonWasPressed(View view) {
         Intent intent = new Intent(this, MainActivity.class);
         startActivity(intent);
    }
}
