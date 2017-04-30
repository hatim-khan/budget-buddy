package area51.budgetbuddy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by natalieshum on 4/25/17.
 */

public class PaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private static final int DATE = 0;
    private static final int PAYMENT = 1;

    private ArrayList<PaymentsScreenCellDataModel> cells = new ArrayList<>();

    public PaymentAdapter(Context context) {
        cells = populateCellsArray();
        this.context = context;
    }


    private ArrayList<PaymentsScreenCellDataModel> populateCellsArray() {
        ArrayList<PaymentsScreenCellDataModel> cells = new ArrayList<PaymentsScreenCellDataModel>();
        ArrayList<Date> uniqueDates = AppVariables.getUniquePaymentDates(AppVariables.currentUser);


        // TODO : make this return an arraylist of each date header with payments sorted within

        return cells;
    }


    private Context getContext() {
        return context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == DATE) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View headerView = inflater.inflate(R.layout.fragment_payments_row_header, parent, false);
            return new PaymentHeaderVH(headerView);
        } else if (viewType == PAYMENT) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View rowView = inflater.inflate(R.layout.fragment_payments_row_content, parent, false);
            return new PaymentRowVH(rowView);
        }
        throw new RuntimeException("there is no type that matches " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof PaymentHeaderVH) {
            PaymentHeaderVH headerHolder = (PaymentHeaderVH) viewHolder;
            TextView textView1 = headerHolder.dateView;
            textView1.setText(cells.get(position).getDateStringForCell());
        } else if (viewHolder instanceof PaymentRowVH) {
            // TODO: check that this doesn't crash
            Payment payment = cells.get(position).getPayment();
            PaymentRowVH paymentHolder = (PaymentRowVH) viewHolder;
            TextView textView1 = paymentHolder.paymentView;
            textView1.setText(payment.getUsername() + " spent $" + payment.getAmountSpent() + " on "
            + AppVariables.getBudgetForPayment(payment));
            TextView textView2 = paymentHolder.paymentNoteView;
            textView2.setText(payment.getNotes());
            TextView textView3 = paymentHolder.owedDueView;
            textView3.setText("");
        }
    }

    @Override
    public int getItemCount() {
        // Number of cells = number of payments plus number of unique dates (for the label headers)
        return cells.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return DATE;
        }
        return PAYMENT;
    }

    public static class PaymentHeaderVH extends RecyclerView.ViewHolder {
        TextView dateView;

        public PaymentHeaderVH(View itemView) {
            super(itemView);

            dateView = (TextView) itemView.findViewById(R.id.date_text_view);
        }
    }

    public static class PaymentRowVH extends RecyclerView.ViewHolder {
        TextView paymentView;
        TextView paymentNoteView;
        TextView owedDueView;

        public PaymentRowVH(View itemView) {
            super(itemView);

            paymentView = (TextView) itemView.findViewById(R.id.payment);
            paymentNoteView = (TextView) itemView.findViewById(R.id.payment_note);
            owedDueView = (TextView) itemView.findViewById(R.id.owed_due);
        }
    }

}
