package area51.budgetbuddy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by paige on 4/16/17.
 */

// Adapter class for the MainActivityView's Fragments
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Tab1", "Tab2", "Tab3" };
    private Context context;

    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // Gets the correct fragment associated with the tab at 'position'
        if (position == 0) {
            return OverviewFragment.newInstance(position + 1);
        }

        else if (position == 1) {
            return PaymentsFragment.newInstance(position + 1);
        }

        else if (position == 2) {
            return TrendsFragment.newInstance(position + 1);
        }
        // TODO: update this
        else {
            Log.e("ERROR", "No Fragment associated with position " + position);
            return OverviewFragment.newInstance(position + 1);
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
