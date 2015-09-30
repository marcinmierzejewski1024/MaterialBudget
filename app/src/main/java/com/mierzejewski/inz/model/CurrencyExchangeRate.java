package com.mierzejewski.inz.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by dom on 24/11/14.
 */
public class CurrencyExchangeRate extends AbsDatabaseItem
{
    @DatabaseField(generatedId = true)
    long exchangeId;
    @DatabaseField()
    Date exchangeDate;
    @DatabaseField()
    Currency from;
    @DatabaseField()
    Currency to;
    @DatabaseField()
    double rate;

    public CurrencyExchangeRate( Date exchangeDate, Currency from, Currency to, double rate)
    {
        super();
        this.exchangeDate = exchangeDate;
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    public CurrencyExchangeRate()
    {
    }

    public long getExchangeId()
    {
        return exchangeId;
    }

    public void setExchangeId(long exchangeId)
    {
        this.exchangeId = exchangeId;
    }

    public Date getExchangeDate()
    {
        return exchangeDate;
    }

    public void setExchangeDate(Date exchangeDate)
    {
        this.exchangeDate = exchangeDate;
    }

    public Currency getFrom()
    {
        return from;
    }

    public void setFrom(Currency from)
    {
        this.from = from;
    }

    public Currency getTo()
    {
        return to;
    }

    public void setTo(Currency to)
    {
        this.to = to;
    }

    public double getRate()
    {
        return rate;
    }

    public void setRate(double rate)
    {
        this.rate = rate;
    }

    @Override
    public String toString()
    {
        return from+" -> "+rate + "" + to+"("+exchangeDate+")";
    }
}
