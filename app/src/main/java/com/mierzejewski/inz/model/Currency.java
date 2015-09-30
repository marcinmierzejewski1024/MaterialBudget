package com.mierzejewski.inz.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mierzejewski.inz.MainApp;
import com.mierzejewski.inz.R;

import java.io.Serializable;

/**
 * Created by dom on 29/10/14.
 */
public enum Currency implements Serializable
{
    PLN("zł", true,"Polish zloty",100),
    USD("$", false,"United States dollar",100),
    GBP("£", false,"Pound sterling",100),
    EUR("€", true,"Euro",100),
    JPY("¥", true, "Japanese yen",100),
    BGN("bgn", true,"Bulgarian lev",100),
    CZK("Kč", true,"Czech koruna",100),
    DKK("k",true,"Danish krone",100),
    HUF("ft",true,"Hungarian forint",100),
    RON("l",true,"Romanian leu",100),
    SEK("kr",true,"Swedish krona",100),
    CHF("fr",true,"Swiss franc",100),
    NOK("kr",true,"Norwegian krone",100),
    HRK("kn",true,"Croatian kuna",100),
    RUB("₽",true,"Russian ruble",100),
    TRY("₺",true,"Turkish lira",100),
    AUD("$",true,"Australian dollar",100),
    BRL("R$",true,"Brazilian real",100),
    CAD("$",true,"Canadian dolar",100),
    CNY("¥",true,"Renminbi",100),
    HKD("HK$",true,"Hong Kong dollar",100),
    IDR("Rp",true,"Indonesian Rupiah",100),
    ILS("₪",true,"Israeli new shekel",100),
    INR("Rp",true,"Indian Rupee",100),
    KRW("₩",true,"South Korean won",100),
    MXN("Mex$",true,"Mexican peso",100),
    MYR("RM",true,"Malaysian ringgit",100),
    PHP("₱",true,"Philippine peso",100),
    SGD("S$",true,"Singapore dollar",100),
    NZD("$",true,"New Zealand dollar",100),
    THB("฿",true,"Thai baht",100),
    ZAR("R",true,"South African rand",100);


    public static final String CURRENCY_KEY = MainApp.getAppContext().getString(R.string.currency_key);


    public final String symbol;
    public final String name;
    public final int penniesSum;
    public final boolean afterAmount;

    Currency(String symbol, boolean afterAmount,String name,int pennies)
    {
        this.symbol = symbol;
        this.afterAmount = afterAmount;
        this.name = name;
        this.penniesSum = pennies;
    }



    public static Currency getDefault()
    {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainApp.getAppContext());
        String abbr = preferences.getString(Currency.CURRENCY_KEY,MainApp.getAppContext().getString(R.string.default_currency));
        return getFromAbbr(abbr);
    }

    public static void setDefault(Currency newDefault)
    {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainApp.getAppContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(Currency.CURRENCY_KEY,newDefault.name());
        edit.commit();
    }

    public static Currency getFromAbbr(String abbr)
    {
        try
        {
            for(Currency item : Currency.values())
            {
                if(item.name().equals(abbr))
                    return item;

            }

            throw new Exception("default currency not found");
        }
        catch (Exception e)
        {
            return EUR;
        }
    }


    @Override
    public String toString()
    {
        return name;
    }
}
