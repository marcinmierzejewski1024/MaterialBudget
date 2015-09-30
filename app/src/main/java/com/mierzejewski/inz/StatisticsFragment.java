package com.mierzejewski.inz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.mierzejewski.inz.common.CommonActivity;
import com.mierzejewski.inz.common.CommonFragment;
import com.mierzejewski.inz.model.*;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dom on 31/03/15.
 */
public class StatisticsFragment extends CommonFragment implements OnChartValueSelectedListener
{
    private enum StatisticsState{
        EXPENSES_AND_INCOMES,EXPENSES,INCOMES,EXPENSES_CATEGORY, INCOMES_CATEGORY
    };

    StatisticsState state = StatisticsState.EXPENSES_AND_INCOMES;


    TimePeriod period = TimePeriod.MONTH;
    private Spinner periodSpinner;
    View rootView;
    PieChart mChart;
    Date since = new Date(0l);
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

    BroadcastReceiver refreshListBroadcast = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            setExpenseAndIncomeData();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(refreshListBroadcast, MainApp.CHANGE_DATA_FILTER);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        getActivity().unregisterReceiver(refreshListBroadcast);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        //((CommonActivity)getActivity()).getToolbar().setDisplayHomeAsUpEnabled(true);
        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);



        rootView = inflater.inflate(R.layout.statistics_fragment,container,false);
        periodSpinner = (Spinner) rootView.findViewById(R.id.spinnerRange);

        ArrayAdapter periodArrayAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.currency_range));
        periodSpinner.setAdapter(periodArrayAdapter);
        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                period = TimePeriod.values()[position];

                if((period == TimePeriod.YEAR))
                {

                    Calendar calendar = Calendar.getInstance();
                    calendar.clear();
                    calendar.set(Calendar.MONTH, 0);
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    since = calendar.getTime();
                }
                else if(period == TimePeriod.MONTH)
                {
                    since = MainApp.getFirstDayOfMonth();
                }
                else if(period == TimePeriod.WEEK)
                {

                    Calendar monday = Calendar.getInstance();
                    monday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    since = monday.getTime();
                }

                setExpenseAndIncomeData();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        createPieChart();

        return rootView;
    }

    public void createPieChart()
    {
        mChart = (PieChart) rootView.findViewById(R.id.pieChart);
        mChart.setUsePercentValues(false);

        mChart.setHoleRadius(50f);


        mChart.setDrawCenterText(true);

        mChart.setDrawHoleEnabled(true);

        mChart.setRotationEnabled(true);


        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);
        mChart.setTouchEnabled(true);



        setExpenseAndIncomeData();

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h)
    {
        if(state == StatisticsState.EXPENSES_AND_INCOMES)
        {
            if(StatisticsState.EXPENSES.equals(e.getData()))
                setExpenseOrIncomeData(true);
            else if(StatisticsState.INCOMES.equals(e.getData()))
                setExpenseOrIncomeData(false);
        }
        else if (state == StatisticsState.EXPENSES || state == StatisticsState.INCOMES)
        {
            Object data = e.getData();
            if(data instanceof Category)
            {
                setCategoryData((Category) data);
            }
        }
        else if(state == StatisticsState.EXPENSES_CATEGORY || state == StatisticsState.INCOMES_CATEGORY)
        {
            if(e.getData() instanceof ExpenseIncome)
            {
                Long expenseId = ((ExpenseIncome) e.getData()).getExpenseId();
                ((CommonActivity) getActivity()).openExpense(expenseId);
            }
        }
    }

    private void setCategoryData(Category category)
    {
        if(category.isIncomeCategory())
        {
            state = StatisticsState.INCOMES_CATEGORY;
        }
        else
        {
            state = StatisticsState.EXPENSES_CATEGORY;
        }

        mChart.setCenterText(category.getName());

        try
        {

            List<ExpenseIncome> categoryData = null;
            if(Category.getRestCategoryExpense().equals(category))
            {

                state = StatisticsState.EXPENSES_CATEGORY;
                categoryData = MainData.getInstance().getExpenseIncomeData().getAllFromRestCategory(ExpenseIncomeType.EXPENSE, since);
            }
            else if(Category.getRestCategoryIncome().equals(category))
            {

                state = StatisticsState.INCOMES_CATEGORY;
                categoryData = MainData.getInstance().getExpenseIncomeData().getAllFromRestCategory(ExpenseIncomeType.INCOME, since);
            }
            else
            {

                categoryData = MainData.getInstance().getExpenseIncomeData().getByCategoryId(category.getCategoryId(), since,null);
            }

            ArrayList<String> xVals = new ArrayList<String>();
            for (int i = 0; i < categoryData.size(); i++)
            {
                if(categoryData.get(i) != null)
                {
                    xVals.add(categoryData.get(i).getName());

                }
            }

            ArrayList<Entry> yVals1 = new ArrayList<Entry>();

            for (int i = 0; i < categoryData.size(); i++)
            {
                if(categoryData.get(i) != null)
                    yVals1.add(new BarEntry(categoryData.get(i).getCash().getPounds(), i, categoryData.get(i)));
            }

            PieDataSet pieDataSet = new PieDataSet(yVals1,"");
            pieDataSet.setSliceSpace(yVals1.size() - 1);

            pieDataSet.setColor(Color.parseColor(category.getHexColor()));



            PieData pieData = new PieData(xVals,pieDataSet);

            pieData.setValueTextSize(10f);
            pieData.setValueFormatter(valueFormater);
            mChart.clear();
            mChart.setData(pieData);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected()
    {

    }

    private void setExpenseOrIncomeData(boolean expenses)
    {
        List<Pair<Category, CashAmmount>> dataPairs = null;

        if(expenses)
        {
            state = StatisticsState.EXPENSES;
            mChart.setCenterText(getString(R.string.expense));
            dataPairs = MainData.getInstance().getCategoryGroupedExpenses(since, 10, false);
        }
        else
        {
            state = StatisticsState.INCOMES;
            mChart.setCenterText(getString(R.string.income));
            dataPairs = MainData.getInstance().getCategoryGroupedIncomes(since, 10, false);
        }

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < dataPairs.size(); i++)
        {
            if(dataPairs.get(i) != null)
            {
                xVals.add(dataPairs.get(i).first.getName());

            }
        }

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < dataPairs.size(); i++)
        {
            if(dataPairs.get(i) != null)
                yVals1.add(new BarEntry((dataPairs.get(i).second.getPounds()), i, dataPairs.get(i).first));
        }

        PieDataSet pieDataSet = new PieDataSet(yVals1,"");
        pieDataSet.setSliceSpace(yVals1.size()-1);

        ArrayList<Integer> colorsList = new ArrayList<Integer>();
        for (Pair<Category,CashAmmount> pair:dataPairs)
        {
            colorsList.add(Color.parseColor(pair.first.getHexColor()));
        }

        pieDataSet.setColors(colorsList);
        


        PieData data = new PieData(xVals,pieDataSet);
        data.setValueFormatter(valueFormater);
        data.setValueTextSize(10f);
        mChart.clear();
        mChart.setData(data);
    }


    private void setExpenseAndIncomeData()
    {
        state = StatisticsState.EXPENSES_AND_INCOMES;

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add(getString(R.string.expense));
        xVals.add(getString(R.string.income));

        CashAmmount expensesSum = MainData.getInstance().getExpenseIncomeData().getSum(since, ExpenseIncomeType.EXPENSE);
        CashAmmount incomeSum = MainData.getInstance().getExpenseIncomeData().getSum(since, ExpenseIncomeType.INCOME);

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        yVals1.add(new BarEntry(expensesSum.getPounds(),0,StatisticsState.EXPENSES));
        yVals1.add(new BarEntry(incomeSum.getPounds(),1,StatisticsState.INCOMES));


        mChart.setCenterText(getString(R.string.expenses_and_incomes));


        PieDataSet pieDataSet = new PieDataSet(yVals1,"");
        pieDataSet.setSliceSpace(yVals1.size()-1);

        ArrayList<Integer> colorsList = new ArrayList<Integer>();

        colorsList.add(getResources().getColor(R.color.negative_color));
        colorsList.add(getResources().getColor(R.color.positive_color));

        pieDataSet.setColors(colorsList);



        PieData data = new PieData(xVals,pieDataSet);

        data.setValueTextSize(10f);

        data.setValueFormatter(valueFormater);
        mChart.clear();
        mChart.setData(data);
    }

    /**
     *
     * @return return true if handled
     */
    public boolean goBack()
    {
        if(state == StatisticsState.EXPENSES || state == StatisticsState.INCOMES)
        {
            setExpenseAndIncomeData();
            return true;
        }
        else if (state == StatisticsState.EXPENSES_CATEGORY)
        {
            setExpenseOrIncomeData(true);
            return true;
        }
        else if (state == StatisticsState.INCOMES_CATEGORY)
        {
            setExpenseOrIncomeData(false);
            return true;
        }
        return false;
    }



}
