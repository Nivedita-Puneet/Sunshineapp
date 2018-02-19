/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sunshineapp.example.com.sunshineapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static sunshineapp.example.com.sunshineapp.data.TestUtilities.getConstantNameByStringValue;
import static sunshineapp.example.com.sunshineapp.data.TestUtilities.getStaticIntegerField;
import static sunshineapp.example.com.sunshineapp.data.TestUtilities.getStaticStringField;
import static sunshineapp.example.com.sunshineapp.data.TestUtilities.studentReadableClassNotFound;
import static sunshineapp.example.com.sunshineapp.data.TestUtilities.studentReadableNoSuchField;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Used to test the database we use in Sunshine to cache weather data. Within these tests, we
 * test the following:
 * <p>
 * <p>
 * 1) Creation of the database with proper table(s)
 * 2) Insertion of single record into our weather table
 * 3) When a record is already stored in the weather table with a particular date, a new record
 * with the same date will overwrite that record.
 * 4) Verify that NON NULL constraints are working properly on record inserts
 * 5) Verify auto increment is working with the ID
 * 6) Test the onUpgrade functionality of the WeatherDbHelper
 */
@RunWith(AndroidJUnit4.class)
public class TestSunshineDatabase {

    /*
     * Context used to perform operations on the database and create WeatherDbHelpers.
     */
    private final Context context = InstrumentationRegistry.getTargetContext();

    /*
     * In order to verify that you have set up your classes properly and followed our TODOs, we
     * need to create what's called a Change Detector Test. In almost any other situation, these
     * tests are discouraged, as they provide no real value in a production setting. However, using
     * reflection to verify that you have set your classes up correctly will help provide more
     * useful errors if you've missed a step in our instructions.
     *
     * Additionally, using reflection for these tests allows you to run the tests when they
     * normally wouldn't compile, as they depend on pieces of your classes that you might not
     * have created when you initially run the tests.
     */
    private static final String packageName = "sunshineapp.example.com.sunshineapp";
    private static final String dataPackageName = packageName + ".data";

    private Class weatherEntryClass;
    private Class weatherDbHelperClass;
    private static final String weatherContractName = ".WeatherContract";
    private static final String weatherEntryName = weatherContractName + "$WeatherEntry";
    private static final String weatherDbHelperName = ".WeatherDBHelper";

    private static final String databaseNameVariableName = "DATABASE_NAME";
    private static String REFLECTED_DATABASE_NAME;

    private static final String databaseVersionVariableName = "DATABASE_VERSION";
    private static int REFLECTED_DATABASE_VERSION;

    private static final String tableNameVariableName = "TABLE_NAME";
    private static String REFLECTED_TABLE_NAME;

    private static final String columnDateVariableName = "COLUMN_DATE";
    static String REFLECTED_COLUMN_DATE;

    private static final String columnWeatherIdVariableName = "COLUMN_WEATHER_ID";
    static String REFLECTED_COLUMN_WEATHER_ID;

    private static final String columnMinVariableName = "COLUMN_MIN_TEMP";
    static String REFLECTED_COLUMN_MIN;

    private static final String columnMaxVariableName = "COLUMN_MAX_TEMP";
    static String REFLECTED_COLUMN_MAX;

    private static final String columnHumidityVariableName = "COLUMN_HUMIDITY";
    static String REFLECTED_COLUMN_HUMIDITY;

    private static final String columnPressureVariableName = "COLUMN_PRESSURE";
    static String REFLECTED_COLUMN_PRESSURE;

    private static final String columnWindSpeedVariableName = "COLUMN_WIND_SPEED";
    static String REFLECTED_COLUMN_WIND_SPEED;

    private static final String columnWindDirVariableName = "COLUMN_DEGREES";
    static String REFLECTED_COLUMN_WIND_DIR;

    private SQLiteDatabase database;
    private SQLiteOpenHelper dbHelper;

