package com.mierzejewski.inzynierka;

import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mierzejewski.inzynierka.common.CommonFragment;
import com.mierzejewski.inzynierka.model.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dom on 04/11/14.
 */
public class OverviewFragment extends CommonFragment
{
    private static final String TAG = "OverviewFragment";

    Date since;

    View expenseProgress;
    TextView outcomeText;
    TextView incomeText;
    View rootView;
    private TextView monthText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        since = MainApp.getFirstDayOfMonth();

        rootView = inflater.inflate(R.layout.overview, container, false);
        expenseProgress = rootView.findViewById(R.id.expense_progress);
        outcomeText = (TextView) rootView.findViewById(R.id.outcome_text);
        incomeText = (TextView) rootView.findViewById(R.id.income_text);
        monthText = (TextView) rootView.findViewById(R.id.monthText);


        CashAmmount expensesSum = MainData.getInstance().getExpenseIncomeData().getSum(since, ExpenseIncomeType.EXPENSE);
        CashAmmount incomeSum = MainData.getInstance().getExpenseIncomeData().getSum(since, ExpenseIncomeType.INCOME);
        outcomeText.setText("-"+expensesSum);
        incomeText.setText("+"+incomeSum);


        double incomeToExpense = 0.5;
        if(!(expensesSum.getPennies() == 0 && incomeSum.getPennies() == 0))
            incomeToExpense = incomeSum.getPennies()/(expensesSum.getPennies()*1.0f+incomeSum.getPennies());

        int percent = 100 -(int) (incomeToExpense*100);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT,percent);
        expenseProgress.findViewById(R.id.value).setLayoutParams(params);
        expenseProgress.findViewById(R.id.value).invalidate();


        DateFormat fmt = new SimpleDateFormat("MMMM yyyy",getResources().getConfiguration().locale);
        monthText.setText(fmt.format(since));

        return rootView;
    }



}
