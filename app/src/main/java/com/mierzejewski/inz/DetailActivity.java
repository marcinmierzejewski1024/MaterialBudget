package com.mierzejewski.inz;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mierzejewski.inz.common.CommonActivity;
import com.mierzejewski.inz.model.AddCategoryFragment;

/**
 * Created by dom on 05/11/14.
 */
public class DetailActivity extends CommonActivity implements DiffrentAmmountDialog.EventListener
{
    public static final String CATEGORY_FRAGMENT = "categoryFragment";
    public static final String EXPENSE_DETAILS_FRAGMENT = "expenseDetailsFragment";
    public static final String ADD_INCOME_FRAGMENT = "addincomeFragment";
    public static final String ADD_EXPENSE_FRAGMENT = "addExpenseFragment";
    public static final String ADD_CATEGORY_FRAGMENT = "addCategoryFragment";




    public static final String TAG = "DetailActivity";
    public static final String FRAGMENT_KEY = "fragmentKey";
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.without_drawer);

        Toolbar actionbar = getToolbar();


        setSupportActionBar(actionbar);

        actionbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);


        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        try
        {
            String fragmentToShow = getIntent().getStringExtra(FRAGMENT_KEY);
            if(fragmentToShow.equals(CATEGORY_FRAGMENT))
            {
                setFragment(new CategoryFragment());
            }
            else if(fragmentToShow.equals(ADD_EXPENSE_FRAGMENT))
            {
                setFragment(new AddFragment());
            }
            else if(fragmentToShow.equals(ADD_INCOME_FRAGMENT))
            {
                setFragment(new AddFragment());
            }
            else if(fragmentToShow.equals(EXPENSE_DETAILS_FRAGMENT))
            {
                setFragment(new ExpenseDetailsFragment());
            }

            else if(fragmentToShow.equals(ADD_CATEGORY_FRAGMENT))
            {
                setFragment(new AddCategoryFragment());
            }
            else
            {
                throw new Exception("nie znany typ fragmentu do otworzenia w szczegolwej aktywnosci");
            }
        } catch (Exception e)
        {

            Log.e(TAG, "cos sie popsulo", e);
        }
    }


    public void setFragment(Fragment fragment)
    {
        currentFragment = fragment;
        if (fragment != null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        }
    }


    @Override
    public void onChangeAmmount(double newVal)
    {
        if(currentFragment instanceof DiffrentAmmountDialog.EventListener)
            ((DiffrentAmmountDialog.EventListener) currentFragment).onChangeAmmount(newVal);
    }

    @Override
    public Fragment getFragment()
    {
        return currentFragment;
    }
}
