package sunshineapp.example.com.sunshineapp.Utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PUNEETU on 06-02-2017.
 */

public final class WeatherJsonUtils {

    public static String[] getWeatherAttributesFromJSON(Context context, String weatherJSON){
        String[] parsedWeatherArray = null;

        final String WEATHERJSON_LIST = "list";
        final String TAG = WeatherJsonUtils.class.getSimpleName();


        final String WEATHERJSON_COD= "cod";
        final String WEATHERJSON_CNT = "cnt";
        final String WEATHERJSON_DATE = "dt";

        final String WEATHERJSON_TEMPRATURE = "temp";
        final String WEATHERJSON_DAY = "day";
        final String WEATHERJSON_MINTEMP = "min";
        final String WEATHERJSON_MAXTEMP = "max";
        final String WEATHERJSON_NIGNTTEMP = "night";
        final String WEATHERJSON_EVENINGTEMP = "eve";
        final String WEATHERJSON_MORNINGTEMP = "morn";
        final String WEATHERJSON_PRESSURE = "pressure";
        final String WEATHERJSON_HUMIDITY = "humidity";
        final String WEATHERJSON_WEATHER = "weather";
        final String WEATHERJSON_MAIN = "main";
        final String WEATHERJSON_DESC = "description";
        final String WEATHERJSON_SPEED = "speed";
        final String WEATHERJSON_DEG = "deg";
        final String WEATHERJSON_CLOUDS = "clouds";
        final String WEATHERJSON_RAIN = "rain";

        try{
            JSONObject jsonObject = new JSONObject(weatherJSON);
            JSONArray array = jsonObject.getJSONArray(WEATHERJSON_LIST);
            int len = array.length();
            Log.i(TAG, ""+ len);

            parsedWeatherArray = new String[array.length()];
            int parsedArraylength =parsedWeatherArray.length;
            Log.i(TAG, ""+ parsedArraylength );
            long localDate = System.currentTimeMillis();
            long utcDate = SunshineDateUtils.getUTCDateFromLocal(localDate);
            long startDay = SunshineDateUtils.normalizeDate(utcDate);


            for (int i=0; i<array.length(); i++){

                String date;
                String highAndLow;

            /* These are the values that will be collected */
                long dateTimeMillis;
                double high;
                double low;
                String description;

                JSONObject dayForecast = array.getJSONObject(i);

                dateTimeMillis = startDay + SunshineDateUtils.DAY_IN_MILLIS * i;
                date = SunshineDateUtils.getFriendlyDateString(context, dateTimeMillis, false);
                Log.i(TAG, date);
                JSONObject dailyWeather = dayForecast.getJSONArray(WEATHERJSON_WEATHER).getJSONObject(0);
                description = dailyWeather.getString(WEATHERJSON_MAIN);

                JSONObject dailyTemprature = dayForecast.getJSONObject(WEATHERJSON_TEMPRATURE);
                high = dailyTemprature.getDouble(WEATHERJSON_MAXTEMP);
                low = dailyTemprature.getDouble(WEATHERJSON_MINTEMP);
                highAndLow = SunshineWeatherUtils.formatHighLows(context,high,low);

                parsedWeatherArray[i] = date + "-" + description + "-" +highAndLow;
            }
            return parsedWeatherArray;
        }catch (JSONException exception){
            exception.printStackTrace();
        }



        return parsedWeatherArray;
    }
}
