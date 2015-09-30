package com.mierzejewski.inz;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import com.mierzejewski.inz.model.Currency;

/**
 * Created by dom on 04/11/14.
 */
public class SettingsFragment extends PreferenceFragment
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.preferences);
        ListPreference currencyPref = (ListPreference) findPreference(Currency.CURRENCY_KEY);

        String[] names = new String[Currency.values().length];
        String[] values = new String[Currency.values().length];

        int i = 0;
        int foundIndex = 0;
        for(Currency item : Currency.values())
        {
            names[i] = item.name;
            values[i++] = item.name();
            if(item.equals(Currency.getDefault()))
            {
                foundIndex = i;
            }
        }
        currencyPref.setValueIndex(foundIndex);
        currencyPref.setEntries(names);
        currencyPref.setEntryValues(values);
        currencyPref.setSummary(Currency.getDefault().name);
        currencyPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                Currency currency = Currency.getFromAbbr((String) newValue);
                Currency.setDefault(currency);
                preference.setSummary(currency.name);
                return true;
            }
        });

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
    }

}
