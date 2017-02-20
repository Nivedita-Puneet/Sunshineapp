package sunshineapp.example.com.sunshineapp;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

import sunshineapp.example.com.sunshineapp.Utilities.NetworkUtils;
import sunshineapp.example.com.sunshineapp.Utilities.WeatherJsonUtils;

public class HomeActivity extends AppCompatActivity {

    private TextView mErrorMessageDisplay;
    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private ProgressBar mLoadingIndicator;

    static String TAG = HomeActivity.class.getSimpleName();

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
        displayWeatherDataFromCloud();
    }

    private void displayWeatherDataFromCloud(){

        showWeatherDataView();
        new FetchWeatherTask().execute("2142");
    }

    private void showWeatherDataView(){

        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String[]>{


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
        @Override
        protected String[] doInBackground(String... params) {

            if(params.length == 0){
                return null;
            }

            String location = params[0];
            URL httpWeatherUrl = NetworkUtils.buildUrl(location);
            try {

                String httpWeatherJSONResponse = NetworkUtils.getResponseFromHttpURL(httpWeatherUrl);
                String[] parsedWeatherJSON = WeatherJsonUtils.getWeatherAttributesFromJSON(HomeActivity.this, httpWeatherJSONResponse);
                for(int i=0; i<parsedWeatherJSON.length; i++){
                    Log.i(HomeActivity.class.getSimpleName(), parsedWeatherJSON[i]);
                }

                return parsedWeatherJSON;
            }catch (IOException  exception){
                Log.e(TAG, exception.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected  void  onPostExecute(String[] weatherData){

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(weatherData != null){
                showWeatherDataView();
                mForecastAdapter.setWeatherData(weatherData);
            }else {
                showErrorMessage();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.action_refresh:
                mForecastAdapter.setWeatherData(null);
                displayWeatherDataFromCloud();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }


}
