package com.mierzejewski.inzynierka;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.mierzejewski.inzynierka.common.CommonFragment;
import com.mierzejewski.inzynierka.model.*;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by dom on 04/11/14.
 */
public class OverviewFragment extends CommonFragment
{
    private static final String TAG = "OverviewFragment";

    Calendar since;
    TextView outcomeText;
    TextView incomeText;
    View rootView;
    private TextView monthText;
    private LineChart monthChart;

    private ValueFormatter valueFormater = new ValueFormatter()
    {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return getFormattedValue(value);
        }

        public String getFormattedValue(float value)
        {
            return String.format("%.2f", value)+Currency.getDefault().symbol;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        since = MainApp.getFirstDayOfMonth();

        rootView = inflater.inflate(R.layout.overview, container, false);
        monthChart = (LineChart) rootView.findViewById(R.id.monthChart);
        outcomeText = (TextView) rootView.findViewById(R.id.outcome_text);
        incomeText = (TextView) rootView.findViewById(R.id.income_text);
        monthText = (TextView) rootView.findViewById(R.id.monthText);

        monthChart.setOnTouchListener(new View.OnTouchListener() {
            public float x1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                    switch(event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            x1 = event.getX();
                            break;
                        case MotionEvent.ACTION_UP:
                            float x2 = event.getX();
                            float deltaX = x2 - x1;

                            if (Math.abs(deltaX) > 20)
                            {
                                // Left to Right swipe action
                                if (x2 > x1)
                                {
                                    changeMonth(1);
                                }

                                // Right to left swipe action
                                else
                                {
                                    changeMonth(-1);
                                }

                            }
                            else
                            {
                                // consider as something else - a screen tap for example
                            }
                            break;
                    }
                    return false;
                }
        });

        updateViews();
        return rootView;
    }

    private void changeMonth(int months)
    {

        since.add(Calendar.MONTH, months);
        updateViews();

    }

    private void updateViews() {

        Calendar endOfMonth = Calendar.getInstance();
        endOfMonth.setTime(since.getTime());
        endOfMonth.add(Calendar.MONTH,1);

        CashAmmount expensesSum = MainData.getInstance().getExpenseIncomeData().getSum(since.getTime(),endOfMonth.getTime(), ExpenseIncomeType.EXPENSE);
        CashAmmount incomeSum = MainData.getInstance().getExpenseIncomeData().getSum(since.getTime(),endOfMonth.getTime(), ExpenseIncomeType.INCOME);


        outcomeText.setText("-" + expensesSum);
        incomeText.setText("+" + incomeSum);

        try {
            List<ExpenseIncome> incomesAndExpenses = MainData.getInstance().getExpenseIncomeData().getByCategoryId(null, since.getTime(),endOfMonth.getTime(), null);

            CashAmmount balance = new CashAmmount();
            final ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            List<String> xVals = new ArrayList<>();

            int numbersOfDaysInMonth = since.getActualMaximum(Calendar.DAY_OF_MONTH);
            int maxOperationsInOneDay = incomesAndExpenses.size()/2;//pretty lazy

            for (int index = 1;index<numbersOfDaysInMonth;index++)
            {
                xVals.add(""+index);
                for(int i = 1;i<maxOperationsInOneDay;i++)
                    xVals.add("");
            }


            int dayOfMonth = 0;
            int dayOffset = 0;

            if(incomesAndExpenses.size() > 0)
                yVals1.add(new Entry(0,0));

            for (ExpenseIncome item : incomesAndExpenses) {


                if(item.getType() == ExpenseIncomeType.INCOME)
                    balance.addCash(item.getCash());
                else
                    balance.subCash(item.getCash());

                Calendar date = Calendar.getInstance();
                date.setTime(item.getDate());


                if(dayOfMonth == date.get(Calendar.DAY_OF_MONTH))
                    dayOffset++;
                else
                    dayOffset = 0;

                dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
                int xValsIndex = maxOperationsInOneDay * dayOfMonth + dayOffset;

                yVals1.add(new Entry(balance.getPennies(Currency.getDefault()), xValsIndex));

            }



            LineDataSet set1 = new LineDataSet(yVals1, getString(R.string.expenses_and_incomes));
            set1.setDrawFilled(false);
            set1.setDrawCubic(false);
            set1.setCircleSize(5);
            set1.setColor(getResources().getColor(R.color.black_semi_transparent));
            set1.setCircleColorHole(getResources().getColor(R.color.accent_color));

            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(set1);

            LineData data = new LineData(xVals, dataSets);
            data.setValueTextSize(14f);

            data.setValueFormatter(valueFormater);
            monthChart.clear();
            monthChart.setData(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }


        DateFormat fmt = new SimpleDateFormat("MMMM yyyy",getResources().getConfiguration().locale);
        monthText.setText(fmt.format(since.getTime()));
    }


    @Override
    public String getTitle() {

        return getResources().getString(R.string.overview);
    }

    @Override
    public Drawable getLogo() {
        return getResources().getDrawable(R.drawable.ic_home);
    }
}
