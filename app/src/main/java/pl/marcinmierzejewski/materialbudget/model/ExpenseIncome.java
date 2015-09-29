package pl.marcinmierzejewski.materialbudget.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by dom on 29/10/14.
 */
public class ExpenseIncome extends AbsDatabaseItem
{
    @DatabaseField(generatedId = true)
    long expenseId;
    @DatabaseField()
    String name;
    @DatabaseField()
    String description;
    @DatabaseField()
    Date date;

    @DatabaseField()
    long cashAmmountId;
    @DatabaseField()
    long categoryId;


    @DatabaseField(dataType = DataType.ENUM_STRING)
    ExpenseIncomeType type;

    CashAmmount cash;
    Category category;

    public long getExpenseId()
    {
        return expenseId;
    }

    public void setExpenseId(long expenseId)
    {
        this.expenseId = expenseId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        setChanged();
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        setChanged();
        this.description = description;
    }

    public long getCashAmmountId()
    {
        return cashAmmountId;
    }

    public void setCashAmmountId(long cashAmmountId)
    {
        setChanged();
        this.cashAmmountId = cashAmmountId;
    }

    public long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(long categoryId)
    {
        this.categoryId = categoryId;
    }

    public CashAmmount getCash()
    {
        return cash;
    }

    public void setCash(CashAmmount cash)
    {
        setChanged();
        this.cash = cash;
    }

    public Category getCategory()
    {
        return category;
    }

    public void setCategory(Category category)
    {
        setChanged();
        this.category = category;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        setChanged();
        this.date = date;
    }

    public ExpenseIncomeType getType()
    {
        return type;
    }

    public void setType(ExpenseIncomeType type)
    {
        this.type = type;
    }



    public ExpenseIncome(String name, String description, Date date, CashAmmount cash, Category category,ExpenseIncomeType type)
    {
        this.type = type;
        this.name = name;
        this.description = description;
        this.date = date;
        this.cash = cash;
        this.category = category;
    }

    public ExpenseIncome()
    {
    }

}
