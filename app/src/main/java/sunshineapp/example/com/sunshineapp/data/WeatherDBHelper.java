package sunshineapp.example.com.sunshineapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dalvik.annotation.TestTarget;

import static sunshineapp.example.com.sunshineapp.data.WeatherContract.WeatherEntry.COLUMN_DATE;

/**
 * Created by PUNEETU on 21-03-2017.
 */

public class WeatherDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weather.db";
    public static final int DATABASE_VERSION= 2;

    public WeatherDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +

                /*
                 * WeatherEntry did not explicitly declare a column called "_ID". However,
                 * WeatherEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        WeatherContract.WeatherEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_DATE       + " INTEGER NOT NULL,"                 +

                        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, "                 +

                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP   + " REAL NOT NULL, "                    +
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP   + " REAL NOT NULL, "                    +

                        WeatherContract.WeatherEntry.COLUMN_HUMIDITY   + " REAL NOT NULL, "                    +
                        WeatherContract.WeatherEntry.COLUMN_PRESSURE   + " REAL NOT NULL, "                    +

                        WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "                    +
                        WeatherContract.WeatherEntry.COLUMN_DEGREES    + " REAL NOT NULL," +
                        " UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        db.execSQL(SQL_CREATE_WEATHER_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(db);

    }


}
