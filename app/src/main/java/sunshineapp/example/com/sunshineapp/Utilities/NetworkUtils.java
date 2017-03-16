package sunshineapp.example.com.sunshineapp.Utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by PUNEETU on 02-02-2017.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String DYNAMIC_WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/forecast/daily?";

    private static final String STATIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/staticweather";

    private static final String FORECAST_BASE_URL = STATIC_WEATHER_URL;

    private static final String format = "json";
    private static final String units = "metric";
    private static final int numDays = 14;

    final static String QUERY_PARAM = "q";
    final static String LAT_PARAM = "lat";
    final static String LON_PARAM = "lon";
    final static String FORMAT_PARAM = "mode";
    final static String UNITS_PARAM = "units";
    final static String DAYS_PARAM = "cnt";
    final static String KEY_PARAM = "appid";
    final static String WEATHER_API_KEY = "d84eece3449ebc57d66aa0620e8dce62";

    public static URL buildUrl(String locationQuery){

       // StringBuilder stringBuilder = new StringBuilder(locationQuery).append(",").append("Sydney");
        StringBuilder stringBuilder = new StringBuilder(locationQuery);

        Log.i(NetworkUtils.class.getSimpleName(), stringBuilder.toString());

        Uri builtUri = Uri.parse(DYNAMIC_WEATHER_URL).buildUpon().
                         appendQueryParameter(QUERY_PARAM,stringBuilder.toString())
                        .appendQueryParameter(FORMAT_PARAM,format )
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays)).
                         appendQueryParameter(KEY_PARAM,WEATHER_API_KEY).build();
        Log.i(TAG,builtUri.toString());
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        }catch (MalformedURLException exception){

        }

        return url;
    }

    public static String getResponseFromHttpURL(URL url) throws IOException{

        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        try {
            InputStream inputStream = httpURLConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            boolean hasInputStream = scanner.hasNext();
            if (hasInputStream) {
               return scanner.next();
            } else {
                return null;
            }
        }finally {

            httpURLConnection.disconnect();
        }


    }





}
