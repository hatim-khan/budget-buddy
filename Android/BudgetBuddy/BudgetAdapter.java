public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView budgetNameView;
        public TextView budgetRemainingView;
        public TextView budgetSpentView;
        //public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            budgetNameView = (TextView) itemView.findViewById(R.id.budget_type);
            budgetSpentView = (TextView) itemView.findViewById(R.id.budget_spent);
            budgetRemainingView = (TextView) itemView.findViewById(R.id.budget_remaining);
            //progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar1);
        }
    }

    private List<Budget> allBudgets;
    private Context budgetContext;

    //constructor with context all budgets
    public BudgetAdapter(Context context, List<Budget> budgets) {
        allBudgets = budgets;
        budgetContext = context;
    }

    private Context getContext() {
        return budgetContext;
    }

    //creates new views to be used by the layout manager
    @Override
    public BudgetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(budgetContext);

        View budgetView = inflater.inflate(R.layout.fragment_overview_row_content, parent, false);

        ViewHolder viewHolder = new ViewHolder(budgetView);
        return viewHolder;
    }

    //layout manager uses this to replace the contents of a view when scrolling to reduce loading
    @Override
    public void onBindViewHolder(BudgetAdapter.ViewHolder viewHolder, int position) {
        Budget budget = allBudgets.get(position);

        TextView textView1 = viewHolder.budgetSpentView;
        textView1.setText("$"budget.getAmountSpentInBudget() + "/$" + budget.getBudgetLimit());
        TextView textView2 = viewHolder.budgetNameView;
        textView2.setText(budget.getName());
        TextView textView3 = viewHolder.budgetRemainingView;
        textView3.setText("$" + budget.getAmountLeftInBudget() + " remaining");

    }
}