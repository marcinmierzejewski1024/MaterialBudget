package com.mierzejewski.inzynierka.model;

import android.util.Log;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by dom on 09/11/14.
 */
public class ExpenseIncomeData extends BaseData

{
    public ExpenseIncomeData(MainData mainData)
    {
        super(mainData);
    }


    private Dao<ExpenseIncome, Long> getDao()
            throws SQLException
    {
        return mainData.get().getDao(ExpenseIncome.class);
    }


    public ExpenseIncome store(final ExpenseIncome expenseIncome) throws SQLException
    {
        return TransactionManager.callInTransaction(mainData.get().getConnectionSource(), new Callable<ExpenseIncome>()
        {
            @Override
            public ExpenseIncome call() throws Exception
            {

                expenseIncome.setCashAmmountId(getMainData().get().getCashAmmountData().storeCashAmmount(expenseIncome.getCash()).getCashAmmountId());
                expenseIncome.setCategoryId(getMainData().get().getCategoryData().storeCategory(expenseIncome.getCategory()).getCategoryId());

                getDao().createOrUpdate(expenseIncome);
                expenseIncome.setStoredInDb(true);
                return expenseIncome;
            }
        });
    }

    public ExpenseIncome update(final ExpenseIncome expenseIncome) throws SQLException
    {
        return TransactionManager.callInTransaction(mainData.get().getConnectionSource(), new Callable<ExpenseIncome>()
        {
            @Override
            public ExpenseIncome call() throws Exception
            {

                expenseIncome.setCashAmmountId(getMainData().get().getCashAmmountData().updateCashAmmount(expenseIncome.getCash()).getCashAmmountId());
                expenseIncome.setCategoryId(getMainData().get().getCategoryData().storeCategory(expenseIncome.getCategory()).getCategoryId());
                getDao().update(expenseIncome);
                expenseIncome.setStoredInDb(true);
                return expenseIncome;
            }
        });
    }

    public ExpenseIncome getById(long id) throws SQLException
    {
        final QueryBuilder<ExpenseIncome, Long> qb = getDao().queryBuilder();

        qb.where().eq("expenseId", id);

        ExpenseIncome result = qb.queryForFirst();
        result.setCategory(getMainData().getCategoryData().getCategoryById(result.getCategoryId()));
        result.setCash(getMainData().getCashAmmountData().getCashAmmountById(result.getCashAmmountId()));
        result.setStoredInDb(true);
        return result;
    }

    public List<ExpenseIncome> getAll(ExpenseIncomeType type) throws SQLException
    {

        final QueryBuilder<ExpenseIncome, Long> qb = getDao().queryBuilder();

        qb.where().eq("type",type);
        List<ExpenseIncome> results =  qb.query();

        for(ExpenseIncome result : results)
        {
            result.setCategory(getMainData().getCategoryData().getCategoryById(result.getCategoryId()));
            result.setCash(getMainData().getCashAmmountData().getCashAmmountById(result.getCashAmmountId()));
            result.setStoredInDb(true);
        }
        return results;
    }

    public List<ExpenseIncome> getAllFromRestCategory(ExpenseIncomeType type, Date since) throws SQLException
    {

        final QueryBuilder<ExpenseIncome, Long> qb = getDao().queryBuilder();

        if(since == null)
            since = new Date(0);

        Where<ExpenseIncome, Long> query = qb.where().ge("date", since);

        if(type != null)
            query.and().eq("type",type);


        List<ExpenseIncome> results =  query.query();

        for (Object next : results.toArray())
        {
            ExpenseIncome item = (ExpenseIncome) next;
            Category category = getMainData().getCategoryData().getCategoryById(item.getCategoryId());
            if(category != null)
            {
                results.remove(next);
                continue;
            }
            item.setCash(getMainData().getCashAmmountData().getCashAmmountById(item.getCashAmmountId()));
            item.setStoredInDb(true);
        }
        return results;
    }

    public List<ExpenseIncome> getByCategoryId(Long categoryId, Date since,Date to, ExpenseIncomeType type) throws SQLException
    {
        final QueryBuilder<ExpenseIncome, Long> qb = getDao().queryBuilder();

        if(since == null)
            since = new Date(0);


        Where<ExpenseIncome, Long> query = qb.where().ge("date", since);

        if(to != null)
            query.and().le("date",to);

        if(type != null)
            query.and().eq("type",type);

        if(categoryId != null)
            query.and().eq("categoryId",categoryId);

        List<ExpenseIncome> results =  query.query();

        for(ExpenseIncome result : results)
        {
            result.setCategory(getMainData().getCategoryData().getCategoryById(result.getCategoryId()));
            result.setCash(getMainData().getCashAmmountData().getCashAmmountById(result.getCashAmmountId()));
            result.setStoredInDb(true);
        }

        return results;
    }


    public boolean deleteExpenseIncome(long expenseId)
    {
        try
        {
            final DeleteBuilder<ExpenseIncome, Long> qb = getDao().deleteBuilder();
            qb.where().eq("expenseId", expenseId);
            qb.delete();
            return true;
        }
        catch (Exception e)
        {
            Log.e("ExpenseData",e.toString());
            return false;
        }
    }

    public CashAmmount getSum(Date since,Date to,ExpenseIncomeType type)
    {
        CashAmmount sum = new CashAmmount();
        try
        {
            for (ExpenseIncome expenseIncome : getByCategoryId(null, since,to,type))
            {
                sum.addCash(expenseIncome.getCash());
            }
        }
        catch (Exception e)
        {
            Log.e("expenseData","getExpsum",e);
        }

        return sum;
    }
}
