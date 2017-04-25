package area51.budgetbuddy;

/**
 * Created by natalieshum on 4/23/17.
 */

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.ArrayList;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //holds both group and personal budget viewholders
    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView budgetNameView;
        TextView budgetRemainingView;
        TextView budgetSpentView;
        RoundCornerProgressBar progressBar;

        public BudgetViewHolder(View itemView) {
            super(itemView);

            budgetNameView = (TextView) itemView.findViewById(R.id.budget_name);
            budgetSpentView = (TextView) itemView.findViewById(R.id.budget_spent);
            budgetRemainingView = (TextView) itemView.findViewById(R.id.budget_remaining);
            progressBar = (RoundCornerProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }

    //group header viewholder
    public static class GroupHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView budgetHeader;

        public GroupHeaderViewHolder(View headerView) {
            super(headerView);

            budgetHeader = (TextView) itemView.findViewById(R.id.header);
        }

    }

    //personal header viewholder
    public static class PersonalHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView personalBudgetHeader;

        public PersonalHeaderViewHolder(View headerView) {
            super(headerView);

            personalBudgetHeader = (TextView) itemView.findViewById(R.id.header);
        }

    }

    private ArrayList<Budget> allBudgets;
    private Context budgetContext;
    private static final int GROUP_HEADER = 0;
    private static final int BUDGET = 1;
    private static final int PERSONAL_HEADER = 2;


    //constructor with context all budgets
    public BudgetAdapter(Context context, ArrayList<Budget> budgets) {
        allBudgets = budgets;
        budgetContext = context;
    }

    private Context getContext() {
        return budgetContext;
    }

    //creates new views to be used by the layout manager
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context budgetContext = parent.getContext();
        //LayoutInflater inflater = LayoutInflater.from(budgetContext);

        //View budgetView = inflater.inflate(R.layout.fragment_overview_row_content, parent, false);

        //ViewHolder viewHolder = new ViewHolder(budgetView);
        //return viewHolder;

        if (viewType == GROUP_HEADER) {
            LayoutInflater inflater = LayoutInflater.from(budgetContext);
            View groupHeaderView = inflater.inflate(R.layout.fragment_overview_row_header, parent, false);
            GroupHeaderViewHolder ghviewHolder = new GroupHeaderViewHolder(groupHeaderView);
            return ghviewHolder;
        } else if (viewType == BUDGET) {
            LayoutInflater inflater = LayoutInflater.from(budgetContext);
            View budgetView = inflater.inflate(R.layout.fragment_overview_row_content, parent, false);
            BudgetViewHolder bviewHolder = new BudgetViewHolder(budgetView);
            return bviewHolder;
        } else if (viewType == PERSONAL_HEADER) {
            LayoutInflater inflater = LayoutInflater.from(budgetContext);
            View personalHeaderView = inflater.inflate(R.layout.fragment_overview_row_header, parent, false);
            PersonalHeaderViewHolder phviewHolder = new PersonalHeaderViewHolder(personalHeaderView);
            return phviewHolder;
        }
        throw new RuntimeException("there is no type that matches" + viewType);
    }

    //layout manager uses this to replace the contents of a view when scrolling to reduce loading
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof GroupHeaderViewHolder) {
            Budget budget = allBudgets.get(position);
            GroupHeaderViewHolder groupHeaderHolder = (GroupHeaderViewHolder) viewHolder;
            TextView groupTextViewHeader = groupHeaderHolder.budgetHeader;
            groupTextViewHeader.setText("Group Budgets");

        } else if (viewHolder instanceof BudgetViewHolder) {
            Budget budget = allBudgets.get(position);
            BudgetViewHolder budgetHolder = (BudgetViewHolder) viewHolder;
            TextView textView1 = budgetHolder.budgetSpentView;
            textView1.setText("$" + budget.getAmountSpentInBudget() + " out of $" + budget.getBudgetLimit());
            TextView textView2 = budgetHolder.budgetNameView;
            textView2.setText(budget.getName());
            TextView textView3 = budgetHolder.budgetRemainingView;
            textView3.setText("$" + budget.getAmountLeftInBudget() + " remaining");
            budgetHolder.progressBar.setMax(budget.getBudgetLimit().floatValue());
            budgetHolder.progressBar.setProgress(budget.getAmountSpentInBudget().floatValue());

            if(budget.getAmountSpentInBudget() >= budget.getBudgetLimit()) {
                budgetHolder.progressBar.setProgressColor(Color.parseColor("#FF0000"));
            } else if (budget.getAmountSpentInBudget() / budget.getBudgetLimit() >= 0.75) {
                budgetHolder.progressBar.setProgressColor(Color.parseColor("#FFD400"));
            }

        } else if(viewHolder instanceof PersonalHeaderViewHolder) {
            Budget budget = allBudgets.get(position);
            PersonalHeaderViewHolder personalHeaderHolder = (PersonalHeaderViewHolder) viewHolder;
            TextView personalTextViewHeader = personalHeaderHolder.personalBudgetHeader;
            personalTextViewHeader.setText("Personal Budgets");
        }


    }

    @Override
    public int getItemCount() {
        return allBudgets.size() + 2;
    }


    public int getItemViewType(int position, ArrayList<Budget> allBudgets) {
        int len = groupBudgetLen(allBudgets);
        if (len >= 0) {
            if (position == 0) {
                return GROUP_HEADER;
            }
            if (position == len + 1) { //TODO position is the length of group budget + 1
                return PERSONAL_HEADER;
            }
            return BUDGET;
        } else {
            if (position == 0) {
                return GROUP_HEADER;
            }
            return BUDGET;
        }
    }

    private int groupBudgetLen (ArrayList<Budget> allBudgets) {
        int groupBudgetLength = 0;
        for (int i = 0; i < allBudgets.size(); i++) {
            if (allBudgets.get(i).isGroupBudget()) {
                return groupBudgetLength;
            }
            groupBudgetLength ++;
        }
        if (groupBudgetLength == allBudgets.size() - 1) {
            groupBudgetLength = -1;
        }
        return groupBudgetLength;
    }
}
