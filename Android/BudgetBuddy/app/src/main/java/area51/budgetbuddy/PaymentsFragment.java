package area51.budgetbuddy;


/**
 * Created by paige on 4/16/17.
 */

import android.content.Context;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PaymentsFragment extends Fragment {
    private int mPage;
    RecyclerView rvPayments;
    LinearLayoutManager layoutManager;
    ArrayList<Payment> paymentArr;

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
        rvPayments = (RecyclerView) view.findViewById(R.id.payment_recycler_view);
        PaymentAdapter adapter = new PaymentAdapter(this.getContext()); // here is where we go to adapter
        rvPayments.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getContext());
        rvPayments.setLayoutManager(layoutManager);
        return view;
    }
}