    @Before
    public void before() {
        try {

            weatherEntryClass = Class.forName(dataPackageName + weatherEntryName);
            if (!BaseColumns.class.isAssignableFrom(weatherEntryClass)) {
                String weatherEntryDoesNotImplementBaseColumns = "WeatherEntry class needs to " +
                        "implement the interface BaseColumns, but does not.";
                fail(weatherEntryDoesNotImplementBaseColumns);
            }

            REFLECTED_TABLE_NAME = getStaticStringField(weatherEntryClass, tableNameVariableName);
            REFLECTED_COLUMN_DATE = getStaticStringField(weatherEntryClass, columnDateVariableName);
            REFLECTED_COLUMN_WEATHER_ID = getStaticStringField(weatherEntryClass, columnWeatherIdVariableName);
            REFLECTED_COLUMN_MIN = getStaticStringField(weatherEntryClass, columnMinVariableName);
            REFLECTED_COLUMN_MAX = getStaticStringField(weatherEntryClass, columnMaxVariableName);
            REFLECTED_COLUMN_HUMIDITY = getStaticStringField(weatherEntryClass, columnHumidityVariableName);
            REFLECTED_COLUMN_PRESSURE = getStaticStringField(weatherEntryClass, columnPressureVariableName);
            REFLECTED_COLUMN_WIND_SPEED = getStaticStringField(weatherEntryClass, columnWindSpeedVariableName);
            REFLECTED_COLUMN_WIND_DIR = getStaticStringField(weatherEntryClass, columnWindDirVariableName);

            weatherDbHelperClass = Class.forName(dataPackageName + weatherDbHelperName);

            Class weatherDbHelperSuperclass = weatherDbHelperClass.getSuperclass();

            if (weatherDbHelperSuperclass == null || weatherDbHelperSuperclass.equals(Object.class)) {
                String noExplicitSuperclass =
                        "WeatherDbHelper needs to extend SQLiteOpenHelper, but yours currently doesn't extend a class at all.";
                fail(noExplicitSuperclass);
            } else if (weatherDbHelperSuperclass != null) {
                String weatherDbHelperSuperclassName = weatherDbHelperSuperclass.getSimpleName();
                String doesNotExtendOpenHelper =
                        "WeatherDbHelper needs to extend SQLiteOpenHelper but yours extends "
                                + weatherDbHelperSuperclassName;

                assertTrue(doesNotExtendOpenHelper,
                        SQLiteOpenHelper.class.isAssignableFrom(weatherDbHelperSuperclass));
            }

            REFLECTED_DATABASE_NAME = getStaticStringField(
                    weatherDbHelperClass, databaseNameVariableName);

            REFLECTED_DATABASE_VERSION = getStaticIntegerField(
                    weatherDbHelperClass, databaseVersionVariableName);

            Constructor weatherDbHelperCtor = weatherDbHelperClass.getConstructor(Context.class);

            dbHelper = (SQLiteOpenHelper) weatherDbHelperCtor.newInstance(context);

            context.deleteDatabase(REFLECTED_DATABASE_NAME);

            Method getWritableDatabase = SQLiteOpenHelper.class.getDeclaredMethod("getWritableDatabase");
            database = (SQLiteDatabase) getWritableDatabase.invoke(dbHelper);

        } catch (ClassNotFoundException e) {
            fail(studentReadableClassNotFound(e));
        } catch (NoSuchFieldException e) {
            fail(studentReadableNoSuchField(e));
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        } catch (InstantiationException e) {
            fail(e.getMessage());
        } catch (InvocationTargetException e) {
            fail(e.getMessage());
            Log.e(TestSunshineDatabase.class.getSimpleName(), e.getLocalizedMessage());
        }
    }

    @Test
    public void testDatabaseVersionWasIncremented() {
        int expectedDatabaseVersion = 2;
        String databaseVersionShouldBe1 = "Database version should be "
                + expectedDatabaseVersion + " but isn't."
                + "\n Database version: ";

        assertEquals(databaseVersionShouldBe1,
                expectedDatabaseVersion,
                REFLECTED_DATABASE_VERSION);
    }

    /*
    * Mock the test case in order to verify insertion of records into databse.
    * Mock the entries from test utility and verify if insertion works properly.
    * */

    @Test
    public void testDuplicateInsertBehaviourShouldReplace() {

        /*get the weather values from the util class */

        ContentValues testWeatherValues = TestUtilities.createTestWeatherContentValues();

        /*Get original weather id from the database to ensure that we will avoid duplicate entries*/

        long originalWeatherId = testWeatherValues.getAsLong(REFLECTED_COLUMN_WEATHER_ID);

        /*Insert content values with original weather id*/
        database.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, testWeatherValues);

        /*Check the new id*/
        long newWeatherId = originalWeatherId + 1;

        testWeatherValues.put(REFLECTED_COLUMN_WEATHER_ID, newWeatherId);

