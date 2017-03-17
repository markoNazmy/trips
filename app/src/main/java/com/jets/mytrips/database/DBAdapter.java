package com.jets.mytrips.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jets.mytrips.beans.Note;
import com.jets.mytrips.beans.Trip;

import java.util.ArrayList;



/**
 * Created by rocke on 3/14/2017.
 */

public class DBAdapter {

    DatabaseHelper helper;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        // Logcat tag
        private static final String LOG = "DatabaseHelper";

        // Database Version
        private static final int DATABASE_VERSION = 1;

        // Database Name
        private static final String DATABASE_NAME = "trips.db";

        // Table Names
        private static final String TABLE_TRIPS = "trips";
        private static final String TABLE_NOTES = "notes";

        // TRIPS Table - column names
        private static final String ID = "id";
        private static final String USER_ID = "userId";
        private static final String TRIP_START_DEST = "startDest";
        private static final String TRIP_START_COORD = "startCoord";
        private static final String TRIP_END_DEST = "endDest";
        private static final String TRIP_END_COORD = "endCoord";
        private static final String TRIP_DATE = "date";
        private static final String TRIP_TIME = "time";
        private static final String TRIP_NOTES = "notes";
        private static final String TRIP_STATUS = "status";
        private static final String TRIP_IS_DONE = "done";

        // NOTES Table - column names
        private static final String TRIP_ID = "tripId";
        private static final String NOTE = "note";

        // Trips table create statement
        private static final String CREATE_TABLE_TRIPS = "CREATE TABLE " + TABLE_TRIPS
                + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + USER_ID + " INTEGER,"
                + TRIP_START_DEST + " TEXT, " + TRIP_START_COORD   + " TEXT," + TRIP_END_DEST + " TEXT,"
                + TRIP_END_COORD + " TEXT," + TRIP_DATE + " TEXT," + TRIP_TIME + " TEXT," + TRIP_NOTES +
                " TEXT," + TRIP_STATUS + " TEXT," + TRIP_IS_DONE + " INTEGER" + ")";

        // Notes table create statement
        private static final String CREATE_TABLE_NOTES = "CREATE TABLE " + TABLE_NOTES
                + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +  TRIP_ID + " INTEGER, " + NOTE + " TEXT)";

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
        values.put(DatabaseHelper.TRIP_START_DEST, trip.getStart());
        values.put(DatabaseHelper.TRIP_START_COORD, trip.getStartCoord());
        values.put(DatabaseHelper.TRIP_END_DEST, trip.getEnd());
        values.put(DatabaseHelper.TRIP_END_COORD, trip.getEndCoord());
        values.put(DatabaseHelper.TRIP_DATE, trip.getDate());
        values.put(DatabaseHelper.TRIP_TIME, trip.getTime());
        values.put(DatabaseHelper.TRIP_STATUS, trip.getStatus());
        values.put(DatabaseHelper.TRIP_IS_DONE, trip.isDone());

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
            trip.setId(c.getInt((c.getColumnIndex(DatabaseHelper.ID))));
            trip.setUserId((c.getInt(c.getColumnIndex(DatabaseHelper.USER_ID))));
            trip.setStart(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_START_DEST)));
            trip.setStartCoord(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_START_COORD)));
            trip.setEnd(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_END_DEST)));
            trip.setEndCoord(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_END_COORD)));
            trip.setDate(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_DATE)));
            trip.setTime(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_TIME)));
            trip.setStatus(c.getString(c.getColumnIndex(DatabaseHelper.TRIP_STATUS)));
            trip.setDone(c.getInt(c.getColumnIndex(DatabaseHelper.TRIP_IS_DONE)));

            trips.add(trip);
        }

        return trips;
    }


    public int updateTrip(Trip trip) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //id is auto incremented
        values.put(DatabaseHelper.USER_ID, trip.getUserId());
        values.put(DatabaseHelper.TRIP_START_DEST, trip.getStart());
        values.put(DatabaseHelper.TRIP_START_COORD, trip.getStartCoord());
        values.put(DatabaseHelper.TRIP_END_DEST, trip.getEnd());
        values.put(DatabaseHelper.TRIP_END_COORD, trip.getEndCoord());
        values.put(DatabaseHelper.TRIP_DATE, trip.getDate());
        values.put(DatabaseHelper.TRIP_TIME, trip.getTime());
        values.put(DatabaseHelper.TRIP_STATUS, trip.getStatus());
        values.put(DatabaseHelper.TRIP_IS_DONE, trip.isDone());

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

    public ArrayList<Note> getTripNotes(int tripId){

        ArrayList<Note> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_NOTES;

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        c.moveToFirst();
        while (c.moveToNext()) {
            Note note = new Note();
            note.setId(c.getInt((c.getColumnIndex(DatabaseHelper.ID))));
            note.setTripId((c.getInt(c.getColumnIndex(DatabaseHelper.TRIP_ID))));
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
}
