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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    Group group;
    DatabaseReference dataBaseRef = FirebaseDatabase.getInstance().getReference();
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        // adds the "back button", which goes back to the MainActivity (where you can see Overview / Payments / Trends
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        currentUser = AppVariables.currentUser;
        if (currentUser == null) throw new AssertionError("Current User cannot be null");

        // Setting values for the budgetSpinner

        Spinner budgetSpinner = (Spinner) findViewById(R.id.budget_name_spinner);
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
                    dataSourceList = AppVariables.currentUser.userPersonalBudgetStrings();
                }
                adapter.clear();
                adapter.addAll(dataSourceList);
                adapter.notifyDataSetChanged();
            }
        });

        // Listener for when the user switches between adding a payment or adding a group
        RadioGroup paymentOrBudgetRadioGroup = (RadioGroup) findViewById(R.id.payment_budget_radio_group);
        paymentOrBudgetRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                ArrayList<String> dataSourceList = new ArrayList<String>();
                if (checkedId == R.id.payment_radio_button) {
                    changeUIForAddingPayments();
                }
                else  if (checkedId == R.id.budget_radio_button) {
                    changeUIForAddingABudget();

                }
            }
        });
    }

    private void changeUIForAddingABudget() {
        EditText amountSpentEditText = (EditText) findViewById(R.id.amount_spent_or_budget_name_edit_text);
        TextView amountSpentOrBudgetName = (TextView) findViewById(R.id.amount_spent_or_budget_name_text_view);
        amountSpentOrBudgetName.setText("Budget Name");
        amountSpentEditText.setHint("Budget Name");

        TextView budgetOrPaymentTitleTextView = (TextView) findViewById(R.id.details_textview);
        budgetOrPaymentTitleTextView.setText("Budget Details");


        TextView budgetNameOrMonthlyLimit = (TextView) findViewById(R.id.budget_name_or_monthly_limit);
        budgetNameOrMonthlyLimit.setText("Monthly Budget Limit");

        EditText budgetLimit = (EditText) findViewById(R.id.budget_limit);
        budgetLimit.setVisibility(View.VISIBLE);

        // Hide the budget spinner
        ((Spinner) findViewById(R.id.budget_name_spinner)).setVisibility(View.GONE);

        // Hide the Purchase Date edit text and textview
        ((EditText) findViewById(R.id.purchase_date)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.purchase_date_text_view)).setVisibility(View.INVISIBLE);

        // Hide the notes edit text
        ((TextView) findViewById(R.id.notes)).setVisibility(View.INVISIBLE);
    }

    private void changeUIForAddingPayments() {
        EditText amountSpentEditText = (EditText) findViewById(R.id.amount_spent_or_budget_name_edit_text);
        TextView amountSpentOrBudgetName = (TextView) findViewById(R.id.amount_spent_or_budget_name_text_view);
        amountSpentOrBudgetName.setText("Amount Spent");
        amountSpentEditText.setHint("$0.00");

        TextView budgetNameOrMonthlyLimit = (TextView) findViewById(R.id.budget_name_or_monthly_limit);
        budgetNameOrMonthlyLimit.setText("Budget Name");

        TextView budgetOrPaymentTitleTextView = (TextView) findViewById(R.id.details_textview);
        budgetOrPaymentTitleTextView.setText("Payment Details");

        EditText budgetLimit = (EditText) findViewById(R.id.budget_limit);
        budgetLimit.setVisibility(View.GONE);

        // Unhide the things we hid for the budgets view
        ((Spinner) findViewById(R.id.budget_name_spinner)).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.purchase_date)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.purchase_date_text_view)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.notes)).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    // MARK: Database methods

    private void createNewPaymentFromFields(Boolean isGroupPayment) {

        EditText amountSpentEditText = (EditText) findViewById(R.id.amount_spent_or_budget_name_edit_text);
        Spinner budgetSpinner = (Spinner) findViewById(R.id.budget_name_spinner);
        EditText dateEditText = (EditText) findViewById(R.id.purchase_date);
        String notes = ((EditText) findViewById(R.id.notes)).getText().toString();
        String budgetName = budgetSpinner.getSelectedItem().toString();

        Double amountSpent = Double.valueOf(amountSpentEditText.getText().toString());
        Budget budget = AppVariables.currentUser.getUserBudgetFromName(budgetName, isGroupPayment);
        String date = dateEditText.getText().toString();
        String username = currentUser.getUsername();
        // Create the new payment
        Payment newPayment = new Payment(amountSpent, date, notes, username);

        // Add the payment to the selected budget
        budget.addUserPayment(newPayment);
        storePaymentToDataBase(newPayment, budget, isGroupPayment);

    }

    // Returns true on success, false if budget with name already exists
    private boolean createNewBudgetFromFields(Boolean isGroupBudget) {

        User currentUser = AppVariables.currentUser;
        EditText budgetNameEditText = (EditText) findViewById(R.id.amount_spent_or_budget_name_edit_text);
        EditText budgetLimitEditText = (EditText) findViewById(R.id.budget_limit);

        Double budgetLimit = Double.valueOf(budgetLimitEditText.getText().toString());
        String budgetName = budgetNameEditText.getText().toString();
        String username = currentUser.getUsername();

        // Check if the budget with the given name already exists
        if (isGroupBudget && currentUser.userGroupBudgetStrings().contains(budgetName)) {
            // Group budget with this name already exists
            return false;
        }

        if (!isGroupBudget && currentUser.userPersonalBudgetStrings().contains(budgetName)) {
            // Personal budget with this name already exists
            return false;
        }

        Budget newBudget = new Budget(budgetName, new ArrayList<Payment>(), isGroupBudget, budgetLimit, 0.0);
        AppVariables.currentUser.addBudgetToUserBudgetList(newBudget);

        storeBudgetToDataBase(newBudget, isGroupBudget);
        return true;
    }

    private boolean storeBudgetToDataBase(Budget budget, boolean isGroupBudget) {
        Group usergroup = AppVariables.currentUser.getGroup();
        DatabaseReference groupRef = dataBaseRef.child("Group").child(usergroup.getName());
        if (isGroupBudget) {
            groupRef.child("groupBudgets").child(budget.getName()).setValue(budget);
        }
        else {
            groupRef.child("groupMembers").
                    child(currentUser.getUsername()).
                    child("personalBudgets").child(budget.getName()).setValue(budget);
        }
        return true;
    }

    private void storePaymentToDataBase(Payment payment, Budget budget,  boolean isGroupPayment) {
        Group usergroup = AppVariables.currentUser.getGroup();
        DatabaseReference groupRef = dataBaseRef.child("Group").child(usergroup.getName());
        if (!budget.getPayments().contains(payment)) throw new AssertionError("Payment is not in budget array");
        String indexString = Integer.toString(budget.getPayments().size() - 1);

        if (isGroupPayment) {
            groupRef.child("groupBudgets").child(budget.getName()).child("payments").child(indexString).setValue(payment);
            groupRef.child("groupBudgets").child(budget.getName()).child("amountSpentInBudget").setValue(budget.getAmountSpentInBudget());
        }
        else {
            groupRef.child("groupMembers").
                    child(currentUser.getUsername()).
                    child("personalBudgets").
                    child(budget.getName()).
                    child("payments").
                    child(indexString).setValue(payment);
            groupRef.child("groupMembers").
                    child(currentUser.getUsername()).
                    child("personalBudgets").
                    child(budget.getName()).
                    child("amountSpentInBudget").setValue(budget.getAmountSpentInBudget());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Figure out if we are storing a payment or a budget
        RadioGroup paymentOrBudget = (RadioGroup) findViewById(R.id.payment_budget_radio_group);
        int radioButtonID = paymentOrBudget.getCheckedRadioButtonId();
        View radioButton = paymentOrBudget.findViewById(radioButtonID);
        int radioButtonIndex = paymentOrBudget.indexOfChild(radioButton);
        Boolean isNewPayment = radioButtonIndex == 0;

        // Figure out if what we are storing is group or a personal
        RadioGroup personalOrGroup = (RadioGroup) findViewById(R.id.radio_group);
        View personalGroupRadioButton = personalOrGroup.findViewById(personalOrGroup.getCheckedRadioButtonId());
        int personalOrGroupInt = personalOrGroup.indexOfChild(personalGroupRadioButton);
        Boolean isGroup = personalOrGroupInt == 0;

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_add:
                if (isNewPayment) {
                    // TODO: Add error checking
                    createNewPaymentFromFields(isGroup);
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                else {
                    if (createNewBudgetFromFields(isGroup)) {
                        NavUtils.navigateUpFromSameTask(this);
                        return true;
                    }
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
