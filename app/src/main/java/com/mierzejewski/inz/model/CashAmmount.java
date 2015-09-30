package com.mierzejewski.inz.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by dom on 29/10/14.
 */
public class CashAmmount extends AbsDatabaseItem
{
    @DatabaseField(generatedId = true)
    long cashAmmountId;
    @DatabaseField()
    int pennies;
    @DatabaseField(dataType= DataType.SERIALIZABLE)
    Currency currency;

    public CashAmmount(int pennies, Currency currency)
    {
        this.pennies = pennies;
        this.currency = currency;
    }

    public CashAmmount()
    {
        currency = Currency.getDefault();

    }

    @Override
    public String toString()
    {
//        if(Currency.getDefault() == currency)
//        {
            String ammountInCurrency = formatDecimal(pennies / (1.0f*currency.penniesSum));

            if (currency.afterAmount)
                return ammountInCurrency  + currency.symbol;
            else
                return currency.symbol + ammountInCurrency;
//        }
    }


    private String formatDecimal(float number)
    {
        float epsilon = 0.004f; // 4 tenths of a cent
        if (Math.abs(Math.round(number) - number) < epsilon) {
            return String.format("%10.0f", number); // sdb
        } else {
            return String.format("%10.2f", number); // dj_segfault
        }
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

    public Integer getPennies()
    {
        return getPennies(Currency.getDefault());
    }

    public float getPounds()
    {
        return Math.round(getPennies(Currency.getDefault())/currency.penniesSum*100.0)/100.0f;
    }

    public Integer getPennies(Currency currencyTo)
    {
        CurrencyExchangeRateData dao = MainData.getInstance().getExchangeRateData();

        try
        {
            return (int)(pennies * dao.getLastRating(currency,currencyTo));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }


    public void setPennies(Integer pennies)
    {
        setChanged();
        this.pennies = pennies;
    }

    public void addCash(CashAmmount cashAmmount)
    {
        setChanged();
        if(cashAmmount!= null)
        {
            this.pennies += cashAmmount.getPennies(currency);
        }
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public void setCurrency(Currency currency)
    {
        setChanged();
        this.currency = currency;
    }

    public boolean isZero()
    {
        return pennies==0;
    }

}
