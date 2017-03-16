package sunshineapp.example.com.sunshineapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

import sunshineapp.example.com.sunshineapp.Utilities.NetworkUtils;
import sunshineapp.example.com.sunshineapp.Utilities.WeatherJsonUtils;
import sunshineapp.example.com.sunshineapp.data.SunshinePreferences;

public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]> ,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private TextView mErrorMessageDisplay;
    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private ProgressBar mLoadingIndicator;

    static String TAG = HomeActivity.class.getSimpleName();
    final static  int SUNSHINE_WEATHER_CODE = 101;
    private static  boolean HAS_PREFERENCES_BEEN_UPDATED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        return true;
    }

    private void initializeControls(){
        mErrorMessageDisplay = (TextView)findViewById(R.id.tv_error_message_display);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_forecast);
        mLoadingIndicator = (ProgressBar)findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mForecastAdapter = new ForecastAdapter(new ForecastAdapter.ForecastAdapterOnClickHandler() {
            @Override
            public void clickListener(String weatherForToday) {

                Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                intent.putExtra("WeatherForToday", weatherForToday);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mForecastAdapter);
        getSupportLoaderManager().initLoader(SUNSHINE_WEATHER_CODE, null, HomeActivity.this);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    private void showWeatherDataView(){

        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<String[]>(this) {

            String[] mWeatherData = null;
            @Override
            protected void onStartLoading(){
                super.onStartLoading();
                if(mWeatherData != null){
                    deliverResult(mWeatherData);
                }else{
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

            }
            @Override
            public String[] loadInBackground() {
                String preferredLocation = SunshinePreferences.getPreferredWeatherLocation(HomeActivity.this);
                URL httpWeatherUrl = NetworkUtils.buildUrl(preferredLocation);
                try {

                    String httpWeatherJSONResponse = NetworkUtils.getResponseFromHttpURL(httpWeatherUrl);
                    String[] parsedWeatherJSON = WeatherJsonUtils.getWeatherAttributesFromJSON(HomeActivity.this, httpWeatherJSONResponse);
                    for (int i = 0; i < parsedWeatherJSON.length; i++) {
                        Log.i(HomeActivity.class.getSimpleName(), parsedWeatherJSON[i]);
                    }

                    return parsedWeatherJSON;
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                return null;
            }

            public void deliverResult(String[] data){
                mWeatherData = data;
                super.deliverResult(data);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if(data != null){
            showWeatherDataView();
            mForecastAdapter.setWeatherData(data);
        }else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.action_refresh:
                mForecastAdapter.setWeatherData(null);
                getSupportLoaderManager().restartLoader(SUNSHINE_WEATHER_CODE, null, HomeActivity.this);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if(HAS_PREFERENCES_BEEN_UPDATED){
            getSupportLoaderManager().restartLoader(SUNSHINE_WEATHER_CODE, null, HomeActivity.this);
            HAS_PREFERENCES_BEEN_UPDATED = false;

        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        HAS_PREFERENCES_BEEN_UPDATED = true;
    }
}
