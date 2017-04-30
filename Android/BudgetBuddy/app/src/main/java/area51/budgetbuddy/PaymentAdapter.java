package area51.budgetbuddy;

import android.content.Context;
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
        System.out.println("++++++++++++++++++++++++++++SEE FIRST ++++++++++++++++++++++++++++++++");
        ArrayList<Date> uniqueDates = AppVariables.getUniquePaymentDates(AppVariables.currentUser); // array
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@NOT SEEING THIS @@@@@@@@@@@@@@@@@@@@@@@@@");
        // added this one too
        ArrayList<String> datesString = AppVariables.getUniquePaymentDateStrings(AppVariables.currentUser);

        // TODO : make this return an arraylist of each date header with payments sorted within
        ArrayList<Payment> sortedPayments = AppVariables.getAllPaymentsSorted(AppVariables.currentUser);
        ArrayList<PaymentsScreenCellDataModel> dateModel = new ArrayList<PaymentsScreenCellDataModel>(); // for dates
        ArrayList<PaymentsScreenCellDataModel> payModel = new ArrayList<PaymentsScreenCellDataModel>(); // for payments
//        for (Payment payment : AppVariables.getAllPaymentsSorted(AppVariables.currentUser)) {
//            paymentsString += "Payment made for: $" + payment.getAmountSpent() + "\n";
//            Log.d("Payment = ", paymentsString);
//        }
        // returns arraylist
        // have to iterate over uniqueDates and create new PaymentsScreenCell of type date

        // try double for loops at same level filling & converting done seperately
        for (Date date : uniqueDates) {
            PaymentsScreenCellDataModel payCellDate = new PaymentsScreenCellDataModel(date);
            dateModel.add(payCellDate);
        }

        for (Payment payment : sortedPayments) {
            PaymentsScreenCellDataModel payCellPayment = new PaymentsScreenCellDataModel(payment);
            payModel.add(payCellPayment);
        }
        // would I have to account for when # of dates > # of payments ????????
        //for (String dateString :datesString) { // for paymentdates for now 3 // converting now to traditional for loop
        int i = 0; // starting index
        //for (int i = 0; i < datesString.size(); i++) { // maybe while loop
        while (i < datesString.size()) { // 0 < 3
            // looking at 01/12
            boolean firstTime = true; // should work outside loop thus not overiding value
            int counter = 0;
            int done = datesString.size(); // lolz
            for (int j = 0; j < sortedPayments.size(); j++) { // mini example 6
                //boolean firstTime = true;
                // use get when indexing
                if (counter == done) {
                    break; // should break out successfully
                }
                if (sortedPayments.get(j).getPurchaseDate() == datesString.get(i)) { // once it is not equal move on immediately to next date
                    if (firstTime) { // first new Date encountered
                        // note index i only refers to position in payments
                        PaymentsScreenCellDataModel firstDate = dateModel.get(i); // working syntax
                        cells.add(firstDate); // added date
                        // i believe I still have to add first payment as well if it exists
                        cells.add(payModel.get(j));
                        firstTime = false;
                        //my count 1,
                    } else { // since it is false
                        cells.add(payModel.get(j)); // getting payCellModel in order
                        //my count 2,3
                        //firstTime = true;
                    }
                    // need this case
                } else { // we dont have matching dates
                    firstTime = true; // make immediately true
                    if (firstTime) {
                        // updated i now it is 1 got incremented here now i = 2
                        PaymentsScreenCellDataModel nextDate = dateModel.get(i + 1); // now moved on correctly to second date
                        counter += 1;
                        cells.add(nextDate); // second date
                        cells.add(payModel.get(j)); // fourth payment
                        firstTime = false;
                        // my count 4, 5
                        // do it again for date 4/12 & payment #5
                    } else {
                        cells.add(payModel.get(j));
                        // my count 6
                    }
                    //else if () { // like this or else case???

                }
            }

        }
        return cells;
    }

        // using traditional for loop
        //for (int i = 0; i < yourArrayList.size(); i ++) {
        // i is the index
        // yourArrayList.get(i) is the element
        //}

        // old work
//        for (Date date : uniqueDates) {
//            // need this one to be able to correctly convert to CellDataModel
//            PaymentsScreenCellDataModel payCellDate = new PaymentsScreenCellDataModel(date); // we know isPayment is false
//            AppVariables.getUniquePaymentDateStrings(AppVariables.currentUser);
//            for (Payment payment : sortedPayments) {
//                if (payment.getPurchaseDate() == )
//                PaymentsScreenCellDataModel payCellPayment = new PaymentsScreenCellDataModel(payment);
//                cells.add(payCellDate); // first entry added is date
//                // date format in Payment 10/30/2017
//                if (payCellDate.getPayment()) // thus this if condition has to be before any toString or conversions done
//                    // need this version for comparison purposes
//                    //AppVariables.getUniquePaymentDateStrings(AppVariables.currentUser);
//                if (payCell.getViewType() == 0) { // so we have a date object
//                    cells.add(payCell);
//                } else {
//                    cells.add(payment); // have to convert this to CellDataModel to payment type
//                }
//            }
//        }
        // this will handle payments sorted within
        //return cells;
    //}


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
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) { // should be good
        // might change if statement
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
    // try for now
//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0) {
//            return DATE;
//        }
//        return PAYMENT;
//    }
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
        TextView owedDueView;

        public PaymentRowVH(View itemView) {
            super(itemView);

            paymentView = (TextView) itemView.findViewById(payment);
            paymentNoteView = (TextView) itemView.findViewById(R.id.payment_note);
            owedDueView = (TextView) itemView.findViewById(R.id.owed_due);
        }
    }

}
