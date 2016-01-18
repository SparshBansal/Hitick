package com.hitick.app.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import android.util.StringBuilderPrinter;

/**
 * Created by Sparsha on 11/13/2015.
 */

public class DataProvider extends ContentProvider {

    private static final String LOG_TAG = DataProvider.class.getSimpleName();
    private static DatabaseHelper mDatabaseHelper;

    //The URI Matcher used by this Content Provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // SQLiteQueryBuilder for the join query
    private static SQLiteQueryBuilder mUserParticipationWithGroupQueryBuilder;


    /*
        Integer Constants for the URI Matcher Class , they will be used to determine
        the type of URI using the Uri Matcher Object
    */
    private static final int USERS = 100;
    private static final int USERS_ID = 101;
    private static final int USER_GROUP_PARTICIPATION = 102;
    private static final int USER_PARTICIPATION_WITH_GROUP = 103;
    private static final int GROUPS = 104;
    private static final int GROUPS_ID = 105;
    private static final int GROUP_DETAILS = 106;


    /*
        We Initialize the database helper in the onCreate() Method and return true for
        successful creation
    */
    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }


    /*
        Query is the most important method that needs to be implemented.
        It is particularly easy to implement Query on the top of an SQLite Database.
    */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Using the URI Matcher class we determine the type of Query to be performed
        int match = sUriMatcher.match(uri);

        Cursor retCursor = null;

        switch (match) {
            case USERS:
                retCursor = mDatabaseHelper.getWritableDatabase().query(
                        DatabaseContract.UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case USERS_ID:
                final long userId = ContentUris.parseId(uri);
                if (userId != -1) {
                    final String SELECTION = DatabaseContract.UserEntry.COLUMN_USER_ID + "=?";
                    final String[] SELECTION_ARGS = new String[]{String.valueOf(userId)};
                    retCursor = mDatabaseHelper.getWritableDatabase().query(
                            DatabaseContract.UserEntry.TABLE_NAME,
                            projection,
                            SELECTION,
                            SELECTION_ARGS,
                            null,
                            null,
                            sortOrder
                    );
                } else {
                    Log.d(LOG_TAG, "No Appended Id found");
                }
                break;

            case USER_GROUP_PARTICIPATION:
                // first get the table name from the URI using the helper method
                String tableName = DatabaseContract.UserParticipationEntry.getTableNameFromUri(uri);

                // Now query the database
                retCursor = mDatabaseHelper.getWritableDatabase().query(
                        tableName,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case USER_PARTICIPATION_WITH_GROUP:
                tableName = DatabaseContract.Joins.getTableNameFromUserParticipationWithGroupUri(uri);
                retCursor = queryUserParticipationWithGroup(tableName, projection, selection, selectionArgs, sortOrder);
                break;

            case GROUPS:
                retCursor = mDatabaseHelper.getWritableDatabase().query(
                        DatabaseContract.GroupEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case GROUPS_ID:
                retCursor = mDatabaseHelper.getWritableDatabase().query(
                        DatabaseContract.GroupEntry.TABLE_NAME,
                        projection,
                        DatabaseContract.GroupEntry._ID + " = " + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;


            case GROUP_DETAILS:
                // First get the table name from the URI
                String groupDetailsTableName = DatabaseContract.GroupDetailsEntry.
                        getTableNameFromUri(uri);

                // Now query the database
                retCursor = mDatabaseHelper.getWritableDatabase().query(
                        groupDetailsTableName,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                Log.d(LOG_TAG, "No Uri Match found!!");
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        The getType() method returns the mime type of the result set generated by the uri
        We first match the URI with defined types and then return the appropriate type

        Note ** All the mime type prefixes have already been defined in the Database Contract Class
    */
    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            /*

            */
            case USERS:
                return DatabaseContract.UserEntry.CONTENT_TYPE;
            case USERS_ID:
                return DatabaseContract.UserEntry.CONTENT_ITEM_TYPE;
            case USER_GROUP_PARTICIPATION:
                return DatabaseContract.UserParticipationEntry.SUB_CONTENT_TYPE;
            case USER_PARTICIPATION_WITH_GROUP:
                return DatabaseContract.Joins.CONTENT_TYPE;
            case GROUPS:
                return DatabaseContract.GroupEntry.CONTENT_TYPE;
            case GROUPS_ID:
                return DatabaseContract.GroupEntry.CONTENT_ITEM_TYPE;
            case GROUP_DETAILS:
                return DatabaseContract.GroupDetailsEntry.SUB_CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri : " + uri);
        }
    }

    // Required to insert in the database and we allow insertion in all the four tables
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        switch (match) {

            case USERS:
                long _id = mDatabaseHelper
                        .getWritableDatabase()
                        .insert(DatabaseContract.UserEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = DatabaseContract.UserEntry.buildUsersUri(_id);
                else
                    throw new SQLException("Failed to insert row ");
                break;

            case USER_GROUP_PARTICIPATION:
                String tableName = DatabaseContract.UserParticipationEntry.getTableNameFromUri(uri);
                _id = mDatabaseHelper
                        .getWritableDatabase()
                        .insert(tableName, null, contentValues);
                if (_id > 0) {
                    returnUri = uri;
                    // Notify change on the join uri
                    getContext().getContentResolver().notifyChange(
                            DatabaseContract.Joins.JOIN_BASE_CONTENT_URI, null);
                } else
                    throw new SQLException("Failed to insert row ");
                break;

            case GROUPS:
                _id = mDatabaseHelper
                        .getWritableDatabase()
                        .insert(DatabaseContract.GroupEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = DatabaseContract.GroupEntry.buildGroupsUri(_id);
                    // Notify change on the join uri
                    getContext().getContentResolver().notifyChange(
                            DatabaseContract.Joins.JOIN_BASE_CONTENT_URI, null);
                } else
                    throw new SQLException("Failed to insert row ");
                break;

            case GROUP_DETAILS:
                _id = mDatabaseHelper
                        .getWritableDatabase()
                        .insert(DatabaseContract.GroupDetailsEntry.getTableNameFromUri(uri), null, contentValues);
                if (_id > 0)
                    returnUri = uri;
                else
                    throw new SQLException("Failed to insert row ");
                break;
            default:
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    // Required to delete from the database , We will allow delete from all the tables
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int returnRows = 0;
        switch (match) {
            case USERS:
                returnRows = mDatabaseHelper
                        .getWritableDatabase()
                        .delete(DatabaseContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GROUPS:
                returnRows = mDatabaseHelper
                        .getWritableDatabase()
                        .delete(DatabaseContract.GroupEntry.TABLE_NAME, selection, selectionArgs);
                // Notify change on the join uri
                getContext().getContentResolver().notifyChange(
                        DatabaseContract.Joins.JOIN_BASE_CONTENT_URI, null);

                break;
            case USER_GROUP_PARTICIPATION:
                returnRows = mDatabaseHelper
                        .getWritableDatabase()
                        .delete(DatabaseContract.UserParticipationEntry.getTableNameFromUri(uri), selection, selectionArgs);
                // Notify change on the join uri
                getContext().getContentResolver().notifyChange(
                        DatabaseContract.Joins.JOIN_BASE_CONTENT_URI, null);
                break;
            case GROUP_DETAILS:
                returnRows = mDatabaseHelper
                        .getWritableDatabase()
                        .delete(DatabaseContract.GroupDetailsEntry.getTableNameFromUri(uri), selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Operation");
        }

        // Null in the selection clause deletes all the rows...
        if (selection == null || returnRows != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnRows;
    }


    /*
        Required to update the database
        Since we will be updating quite a lot in the groups table and group details table , we
        need to implement this method
    */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int returnRows = 0;
        switch (match) {
            case USERS:
                returnRows = mDatabaseHelper
                        .getWritableDatabase()
                        .update(DatabaseContract.UserEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case GROUPS:
                returnRows = mDatabaseHelper
                        .getWritableDatabase()
                        .update(DatabaseContract.GroupEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                // Notify change on the join uri
                getContext().getContentResolver().notifyChange(
                        DatabaseContract.Joins.JOIN_BASE_CONTENT_URI,null);
                break;
            case USER_GROUP_PARTICIPATION:
                returnRows = mDatabaseHelper
                        .getWritableDatabase()
                        .update(DatabaseContract.UserParticipationEntry.getTableNameFromUri(uri), contentValues, selection, selectionArgs);
                // Notify change on the join uri
                getContext().getContentResolver().notifyChange(
                        DatabaseContract.Joins.JOIN_BASE_CONTENT_URI,null);
                break;
            case GROUP_DETAILS:
                returnRows = mDatabaseHelper
                        .getWritableDatabase()
                        .update(DatabaseContract.GroupDetailsEntry.getTableNameFromUri(uri), contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Operation");
        }

        // Null in the selection clause deletes all the rows...
        if (returnRows != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnRows;
    }

    /*
        Helper method to build the URI Matcher object that maps each of the URIs to their respective
        Constants
    */
    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String CONTENT_AUTHORITY = DatabaseContract.CONTENT_AUTHORITY;

        /* We now add each of the Uri types to the matcher and assign them to the specific code
         So whenever a query will be performed the URI Matcher will return the code and
         we can do the respective query */

        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.PATH_USERS, USERS);
        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.PATH_USERS + "/#", USERS_ID);
        /*
            Since every group participation query URI will have the table name attached with the
            SUB_CONTENT_URI , therefore we match it with SUB_CONTENT_URI plus the table name.
        */
        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.PATH_USER_PARTICIPATION + "/*", USER_GROUP_PARTICIPATION);

        /* Special Uri for join between user-participation and groups table */
        uriMatcher.addURI(CONTENT_AUTHORITY,
                DatabaseContract.Joins.PATH_JOIN +
                        "/" + DatabaseContract.Joins.PATH_USER_PARTICPIATION_WITH_GROUPS,
                USER_PARTICIPATION_WITH_GROUP);

        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.PATH_GROUPS, GROUPS);
        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.PATH_GROUPS + "/#", GROUPS_ID);

        /*
            Since every group details query URI will have the table name attached with the
            SUB_CONTENT_URI , therefore we match it with SUB_CONTENT_URI plus the table name.
        */
        uriMatcher.addURI(CONTENT_AUTHORITY, DatabaseContract.PATH_GROUP_DETAILS + "/*", GROUP_DETAILS);


        return uriMatcher;
    }

    /* Helper method to query the datbase for the join between the User Participation and Group Table*/
    private static final Cursor queryUserParticipationWithGroup(String tableName,
                                                                String[] projection,
                                                                String selection,
                                                                String[] selectionArgs,
                                                                String sortOrder) {

        mUserParticipationWithGroupQueryBuilder = new SQLiteQueryBuilder();
        mUserParticipationWithGroupQueryBuilder.setTables(tableName + "INNER JOIN " +
                DatabaseContract.GroupEntry.TABLE_NAME + " ON " +
                DatabaseContract.UserParticipationEntry.COLUMN_GROUP_KEY + " = " +
                DatabaseContract.GroupEntry._ID
        );
        return mUserParticipationWithGroupQueryBuilder.query(
                mDatabaseHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
}
