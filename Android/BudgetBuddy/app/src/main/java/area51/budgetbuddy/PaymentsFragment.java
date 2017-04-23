package area51.budgetbuddy;


/**
 * Created by paige on 4/16/17.
 */

import android.content.Context;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PaymentsFragment extends Fragment {
    private int mPage;

    public static final String ARG_PAGE = "ARG_PAGE";

    public static PaymentsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PaymentsFragment fragment = new PaymentsFragment();
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
        View view = inflater.inflate(R.layout.fragment_payments, container, false);

        // TODO : Customize the UI for the Payments Screen here
        // TODO Erick : replace - this is just here for an example on how to get payment data

        TextView textView = (TextView) view.findViewById(R.id.textview);
        User currentUser = AppVariables.currentUser;
        String paymentsString = new String();

        // I use the method `getAllPaymentsSorted` and think it works
        // but you may want to test that it actually sorts by date correctly
        for (Payment payment : AppVariables.getAllPaymentsSorted(AppVariables.currentUser)) {
            paymentsString += "Payment made for: $" + payment.getAmountSpent() + "/n";
        }
        textView.setText("Payments: " + paymentsString);

        return view;
    }
}