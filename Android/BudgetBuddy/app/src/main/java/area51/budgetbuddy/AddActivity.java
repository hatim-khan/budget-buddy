package area51.budgetbuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setContentView(R.layout.activity_add);
        // adds the "back button", which goes back to the MainActivity (where you can see Overview / Payments / Trends
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
