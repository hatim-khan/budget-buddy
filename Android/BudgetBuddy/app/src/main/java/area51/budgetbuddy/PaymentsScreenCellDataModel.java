package area51.budgetbuddy;

import java.text.Format;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by paige on 4/29/17.
 */

public class PaymentsScreenCellDataModel {

    boolean isPayment;
    Payment payment;
    Date date;

    // Initializer for a header (just pass in the string for the date)
    public PaymentsScreenCellDataModel(Date date) { // see how this is defined
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

    public int getViewType() { //itemView?
        if (isPayment) {
            return 1;
        }
        return 0;
    }

    public String getDateStringForCell() { // only used for display
        if (date != null) {
            // TODO: Erick make this use dateformatter done
            Format formatter = new SimpleDateFormat("EEEE, MMMM dd");
            // right now showing Thurday, September, 01 (need )
            String dateString = formatter.format(date);
            return dateString; //date.toString();
        }
        return "ERROR";
    }

    public Payment getPayment() {
        return payment;
    }
}
