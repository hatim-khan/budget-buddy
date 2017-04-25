package area51.budgetbuddy;

/**
 * Created by paige on 4/16/17.
 */

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverviewFragment extends Fragment {
    private int mPage;
    Collection<Budget> groupBudgetsCollection;
    Collection<Budget> personalBudgetsCollection;
    ArrayList<Budget> budgetArr = new ArrayList<>();
    RecyclerView rvBudgets;
    LinearLayoutManager rvLinearLayoutManager;

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
        User currentUser = AppVariables.currentUser;

<<<<<<< HEAD

=======
>>>>>>> 650894ec9bfb44f2ca7205cc7a1334ef9b7a7a81
        rvBudgets = (RecyclerView) view.findViewById(R.id.recycler_view);
        groupBudgetsCollection = currentUser.userGroupBudgets().values();
        budgetArr.addAll(groupBudgetsCollection);
        personalBudgetsCollection = currentUser.getPersonalBudgets().values();
        budgetArr.addAll(personalBudgetsCollection);
        BudgetAdapter adapter = new BudgetAdapter(this.getContext(), budgetArr);
        rvBudgets.setAdapter(adapter);
        rvLinearLayoutManager = new LinearLayoutManager(this.getContext());
        rvBudgets.setLayoutManager(rvLinearLayoutManager);


<<<<<<< HEAD
        //for (Budget budget : currentUser.getUserGroupBudgets()) {
            //String budgetName = budget.name;
            //budgetsString += budgetName + " " +
                    //budget.getAmountSpentInBudget() + " / "
                    //+ budget.getBudgetLimit() + "\n";
        //}
        //textView.setText("Budgets: " + budgetsString);
      
      /**  for (Budget budget : currentUser.userGroupBudgets().values()) {
=======
        /**for (Budget budget : currentUser.userGroupBudgets().values()) {
>>>>>>> 650894ec9bfb44f2ca7205cc7a1334ef9b7a7a81
            String budgetName = budget.name;
            budgetsString += budgetName + " " +
                    budget.getAmountSpentInBudget() + " / "
                    + budget.getBudgetLimit() + "\n";
        }
        textView.setText("Budgets: " + budgetsString);*/
<<<<<<< HEAD

=======
>>>>>>> 650894ec9bfb44f2ca7205cc7a1334ef9b7a7a81

        return view;
    }
}