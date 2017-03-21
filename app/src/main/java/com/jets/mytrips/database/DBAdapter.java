package com.jets.mytrips.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jets.mytrips.beans.Note;
import com.jets.mytrips.beans.Trip;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by rocke on 3/14/2017.
 */

public class DBAdapter {

    DatabaseHelper helper;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        // Logcat tag
        private static final String LOG = "DatabaseHelper";

        // Database Version
        private static final int DATABASE_VERSION = 5;

        // Database Name
        private static final String DATABASE_NAME = "trips.db";

        // Table Names
        private static final String TABLE_TRIPS = "trips";
        private static final String TABLE_NOTES = "notes";

        // TRIPS Table - column names
        private static final String ID = "id";
        private static final String USER_ID = "userId";
        private static final String TRIP_NAME = "name";
        private static final String TRIP_START_DEST = "start";
        private static final String TRIP_START_X = "startX";
        private static final String TRIP_START_Y = "startY";
        private static final String TRIP_END_DEST = "end";
        private static final String TRIP_END_X = "endX";
        private static final String TRIP_END_Y = "endY";
        private static final String TRIP_DATE = "date";
        private static final String TRIP_TIME = "time";
        private static final String TRIP_NOTES = "notes";
        private static final String TRIP_STATUS = "status";
        private static final String TRIP_IS_DONE = "done";
        private static final String TRIP_IMAGE = "image";
        private static final String TRIP_ALARM_ID = "alarmId";

        // NOTES Table - column names
        private static final String TRIP_ID = "tripId";
        private static final String NOTE = "note";

        // Trips table create statement
        private static final String CREATE_TABLE_TRIPS = "CREATE TABLE " + TABLE_TRIPS
                + "(" + ID + " TEXT, " + USER_ID + " INTEGER, " + TRIP_NAME + " TEXT, "
                + TRIP_START_DEST + " TEXT, " + TRIP_START_X   + " DOUBLE, " + TRIP_START_Y  + " DOUBLE, " + TRIP_END_DEST + " TEXT, "
                + TRIP_END_X + " DOUBLE, " + TRIP_END_Y + " DOUBLE, " + TRIP_DATE + " TEXT, " + TRIP_TIME + " TEXT, " + TRIP_NOTES +
                " TEXT, " + TRIP_STATUS + " TEXT, " + TRIP_IS_DONE + " INTEGER," + TRIP_IMAGE + " TEXT, " + TRIP_ALARM_ID + " INTEGER)";

        // Notes table create statement
        private static final String CREATE_TABLE_NOTES = "CREATE TABLE " + TABLE_NOTES
                + "(" + ID + " TEXT," +  TRIP_ID + " TEXT, " + NOTE + " TEXT, " +
                " FOREIGN KEY (" + TRIP_ID + ") REFERENCES "+ TABLE_TRIPS + "(" + ID + "))";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // creating required tables
            db.execSQL(CREATE_TABLE_TRIPS);
            db.execSQL(CREATE_TABLE_NOTES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // on upgrade drop older tables
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);

