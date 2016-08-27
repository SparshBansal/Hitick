package com.hitick.app.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.hitick.app.Data.DatabaseContract.*;

/**
 * Created by Sparsha on 11/10/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /*
        Constructor for Creating the database
    */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create the User Table first
        final String SQL_CREATE_USER_TABLE =
                "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +

                        UserEntry.COLUMN_USER_ID + " TEXT PRIMARY KEY NOT NULL," +
                        UserEntry.COLUMN_USERNAME + " TEXT NOT NULL," +
                        UserEntry.COLUMN_MOBILE_NUMBER + " TEXT NOT NULL, " +
                        UserEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                        UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +

                        "UNIQUE (" + UserEntry.COLUMN_USER_ID + ") ON CONFLICT REPLACE);";

        //Create groups table
        final String SQL_CREATE_GROUP_TABLE =
                "CREATE TABLE " + GroupEntry.TABLE_NAME + " (" +

                        GroupEntry.COLUMN_GROUP_ID + " TEXT PRIMARY KEY NOT NULL, " +
                        GroupEntry.COLUMN_GROUP_NAME + " TEXT NOT NULL, " +
                        GroupEntry.COLUMN_GROUP_PASSWORD + " TEXT NOT NULL, " +
                        GroupEntry.COLUMN_GROUP_MEMBERS + " INTEGER NOT NULL, " +
                        GroupEntry.COLUMN_GROUP_ADMIN_ID + " INTEGER NOT NULL," +

                        "UNIQUE (" + GroupEntry.COLUMN_GROUP_ID + " ) ON CONFLICT REPLACE);";

        //Create group details table
        final String SQL_CREATE_GROUP_DETAILS_TABLE =
                "CREATE TABLE " + GroupDetailsEntry.TABLE_NAME + " (" +

                        GroupDetailsEntry.COLUMN_POLL_ID + " TEXT PRIMARY KEY NOT NULL, " +
                        GroupDetailsEntry.COLUMN_GROUP_ID + " TEXT NOT NULL, " +
                        GroupDetailsEntry.COLUMN_POLL_DATETIME + " TEXT NOT NULL, " +
                        GroupDetailsEntry.COLUMN_STIPULATED_TIME + " INTEGER NOT NULL, " +
                        GroupDetailsEntry.COLUMN_POLL_ONGOING + " INTEGER NOT NULL, " +
                        GroupDetailsEntry.COLUMN_POLL_TOPIC + " TEXT NOT NULL, " +
                        GroupDetailsEntry.COLUMN_IN_FAVOR + " INTEGER NOT NULL, " +
                        GroupDetailsEntry.COLUMN_OPPOSED + " INTEGER NOT NULL, " +
                        GroupDetailsEntry.COLUMN_NOT_VOTED + " INTEGER NOT NULL, " +
                        GroupDetailsEntry.COLUMN_POLL_RESULT + " TEXT , " +

                        /*Foreign key Constraint*/
                        "FOREIGN KEY (" + GroupDetailsEntry.COLUMN_GROUP_ID + ")" +
                        " REFERENCES " + GroupEntry.TABLE_NAME + "(" +
                        GroupEntry.COLUMN_GROUP_ID + "), " +

                        /*
                            Our Table will have the each poll with a unique id as assigned by the API
                            Therefore we must add a unique constraint on the poll id
                        */
                        "UNIQUE (" + GroupDetailsEntry.COLUMN_POLL_ID + ") ON CONFLICT REPLACE);";

        //Create user participation table
        final String SQL_CREATE_PARTICIPATION_TABLE =
                "CREATE TABLE " + UserParticipationEntry.TABLE_NAME + " (" +

                        UserParticipationEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +
                        UserParticipationEntry.COLUMN_GROUP_ID + " TEXT NOT NULL, " +
                        UserParticipationEntry.COLUMN_GROUP_ADMINISTRATOR + " INTEGER NOT NULL, " +

                        // Primary Key Constraint
                        "PRIMARY KEY ( " + UserParticipationEntry.COLUMN_USER_ID + "," +
                        UserParticipationEntry.COLUMN_GROUP_ID + " ), " +

                        // Uniquer Constraint
                        "UNIQUE ( " + UserParticipationEntry.COLUMN_GROUP_ID + "," +
                        UserParticipationEntry.COLUMN_USER_ID + " ) ON CONFLICT REPLACE, " +

                        // Foreign Key Constraints
                        "FOREIGN KEY (" + UserParticipationEntry.COLUMN_USER_ID + ") REFERENCES " +
                        UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_USER_ID + "), " +

                        " FOREIGN KEY (" + UserParticipationEntry.COLUMN_GROUP_ID + ") REFERENCES " +
                        GroupEntry.TABLE_NAME + " (" + GroupEntry.COLUMN_GROUP_ID +
                        "));";


        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PARTICIPATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GROUP_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GROUP_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserParticipationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GroupDetailsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GroupEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
