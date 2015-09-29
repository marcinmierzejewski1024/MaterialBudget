package pl.marcinmierzejewski.materialbudget.common;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import pl.marcinmierzejewski.materialbudget.*;
import pl.marcinmierzejewski.materialbudget.model.Category;
import pl.marcinmierzejewski.materialbudget.model.MainData;

import java.io.Serializable;

/**
 * Created by dom on 16/11/14.
 */
public abstract class CommonActivity extends AppCompatActivity
{


    private ActionBarDrawerToggle drawerToggle;

    public abstract Fragment getFragment();


    public void openCategory(Category category,View header)
    {
        Intent i = new Intent(this,DetailActivity.class);
        i.putExtra(DetailActivity.FRAGMENT_KEY,DetailActivity.CATEGORY_FRAGMENT);
        i.putExtra(CategoryFragment.CATEGORY_ID_KEY, category.getCategoryId());

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, header, "header");
        startActivity(i, options.toBundle());
    }
    public void openExpense(long expenseId)
    {
        Intent i = new Intent(this,DetailActivity.class);
        i.putExtra(DetailActivity.FRAGMENT_KEY,DetailActivity.EXPENSE_DETAILS_FRAGMENT);
        i.putExtra(ExpenseDetailsFragment.EXPENSE_ID_KEY, expenseId);
        startActivity(i);
    }
    public void openAddIncome()
    {

        Intent i = new Intent(this,DetailActivity.class);
        i.putExtra(AddFragment.TYPE_OF_ADD_KEY, AddFragment.INCOME);
        i.putExtra(DetailActivity.FRAGMENT_KEY,DetailActivity.ADD_INCOME_FRAGMENT);
        startActivity(i);
    }

    public void openAddExpense()
    {

        Intent i = new Intent(this,DetailActivity.class);

        i.putExtra(AddFragment.TYPE_OF_ADD_KEY, AddFragment.EXPENSE);
        i.putExtra(DetailActivity.FRAGMENT_KEY,DetailActivity.ADD_EXPENSE_FRAGMENT);
        startActivity(i);
    }

    public void openAddCategory()
    {

        Intent i = new Intent(this,DetailActivity.class);

        i.putExtra(DetailActivity.FRAGMENT_KEY,DetailActivity.ADD_CATEGORY_FRAGMENT);
        startActivity(i);
    }


    public void deleteCategory(final long categoryId)
    {
        CommonQuestionDialog.show(this, getString(R.string.delete_category_question), new CommonQuestionDialogFragment.EventsListener() {

            @Override
            public void onQuestionDialogClick(CommonQuestionDialogFragment.Answer answer, Serializable data) {
                if (answer == CommonQuestionDialogFragment.Answer.YES) {
                    MainData dao = MainData.getInstance();
                    boolean result = dao.getCategoryData().deleteCategory(categoryId);
                    if (result) {
                        Toast.makeText(CommonActivity.this, R.string.delete_category_success, Toast.LENGTH_SHORT).show();
                        sendBroadcast(new Intent(MainApp.CHANGE_DATA_BROADCAST));

                    } else {
                        Toast.makeText(CommonActivity.this, R.string.delete_category_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public Toolbar getToolbar()
    {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toolbar toolbar = getToolbar();
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if(bar != null)
            bar.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(getFragment()!= null)
            getFragment().onCreateOptionsMenu(menu,getMenuInflater());
        return true;
    }
}
