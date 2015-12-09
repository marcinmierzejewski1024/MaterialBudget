package com.mierzejewski.inzynierka;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;

import com.mierzejewski.inzynierka.services.CurrencyExchangeDownloadService;

import java.util.*;

/**
 * Created by dom on 15/11/14.
 */
public class MainApp extends Application
{
    private static Context context;
    public final static String CHANGE_DATA_BROADCAST = "CHANGE_DATA_FILTER";
    public final static android.content.IntentFilter CHANGE_DATA_FILTER = new IntentFilter(CHANGE_DATA_BROADCAST);



    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
        CurrencyExchangeDownloadService.startService(context);
    }

    public static Context getAppContext()
    {
        return context;
    }

    public static Calendar getFirstDayOfMonth()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal;
    }

    public boolean isDownloadingServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (CurrencyExchangeDownloadService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence getAppTitle() {

        return getAppContext().getResources().getString(R.string.app_name);
    }

    public static Drawable getAppLogo() {
        return getAppContext().getResources().getDrawable(R.drawable.ic_home);
    }
}
