package area51.budgetbuddy;

/**
 * Created by paige on 4/16/17.
 */

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class OverviewFragment extends Fragment {
    private int mPage;

    public static final String ARG_PAGE = "ARG_PAGE";

    public static OverviewFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        OverviewFragment fragment = new OverviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        // TODO Natalie: Customize the UI for the Overview Screen here

        // TODO Natalie : replace - this is just here for an example on how to get budget data
        TextView textView = (TextView) view.findViewById(R.id.textview);
        User currentUser = AppVariables.currentUser;
        String budgetsString = new String();

        for (Budget budget : currentUser.userGroupBudgets().values()) {
            String budgetName = budget.name;
            budgetsString += budgetName + " " +
                    budget.getAmountSpentInBudget() + " / "
                    + budget.getBudgetLimit() + "\n";
        }
        textView.setText("Budgets: " + budgetsString);

        return view;
    }
}