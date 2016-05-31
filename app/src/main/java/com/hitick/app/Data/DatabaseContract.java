package com.hitick.app.Data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

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
    public static int DATABASE_VERSION = 2;

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

        /* Helper method to encode the URI's which will be used to query our database */
        public static final Uri buildUsersUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
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
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_PARTICIPATION).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + PATH_USER_PARTICIPATION;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + PATH_USER_PARTICIPATION;


        /*Table name for the table...*/
        public static final String TABLE_NAME = "user_participation";

        /*
        *   Column for User id , referencing the users table
        * */
        public static final String COLUMN_USER_ID = "user_id";
        /*
            Column for id of groups he is part of..
        */
        public static final String COLUMN_GROUP_ID = "group_id";

        /*
            Column for storing an integer indicating whether the user is the administrator of the
            group
            1 -- TRUE -- Indicates that the user is the administrator
            0 -- FALSE -- Indicates that the user is a member of the group
        */
        public static final String COLUMN_GROUP_ADMINISTRATOR = "group_administrator";


        // Helper method to encode and decode the uris
        public static final Uri buildUserparticipationUri(long id){
            Uri returnUri = ContentUris.withAppendedId(CONTENT_URI , id);
            return returnUri;
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

        /*Column for group Admin's Id*/
        public static final String COLUMN_GROUP_ADMIN_ID = "group_admin_id";

        // Column for storing the unique group id assigned by the server
        public static final String COLUMN_GROUP_ID = "group_id";

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
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROUP_DETAILS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + PATH_GROUP_DETAILS;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + PATH_GROUP_DETAILS;

        /*Table name of the table*/
        public static final String TABLE_NAME = "group_details";

        /*Group id foreign key referencing groups table*/
        public static final String COLUMN_GROUP_ID = "group_id";

        /* Column for the Unique Id of each poll as assigned by the server dispatcher */
        public static final String COLUMN_POLL_ID = "poll_id";

        /*
            Column for question details as provided by the API
        */
        public static final String COLUMN_POLL_TOPIC = "poll_topic";

        /* Column for storing the datetime for the poll in UNIX Timestamp */
        public static final String COLUMN_POLL_DATETIME = "poll_datetime";

        /* Column for storing the last updated time left for the poll to expire */
        public static final String COLUMN_STIPULATED_TIME = "stipulated_time";

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

        // Helper method for encoding and decoding uris

        // method to construct the uri for poll data with group id
        public static final Uri buildGroupDetailsUri(long groupId) {
            return ContentUris.withAppendedId(CONTENT_URI , groupId);
        }

        //Method to get the group id from the uri
        public static final long getGroupIdFromUri (Uri uri){
            return ContentUris.parseId(uri);
        }
    }

    /**
     * Special class to handle join queries, and notifying change whenever required
     */
    public static final class Joins {

        // Base paths for the join queries
        public static final String PATH_JOIN = "join";
        public static final String PATH_USER_PARTICPIATION_WITH_GROUPS =
                "user_participation_with_groups";

        // Key for appending Table_Name parameters to end of the join queries
        private static final String KEY_USER_PARTICIPATION_USER_ID =
                "key_user_partcipation_table_name";

        // Base Uri for all the join queries
        public static final Uri JOIN_BASE_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_JOIN).build();

        // CONTENT-TYPE and ITEM-TYPE strings
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + PATH_JOIN + "/" + PATH_USER_PARTICPIATION_WITH_GROUPS;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + PATH_JOIN + "/" + PATH_USER_PARTICPIATION_WITH_GROUPS;

        // Method to create a join query uri between User-Participation-Table and Group-Table
        public static final Uri buildUserPartcipationWithGroupUri(long userId) {
            return JOIN_BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_USER_PARTICPIATION_WITH_GROUPS)
                    .appendQueryParameter(KEY_USER_PARTICIPATION_USER_ID, String.valueOf(userId))
                    .build();
        }

        // Method to decode get the Table Name from the join uri
        public static final String getUserIdFromUserParticipationWithGroupUri(Uri uri) {
            return uri.getQueryParameter(KEY_USER_PARTICIPATION_USER_ID);
        }
    }
}
