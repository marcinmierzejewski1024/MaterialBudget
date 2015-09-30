package com.mierzejewski.inz.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mierzejewski.inz.model.*;
import com.mierzejewski.inz.model.Currency;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dom on 29/10/14.
 */
public class CurrencyExchangeDownloadService extends IntentService
{
    private static final String XML_LAST_90_DAYS_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";

    private static final String RATE_ATRIBUTE = "rate";
    private static final String CURRENCY_ATRIBUTE = "currency";
    private static final String TIME_ATRIBUTE = "time";

    private final CurrencyExchangeRateData dao;
    private static final int UPDATE_FREQUENCY_IN_DAYS = 1;


    public CurrencyExchangeDownloadService()
    {
        super("CurrencyExchangeDownloadService");
        dao = MainData.getInstance().getExchangeRateData();
    }

    public CurrencyExchangeDownloadService(String name)
    {
        super(name);
        dao = MainData.getInstance().getExchangeRateData();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {

        BufferedInputStream stream;

        if(!shouldStart())
            return;

        stream = getLast90DaysCurrencyXmlStream();



        try
        {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();

            myParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            myParser.setInput(stream, "utf-8");

            ArrayList<CurrencyExchangeRate> list = parseXML(myParser, Currency.EUR);
            Log.wtf("tag","list:"+list);


            dao.storeRates(list);

        }
        catch (Exception e)
        {
            Log.e("CurrencyExchangeService","e",e);
        }

    }




    private ArrayList<CurrencyExchangeRate> parseXML(XmlPullParser parser,Currency to) throws XmlPullParserException, IOException, ParseException
    {
        ArrayList<CurrencyExchangeRate> CurrencyExchangeRates = null;
        int eventType = parser.getEventType();

        CurrencyExchangeRate currentCurrencyExchangeRate = null;

        Date exchangeDate = null;

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    CurrencyExchangeRates = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();


                    if (name.equalsIgnoreCase("CUBE"))
                    {
                        //get all atributes
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String attributeName = parser.getAttributeName(i);
                            String attributeValue = parser.getAttributeValue(i);
                            if(TIME_ATRIBUTE.equals(attributeName))
                            {
                                //zmiana daty 2015-04-24
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                exchangeDate = format.parse(attributeValue);

                                try
                                {
                                    if(dao.haveDateFromDay(exchangeDate))
                                    {

                                        Log.w("CurrencyExchangeDownloadService","exchange date is same or older than last update date");
                                        break;
                                    }
                                } catch (SQLException e)
                                {
                                    Log.w("CurrencyExchangeDownloadService","cant get if data was downloaded before",e);
                                }
                            }
                            else if(CURRENCY_ATRIBUTE.equals(attributeName))
                            {
                                //stworzenie nowego
                                Currency from = Currency.getFromAbbr(attributeValue);

                                currentCurrencyExchangeRate = new CurrencyExchangeRate();
                                currentCurrencyExchangeRate.setExchangeDate(exchangeDate);
                                currentCurrencyExchangeRate.setFrom(from);
                                currentCurrencyExchangeRate.setTo(to);
                            }
                            else if(RATE_ATRIBUTE.equals(attributeName))
                            {

                                currentCurrencyExchangeRate.setRate(1.0/Double.parseDouble(attributeValue));
                                CurrencyExchangeRates.add(currentCurrencyExchangeRate);
                            }

                        }

                    }
                    break;
            }
            eventType = parser.next();
        }

        return CurrencyExchangeRates;
    }

    private BufferedInputStream getLast90DaysCurrencyXmlStream()
    {
        try
        {
            URL xmlUrl = new URL(XML_LAST_90_DAYS_URL);
            HttpURLConnection http = openGetConnection(xmlUrl);

            int responseCode = http.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                BufferedInputStream inputStream = new BufferedInputStream(http.getInputStream());
                return inputStream;
            }
            else
            {
                throw new Exception("response code is not ok:"+responseCode);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }




    private static HttpURLConnection openGetConnection(URL url) throws IOException
    {
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setConnectTimeout(15000);
        return http;
    }

    private boolean shouldStart()
    {
        try
        {
            Date now = new Date();
            Date lastUpdate = dao.getLastUpdateDate();

            if (now.getTime() < lastUpdate.getTime() + (UPDATE_FREQUENCY_IN_DAYS * 24 * 60 * 60 * 1000))
                return false;

            return true;
        }
        catch (Exception e)
        {
            Log.d("shouldStart","exception",e);
            return true;
        }
    }



    static String convertStreamToString(java.io.InputStream is)
    {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
    }


    public static void startService(Context context)
    {
        Intent serviceIntent = new Intent(context,CurrencyExchangeDownloadService.class);
        context.startService(serviceIntent);
    }
}

