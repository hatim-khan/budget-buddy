package area51.budgetbuddy;

import java.util.Date;

/**
 * Created by paige on 4/29/17.
 */

public class PaymentsScreenCellDataModel {

    boolean isPayment;
    Payment payment;
    Date date;

    // Initializer for a header (just pass in the string for the date)
    public PaymentsScreenCellDataModel(Date date) {
        this.isPayment = false;
        this.date = date;
        this.payment = null;
    }

    // Initializer for a payment cell (just pass in the Payment object)
    public PaymentsScreenCellDataModel(Payment payment) {
        this.isPayment= true;
        this.payment = payment;
        this.date = null;
    }

    public int getViewType() {
        if (isPayment) {
            return 1;
        }
        return 0;
    }

    public String getDateStringForCell() {
        if (date != null) {
            // TODO: Erick make this use dateformatter
            return date.toString();
        }
        return "ERROR";
    }

    public Payment getPayment() {
        return payment;
    }
}
