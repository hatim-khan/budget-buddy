package area51.budgetbuddy;

/**
 * Created by natalieshum on 4/29/17.
 */

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collection;

public class EditGroupActivity extends AppCompatActivity {

    User currentUser;
    ArrayList<User> group;
    RecyclerView recyclerViewUser;
    Collection<User> groupMembers;
    LinearLayoutManager userLayoutManager;
    GroupMembersAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        // adds the "back button", which goes back to the MainActivity (where you can see Overview / Payments / Trends
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        currentUser = AppVariables.currentUser;
        if (currentUser == null) throw new AssertionError("Current User cannot be null");

        //creating recyclerview to show all group members
        group = new ArrayList<>();
        groupMembers = AppVariables.currentUser.getGroup().getGroupMembers().values();
        group.addAll(groupMembers);
        recyclerViewUser = (RecyclerView) findViewById(R.id.recycler_view_group_members);
        userLayoutManager = new LinearLayoutManager(this);
        recyclerViewUser.setLayoutManager(userLayoutManager);
        groupAdapter = new GroupMembersAdapter(this, group);
        recyclerViewUser.setAdapter(groupAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_add_group:
                // TODO: add firebase group editing
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
