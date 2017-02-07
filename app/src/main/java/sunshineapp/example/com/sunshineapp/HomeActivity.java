package sunshineapp.example.com.sunshineapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

import sunshineapp.example.com.sunshineapp.Utilities.NetworkUtils;
import sunshineapp.example.com.sunshineapp.Utilities.WeatherJsonUtils;

public class HomeActivity extends AppCompatActivity {

    TextView mWeatherTextView;

    static String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeControls();
    }

    private void initializeControls(){
        mWeatherTextView = (TextView)findViewById(R.id.weather_data);
        displayWeatherDataFromCloud();
    }

    /*private void displayDummyWeatherData(TextView mWeatherTextView){

        String[] dummyWeatherData = {
                "Today, May 17 - Clear - 17°C / 15°C",
                "Tomorrow - Cloudy - 19°C / 15°C",
                "Thursday - Rainy- 30°C / 11°C",
                "Friday - Thunderstorms - 21°C / 9°C",
                "Saturday - Thunderstorms - 16°C / 7°C",
                "Sunday - Rainy - 16°C / 8°C",
                "Monday - Partly Cloudy - 15°C / 10°C",
                "Tue, May 24 - Meatballs - 16°C / 18°C",
                "Wed, May 25 - Cloudy - 19°C / 15°C",
                "Thu, May 26 - Stormy - 30°C / 11°C",
                "Fri, May 27 - Hurricane - 21°C / 9°C",
                "Sat, May 28 - Meteors - 16°C / 7°C",
                "Sun, May 29 - Apocalypse - 16°C / 8°C",
                "Mon, May 30 - Post Apocalypse - 15°C / 10°C",
        };

        for(String dummyWeatherDay:dummyWeatherData){

            mWeatherTextView.append(dummyWeatherDay + "\n\n\n");

        }

    }*/

    private void displayWeatherDataFromCloud(){
        new FetchWeatherTask().execute("2142");
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... params) {

            if(params.length == 0){
                return null;
            }

            String location = params[0];
            URL httpWeatherUrl = NetworkUtils.buildUrl("2142");
            try {

                String httpWeatherJSONResponse = NetworkUtils.getResponseFromHttpURL(httpWeatherUrl);
                String[] parsedWeatherJSON = WeatherJsonUtils.getWeatherAttributesFromJSON(HomeActivity.this, httpWeatherJSONResponse);
                return parsedWeatherJSON;
            }catch (IOException  exception){
                Log.e(TAG, exception.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected  void  onPostExecute(String[] weatherData){
            if(weatherData != null){
                for(String weatherString: weatherData){
                   mWeatherTextView.append((weatherString) + "\n\n\n");
                }
            }
        }

    }

}
