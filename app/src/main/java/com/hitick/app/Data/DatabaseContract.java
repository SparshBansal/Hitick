package com.hitick.app.Data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sparsha on 11/10/2015.
 */
/*
    Contract Class for our Database Model
*/
public class DatabaseContract {
    /*
        Content Authority for our Content Provider Interface. It is the package name
        backwards. It will be used to build a base Content Uri that will be appended by
        suitable parameters to allow querying our database
    */
    public static final String CONTENT_AUTHORITY = "com.hitick.app";

    /* Using the content authority we now build the base Content Uri */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /* Possible paths to our tables */
    public static final String PATH_USERS = "users";
    public static final String PATH_GROUPS = "groups";
    public static final String PATH_USER_PARTICIPATION = "user_participation";
    public static final String PATH_GROUP_DETAILS = "group_details";


    /* Details for the database Helper Class */
    public static final String DATABASE_NAME = "database_hitick";
    public static int DATABASE_VERSION = 1;

    /*
        User Entry -- User Table -- Details of Users
    */
    public static final class UserEntry implements BaseColumns {

        /* Content Uri pointing to the base Users Table*/
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + PATH_USERS;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + PATH_USERS;

        /*
            Table Name of our User's Table
        */
        public static final String TABLE_NAME = "users";

        /*
           Column for User's First Name
        */
        public static final String COLUMN_FIRST_NAME = "first_name";

        /*
            Column for User's Last Name
        */
        public static final String COLUMN_LAST_NAME = "last_name";

        // User's Mobile Number as provided by the user himself
        public static final String COLUMN_MOBILE_NUMBER = "mobile_number";

        /*User's Password as entered by him
        Used for valid Mobile Number Confirmation */
        public static final String COLUMN_PASSWORD = "password";

        /*User's valid Email Id
        Used for Sending Confirmation to the user*/
        public static final String COLUMN_EMAIL = "email_id";

        // Unique Id assigned to each user by the server
        public static final String COLUMN_USER_ID = "user_id";
        /*
        Column for User's group participation table
        It will point to table containing the names of the groups the user is a part of
        referenced by foreign key to the group details table
        */
        public static final String COLUMN_USER_GROUP_PARTICIPATION_TABLE = "user_group_participation";

        /* Helper method to encode the URI's which will be used to query our database */

        //Method to build the URI which will return the item from the user table on
        //being given the mobileNumber and password
        public static final Uri buildContentUri(String mobileNumber, String password) {
            return CONTENT_URI.buildUpon().
                    appendQueryParameter(COLUMN_MOBILE_NUMBER, mobileNumber).
                    appendQueryParameter(COLUMN_PASSWORD, password).
                    build();
        }

        public static final Uri buildUsersUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        /* Helper Methods to decode the Uri's */

