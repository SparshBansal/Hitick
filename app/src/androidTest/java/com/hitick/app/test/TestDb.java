package com.hitick.app.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

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
public class TestDb extends AndroidTestCase {
    private static final String LOG_TAG = TestDb.class.getSimpleName();
    /*
        Testing Framework for Android
    */

    /*
        Test Case for running and testing database creation and functioning
    */

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(DATABASE_NAME);
        SQLiteDatabase database = new DatabaseHelper(mContext).getWritableDatabase();

        assertEquals(true, database.isOpen());
        database.close();
    }

    /*
        Test Case for testing insertion and reading from the database
    */
    public void testInsertReadDb() throws Throwable {

        /*
            If there is an error in the SQL Table Creation Strings, it will be thrown here in
            getWritableDatabase() method call
        */
        DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        /*
            Get the ContentValues object from the helper Method and insert the data into the database
            Assert to see if the row is actually inserted
        */
        ContentValues userValues = getUserContentValues();
        long userRowId = database.insert(UserEntry.TABLE_NAME, null, userValues);

        assertTrue(userRowId != -1);


        /*
            Read from the database to check if we got the row stored
            Using the helper method we Validate the Cursor Object
        */
        Cursor cursor = database.query(
                UserEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            validateCursor(cursor, userValues);
        }   else {
            fail("No rows returned");
        }


        /*
            Now that the user's table is working fine , we will now test the Group Table
            Get the ContentValues Object for the Group Table from the helper Method
            Insert the data into the database
            Assert if we the data was successfully inserted
        */

        ContentValues groupValues = getGroupContentValues();
        long groupRowId = database.insert(GroupEntry.TABLE_NAME, null, groupValues);

        assertTrue(groupRowId != -1);

        /*
            Read from the database to check if we got the row stored
            and Validate the Cursor object using the helper Method
        */
        cursor = database.query(
                GroupEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            validateCursor(cursor, groupValues);
        }   else {
            fail("No rows returned");
        }

        /*
            Lets Create a user Participation table using the method we declared in the Database Helper Class
            Get the ContentValues Object from the helper method and insert data in the database
            Assert to see if data was successfully inserted
        */
        ContentValues groupParticipationValues = getUserParticipationValues();
        long groupParticipationId = database.insert(UserParticipationEntry.TABLE_NAME, null, groupParticipationValues);

        assertTrue(groupParticipationId != -1);
        /*
            Read from the database to see if we got the data back
            Validate the Cursor using the helper Method
        */
        cursor = database.query(
                UserParticipationEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            validateCursor(cursor, groupParticipationValues);
        }   else {
            fail("No rows returned");
        }

        /*
            Lets Create a user Participation table using the method we declared in the Database Helper Class
            Get the ContentValues Object from the helper method and insert data in the database
            Assert to see if data was successfully inserted
        */
        ContentValues groupDetailsValues = getGroupDetailsValues();
        long groupDetailsRowId = database.insert(GroupDetailsEntry.TABLE_NAME, null, groupDetailsValues);

        assertTrue(groupDetailsRowId != -1);

        /*
            Now finally we query the database for the table and check if we got the rows inserted
            Validate the cursor using the helper method
        */
        cursor = database.query(
                GroupDetailsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            validateCursor(cursor, groupDetailsValues);
        }   else {
            fail("No rows returned");
        }

        /*
            All the tests until now have passed. We have created a fully functional Database with
            tables being created and populated at runtime using the helper methods
        */
    }

    /*Helper Method to get User Table Content Values*/
    ContentValues getUserContentValues() {
         /*
            Dummy data for inserting into our users table
        */
        final String testFirstName = "Sparsh";
        final String testLastName = "Bansal";
        final String testMobileNumber = "+919953652224";
        final String testPassword = "abc123";
        final String testEmail = "sparsh.bansal17895@gmail.com";
        long testUserId = 230002;

        /*
            Content Values object to put values into the database
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserEntry.COLUMN_USER_ID , testUserId);
        contentValues.put(UserEntry.COLUMN_FIRST_NAME, testFirstName);
        contentValues.put(UserEntry.COLUMN_LAST_NAME, testLastName);
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
        final int testGroupAdminId = 23089;
        final long testGroupId = 230234;

        /*
            Clear the content values object and put the data to be inserted in the group table
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put(GroupEntry.COLUMN_GROUP_NAME, testGroupName);
        contentValues.put(GroupEntry.COLUMN_GROUP_PASSWORD, testGroupPassword);
        contentValues.put(GroupEntry.COLUMN_GROUP_MEMBERS, testGroupMembers);
        contentValues.put(GroupEntry.COLUMN_GROUP_ADMIN_ID ,testGroupAdminId);
        contentValues.put(GroupEntry.COLUMN_GROUP_ID , testGroupId);
        return contentValues;
    }

    /*
        Helper method to get the Group Participation Table ContentValues Object
    */
    ContentValues getUserParticipationValues() {
         /*
             Now lets try and insert in the table
             Dummy data for group Participation Table
         */
        final long testGroupId = 230234;
        final int testGroupAdministrator = 1;
        final long testUserId = 230002;

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
    ContentValues getGroupDetailsValues(){
         /*Now we insert dummy data into our dynamically created Table*/
        final String testPollTopic = "Mass Bunk Tomorrow?";
        final int testPollOngoing = 1;
        final int testInFavor = 23;
        final int testOpposed = 16;
        final int testNotVoted = 1;
        final int testPollId = 1240;
        final String testPollDatetime = "20151512";
        final int testTimeLeft = 4500;
        final String testPollResult = "Mass Bunk Tomorrow!!";
        final long testGroupId = 230234;

        /*
            Clear the content Values Object and put the new values
        */
        ContentValues contentValues = new ContentValues();
        contentValues.put(GroupDetailsEntry.COLUMN_GROUP_ID , testGroupId);
        contentValues.put(GroupDetailsEntry.COLUMN_POLL_TOPIC, testPollTopic);
        contentValues.put(GroupDetailsEntry.COLUMN_POLL_ID , testPollId);
        contentValues.put(GroupDetailsEntry.COLUMN_POLL_DATETIME , testPollDatetime);
        contentValues.put(GroupDetailsEntry.COLUMN_STIPULATED_TIME , testTimeLeft);
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
