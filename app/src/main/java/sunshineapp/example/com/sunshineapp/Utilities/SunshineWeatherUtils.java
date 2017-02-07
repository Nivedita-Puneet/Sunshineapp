package sunshineapp.example.com.sunshineapp.Utilities;

import android.content.Context;

/**
 * Created by PUNEETU on 06-02-2017.
 */

public final class SunshineWeatherUtils {

    static final String TAG = SunshineWeatherUtils.class.getSimpleName();


    public static String formatHighLows(Context context, double high, double low) {

        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }
}
