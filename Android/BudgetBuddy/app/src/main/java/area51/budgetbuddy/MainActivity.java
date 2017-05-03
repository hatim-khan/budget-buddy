package area51.budgetbuddy;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

// Holds the tabs for Overview, Payments, and Trends Screen
// Shouldn't be doing much work in this class, just kinda holds the
// navigation bar and tabs (should mainly be editing the fragments instead)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get the ViewPager and set it's PagerAdapter so that it can display our Overview, Payments, and Trends tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));

        // Associates the tabLayout with the viewpager created above
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(0).withName(R.string.account).withIcon(R.drawable.ic_account_box_black_24dp);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.edit_group).withIcon(R.drawable.ic_group_black_24dp);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.contact).withIcon(R.drawable.ic_email_black_24dp);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.logout).withIcon(R.drawable.ic_arrow_back_black_24dp);

//        AccountHeader headerResult = new AccountHeaderBuilder()
//                .withActivity(this)
//                .withHeaderBackground(R.drawable.header)
//                .addProfiles().withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
//                    @Override
//                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
//                        return false;
//                    }
//                })
//                .withSelectionListEnabledForSingleProfile(false)
//                .build();


        final Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(myToolbar)
                .addDrawerItems(
                    item1, new DividerDrawerItem(),
                        item2,
                        new DividerDrawerItem(),
                        item3,
                        new DividerDrawerItem(),
                        item4,
                        new DividerDrawerItem()
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Intent intent;
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 0) {
                                //intent = new Intent(MainActivity.this, SignInActivity.class);
                                //startActivity(intent);
                                return true;
                            } else if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(MainActivity.this, EditGroupActivity.class);
                                startActivity(intent);
                                return true;
                            } else if (drawerItem.getIdentifier() == 2) {
                                //intent = new Intent(MainActivity.this, SignInActivity.class);
                                //startActivity(intent);
                                return true;
                            } else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(MainActivity.this, SignInActivity.class);
                                startActivity(intent);
                                return true;
                            }
                        }
                        return false;
                    }
                }).build();





//                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
//                    @Override
//                    public boolean onItemCLick(View view, int position, IDrawerItem drawerItem) {
//                        switch (position) {
//                            case 1:
//                                return true;
//                            case 2:
//                                Intent intent = new Intent(this, EditGroupActivity.class);
//                                startActivityForResult(intent, 1);
//                                return true;
//                            case 3:
//                                return true;
//                            case 4:
//                                Intent intent = new Intent(this, SignInActivity.class);
//                                startActivityForResult(intent, 1);
//                                startActivityForRes
//                                return true;
//                        }
//
//
//                    }
//                })
//                .build();
//
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(this, AddActivity.class);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
