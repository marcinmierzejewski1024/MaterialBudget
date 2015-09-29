package pl.marcinmierzejewski.materialbudget;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import pl.marcinmierzejewski.materialbudget.common.CommonActivity;

public class MainActivity extends CommonActivity
{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private Fragment fragment;
    private boolean isBackPressed = false;
    private View.OnClickListener addingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.addIncome)
            {
                openAddIncome();
            }
            else if(v.getId() == R.id.addExpense)
            {
                openAddExpense();

            }

            else if(v.getId() == R.id.addCategory)
            {
                openAddCategory();

            }

        }
    };
    private ActionBarDrawerToggle drawerToggle;
    private FloatingActionsMenu menuMultipleActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));

        navMenuIcons.recycle();
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        //getToolbar().setDisplayHomeAsUpEnabled(true);
        //getToolbar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                getToolbar(),
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                getToolbar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getToolbar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }

        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.right_labels);

        final FloatingActionButton addCategory = (FloatingActionButton) findViewById(R.id.addCategory);
        final FloatingActionButton addExpense = (FloatingActionButton) findViewById(R.id.addExpense);
        final FloatingActionButton addIncome = (FloatingActionButton) findViewById(R.id.addIncome);


        addCategory.setOnClickListener(addingClickListener);
        addExpense.setOnClickListener(addingClickListener);
        addIncome.setOnClickListener(addingClickListener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isBackPressed = false;

        Toolbar toolbar = getToolbar();
        setSupportActionBar(toolbar);

        drawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);


      }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }


    private void displayView(int position)
    {
        fragment = null;
        Bundle arguments = new Bundle();
        switch (position) {
            case 0:
                fragment = new OverviewFragment();
                break;
            case 1:
                fragment = new ExpensesFragment();
                arguments.putSerializable(ExpensesFragment.TYPE_KEY, ExpensesFragment.ExpenseFragmentType.EXPENSES);
                fragment.setArguments(arguments);
                break;
            case 2:
                fragment = new ExpensesFragment();
                arguments.putSerializable(ExpensesFragment.TYPE_KEY, ExpensesFragment.ExpenseFragmentType.INCOMES);
                fragment.setArguments(arguments);
                break;
            case 3:
                fragment = new StatisticsFragment();
                break;
            case 4:
                fragment = new CurrencyExchangeFragment();
                break;
            case 5:
                fragment = new SettingsFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);

            getToolbar().setLogo(navDrawerItems.get(position).getIcon());

            mDrawerLayout.closeDrawer(mDrawerList);
        } else {

            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getToolbar().setTitle(mTitle);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed()
    {
        if(menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapse();
            return;
        }

        //handled by fragment
        if(fragment instanceof StatisticsFragment && ((StatisticsFragment) fragment).goBack() == true)
            return;

        if(mDrawerList.getVisibility() == View.VISIBLE)
        {
            mDrawerLayout.closeDrawers();
            return;
        }


        if(isBackPressed)
        {
            finish();
        }
        else
        {
            isBackPressed = true;
            Toast.makeText(this,R.string.double_back_to_finish,Toast.LENGTH_SHORT).show();
            return;
        }


        super.onBackPressed();
    }



    @Override
    public Fragment getFragment()
    {
        return fragment;
    }
}