package sunshineapp.example.com.sunshineapp.Utilities;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by PUNEETU on 02-02-2017.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String DYNAMIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/weather";

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

    public static URL buildUrl(String locationQuery){

        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().
                        appendQueryParameter(QUERY_PARAM,locationQuery)
                        .appendQueryParameter(FORMAT_PARAM,format )
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays)).build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        }catch (MalformedURLException exception){

        }

        return url;
    }





}
