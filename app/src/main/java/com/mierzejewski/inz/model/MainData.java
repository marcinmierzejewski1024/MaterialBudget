package com.mierzejewski.inz.model;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;
import com.mierzejewski.inz.MainApp;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mierzejewski.inz.R;

import java.sql.SQLException;
import java.util.*;


public class MainData
	extends OrmLiteSqliteOpenHelper
{
	private final static String DATABASE_NAME = "main_data.sqlite";
	private final static int DATABASE_VERSION = 12;

    private final ExpenseIncomeData expenseIncomeData;
    private final CategoryData categoryData;
    private final CashAmmountData cashAmmountData;
    private final CurrencyExchangeRateData exchangeRateData;
    public final DataBaseExporter dataBaseExporter;
    static private MainData instance;

    private MainData()
	{
		super(MainApp.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);

        this.expenseIncomeData = new ExpenseIncomeData(this);
        this.categoryData = new CategoryData(this);
        this.cashAmmountData = new CashAmmountData(this);
        this.exchangeRateData = new CurrencyExchangeRateData(this);


        this.dataBaseExporter = new DataBaseExporter(MainApp.getAppContext(), DATABASE_NAME);

    }
    public static MainData getInstance()
    {
        if(instance == null)
            instance = new MainData();

        return instance;
    }

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
	{
		try
		{
			TableUtils.createTable(connectionSource, ExpenseIncome.class);
			TableUtils.createTable(connectionSource, CashAmmount.class);
			TableUtils.createTable(connectionSource, Category.class);
            TableUtils.createTable(connectionSource, CurrencyExchangeRate.class);

            addPredefinedCategories();

        }
		catch (SQLException ex)
		{
		    throw new RuntimeException(ex);
		}
	}



    private void addPredefinedCategories() throws SQLException
    {
        Category cat0 = new Category(MainApp.getAppContext().getString(R.string.house),"#12aaee",null,false);
        Category cat1 = new Category(MainApp.getAppContext().getString(R.string.transport),"#aacc44",null,false);
        Category cat2 = new Category(MainApp.getAppContext().getString(R.string.food),"#a14897",null,false);
        Category cat3 = new Category(MainApp.getAppContext().getString(R.string.entierement),"#13a412",null,false);
        Category cat4 = new Category(MainApp.getAppContext().getString(R.string.education),"#658732",null,false);
        Category cat5 = new Category(MainApp.getAppContext().getString(R.string.bills),"#98563f",null,false);
        Category cat6 = new Category(MainApp.getAppContext().getString(R.string.loans),"#dd5544",null,false);


        Category cat7 = new Category(MainApp.getAppContext().getString(R.string.job),"#4da4dd",null,true);
        Category cat8 = new Category(MainApp.getAppContext().getString(R.string.loan),"#eeaa33",null,true);
        Category cat9 = new Category(MainApp.getAppContext().getString(R.string.present),"#438723",null,true);
        Category cat10 = new Category(MainApp.getAppContext().getString(R.string.investitions),"#997723",null,true);
        Category cat11 = new Category(MainApp.getAppContext().getString(R.string.other),"#f12ea1",null,true);


        getCategoryData().storeCategory(cat0);
        getCategoryData().storeCategory(cat1);
        getCategoryData().storeCategory(cat2);
        getCategoryData().storeCategory(cat3);
        getCategoryData().storeCategory(cat4);
        getCategoryData().storeCategory(cat5);
        getCategoryData().storeCategory(cat6);
        getCategoryData().storeCategory(cat7);
        getCategoryData().storeCategory(cat8);
        getCategoryData().storeCategory(cat9);
        getCategoryData().storeCategory(cat10);
        getCategoryData().storeCategory(cat11);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2)
    {
        try
        {
            TableUtils.dropTable(connectionSource, ExpenseIncome.class,true);
            TableUtils.dropTable(connectionSource, CashAmmount.class, true);
            TableUtils.dropTable(connectionSource, Category.class, true);
            TableUtils.dropTable(connectionSource, CurrencyExchangeRate.class, true);
        }
        catch (SQLException ex)
        {
            throw new RuntimeException(ex);
        }

        onCreate(sqLiteDatabase, connectionSource);
    }


    public ExpenseIncomeData getExpenseIncomeData()
    {
        return expenseIncomeData;
    }

    public CategoryData getCategoryData()
    {
        return categoryData;
    }

    public CashAmmountData getCashAmmountData()
    {
        return cashAmmountData;
    }

    public CurrencyExchangeRateData getExchangeRateData()
    {
        return exchangeRateData;
    }

    private List<Pair<Category,CashAmmount>> getCategoryGroupedData(Date startDate, int numberOfCategories, boolean addEmptyCategories, ExpenseIncomeType type)
    {
        try
        {

            List<Pair<Category,CashAmmount>> result = new ArrayList<Pair<Category, CashAmmount>>();

            List<Category> categories = null;
            Category restCategory = null;
            if(type == ExpenseIncomeType.EXPENSE)
            {
                restCategory = Category.getRestCategoryExpense();
                categories = getCategoryData().getExpenseCategories();
            }
            else
            {

                restCategory = Category.getRestCategoryIncome();
                categories = getCategoryData().getIncomeCategories();
            }
            for(Category singleCategory : categories)
            {
                result.add(new Pair<Category, CashAmmount>(singleCategory,new CashAmmount(0,Currency.getDefault())));
            }

            int allSum = 0;
            for(ExpenseIncome single : getExpenseIncomeData().getAll(type))
            {

                if(single.getDate().after(startDate))
                {

                    if(single.getCash()!=null)
                    {
                        allSum += single.getCash().getPennies();
                    }

                    for(Pair<Category,CashAmmount> singlePair : result)
                    {
                        if(singlePair.first.equals(single.getCategory()))
                            {
                                singlePair.second.addCash(single.getCash());

                            }
                    }
                    //Currency.getDefault().
                }
            }


            List<Pair<Category,CashAmmount>> bigests = new ArrayList<Pair<Category,CashAmmount>>(numberOfCategories);
            Pair<Category,CashAmmount> smallest = null;

            for(Pair<Category,CashAmmount> singlePair : result)
            {
                if(bigests.size()>=numberOfCategories)
                {
                    //wyznaczenie obecnie najmniejszej
                    for (Pair<Category, CashAmmount> oneOfCurrentBigests : bigests)
                    {
                        if(smallest == null)
                            smallest = oneOfCurrentBigests;

                        if(smallest.second.getPennies()< oneOfCurrentBigests.second.getPennies())
                        {
                            smallest = oneOfCurrentBigests;
                        }
                    }

                    if(singlePair.second.getPennies() < smallest.second.getPennies())
                    {
                        bigests.remove(smallest);
                        bigests.add(singlePair);
                    }

                }
                else
                {
                    if(singlePair.second.getPennies() != 0 || addEmptyCategories)
                        bigests.add(singlePair);
                }

            }

            Collections.sort(bigests, new Comparator<Pair<Category, CashAmmount>>()
            {
                @Override
                public int compare(Pair<Category, CashAmmount> lhs, Pair<Category, CashAmmount> rhs)
                {
                   return rhs.second.getPennies()-lhs.second.getPennies();
                }
            });

            int biggestSum = 0;
            for(Pair<Category,CashAmmount> singlePair :bigests)
            {
                biggestSum += singlePair.second.getPennies();
            }

            if(allSum-biggestSum != 0)
            {
                Pair<Category, CashAmmount> restPair = new Pair<Category, CashAmmount>(restCategory, new CashAmmount(allSum - biggestSum, Currency.getDefault()));
                bigests.add(restPair);
            }



            return bigests;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }



    public MainData get()
    {
        return this;
    }

    public List<Pair<Category, CashAmmount>> getCategoryGroupedExpenses(Date date, int i, boolean b)
    {
        ExpenseIncomeType type = ExpenseIncomeType.EXPENSE;
        return getCategoryGroupedData(date,i,b,type);
    }

    public List<Pair<Category, CashAmmount>> getCategoryGroupedIncomes(Date date, int i, boolean b)
    {
        ExpenseIncomeType type = ExpenseIncomeType.INCOME;
        return getCategoryGroupedData(date,i,b,type);
    }
}
