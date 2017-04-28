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
import android.widget.TextView;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import java.util.ArrayList;

public class BudgetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Budget> allBudgets = new ArrayList<>();
    private Context budgetContext;
    private static final int GROUP_HEADER = 0;
    private static final int BUDGET = 1;
    private static final int PERSONAL_HEADER = 2;
    private static final int PBUDGET = 3;


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

        if (viewType == GROUP_HEADER) {
            LayoutInflater inflater = LayoutInflater.from(budgetContext);
            View groupHeaderView = inflater.inflate(R.layout.fragment_overview_row_header, parent, false);
            return new GroupHeaderViewHolder(groupHeaderView);
        } else if (viewType == BUDGET) {
            LayoutInflater inflater = LayoutInflater.from(budgetContext);
            View budgetView = inflater.inflate(R.layout.fragment_overview_row_content, parent, false);
            return new BudgetViewHolder(budgetView);
        } else if (viewType == PERSONAL_HEADER) {
            LayoutInflater inflater = LayoutInflater.from(budgetContext);
            View personalHeaderView = inflater.inflate(R.layout.fragment_overview_row_header, parent, false);
            return new PersonalHeaderViewHolder(personalHeaderView);
        } else if (viewType == PBUDGET) {
            LayoutInflater inflater = LayoutInflater.from(budgetContext);
            View pBudgetView = inflater.inflate(R.layout.fragment_overview_row_content, parent, false);
            return new PersonalBudgetViewHolder(pBudgetView);
        }
        throw new RuntimeException("there is no type that matches" + viewType);
    }

    //layout manager uses this to replace the contents of a view when scrolling to reduce loading
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof GroupHeaderViewHolder) {
            GroupHeaderViewHolder ghviewHolder = (GroupHeaderViewHolder) viewHolder;
            TextView groupTextViewHeader = ghviewHolder.budgetHeader;
            groupTextViewHeader.setText("Group Budgets");
        } else if (viewHolder instanceof BudgetViewHolder) {
            Budget budget = allBudgets.get(position - 1);
            BudgetViewHolder budgetHolder = (BudgetViewHolder) viewHolder;
            TextView textView1 = budgetHolder.budgetSpentView;
            textView1.setText("$" + budget.getAmountSpentInBudget() + " out of $" + budget.getBudgetLimit());
            TextView textView2 = budgetHolder.budgetNameView;
            String budgetName = budget.getName();
            textView2.setText(budgetName);
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
            PersonalHeaderViewHolder personalHeaderHolder = (PersonalHeaderViewHolder) viewHolder;
            TextView personalTextViewHeader = personalHeaderHolder.personalBudgetHeader;
            personalTextViewHeader.setText("Personal Budgets");
        }  else if (viewHolder instanceof PersonalBudgetViewHolder) {
            Budget budget = allBudgets.get(position - 2);
            PersonalBudgetViewHolder budgetHolder = (PersonalBudgetViewHolder) viewHolder;
            TextView textView1 = budgetHolder.pBudgetSpentView;
            textView1.setText("$" + budget.getAmountSpentInBudget() + " out of $" + budget.getBudgetLimit());
            TextView textView2 = budgetHolder.pBudgetNameView;
            String budgetName = budget.getName();
            textView2.setText(budgetName);
            TextView textView3 = budgetHolder.pBudgetRemainingView;
            textView3.setText("$" + budget.getAmountLeftInBudget() + " remaining");
            budgetHolder.pProgressBar.setMax(budget.getBudgetLimit().floatValue());
            budgetHolder.pProgressBar.setProgress(budget.getAmountSpentInBudget().floatValue());
            if(budget.getAmountSpentInBudget() >= budget.getBudgetLimit()) {
                budgetHolder.pProgressBar.setProgressColor(Color.parseColor("#FF0000"));
            } else if (budget.getAmountSpentInBudget() / budget.getBudgetLimit() >= 0.75) {
                budgetHolder.pProgressBar.setProgressColor(Color.parseColor("#FFD400"));
            }
        }
    }

    @Override
    public int getItemCount() {
        // TODO: change this
        return allBudgets.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        int len = AppVariables.currentUser.userGroupBudgets().size();
        if (position == 0) {
            return GROUP_HEADER;
        }
        if (position == len + 1) {
            return PERSONAL_HEADER;
        }
        if (position > len + 1) {
            return PBUDGET;
        }
        return BUDGET;
    }

    public void swapItems(ArrayList<Budget> allBudgets) {

    }

    //holds group budget viewholders
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

    //holds personal budget viewholders
    public static class PersonalBudgetViewHolder extends RecyclerView.ViewHolder {
        TextView pBudgetNameView;
        TextView pBudgetRemainingView;
        TextView pBudgetSpentView;
        RoundCornerProgressBar pProgressBar;

        public PersonalBudgetViewHolder(View itemView) {
            super(itemView);

            pBudgetNameView = (TextView) itemView.findViewById(R.id.budget_name);
            pBudgetSpentView = (TextView) itemView.findViewById(R.id.budget_spent);
            pBudgetRemainingView = (TextView) itemView.findViewById(R.id.budget_remaining);
            pProgressBar = (RoundCornerProgressBar) itemView.findViewById(R.id.progress_bar);
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
}
