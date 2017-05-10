package area51.budgetbuddy;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static area51.budgetbuddy.R.id.payment;
import static area51.budgetbuddy.R.id.visible;

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
        ArrayList<Date> uniqueDates = AppVariables.getUniquePaymentDates(AppVariables.currentUser); // array of uniqueDates
        ArrayList<String> datesString = AppVariables.getUniquePaymentDateStrings(AppVariables.currentUser); // datesString
        ArrayList<Payment> sortedPayments = AppVariables.getAllPaymentsSorted(AppVariables.currentUser); // sortedPayments

        ArrayList<PaymentsScreenCellDataModel> dateModel = new ArrayList<PaymentsScreenCellDataModel>(); // for dates
        ArrayList<PaymentsScreenCellDataModel> payModel = new ArrayList<PaymentsScreenCellDataModel>(); // for payments

        // completing dateModel array
        for (Date date : uniqueDates) {
            PaymentsScreenCellDataModel payCellDate = new PaymentsScreenCellDataModel(date);
            dateModel.add(payCellDate);
        }

        // completing payModel array
        for (Payment payment : sortedPayments) { // was sortedPayments
            PaymentsScreenCellDataModel payCellPayment = new PaymentsScreenCellDataModel(payment, payment.isGroupPayment());
            payModel.add(payCellPayment);
        }
        int i = 0; // starting index that is for keeping track of #dateStrings which is smaller or equal to sortedPayments total
        outerLoop:
        while (i < datesString.size()) {
            boolean firstTime = true;
            //int counter = 0; // keeping track of total number of dates we have added to cells
            int done = datesString.size();
            // right now date size is smaller than sortedPayments size usually
            // thus wont hit all cases
            for (int j = 0; j < sortedPayments.size()+1; j++) { // (counter == done)||
                if ((j == sortedPayments.size())) {
                    break outerLoop;
                }
                if (sortedPayments.get(j).getPurchaseDate().equals(datesString.get(i))) { // so if we find new dateModel with its first payment
                    if (firstTime) {
                        if (i < dateModel.size()) {
                            PaymentsScreenCellDataModel firstDate = dateModel.get(i);
                            cells.add(firstDate);
                            cells.add(payModel.get(j));
                            firstTime = false;
                            //counter += 1;
                        }

                    } else { // looking at a payment that is not the first
                        cells.add(payModel.get(j));
                        // counter += 1;  // not sure if this should be here
                    }

                } else { // we don't have matching dates so encountered new date
                    firstTime = true; // make immediately true
                    if (firstTime) {
                        if (i < dateModel.size() - 1) {
                            PaymentsScreenCellDataModel nextDate = dateModel.get(i + 1);
                            //counter += 1;
                            cells.add(nextDate);
                            cells.add(payModel.get(j));
                            firstTime = false;
                            i += 1; // only update when date added
                        }
                    } else {
                        cells.add(payModel.get(j));
                        //counter += 1; // not sure if this should be here
                    }
                }
            }

        }
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

    private void setUIforAlerts(Payment payment, PaymentRowVH paymentHolder) {
        // Alerts
        CardView dueAlertCard = paymentHolder.dueAlertCard;
        TextView dueView = paymentHolder.dueView;
        CardView oweAlertCard = paymentHolder.oweAlertCard;
        TextView owedView = paymentHolder.owedView;

        dueAlertCard.setVisibility(View.GONE);
        dueView.setVisibility(View.GONE);
        oweAlertCard.setVisibility(View.GONE);
        owedView.setVisibility(View.GONE);

        if (payment.isGroupPayment()) {
            // In this case, we are owed dollas
            if (payment.getUsername().equals(AppVariables.currentUser.getUsername())) {
                owedView.setVisibility(View.VISIBLE);
                oweAlertCard.setVisibility(View.VISIBLE);
                owedView.setText("$" + payment.amountDueForPayment() + "\nowed");
            }
            // Otherwise, some money is due
            else {
                dueView.setVisibility(View.VISIBLE);
                dueAlertCard.setVisibility(View.VISIBLE);
                dueView.setText("$" + payment.amountDueForPayment() + "\ndue");
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) { // should be good
        // might change if statement
        if (viewHolder instanceof PaymentHeaderVH) {
            PaymentHeaderVH headerHolder = (PaymentHeaderVH) viewHolder;
            TextView textView1 = headerHolder.dateView;
            textView1.setText(cells.get(position).getDateStringForCell());
        }
        else if (viewHolder instanceof PaymentRowVH) {
            Payment payment = cells.get(position).getPayment();
            PaymentRowVH paymentHolder = (PaymentRowVH) viewHolder;

            TextView textView1 = paymentHolder.paymentView;
            if (AppVariables.currentUser.getUsername().equals(payment.getUsername())) {
                textView1.setText("You spent $" + payment.getAmountSpent() + " on " + AppVariables.getBudgetForPayment(payment));
            }
            else {
                textView1.setText(payment.getUsername() + " spent $" + payment.getAmountSpent() + " on " + AppVariables.getBudgetForPayment(payment));
            }

            TextView textView2 = paymentHolder.paymentNoteView;
            textView2.setText(payment.getNotes());
            setUIforAlerts(payment, paymentHolder);
        }
    }

    @Override
    public int getItemCount() {
        // Number of cells = number of payments plus number of unique dates (for the label headers)
        return cells.size();
    }

    @Override
    public int getItemViewType(int position) {
        return cells.get(position).getViewType();
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
        TextView owedView;
        TextView dueView;
        CardView dueAlertCard;
        CardView oweAlertCard;

        public PaymentRowVH(View itemView) {
            super(itemView);
            paymentView = (TextView) itemView.findViewById(payment);
            paymentNoteView = (TextView) itemView.findViewById(R.id.payment_note);
            owedView = (TextView) itemView.findViewById(R.id.owe_view);
            dueView = (TextView) itemView.findViewById(R.id.due_view);
            dueAlertCard = (CardView) itemView.findViewById(R.id.due_alert_card);
            oweAlertCard = (CardView) itemView.findViewById(R.id.owe_alert_card);
        }
    }
}