        //Method to decode the mobileNumber from the URI
        public static String getMobileNumberFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_MOBILE_NUMBER);
        }

        //Method to decode the Password from the URI
        public static String getPasswordFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_PASSWORD);
        }
    }

    /*
        Table entry for creating a Group Participation Table which will store the id's of groups
        he is a part of..
    */
    public static final class UserParticipationEntry implements BaseColumns {
        /*
            Content Uri pointing to the tables. Unlike the groups and users table, since this table
            will be created at runtime therefore we will append the name of the table to content
            Uri
        */
        public static final Uri SUB_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_PARTICIPATION).build();

        public static final String SUB_CONTENT_TYPE = "vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + PATH_USER_PARTICIPATION;
        public static final String SUB_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + PATH_USER_PARTICIPATION;


        /*
            It will not have a table name as this table will be created
            dynamically at runtime.
            The details of the groups that the user is a part will be parsed at the time of
            login and put into this table
        */

        /*
            Column for id of groups he is part of..
        */
        public static final String COLUMN_GROUP_KEY = "group_key";

        /*
            Column for storing an integer indicating whether the user is the administrator of the
            group
            1 -- TRUE -- Indicates that the user is the administrator
            0 -- FALSE -- Indicates that the user is a member of the group
        */

        public static final String COLUMN_GROUP_ADMINISTRATOR = "group_administrator";

        /*
            Helper Encoding function which will create the appropriate URI from the SUB_CONTENT_URI
            They are required since the actual CONTENT_URI can only be built after the name of
            the table is known and that happens only at runtime
        */

        //Method to return/encode the content uri from SUB_CONTENT_URI
        public static final Uri buildContentUri(String tableName) {
            return SUB_CONTENT_URI.buildUpon().appendPath(tableName).build();
        }

    

        /* Decoder function used to abstract information from the URIs */

        //Function to determine the Table Name from the given URI
        public static String getTableNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    /*
        Group Entry -- Used to store Group Details
    */
    public static final class GroupEntry implements BaseColumns {
        /* Content Uri which will be pointing to the base Group Table */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROUPS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + PATH_GROUPS;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + PATH_GROUPS;

        /*Table Name for Groups Table*/
        public static final String TABLE_NAME = "groups";

        /*Column for Group Name*/
        public static final String COLUMN_GROUP_NAME = "group_name";

        /*Column For Group's Password*/
        public static final String COLUMN_GROUP_PASSWORD = "group_password";

        /*Column for Number of Member in the group*/
        public static final String COLUMN_GROUP_MEMBERS = "group_members";

        /*Column for group administrators Mobile Number*/
        public static final String COLUMN_GROUP_ADMINISTRATOR_MOBILE = "group_administrator_mobile";

        /*Column for group administrators name*/
        public static final String COLUMN_GROUP_ADMINISTRATOR_NAME = "group_administrator_name";

        // Column for storing the unique group id assigned by the server
        public static final String COLUMN_GROUP_ID = "group_id";

        /**
         * Column for group's poll history and details
         * It will point to another table which will contain the history of the polls Conducted
         * till now.
         * It will also contain the details of current ongoing polls and previously held polls
         */

        public static final String COLUMN_GROUP_DETAILS = "group_details";

        /* Helper encoding functions that will be used to create the specific URI's for querying */

        //Function for encoding the URI used to query for the group details
        public static Uri buildGroupsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
       Table Entries for the Group Details Table which contains the poll history
    */
    public static final class GroupDetailsEntry implements BaseColumns {

        /*
           Content Uri pointing to the tables. Unlike the groups and users table, since this table
           will be created at runtime therefore we will append the name of the table to content
           Uri
       */
        public static final Uri SUB_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROUP_DETAILS).build();

        public static final String SUB_CONTENT_TYPE = "vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + PATH_GROUP_DETAILS;
        public static final String SUB_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + PATH_GROUP_DETAILS;

        /*
            We won't have a Table Name defined in the base Contract because it
            will be dynamically generated at runtime to ensure that duplicate tables may not exist
        */

        /* Column for the Unique Id of each poll as assigned by the server dispatcher */
        public static final String COLUMN_POLL_ID = "poll_id";

        /*
            Column for question details as provided by the API
        */
        public static final String COLUMN_POLL_TOPIC = "poll_topic";

        /* Column for storing the datetime for the poll in UNIX Timestamp */
        public static final String COLUMN_POLL_DATETIME = "poll_datetime";

        /* Column for storing the last updated time left for the poll to expire */
        public static final String COLUMN_TIME_LEFT = "time_left";

        /*
            Column for storing a boolean indicating whether the poll is ongoing
            or still in progress
            TRUE -- Poll is still in progress
            FALSE -- Poll has been conducted and result is available
        */
        public static final String COLUMN_POLL_ONGOING = "poll_ongoing";

        /*
            Column for storing number of member who voted in favor of the poll
        */
        public static final String COLUMN_IN_FAVOR = "in_favor";

        /*
            Column for storing number of members who voted against the poll topic
        */
        public static final String COLUMN_OPPOSED = "opposed";

        /*
            Column for storing number of members who didn't vote
        */
        public static final String COLUMN_NOT_VOTED = "not_voted";

        /*
            Column for storing the result of the poll which has been conducted as taken by the administrator
        */
        public static final String COLUMN_POLL_RESULT = "poll_result";

        /* Helper function that will be used to encode the actual Uri for querying */

        //Function to create the URI Pointing to the appropriate Group Details table
        public static final Uri buildContentUri(String tableName) {
            return SUB_CONTENT_URI.buildUpon().appendPath(tableName).build();
        }

        /* Decoder function used to abstract information from the URIs */

        //Function to determine the Table Name from the given URI
        public static String getTableNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
