package sunshineapp.example.com.sunshineapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import sunshineapp.example.com.sunshineapp.Utilities.SunshineDateUtils;

/**
 * Created by PUNEETU on 03-04-2017.
 */

public class WeatherProvider extends ContentProvider {

    WeatherDBHelper mDBHelper;
    private static final int CODE_WEATHER = 100;
    private static final int CODE_WEATHER_WITH_DATE = 101;


    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER, CODE_WEATHER);
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/#", CODE_WEATHER_WITH_DATE );

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new WeatherDBHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri,@NonNull ContentValues[] contentValues){

        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){
            case CODE_WEATHER:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : contentValues) {
                        long weatherDate =
                                value.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);


                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                  break;
            default:
                super.bulkInsert(uri, contentValues);
        }
        return 0;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match){
            case CODE_WEATHER:
                retCursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,null,
                                        sortOrder);
                 break;
            case CODE_WEATHER_WITH_DATE:

                  String id =  uri.getPathSegments().get(1);
                  String mSelection = "_id=?";
                  String[] mSelectionArgs = new String[]{id};

                  retCursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME,
                                       projection,
                                       mSelection,
                                       mSelectionArgs,
                                       null,
                                       null,
                                       sortOrder);

                 break;
            default:
                throw new UnsupportedOperationException("URI query is not appropriate" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int numberOfRowsDeleted = 0;

        switch (sUriMatcher.match(uri)){
            case CODE_WEATHER:
            String id = uri.getPathSegments().get(1);
            String mSelection = "_id=?";
            String[] mSelectionArgs = new String[]{id};
            numberOfRowsDeleted = db.delete(WeatherContract.WeatherEntry.TABLE_NAME, mSelection, mSelectionArgs);
            break;
            default:
                new UnsupportedOperationException("return proper Uri" + uri);
        }

        if(numberOfRowsDeleted >0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
