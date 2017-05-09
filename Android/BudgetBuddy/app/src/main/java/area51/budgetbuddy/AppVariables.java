package area51.budgetbuddy;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.ContentValues.TAG;
import static area51.budgetbuddy.R.id.payment;

/**
 * Created by paige on 4/16/17.
 * Contains the global variables needed for the "wizard of oz"-ing of our app
 */


// Should've named this DataModel or something, but renaming files may cause some merge conflicts so oh well
public class AppVariables extends Application {
    private static AppVariables singleton;

    // The user currently logged in
    // use this variable to access group and personal budgets
    public static User currentUser;

    // TODO: we'll eventually want to map something like groupID's to group objects (rather than name)
    public static Map<String, Group> allGroups = new HashMap<String,Group>();

    public static boolean groupWithNameExists(String name) {
        return allGroups.containsKey(name);
    }

    // TODO: we'll want to replace this with some sort of ID (rather than checking by name)
    public static Group getGroupWithName(String name) {
        return allGroups.get(name);
    }

    public static Map<String, Budget> getBudgetsForGroupWithName(String name) {
        return currentUser.userGroupBudgets();
    }

    // TODO: actually make the name make sense. Right now the `database` is the `allGroups` dictionary
    public static void addGroupToAllGroupsDictionary(Group group) {
        allGroups.put(group.getName(), group);
    }

    public static AppVariables getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    // Get all the payments for the user (both group and personal), sorted by date (maybe heh TODO?)
    public static ArrayList<Payment> getAllPaymentsSorted(User user) {
        // TODO: replace
        ArrayList<Payment> allPayments = new ArrayList<>();

        // adds all the group budget payments to the array
        for (Budget budget : user.userGroupBudgets().values()) {
            ArrayList<Payment> groupPayments = budget.getPayments();
            allPayments.addAll(groupPayments);
        }

        // add all the personal budget payments to the array
        for (Budget budget : user.getPersonalBudgets().values()) {
            ArrayList<Payment> userPayments = budget.getPayments();
            allPayments.addAll(userPayments);
        }

        Collections.sort(allPayments, new Comparator<Payment>() {
            public int compare(Payment m1, Payment m2) {
                return m1.paymentPurchaseDate().compareTo(m2.paymentPurchaseDate());
            }
        });

        return allPayments;
    }

    //gets the budget that a payment goes to
    public static String getBudgetForPayment(Payment payment) {
        for (Budget budget: currentUser.getPersonalBudgets().values()) {
            if (budget.getPayments().contains(payment)) {
                return budget.getName();
            }
        }
        for (Budget budget: currentUser.getGroup().getGroupBudgets().values()) {
            if (budget.getPayments().contains(payment)) {
                return budget.getName();
            }
        }
        throw new AssertionError("Payment made by" + payment.getUsername() +
                " does not have a corresponding budget");
    }

    // Helper method for converting a string to a date
    public static Date convertStringToDate(String dateString) { // here we are messing up
        Date date = new Date();
        String[] formatStrings = {"M/y", "M/d/y", "M-d-y", "MM/dd/yyyy", "M/dd/yyyy"};
        for (String formatString : formatStrings) {
            try {
                return new SimpleDateFormat(formatString).parse(dateString);
            }
            catch (ParseException e) {
                Log.e("ERROR", "could not parse date string: " + dateString);
            }
        }
        return date;
    }
    // difference between this and next method
    public static ArrayList<String> getUniquePaymentDateStrings(User user) { // this one need to change
        ArrayList<Date> datesSet = getUniquePaymentDates(user);

        ArrayList<String> toReturn = new ArrayList<String>();
        for (Date date : datesSet) {
            Format formatter = new SimpleDateFormat("MM/dd/yyyy");
            String dateString = formatter.format(date);
            //String dateString = date.toString();
            toReturn.add(dateString);
        }
        return toReturn;
    }
    // to modify
//    Format formatter = new SimpleDateFormat("EEEE, MMMM dd");
//    // right now showing Thurday, September, 01 (need )
//    String dateString = formatter.format(date);
//    return dateString; //date.toString();

    public static ArrayList<Date> getUniquePaymentDates(User user) {
        HashSet<Date> datesSet = new HashSet<>();
        ArrayList<Payment> allPayments = AppVariables.getAllPaymentsSorted(user); // was able to return successfully i believe
        //HashSet<Date> was going to try and copy
        for (Payment payment: allPayments) {
            // why did it print twice???
            //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!GETING STUCK!!!!!!!!!!!!!!!!!!!");
            Date paymentDate = convertStringToDate(payment.getPurchaseDate()); // here were we get stuck
            datesSet.add(paymentDate);
        } // stuff up here looks good based on debugger
        // Sort the dates so they are in order
        //ArrayList<Date> sortedDates = new ArrayList<Date>();
        // trying this
        ArrayList<Date> sortedList = new ArrayList(datesSet); // convert hashset to list
        Collections.sort(sortedList);
//        for (Date date: datesSet) {
//            Date newestDate = Collections.min(datesSet); // got weird when it got to one case???
//            //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!GETING STUCK!!!!!!!!!!!!!!!!!!!");
//            sortedDates.add(newestDate);
//            datesSet.remove(newestDate);
//        }
//        Set yourHashSet = new HashSet();
//        List sortedList = new ArrayList(yourHashSet);
//        Collections.sort(sortedList);
        //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        //System.out.println(sortedList);
        return sortedList;  //sortedDates; // now returning sortedList
    }



    private static ArrayList<Payment> getAllPaymentsFromBudgetsForMonth(int monthNumber, Map<String, Budget> budgets) {
        // add all the personal budget payments to the array
        ArrayList<Payment> monthPayments = new ArrayList<>();
        for (Budget budget : budgets.values()) {
            ArrayList<Payment> userPayments = budget.getPayments();
            for (Payment payment : budget.getPayments()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(payment.dateForPayment());
                int month = cal.get(Calendar.MONTH);
                if (month == monthNumber) {
                    monthPayments.add(payment);
                }
            }
        }
        return monthPayments;
    }

    public static Float getGroupSpendingForMonth(int monthNumber) {
        Float amountSpentInMonth = 0.0f;
        for (Payment payment: getAllPaymentsFromBudgetsForMonth(monthNumber, currentUser.getGroup().getGroupBudgets())) {
            amountSpentInMonth += new Float(payment.getAmountSpent());
        }
        return amountSpentInMonth;
    }

    public static Float getPersonalSpendingForMonth(int monthNumber) {
        Float amountSpentInMonth = 0.0f;
        for (Payment payment: getAllPaymentsFromBudgetsForMonth(monthNumber, currentUser.getPersonalBudgets())) {
            amountSpentInMonth += new Float(payment.getAmountSpent());
        }
        return amountSpentInMonth;
    }
}