            // create new tables
            onCreate(db);
        }
    }

    public DBAdapter(Context context){
        helper = new DatabaseHelper(context);
    }

    /****************TRIP OPERATIONS**********************/

    public long addTrip(Trip trip) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //id is auto incremented
        values.put(DatabaseHelper.USER_ID, trip.getUserId());
        values.put(DatabaseHelper.TRIP_NAME, trip.getName());
        values.put(DatabaseHelper.TRIP_START_DEST, trip.getStart());
        values.put(DatabaseHelper.TRIP_START_X, trip.getStartX());
        values.put(DatabaseHelper.TRIP_START_Y, trip.getStartY());
        values.put(DatabaseHelper.TRIP_END_DEST, trip.getEnd());
        values.put(DatabaseHelper.TRIP_END_X, trip.getEndX());
        values.put(DatabaseHelper.TRIP_END_Y, trip.getEndY());
        values.put(DatabaseHelper.TRIP_DATE, trip.getDate());
        values.put(DatabaseHelper.TRIP_TIME, trip.getTime());
        values.put(DatabaseHelper.TRIP_STATUS, trip.getStatus());
        values.put(DatabaseHelper.TRIP_IS_DONE, trip.getDone());
        values.put(DatabaseHelper.TRIP_IMAGE, trip.getImage());
        values.put(DatabaseHelper.TRIP_ALARM_ID, trip.getAlarmId());

        // insert row
        long trip_id = db.insert(DatabaseHelper.TABLE_TRIPS, null, values);

        return trip_id;
    }

    public ArrayList<Trip> getUserTrips(int userId){

        ArrayList<Trip> trips = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_TRIPS;

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        c.moveToFirst();
        while (c.moveToNext()) {
            Trip trip = new Trip();
            trip.setId(c.getString((c.getColumnIndex(DatabaseHelper.ID))));
            trip.setUserId((c.getInt(c.getColumnIndex(DatabaseHelper.USER_ID))));
            trip.setName(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_NAME)));
            trip.setStart(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_START_DEST)));
            trip.setStartX(c.getDouble(c.getColumnIndex(DatabaseHelper.TRIP_START_X)));
            trip.setStartY(c.getDouble(c.getColumnIndex(DatabaseHelper.TRIP_START_Y)));
            trip.setEnd(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_END_DEST)));
            trip.setEndX(c.getDouble(c.getColumnIndex(DatabaseHelper.TRIP_END_X)));
            trip.setEndY(c.getDouble(c.getColumnIndex(DatabaseHelper.TRIP_END_Y)));
            trip.setDate(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_DATE)));
            trip.setTime(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_TIME)));
            trip.setStatus(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_STATUS)));
            trip.setDone(c.getInt(c.getColumnIndex(DatabaseHelper.TRIP_IS_DONE)));
            trip.setImage(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_IMAGE)));
            trip.setAlarmId(c.getInt(c.getColumnIndex(DatabaseHelper.TRIP_ALARM_ID)));

            trips.add(trip);
        }

        return trips;
    }


    public int updateTrip(Trip trip) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //id is auto incremented
        values.put(DatabaseHelper.USER_ID, trip.getUserId());
        values.put(DatabaseHelper.TRIP_NAME, trip.getName());
        values.put(DatabaseHelper.TRIP_START_DEST, trip.getStart());
        values.put(DatabaseHelper.TRIP_START_X, trip.getStartX());
        values.put(DatabaseHelper.TRIP_START_Y, trip.getStartY());
        values.put(DatabaseHelper.TRIP_END_DEST, trip.getEnd());
        values.put(DatabaseHelper.TRIP_END_X, trip.getEndX());
        values.put(DatabaseHelper.TRIP_END_Y, trip.getEndY());
        values.put(DatabaseHelper.TRIP_DATE, trip.getDate());
        values.put(DatabaseHelper.TRIP_TIME, trip.getTime());
        values.put(DatabaseHelper.TRIP_STATUS, trip.getStatus());
        values.put(DatabaseHelper.TRIP_IS_DONE, trip.getDone());
        values.put(DatabaseHelper.TRIP_IMAGE, trip.getImage());
        values.put(DatabaseHelper.TRIP_ALARM_ID, trip.getAlarmId());

        // updating row
        return db.update(DatabaseHelper.TABLE_TRIPS, values, DatabaseHelper.ID + " = ?",
                new String[] { String.valueOf(trip.getId()) });
    }

    public void deleteTrip(int trip_id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_TRIPS, DatabaseHelper.ID + " = ?",
                new String[] { String.valueOf(trip_id) });
    }

    /****************NOTES OPERATIONS**********************/

    public long addNote(Note note) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //id is auto incremented
        values.put(DatabaseHelper.ID, note.getId());
        values.put(DatabaseHelper.TRIP_ID, note.getTripId());
        values.put(DatabaseHelper.NOTE, note.getNote());

        // insert row
        long note_id = db.insert(DatabaseHelper.TABLE_NOTES, null, values);

        return note_id;
    }

    public ArrayList<Note> getTripNotes(String tripId){

        ArrayList<Note> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_NOTES+" where id ="+tripId;

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        c.moveToFirst();
        while (c.moveToNext()) {
            Note note = new Note();
            note.setId(c.getString((c.getColumnIndex(DatabaseHelper.ID))));
            note.setTripId((c.getString(c.getColumnIndex(DatabaseHelper.TRIP_ID))));
            note.setNote(c.getString(c.getColumnIndex(DatabaseHelper.NOTE)));

            notes.add(note);
        }

        return notes;
    }


    public int updateNote(Note note) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //id is auto incremented
        values.put(DatabaseHelper.NOTE, note.getNote());

        // updating row
        return db.update(DatabaseHelper.TABLE_NOTES, values, DatabaseHelper.ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
    }

    public void deleteNote(int note_id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.ID + " = ?",
                new String[] { String.valueOf(note_id) });
    }

    /****************CLOSE CONNECTION**********************/

    public void closeDB() {
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /****************LOGOUT**********************/

    public void logout(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from "+ helper.TABLE_TRIPS);
        db.execSQL("delete from "+ helper.TABLE_NOTES);
    }

    /****************GFENERATE ID**********************/

    //call it when creating a new object
    public String generateId(){
        return UUID.randomUUID().toString().substring(10);
    }
}
