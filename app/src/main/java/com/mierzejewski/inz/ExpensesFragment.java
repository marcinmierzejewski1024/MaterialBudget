package com.mierzejewski.inz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.mierzejewski.inz.common.CommonActivity;
import com.mierzejewski.inz.common.CommonFragment;
import com.mierzejewski.inz.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dom on 11/11/14.
 */
public class ExpensesFragment extends CommonFragment
{
    private static final int NAMED_CATEGORIES = 8;
    public static final String TYPE_KEY = "typeKey";
    private Spinner range;

    public enum ExpenseFragmentType {EXPENSES, INCOMES};


    View rootView;

    private TextView sum;
    List<Pair<Category,CashAmmount>> data = new ArrayList<Pair<Category, CashAmmount>>();

    BroadcastReceiver refreshListBroadcast = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            rangeListener.onItemSelected(null,null,0,0);
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

    private AdapterView.OnItemSelectedListener rangeListener = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Date now = new Date();
            String daysString = getResources().getStringArray(R.array.rangeDays)[position];
            long days = Integer.parseInt(daysString);
            long fromMilis = now.getTime();
            long timeDistance = (days * 24l * 60l * 60l * 1000l);
            fromMilis -= timeDistance;
            Date from = new Date(fromMilis);

            ExpenseFragmentType type = (ExpenseFragmentType) getArguments().getSerializable(TYPE_KEY);
            if(type == ExpenseFragmentType.EXPENSES)
                data = MainData.getInstance().getCategoryGroupedExpenses(from, NAMED_CATEGORIES,true);
            else
                data = MainData.getInstance().getCategoryGroupedIncomes(from,NAMED_CATEGORIES,true);

            if(data != null)
            {
                drawChartLegend();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.expenses_fragment, container, false);

        range= (Spinner) rootView.findViewById(R.id.spinnerRange);
        sum = (TextView) rootView.findViewById(R.id.sum);
        range.setOnItemSelectedListener(rangeListener);

        return rootView;
    }


    private void drawChartLegend()
    {
        try
        {
            ListView legend = (ListView) rootView.findViewById(R.id.legendListView);
            Pair<Category, CashAmmount>[] tmp=new Pair[1];
            LegendAdapter adapter = new LegendAdapter((CommonActivity)getActivity(), (Pair<Category, CashAmmount>[]) data.toArray(tmp));

            CashAmmount sumAmmount = new CashAmmount(0, Currency.getDefault());
            for(Pair<Category, CashAmmount> pair : data)
            {
                sumAmmount.addCash(pair.second);
            }

            sum.setText(Html.fromHtml("<b>"+getString(R.string.sum)+"</b> "+sumAmmount.toString()));

            legend.setAdapter(adapter);
            legend.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Category cat = (Category) view.getTag();
                ((CommonActivity)getActivity()).openCategory(cat,view);
            }
        });
        }
        catch (Exception e )
        {
            Log.e("ExpenseFragment", "drawChartLegend", e);
        }
    }
}
