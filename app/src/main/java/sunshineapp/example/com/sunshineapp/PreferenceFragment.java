package sunshineapp.example.com.sunshineapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import sunshineapp.example.com.sunshineapp.R;

/**
 * Created by PUNEETU on 09-03-2017.
 */

public class PreferenceFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, PreferenceScreen.OnPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.pref_general);
        SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();

        for(int i=0; i<count; i++){

            Preference p = preferenceScreen.getPreference(i);
            if(!(p instanceof CheckBoxPreference)){
                String value = preferences.getString(p.getKey(), "");
                p.setSummary(value);
            }
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if( null != preference){
            if(! (preference instanceof CheckBoxPreference)){
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }

    private void setPreferenceSummary(Preference preference, Object value){

        String selectedValue = value.toString();
        String key = preference.getKey();

        if( preference instanceof  ListPreference){

            ListPreference listPreference = (ListPreference)preference;
            int prefIndex= listPreference.findIndexOfValue(selectedValue);
            if(prefIndex >=0 ){
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }else{
                preference.setSummary(selectedValue);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

    @Override
    public  void onStop(){
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
}
