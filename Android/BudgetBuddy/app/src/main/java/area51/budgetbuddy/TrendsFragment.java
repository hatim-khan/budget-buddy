package area51.budgetbuddy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class TrendsFragment extends Fragment {
    private int mPage;

    public static final String ARG_PAGE = "ARG_PAGE";

    public static TrendsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TrendsFragment fragment = new TrendsFragment();
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
        View view = inflater.inflate(R.layout.fragment_trends, container, false);

        // TODO: Customize the UI for the Payments Screen here
        // TODO Susan : replace - this is just here for an example on how to get budget data
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
