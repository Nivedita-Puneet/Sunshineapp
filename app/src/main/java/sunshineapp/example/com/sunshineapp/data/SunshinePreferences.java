package sunshineapp.example.com.sunshineapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import sunshineapp.example.com.sunshineapp.R;

/**
 * Created by PUNEETU on 16-03-2017.
 */

public class SunshinePreferences {

    public static String getPreferredWeatherLocation(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForLocation = context.getString(R.string.pref_location_key);
        String defaultLocation = context.getString(R.string.pref_location_default);

        return preferences.getString(keyForLocation, defaultLocation);

    }

    public static boolean isMetric(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForUnit = context.getString(R.string.pref_units_key);
        String defaultUnit = context.getString(R.string.pref_units_metric);
        String preferredUnits = preferences.getString(keyForUnit, defaultUnit);
        String metric = context.getString(R.string.pref_units_metric);
        boolean userPrefersMetric;
        if(metric.equals(preferredUnits)){
            userPrefersMetric = true;
        }else{
            userPrefersMetric = false;
        }

        return userPrefersMetric;
    }
}
