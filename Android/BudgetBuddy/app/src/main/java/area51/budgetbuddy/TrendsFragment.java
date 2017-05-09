package area51.budgetbuddy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.*;

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

        BarChart chart = (BarChart) view.findViewById(R.id.chart);

        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("");
        chart.animateXY(2000, 2000);
        chart.invalidate();

        // PERSONAL PIE CHART

        User currentUser = AppVariables.currentUser;

        int i = 1;
        PieChart PersonalPieChart = (PieChart) view.findViewById(R.id.ppchart);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> plabels = new ArrayList<String>();

        for (Budget budget : currentUser.getPersonalBudgets().values()) {
            String budgetName = budget.name;
            plabels.add(budgetName);
            double personalDouble = budget.getAmountSpentInBudget();
            float personalAmount = (float) personalDouble;
            if (personalAmount > 0.0f) {
                entries.add(new Entry(personalAmount, i));
            }
            i += 1;
        }

        PieDataSet pdataset = new PieDataSet(entries, "# of Calls");

        PieData pdata = new PieData(plabels, pdataset);
        PersonalPieChart.setDescription("");
        pdataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        PersonalPieChart.setData(pdata);
        PersonalPieChart.animateY(2000);

        //GROUP PIE CHART
        PieChart GroupPieChart = (PieChart) view.findViewById(R.id.gpchart);
        ArrayList<Entry> entriess = new ArrayList<>();
        ArrayList<String> glabels = new ArrayList<String>();
        int j = 1;

        for (Budget budget : currentUser.userGroupBudgets().values()) {
            String gBudgetName = budget.name;
            glabels.add(gBudgetName);
            double groupDouble = budget.getAmountSpentInBudget();
            float groupAmount = (float) groupDouble;
            if (groupAmount > 0.0f) {
                entriess.add(new Entry(groupAmount, j));
                j += 1;
            }
        }

        PieDataSet gdataset = new PieDataSet(entriess, "# of Calls");

        PieData gdata = new PieData(glabels, gdataset);
        GroupPieChart.setDescription("");
        gdataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        GroupPieChart.setData(gdata);
        GroupPieChart.animateY(2000);

        return view;
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        /*
        * For all budgets in Budget
        *   if Payment made in January
        *       Add it in new float = (float) JanuaryEntry
        * */
        ArrayList<BarEntry> personalBar = new ArrayList<>();
        ArrayList<BarEntry> groupBar = new ArrayList<>();

        int monthNum = 0;
        while (monthNum < 12) {
            // Set the personal amount spent values
            Float personalAmountSpent = AppVariables.getPersonalSpendingForMonth(monthNum);
            if (personalAmountSpent > 0.0f) {
                BarEntry personalEntry = new BarEntry(personalAmountSpent, monthNum); // Jan
                personalBar.add(personalEntry);
            }
            // Set the group amount spent values
            Float groupAmountSpent = AppVariables.getGroupSpendingForMonth(monthNum);
            if (groupAmountSpent > 0.0f) {
                BarEntry groupEntry = new BarEntry(groupAmountSpent, monthNum); // Jan
                groupBar.add(groupEntry);
            }
            monthNum++;
        }

        BarDataSet barDataSet1 = new BarDataSet(personalBar, "Personal Budget");
        barDataSet1.setColor(Color.rgb(250, 0, 0));
        BarDataSet barDataSet2 = new BarDataSet(groupBar, "Group Budget");
        barDataSet1.setColor(Color.rgb(65, 105, 225));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        xAxis.add("JUL");
        xAxis.add("AUG");
        xAxis.add("SEP");
        xAxis.add("OCT");
        xAxis.add("NOV");
        xAxis.add("DEC");
        return xAxis;
    }
}