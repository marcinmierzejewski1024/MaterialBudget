package pl.marcinmierzejewski.materialbudget.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by dom on 09/11/14.
 */
public class CashAmmountData extends BaseData

{
    public CashAmmountData(MainData mainData)
    {
        super(mainData);
    }


    private Dao<CashAmmount, Long> getDao()
            throws SQLException
    {
        return mainData.get().getDao(CashAmmount.class);
    }


    public CashAmmount storeCashAmmount(final CashAmmount cashAmmount) throws SQLException
    {
        return TransactionManager.callInTransaction(mainData.get().getConnectionSource(), new Callable<CashAmmount>()
        {
            @Override
            public CashAmmount call() throws Exception
            {
                getDao().createOrUpdate(cashAmmount);
                cashAmmount.setStoredInDb(true);
                return cashAmmount;
            }
        });
    }


    public CashAmmount updateCashAmmount(final CashAmmount cashAmmount) throws SQLException
    {
        return TransactionManager.callInTransaction(mainData.get().getConnectionSource(), new Callable<CashAmmount>()
        {
            @Override
            public CashAmmount call() throws Exception
            {
                getDao().update(cashAmmount);
                cashAmmount.setStoredInDb(true);
                return cashAmmount;
            }
        });
    }
    public CashAmmount getCashAmmountById(long id) throws SQLException
    {
        final QueryBuilder<CashAmmount, Long> qb = getDao().queryBuilder();

        qb.where().eq("cashAmmountId", id);

        CashAmmount result = qb.queryForFirst();

        if(result!=null)
            result.setStoredInDb(true);

        return result;
    }
}