        /*Insert the new value into the database*/
        database.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, testWeatherValues);

        /*Query for the weather record with our new id*/

        Cursor cursor = database.query(REFLECTED_TABLE_NAME, new String[]{REFLECTED_COLUMN_DATE},
                null,
                null,
                null,
                null,
                null);

        String recordWithNewIdNotFound =
                "New record did not overwrite the previous record for the same date.";
        assertTrue(recordWithNewIdNotFound,
                cursor.getCount() == 1);

        /*close the cursor*/
        cursor.close();


    }

    /*Test case to check that the database does not allow the insertion of null values.*/
    @Test
    public void testNullColumnConstraints() {
        /* Use a WeatherDbHelper to get access to a writable database */

        /* We need a cursor from a weather table query to access the column names */
        Cursor weatherTableCursor = database.query(
                REFLECTED_TABLE_NAME,
                /* We don't care about specifications, we just want the column names */
                null, null, null, null, null, null);

        /* Store the column names and close the cursor */
        String[] weatherTableColumnNames = weatherTableCursor.getColumnNames();
        weatherTableCursor.close();

        /* Obtain weather values from TestUtilities and make a copy to avoid altering singleton */
        ContentValues testValues = TestUtilities.createTestWeatherContentValues();
        /* Create a copy of the testValues to save as a reference point to restore values */
        ContentValues testValuesReferenceCopy = new ContentValues(testValues);

        for (String columnName : weatherTableColumnNames) {

            /* We don't need to verify the _ID column value is not null, the system does */
            if (columnName.equals(WeatherContract.WeatherEntry._ID)) continue;

            /* Set the value to null */
            testValues.putNull(columnName);

            /* Insert ContentValues into database and get a row ID back */
            long shouldFailRowId = database.insert(
                    REFLECTED_TABLE_NAME,
                    null,
                    testValues);

            String variableName = getConstantNameByStringValue(
                    WeatherContract.WeatherEntry.class,
                    columnName);

            /* If the insert fails, which it should in this case, database.insert returns -1 */
            String nullRowInsertShouldFail =
                    "Insert should have failed due to a null value for column: '" + columnName + "'"
                            + ", but didn't."
                            + "\n Check that you've added NOT NULL to " + variableName
                            + " in your create table statement in the WeatherEntry class."
                            + "\n Row ID: ";
            assertEquals(nullRowInsertShouldFail,
                    -1,
                    shouldFailRowId);

            /* "Restore" the original value in testValues */
            testValues.put(columnName, testValuesReferenceCopy.getAsDouble(columnName));
        }

        /* Close database */
        dbHelper.close();
    }

    /*Test case used to identify auto increment of the id into the database.*/
    @Test
    public void testIntegerAutoincrement() {

        /* First, let's ensure we have some values in our table initially */
        testInsertSingleRecordIntoWeatherTable();

        /* Obtain weather values from TestUtilities */
        ContentValues testWeatherValues = TestUtilities.createTestWeatherContentValues();

        /* Get the date of the testWeatherValues to ensure we use a different date later */
        long originalDate = testWeatherValues.getAsLong(REFLECTED_COLUMN_DATE);

        /* Insert ContentValues into database and get a row ID back */
        long firstRowId = database.insert(
                REFLECTED_TABLE_NAME,
                null,
                testWeatherValues);

        /* Delete the row we just inserted to see if the database will reuse the rowID */
        database.delete(
                REFLECTED_TABLE_NAME,
                "_ID == " + firstRowId,
                null);

        /*
         * Now we need to change the date associated with our test content values because the
         * database policy is to replace identical dates on conflict.
         */
        long dayAfterOriginalDate = originalDate + TimeUnit.DAYS.toMillis(1);
        testWeatherValues.put(REFLECTED_COLUMN_DATE, dayAfterOriginalDate);

        /* Insert ContentValues into database and get another row ID back */
        long secondRowId = database.insert(
                REFLECTED_TABLE_NAME,
                null,
                testWeatherValues);

        String sequentialInsertsDoNotAutoIncrementId =
                "IDs were reused and shouldn't be if autoincrement is setup properly.";
        assertNotSame(sequentialInsertsDoNotAutoIncrementId,
                firstRowId, secondRowId);
    }

    /*Test case to identify if on upgrade works properly for this database.*/
    @Test
    public void testOnUpgradeBehavesCorrectly() {

        testInsertSingleRecordIntoWeatherTable();

        dbHelper.onUpgrade(database, 13, 14);

        /*
         * This Cursor will contain the names of each table in our database and we will use it to
         * make sure that our weather table is still in the database after upgrading.
         */
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" + REFLECTED_TABLE_NAME + "'",
                null);

        /*
         * Our database should only contain one table, and so the above query should have one
         * record in the cursor that queried for our table names.
         */
        int expectedTableCount = 1;
        String shouldHaveSingleTable = "There should only be one table returned from this query.";
        assertEquals(shouldHaveSingleTable,
                expectedTableCount,
                tableNameCursor.getCount());

        /* We are done verifying our table names, so we can close this cursor */
        tableNameCursor.close();

        Cursor shouldBeEmptyWeatherCursor = database.query(
                REFLECTED_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        int expectedRecordCountAfterUpgrade = 0;
        /* We will finally verify that our weather table is empty after */
        String weatherTableShouldBeEmpty =
                "Weather table should be empty after upgrade, but wasn't."
                        + "\nNumber of records: ";
        assertEquals(weatherTableShouldBeEmpty,
                expectedRecordCountAfterUpgrade,
                shouldBeEmptyWeatherCursor.getCount());

        /* Test is over, close the cursor */
        database.close();
    }

    /*Test case to check if all the tables are created properly in the database*/
    @Test
    public void testCreateDb() {
        /*
         * Will contain the name of every table in our database. Even though in our case, we only
         * have only table, in many cases, there are multiple tables. Because of that, we are
         * showing you how to test that a database with multiple tables was created properly.
         */
        final HashSet<String> tableNameHashSet = new HashSet<>();

        /* Here, we add the name of our only table in this particular database */
        tableNameHashSet.add(REFLECTED_TABLE_NAME);
        /* Students, here is where you would add any other table names if you had them */
//        tableNameHashSet.add(MyAwesomeSuperCoolTableName);
//        tableNameHashSet.add(MyOtherCoolTableNameThatContainsOtherCoolData);

        /* We think the database is open, let's verify that here */
        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen,
                true,
                database.isOpen());

        /* This Cursor will contain the names of each table in our database */
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'",
                null);

        /*
         * If tableNameCursor.moveToFirst returns false from this query, it means the database
         * wasn't created properly. In actuality, it means that your database contains no tables.
         */
        String errorInCreatingDatabase =
                "Error: This means that the database has not been created correctly";
        assertTrue(errorInCreatingDatabase,
                tableNameCursor.moveToFirst());

        /*
         * tableNameCursor contains the name of each table in this database. Here, we loop over
         * each table that was ACTUALLY created in the database and remove it from the
         * tableNameHashSet to keep track of the fact that was added. At the end of this loop, we
         * should have removed every table name that we thought we should have in our database.
         * If the tableNameHashSet isn't empty after this loop, there was a table that wasn't
         * created properly.
         */
        do {
            tableNameHashSet.remove(tableNameCursor.getString(0));
        } while (tableNameCursor.moveToNext());

        /* If this fails, it means that your database doesn't contain the expected table(s) */
        assertTrue("Error: Your database was created without the expected tables.",
                tableNameHashSet.isEmpty());

        /* Always close the cursor when you are finished with it */
        tableNameCursor.close();
    }

    /*Test case used by several other scenarios*/

    @Test
    public void testInsertSingleRecordIntoWeatherTable() {

        /* Obtain weather values from TestUtilities */
        ContentValues testWeatherValues = TestUtilities.createTestWeatherContentValues();

        /* Insert ContentValues into database and get a row ID back */
        long weatherRowId = database.insert(
                REFLECTED_TABLE_NAME,
                null,
                testWeatherValues);

        /* If the insert fails, database.insert returns -1 */
        int valueOfIdIfInsertFails = -1;
        String insertFailed = "Unable to insert into the database";
        assertNotSame(insertFailed,
                valueOfIdIfInsertFails,
                weatherRowId);

        /*
         * Query the database and receive a Cursor. A Cursor is the primary way to interact with
         * a database in Android.
         */
        Cursor weatherCursor = database.query(
                /* Name of table on which to perform the query */
                REFLECTED_TABLE_NAME,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Columns to group by */
                null,
                /* Columns to filter by row groups */
                null,
                /* Sort order to return in Cursor */
                null);

        /* Cursor.moveToFirst will return false if there are no records returned from your query */
        String emptyQueryError = "Error: No Records returned from weather query";
        assertTrue(emptyQueryError,
                weatherCursor.moveToFirst());

        /* Verify that the returned results match the expected results */
        String expectedWeatherDidntMatchActual =
                "Expected weather values didn't match actual values.";
        TestUtilities.validateCurrentRecord(expectedWeatherDidntMatchActual,
                weatherCursor,
                testWeatherValues);

        /*
         * Since before every method annotated with the @Test annotation, the database is
         * deleted, we can assume in this method that there should only be one record in our
         * Weather table because we inserted it. If there is more than one record, an issue has
         * occurred.
         */
        assertFalse("Error: More than one record returned from weather query",
                weatherCursor.moveToNext());

        /* Close cursor */
        weatherCursor.close();
    }

}