package com.hitick.app.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.hitick.app.Data.DatabaseContract;
import com.hitick.app.Data.DatabaseContract.GroupEntry;
import com.hitick.app.Data.DatabaseContract.UserEntry;
import com.hitick.app.Data.DatabaseContract.UserParticipationEntry;
import com.hitick.app.Data.DatabaseHelper;

import java.util.Map;
import java.util.Set;

import static com.hitick.app.Data.DatabaseContract.*;

/**
 * Created by Sparsha on 11/11/2015.
 */
public class TestProvider extends AndroidTestCase {
    private static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testDeleteAllRecords() {
        //Delete the group details table
        mContext.getContentResolver()
                .delete(GroupDetailsEntry.CONTENT_URI, null, null);

        //Delete the Group Participation Table
        mContext.getContentResolver()
                .delete(UserParticipationEntry.CONTENT_URI, null, null);

        //Delete the Groups Table
        mContext.getContentResolver()
                .delete(GroupEntry.CONTENT_URI, null, null);

        //Delete the User Table
        mContext.getContentResolver()
                .delete(UserEntry.CONTENT_URI, null, null);

        //Check if the records were actually deleted
        Cursor cursor = mContext.getContentResolver().query(UserEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(0, cursor.getCount());

        cursor = mContext.getContentResolver().query(GroupEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(0, cursor.getCount());

        cursor = mContext.getContentResolver().query(UserParticipationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(0, cursor.getCount());

        cursor = mContext.getContentResolver().query(GroupDetailsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(0, cursor.getCount());
    }

    public void testGetType() {

        /* Test the USER query first */
        // content://com.hitick.app/users
        String type = mContext.getContentResolver().getType(UserEntry.CONTENT_URI);
        // assert to see if we got the correct type
        assertEquals(UserEntry.CONTENT_TYPE, type);

        // content://com.hitick.app/users/23001
        type = mContext.getContentResolver().getType(UserEntry.buildUsersUri("23006"));
        // assert to see if we got the correct type
        assertEquals(UserEntry.CONTENT_ITEM_TYPE, type);

        /* Now we test the remaining queries */

        // content://com.hitick.app/user_participation/abc123_9953652224
        type = mContext.getContentResolver().getType(UserParticipationEntry.CONTENT_URI);
        assertEquals(UserParticipationEntry.CONTENT_TYPE, type);

        // content://com.hitick.app/groups
        type = mContext.getContentResolver().getType(GroupEntry.CONTENT_URI);
        assertEquals(GroupEntry.CONTENT_TYPE, type);

        //content://com.hitick.app/groups/1
        type = mContext.getContentResolver().getType(GroupEntry.buildGroupsUri("1"));
        assertEquals(GroupEntry.CONTENT_ITEM_TYPE, type);

        // content://com.hitick.app/group_details/A92K14_2K14A9
        type = mContext.getContentResolver().
                getType(GroupDetailsEntry.CONTENT_URI);
        assertEquals(GroupDetailsEntry.CONTENT_TYPE, type);
    }


    public void testInsertReadProvider() throws Throwable {

        /*
            If there is an error in the SQL Table Creation Strings, it will be thrown here in
            getWritableDatabase() method call
        */
        DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
        /*
            Get the ContentValues object from the helper Method and insert the data into the database
            Assert to see if the row is actually inserted
        */
        ContentValues userValues = getUserContentValues();
        Uri userReturnUri = mContext.getContentResolver().insert(UserEntry.CONTENT_URI, userValues);

        Log.d(LOG_TAG, "testInsertReadProvider: " + userReturnUri.toString());

        String userRowId = DatabaseContract.parseId(userReturnUri);
        assertTrue(!userRowId.isEmpty() && userRowId!=null);

        /*
            Read from the database using our Content Provider to check if we got the row stored
            Using the helper method we Validate the Cursor Object
        */
        // This Uri will return a list of items or directory of items
        Cursor cursor = mContext.getContentResolver().query(
                UserEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            validateCursor(cursor, userValues);
        } else {
            fail("No rows returned");
        }

        /*
            We also test our Query for single item Uri
            Note that the single user URI can be easily obtained from our Helper Methods in the contract class
        */
        cursor = mContext.getContentResolver().query(
                UserEntry.buildUsersUri("230002"),
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            validateCursor(cursor, userValues);
        } else {
            fail("No rows returned");
        }

        /*
            Now that the user's table is working fine , we will now test the Group Table
            Get the ContentValues Object for the Group Table from the helper Method
            Insert the data into the database
            Assert if we the data was successfully inserted
        */

        ContentValues groupValues = getGroupContentValues();
        Uri groupRowUri = mContext.getContentResolver().insert(GroupEntry.CONTENT_URI, groupValues);
        long groupRowId = ContentUris.parseId(groupRowUri);
        Log.d(LOG_TAG, "Group Row Id : " + groupRowId);
        assertTrue(groupRowId != -1);

        /*
            Read from the database to check if we got the row stored using our Content Provider
            and Validate the Cursor object using the helper Method
            USE CONTENT URI to get the entire table
            USE CONTENT URI WITH APPENDED ID to get a specific row
            Validate the cursor ...
        */
        cursor = mContext.getContentResolver().query(
                GroupEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            Log.d(LOG_TAG, "Count : " + cursor.getCount());
            validateCursor(cursor, groupValues);
        } else {
            fail("No rows returned");
        }

        cursor = mContext.getContentResolver().query(
                GroupEntry.buildGroupsUri("230234"),
                null,
                null,
                null,
                null
        );
        // Validate the cursor
        if (cursor.moveToFirst()) {
            validateCursor(cursor, groupValues);
        } else {
            fail("No rows returned");
        }

       /*
            Now that the group table is working perfectly its time to test the tables that are
            created dynamically at runtime

            First we create a User Participation Table with groupRowId as the key to the group
            Then we create a Group Details Table..

            Important Note --- Table Names cannot start with a digit and should not contain special
            characters as -,. etc
       */


       /*
            Lets Create a user Participation table using the method we declared in the Database Helper Class
            Get the ContentValues Object from the helper method and insert data in the database
            Assert to see if data was successfully inserted
       */

        ContentValues groupParticipationValues = getUserParticipationValues();
        Uri groupParticipationRowUri = mContext
                .getContentResolver()
                .insert(UserParticipationEntry.CONTENT_URI, groupParticipationValues);
        assertTrue(groupParticipationRowUri != null);

        /*
            Read from the database to see if we got the data back
            Validate the Cursor using the helper Method
        */
        cursor = mContext.getContentResolver().query(
                UserParticipationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            validateCursor(cursor, groupParticipationValues);
        } else {
            fail("No rows returned");
        }

        /*
            Lets Create a group Details table using the method we declared in the Database Helper Class
            Get the ContentValues Object from the helper method and insert data in the database
            Assert to see if data was successfully inserted
        */

        ContentValues groupDetailsValues = getGroupDetailsValues();
        Uri groupDetailsRowUri = mContext.
                getContentResolver().
                insert(GroupDetailsEntry.CONTENT_URI, groupDetailsValues);

        assertTrue(groupDetailsRowUri != null);

        /*
            Now finally we query the database for the table and check if we got the rows inserted
            Validate the cursor using the helper method
        */
        cursor = mContext.getContentResolver().query(
                GroupDetailsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            validateCursor(cursor, groupDetailsValues);
        } else {
            fail("No rows returned");
        }

        // Test our join actually works
        cursor = mContext.getContentResolver().query(
                Joins.buildUserPartcipationWithGroupUri("230002"),
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()){
            validateCursor(cursor,groupParticipationValues);
            validateCursor(cursor,groupValues);
        } else {
            fail("No rows returned");
        }

         /*
            All the tests until now have passed. We have created a fully functional Database with
            tables being created and populated at runtime using the helper methods and we now have a
            Content Provider
        */
    }

    /*Helper Method to get User Table Content Values*/
    public ContentValues getUserContentValues() {
         /*
            Dummy data for inserting into our users table
        */
        final String testUsername = "sparsh_bansal";
        final String testMobileNumber = "+919953652224";
        final String testPassword = "abc123";
        final String testEmail = "sparsh.bansal17895@gmail.com";
        String testUserId = "230002";

        /*
            Content Values object to put values into the database
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserEntry.COLUMN_USER_ID , testUserId);
        contentValues.put(UserEntry.COLUMN_USERNAME, testUsername);
        contentValues.put(UserEntry.COLUMN_MOBILE_NUMBER, testMobileNumber);
        contentValues.put(UserEntry.COLUMN_EMAIL, testEmail);
        contentValues.put(UserEntry.COLUMN_PASSWORD, testPassword);
        return contentValues;
    }

    /*
        Helper method to get the content Values Object for group table
    */
    ContentValues getGroupContentValues() {
         /*
            Dummy data for thr group table entry
        */
        final String testGroupName = "2K14A9";
        final String testGroupPassword = "A92K14";
        final int testGroupMembers = 30;
        final String testGroupAdminId = "2932434";
        final String testGroupId = "230234";

        /*
            Clear the content values object and put the data to be inserted in the group table
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put(GroupEntry.COLUMN_GROUP_NAME, testGroupName);
        contentValues.put(GroupEntry.COLUMN_GROUP_PASSWORD, testGroupPassword);
        contentValues.put(GroupEntry.COLUMN_GROUP_MEMBERS, testGroupMembers);
        contentValues.put(GroupEntry.COLUMN_GROUP_ADMIN_ID,testGroupAdminId);
        contentValues.put(GroupEntry.COLUMN_GROUP_ID, testGroupId);
        return contentValues;
    }


    /*
        Helper method to get the Group Participation Table ContentValues Object
    */
    public ContentValues getUserParticipationValues() {
         /*
             Now lets try and insert in the table
             Dummy data for group Participation Table
         */
        final String testGroupId = "230234";
        final int testGroupAdministrator = 1;
        final String testUserId = "230002";

        /*Now clear the content values object and put the new data*/
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserParticipationEntry.COLUMN_USER_ID , testUserId);
        contentValues.put(UserParticipationEntry.COLUMN_GROUP_ID, testGroupId);
        contentValues.put(UserParticipationEntry.COLUMN_GROUP_ADMINISTRATOR, testGroupAdministrator);

        return contentValues;
    }
    /*
          Helper method to get the Content Values object for Group Details Object
      */
    public ContentValues getGroupDetailsValues() {
         /*Now we insert dummy data into our dynamically created Table*/
        final String testGroupId = "230234";
        final String testPollTopic = "Mass Bunk Tomorrow?";
        final int testPollOngoing = 1;
        final int testInFavor = 23;
        final int testOpposed = 16;
        final int testNotVoted = 1;
        final String testPollId = "1240";
        final String testPollDatetime = "20151512";
        final int testTimeLeft = 4500;
        final String testPollResult = "Mass Bunk Tomorrow!!";

        /*
            Clear the content Values Object and put the new values
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put(GroupDetailsEntry.COLUMN_GROUP_ID , testGroupId);
        contentValues.put(GroupDetailsEntry.COLUMN_POLL_TOPIC, testPollTopic);
        contentValues.put(GroupDetailsEntry.COLUMN_POLL_ID, testPollId);
        contentValues.put(GroupDetailsEntry.COLUMN_POLL_DATETIME, testPollDatetime);
        contentValues.put(GroupDetailsEntry.COLUMN_STIPULATED_TIME, testTimeLeft);
        contentValues.put(GroupDetailsEntry.COLUMN_POLL_ONGOING, testPollOngoing);
        contentValues.put(GroupDetailsEntry.COLUMN_IN_FAVOR, testInFavor);
        contentValues.put(GroupDetailsEntry.COLUMN_OPPOSED, testOpposed);
        contentValues.put(GroupDetailsEntry.COLUMN_NOT_VOTED, testNotVoted);
        contentValues.put(GroupDetailsEntry.COLUMN_POLL_RESULT, testPollResult);

        return contentValues;
    }

    /*
        Helper Method to Validate the Cursor Object using the ContentValues object's read
        properties. Each element in the cursor will be matched with the corresponding value in the
        contentValues Object
    */
    void validateCursor(Cursor cursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int columnIdx = cursor.getColumnIndex(columnName);

            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, cursor.getString(columnIdx));
        }
    }
}
