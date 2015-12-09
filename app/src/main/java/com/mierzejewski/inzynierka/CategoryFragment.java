package com.mierzejewski.inzynierka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.mierzejewski.inzynierka.common.CommonActivity;
import com.mierzejewski.inzynierka.common.CommonFragment;
import com.mierzejewski.inzynierka.model.Category;
import com.mierzejewski.inzynierka.model.ExpenseIncome;
import com.mierzejewski.inzynierka.model.ExpenseIncomeType;
import com.mierzejewski.inzynierka.model.MainData;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by dom on 11/11/14.
 */
public class CategoryFragment extends CommonFragment
{
    public static final String CATEGORY_ID_KEY = "categoryIdKey";


    View rootView;
    TextView categoryName;
    ListView categoryExpenses;
    View emptyView;
    ImageView categoryColor;
    Category data;
    List<ExpenseIncome> expenses;


    BroadcastReceiver refreshListBroadcast = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            Long categoryId = getActivity().getIntent().getExtras().getLong(CATEGORY_ID_KEY);
            loadData(categoryId);
            updateViews();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.category_fragment, container, false);
        rootView.findViewById(R.id.categoryDelete).setVisibility(View.GONE);
        categoryName = (TextView) rootView.findViewById(R.id.categoryName);

        categoryColor = (ImageView) rootView.findViewById(R.id.color);
        categoryExpenses = (ListView) rootView.findViewById(R.id.categoryExpenses);
        emptyView = rootView.findViewById(R.id.empty_list_view);
        categoryExpenses.setEmptyView(emptyView);

        Long categoryId = getActivity().getIntent().getExtras().getLong(CATEGORY_ID_KEY);
        loadData(categoryId);
        updateViews();

        return rootView;
    }

    private void updateViews()
    {


        categoryName.setText(data.getName());

        categoryColor.setImageDrawable(new ColorDrawable(Color.parseColor(data.getHexColor())));
        ExpenseIncome[] arr = {};
        categoryExpenses.setAdapter(new ExpenseAdapter(getActivity(), expenses.toArray(arr)));
        categoryExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Long expenseId = (Long) view.getTag();
                ((CommonActivity)getActivity()).openExpense(expenseId);
            }
        });
    }

    public void loadData(Long categoryId)
    {
        MainData dao = MainData.getInstance();
        try
        {
            if(categoryId == Category.EXPENSE_REST_CATEGORY_ID)
            {
                data = Category.getRestCategoryExpense();
                expenses = dao.getExpenseIncomeData().getAllFromRestCategory(ExpenseIncomeType.EXPENSE,null);
            }
            else if(categoryId == Category.INCOME_REST_CATEGORY_ID)
            {
                data = Category.getRestCategoryExpense();
                expenses = dao.getExpenseIncomeData().getAllFromRestCategory(ExpenseIncomeType.INCOME,null);
            }
            else
            {
                data = dao.getCategoryData().getCategoryById(categoryId);
                expenses = dao.getExpenseIncomeData().getByCategoryId(categoryId, new Date(0l),null, null);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(refreshListBroadcast,MainApp.CHANGE_DATA_FILTER);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        getActivity().unregisterReceiver(refreshListBroadcast);
    }

    @Override
    public String getTitle() {

        return getResources().getString(R.string.category);
    }

    @Override
    public Drawable getLogo() {

        return getResources().getDrawable(R.drawable.ic_home);
    }
}
