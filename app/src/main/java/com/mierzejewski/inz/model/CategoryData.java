package com.mierzejewski.inz.model;

import android.util.Log;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by dom on 09/11/14.
 */
public class CategoryData extends BaseData

{
    public CategoryData(MainData mainData)
    {
        super(mainData);
    }


    private Dao<Category, Long> getDao()
            throws SQLException
    {
        return mainData.get().getDao(Category.class);
    }


    public Category storeCategory(final Category category) throws SQLException
    {
        return TransactionManager.callInTransaction(mainData.get().getConnectionSource(), new Callable<Category>()
        {
            @Override
            public Category call() throws Exception
            {
                getDao().createOrUpdate(category);

                category.setStoredInDb(true);
                return category;
            }
        });
    }

    public Category getCategoryById(long id) throws SQLException
    {
        final QueryBuilder<Category, Long> qb = getDao().queryBuilder();

        qb.where().eq("categoryId", id);

        Category result = qb.queryForFirst();
        if(result!=null)
            result.setStoredInDb(true);
        return result;
    }

    public List<Category> getAllCategories() throws SQLException
    {
        final QueryBuilder<Category, Long> qb = getDao().queryBuilder();

        List<Category> results =  qb.query();

        for(Category result : results)
            result.setStoredInDb(true);

        return results;
    }

    public List<Category> getExpenseCategories() throws SQLException
    {
        final QueryBuilder<Category, Long> qb = getDao().queryBuilder();

        List<Category> results =  qb.where().eq("isIncomeCategory",false).query();

        for(Category result : results)
            result.setStoredInDb(true);

        return results;
    }

    public List<Category> getIncomeCategories() throws SQLException
    {
        final QueryBuilder<Category, Long> qb = getDao().queryBuilder();

        List<Category> results =  qb.where().eq("isIncomeCategory",true).query();

        for(Category result : results)
            result.setStoredInDb(true);

        return results;
    }

    public boolean deleteCategory(long categoryId)
    {
        try
        {
            final DeleteBuilder<Category, Long> qb = getDao().deleteBuilder();
            qb.where().eq("categoryId", categoryId);
            qb.delete();
            return true;
        }
        catch (Exception e)
        {
            Log.e("CategoryData",e.toString());
            return false;
        }
    }
}
