package area51.budgetbuddy;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        // adds the "back button", which goes back to the MainActivity (where you can see Overview / Payments / Trends
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Setting values for the budgetSpinner

        Spinner budgetSpinner = (Spinner) findViewById(R.id.budget_name);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, AppVariables.currentUser.userGroupBudgetStrings());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        budgetSpinner.setAdapter(adapter);

        // Set a listener so list of budgets changes when group/personal is toggled
        RadioGroup personalOrGroup = (RadioGroup) findViewById(R.id.radio_group);

        personalOrGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                ArrayList<String> dataSourceList = new ArrayList<String>();
                if (checkedId == R.id.group_radio_button) {
                    dataSourceList = AppVariables.currentUser.userGroupBudgetStrings();

                }
                else  if (checkedId == R.id.personal_radio_button) {
                    //do work when radioButton2 is active
                    dataSourceList = AppVariables.currentUser.userPersonalBudgetStrings();
                }
                adapter.clear();
                adapter.addAll(dataSourceList);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    private void createNewPaymentFromFields() {

        EditText amountSpentEditText = (EditText) findViewById(R.id.amount_spent);
        Spinner budgetSpinner = (Spinner) findViewById(R.id.budget_name);
        EditText dateEditText = (EditText) findViewById(R.id.purchase_date);
        String notes = ((EditText) findViewById(R.id.notes)).getText().toString();
        String budgetName = budgetSpinner.getSelectedItem().toString();

        // Radio Group Parsing. Group is at index 0, personal is at index 1
        RadioGroup personalOrGroup = (RadioGroup) findViewById(R.id.radio_group);
        int radioButtonID = personalOrGroup.getCheckedRadioButtonId();
        View radioButton = personalOrGroup.findViewById(radioButtonID);
        int radioButtonIndex = personalOrGroup.indexOfChild(radioButton);
        Boolean isGroup = radioButtonIndex == 0;

        Double amountSpent = Double.valueOf(amountSpentEditText.getText().toString());
        Budget budget = AppVariables.currentUser.getUserBudgetFromName(budgetName, isGroup);
        String date = dateEditText.getText().toString();
        // Create the new payment
        Payment newPayment = new Payment(amountSpent, date, notes);

        // Add the payment to the selected budget
        budget.addUserPayment(newPayment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_add:
                // TODO: add support for creating a new budget
                createNewPaymentFromFields();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}