package com.hitick.app.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sparsha on 11/10/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /*
        Constructor for Creating the database
    */
    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    /*
        Helper method to dynamically add User Group Participation Table
    */
    public void addGroupParticipationTable(String tableName) {
        /*
            Create a new participation table for the user
        */
        final String SQL_CREATE_PARTICIPATION_TABLE =
                "CREATE TABLE " + tableName + " (" +

                        DatabaseContract.UserParticipationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseContract.UserParticipationEntry.COLUMN_GROUP_KEY + " INTEGER NOT NULL, " +
                        DatabaseContract.UserParticipationEntry.COLUMN_GROUP_ADMINISTRATOR + " INTEGER NOT NULL, " +
                        /*
                            We use Group Key as a foreign key here
                        */
                        " FOREIGN KEY (" + DatabaseContract.UserParticipationEntry.COLUMN_GROUP_KEY + ") REFERENCES " +
                        DatabaseContract.GroupEntry.TABLE_NAME + " (" + DatabaseContract.GroupEntry.COLUMN_GROUP_ID + "));";

        getWritableDatabase().execSQL(SQL_CREATE_PARTICIPATION_TABLE);
    }

    /*
        Helper Method to dynamically add a new Group Details table
    */
    public void addGroupDetailsTable(String tableName) {
        /*
            Create a new group details table for the group
        */
        final String SQL_CREATE_GROUP_DETAILS_TABLE =
                "CREATE TABLE " + tableName + " (" +

                        DatabaseContract.GroupDetailsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseContract.GroupDetailsEntry.COLUMN_POLL_ID + " INTEGER NOT NULL, " +
                        DatabaseContract.GroupDetailsEntry.COLUMN_POLL_DATETIME + " TEXT NOT NULL, " +
                        DatabaseContract.GroupDetailsEntry.COLUMN_TIME_LEFT + " INTEGER NOT NULL, " +
                        DatabaseContract.GroupDetailsEntry.COLUMN_POLL_ONGOING + " INTEGER NOT NULL, " +
                        DatabaseContract.GroupDetailsEntry.COLUMN_POLL_TOPIC + " TEXT NOT NULL, " +
                        DatabaseContract.GroupDetailsEntry.COLUMN_IN_FAVOR + " INTEGER NOT NULL, " +
                        DatabaseContract.GroupDetailsEntry.COLUMN_OPPOSED + " INTEGER NOT NULL, " +
                        DatabaseContract.GroupDetailsEntry.COLUMN_NOT_VOTED + " INTEGER NOT NULL, " +
                        DatabaseContract.GroupDetailsEntry.COLUMN_POLL_RESULT + " TEXT , " +
                        /*
                            Our Table will have the each poll with a unique id as assigned by the API
                            Therefore we must add a unique constraint on the poll id
                        */
                        "UNIQUE (" + DatabaseContract.GroupDetailsEntry.COLUMN_POLL_ID + ") ON CONFLICT REPLACE);";

        getWritableDatabase().execSQL(SQL_CREATE_GROUP_DETAILS_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create the User Table first
        final String SQL_CREATE_USER_TABLE =
                "CREATE TABLE " + DatabaseContract.UserEntry.TABLE_NAME + " (" +

                        DatabaseContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DatabaseContract.UserEntry.COLUMN_USER_ID + " TEXT NOT NULL," +
                        DatabaseContract.UserEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                        DatabaseContract.UserEntry.COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                        DatabaseContract.UserEntry.COLUMN_MOBILE_NUMBER + " TEXT NOT NULL, " +
                        DatabaseContract.UserEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                        DatabaseContract.UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                        DatabaseContract.UserEntry.COLUMN_USER_GROUP_PARTICIPATION_TABLE + " TEXT, " +
                        /*
                            Our Users Table will have Unique Mobile Numbers
                            On Conflicting numbers we replace the existing user
                        */
                        "UNIQUE (" + DatabaseContract.UserEntry.COLUMN_USER_ID + ") ON CONFLICT REPLACE);";
        /*
            Create the Group's Table now
        */
        final String SQL_CREATE_GROUP_TABLE =
                "CREATE TABLE " + DatabaseContract.GroupEntry.TABLE_NAME + " (" +

                        DatabaseContract.GroupEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseContract.GroupEntry.COLUMN_GROUP_ID + " INTEGER NOT NULL, " +
                        DatabaseContract.GroupEntry.COLUMN_GROUP_NAME + " TEXT NOT NULL, " +
                        DatabaseContract.GroupEntry.COLUMN_GROUP_PASSWORD + " TEXT NOT NULL, " +
                        DatabaseContract.GroupEntry.COLUMN_GROUP_MEMBERS + " INTEGER NOT NULL, " +
                        DatabaseContract.GroupEntry.COLUMN_GROUP_ADMIN_ID + " INTEGER NOT NULL," +
                        DatabaseContract.GroupEntry.COLUMN_GROUP_DETAILS + " TEXT NOT NULL, " +
                        "UNIQUE (" + DatabaseContract.GroupEntry.COLUMN_GROUP_ID + " ) ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GROUP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        /*
            We need to first drop all the Group Details Tables
            and then we need to drop all the User Participation Tables
            Finally we can drop the Group and User Tables

            First drop the group details table
            Query the Group table for all the references to Group Details Table
        */
        Cursor cursor = sqLiteDatabase.query(
                DatabaseContract.GroupEntry.TABLE_NAME,
                new String[]{DatabaseContract.GroupEntry.COLUMN_GROUP_DETAILS},
                null,
                null,
                null,
                null,
                null
        );
        /*
            Drop all the group details tables
        */
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String tableName = cursor.getString(cursor.getColumnIndex(DatabaseContract.GroupEntry.COLUMN_GROUP_DETAILS));
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
            }
        }
        /*
            Now Drop all the User Participation Table
            So first query the user's table to get references to USER Participation Tables
        */
        cursor = sqLiteDatabase.query(
                DatabaseContract.UserEntry.TABLE_NAME,
                new String[]{DatabaseContract.UserEntry.COLUMN_USER_GROUP_PARTICIPATION_TABLE},
                null,
                null,
                null,
                null,
                null
        );
        /*
            Drop all the User Participation Tables
        */
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String tableName = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_USER_GROUP_PARTICIPATION_TABLE));
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
            }
        }
        /*
            Now we can drop the Group and User Tables since we have removed the depending tables first
        */
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.UserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.GroupEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
