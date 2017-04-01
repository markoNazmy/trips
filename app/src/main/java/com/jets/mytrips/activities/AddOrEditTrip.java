package com.jets.mytrips.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.jets.mytrips.R;
import com.jets.mytrips.beans.Note;
import com.jets.mytrips.beans.Trip;
import com.jets.mytrips.controllers.TripController;
import com.jets.mytrips.database.DBAdapter;
import com.jets.mytrips.services.AlarmManager;
import com.jets.mytrips.services.TripListData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.UUID;

public class AddOrEditTrip extends AppCompatActivity {
    EditText tripName;
    EditText tripNote;
    PlaceAutocompleteFragment tripFrom;
    PlaceAutocompleteFragment tripTo;
    TextView tripStatus;
    TextView tripTime;
    TextView tripDate;
    TextView doneTextView;
    Button pickDate;
    Button pickTime;
    Button addNote;
    Button save;
    CheckBox doneCheckBox;
    CheckBox roundCheckBox;
    LinearLayout doneLayout;
    LinearLayout linearLayout;
    LinearLayout roundLayout;
    ArrayList<Note> notes;
    ArrayList<Note> notesForUpdate;
    int year;
    int month;
    int day;
    int hours;
    int minutes;
    Trip trip;
    String tripId;
    int tripPositionAtList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_trip);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tripName = (EditText) findViewById(R.id.trip_name);
        tripNote = (EditText) findViewById(R.id.note_text);
        tripStatus = (TextView) findViewById(R.id.status);
        tripDate = (TextView) findViewById(R.id.date_view);
        tripTime = (TextView) findViewById(R.id.time_view);
        doneTextView = (TextView) findViewById(R.id.doneText);
        tripFrom = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.from_auto_complete);
        tripTo = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.to_auto_complete);
        pickDate = (Button) findViewById(R.id.pick_date);
        pickTime = (Button) findViewById(R.id.pick_time);
        addNote = (Button) findViewById(R.id.add_note);
        save = (Button) findViewById(R.id.save);
        doneCheckBox = (CheckBox) findViewById(R.id.done_checkBox);
        roundCheckBox = (CheckBox) findViewById(R.id.round_checkBox);
        linearLayout = (LinearLayout) findViewById(R.id.note_layout);
        doneLayout = (LinearLayout) findViewById(R.id.done_layout);
        roundLayout = (LinearLayout) findViewById(R.id.round_layout);

        Intent intent = getIntent();
        tripPositionAtList = intent.getIntExtra("tripPositionAtList", -1);
        System.out.println(tripPositionAtList);


        tripFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("dsfdsf", "Place: " + place.getName());
                trip.setStart(place.getName().toString());
                trip.setStart(place.getName().toString());
                trip.setStartX(place.getLatLng().latitude);
                trip.setStartY(place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("dsfdsf", "An error occurred: " + status);
            }

        });
        tripFrom.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripFrom.setText("");
                trip.setStart("");
                trip.setStartX(0.0);
                trip.setStartY(0.0);
            }
        });
        tripTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("dsfdsf", "Place: " + place.getName());
                trip.setEnd(place.getName().toString());
                trip.setEnd(place.getName().toString());
                trip.setEndX(place.getLatLng().latitude);
                trip.setEndY(place.getLatLng().longitude);

                //image
                trip.setImage(place.getId());
                System.out.println("palce iddddddddddd" + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("dsfdsf", "An error occurred: " + status);
            }

        });
        tripTo.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripTo.setText("");
                trip.setEnd("");
                trip.setEndX(0.0);
                trip.setEndY(0.0);
            }
        });


        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddOrEditTrip.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                System.out.println(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                tripDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, year, month, day);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });


        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                hours = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                minutes = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOrEditTrip.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        System.out.println(selectedHour + ":" + selectedMinute);
                        tripTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hours, minutes, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }

        });


        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tripNote.getText().toString() != null && !tripNote.getText().toString().trim().equals("")) {
                    Note note = new Note();
                    note.setId(UUID.randomUUID().toString().substring(10));
                    note.setNote(tripNote.getText().toString());
                    note.setTripId(trip.getId());
                    System.out.println(note.getNote());
                    tripNote.setText("");
                    TextView textView = new TextView(AddOrEditTrip.this);
                    textView.setText(note.getNote());
                    linearLayout.addView(textView);
                    if (tripPositionAtList == -1) {
                        notes.add(note);
                    } else {
                        notesForUpdate.add(note);
                    }
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you did'nt entered a note to add !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });


        save.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                long diff_in_ms = 0;
                long currentDay = System.currentTimeMillis();
                if (checkText(tripName.getText().toString())) {
                    trip.setName(tripName.getText().toString());
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter a name for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }


                if (!checkText(trip.getStart())) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter Start destination for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


                if (!checkText(trip.getEnd())) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter End destination for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


                if (!checkText(tripDate.getText().toString())) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter a date for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


                if (!checkText(tripTime.getText().toString())) {

                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter a time for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                if (checkText(tripDate.getText().toString()) && checkText(tripTime.getText().toString())) {
                    if (checkTimeUpcoming(tripDate.getText().toString(), tripTime.getText().toString())) {
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        Date date = null;
                        try {
                            date = df.parse(tripDate.getText().toString() + " " + tripTime.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        GregorianCalendar nextDay = new GregorianCalendar();
                        nextDay.setTime(date);
                        diff_in_ms = nextDay.getTimeInMillis() - currentDay;
                        System.out.println("diffffffffff " + diff_in_ms);

                        trip.setDate(tripDate.getText().toString());
                        trip.setTime(tripTime.getText().toString());
                    } else {
                        Snackbar.make(getWindow().getDecorView().getRootView(), "you should not enter a past time", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }

                TripController tripController = TripController.getInstance(AddOrEditTrip.this);
                SharedPreferences sharedPreferences = getSharedPreferences("MyTrips", MODE_PRIVATE);
                int user_id = sharedPreferences.getInt("id", -1);
                if (user_id != -1) {
                    trip.setUserId(user_id);
                }

                if (tripPositionAtList == -1) {
                    if (checkText(tripName.getText().toString()) && checkText(tripDate.getText().toString()) && checkText(tripTime.getText().toString()) && checkText(trip.getStart()) && checkText(trip.getEnd()) && checkTimeUpcoming(tripDate.getText().toString(), tripTime.getText().toString())) {
                        //trip.setImage("aaa");
                        trip.setStatus("upcoming");
                        trip.setAlarmId(new Random().nextInt(100000 - 5) + 5);
                        trip.setMilliSeconds(diff_in_ms);
                        AlarmManager.setTask(trip, AddOrEditTrip.this, diff_in_ms);
                        if (!notes.isEmpty()) {

                            for (Note note : notes) {

                                System.out.println("note idddddddddddd" + new DBAdapter(AddOrEditTrip.this).addNote(note));
                            }
                            trip.setNotes(notes);
                        }
                        if (roundCheckBox.isChecked()) {
                            Trip roundTrip = new Trip();
                            roundTrip.setId(new DBAdapter(getApplicationContext()).generateId());
                            roundTrip.setName(trip.getName() + " round trip");
                            roundTrip.setImage(trip.getImage());
                            System.out.println("trips round nameeeeeee" + roundTrip.getName());
                            roundTrip.setDate(trip.getDate());
                            hours++;
                            roundTrip.setTime(hours + ":" + minutes);
                            if (!notes.isEmpty()) {

                                for (Note note : notes) {
                                    note.setTripId(roundTrip.getId());
                                    System.out.println("note idddddddddddd" + new DBAdapter(AddOrEditTrip.this).addNote(note));
                                }
                                roundTrip.setNotes(notes);
                            }
                            roundTrip.setStatus("upcoming");
                            roundTrip.setStart(trip.getEnd());
                            roundTrip.setStartX(trip.getEndX());
                            roundTrip.setStartY(trip.getEndY());
                            roundTrip.setEnd(trip.getStart());
                            roundTrip.setEndX(trip.getStartX());
                            roundTrip.setEndY(trip.getStartY());
                            roundTrip.setUserId(user_id);
                            roundTrip.setAlarmId(new Random().nextInt(100000 - 5) + 5);
                            AlarmManager.setTask(roundTrip, AddOrEditTrip.this, (diff_in_ms + 3600000));
                            new DBAdapter(AddOrEditTrip.this).addTrip(roundTrip);
                            TripListData.getUpcomingTripsListInstance().add(roundTrip);
                        }
                        new DBAdapter(AddOrEditTrip.this).addTrip(trip);
                        // User trips is now asynchronous
                        tripController.setUserTripsSynchronized(false);
                        TripListData.getUpcomingTripsListInstance().add(trip);
                        TripListData.getMyTripsListAdapterInstance(AddOrEditTrip.this, TripListData.getUpcomingTripsListInstance()).notifyDataSetChanged();
                        finish();
                    }
                } else {
                    if (checkText(tripName.getText().toString()) && checkText(trip.getStart()) && checkText(trip.getEnd()) && checkTimeUpcoming(tripDate.getText().toString(), tripTime.getText().toString())) {
                        // User trips is now asynchronous
                        tripController.setUserTripsSynchronized(false);
                        TripListData.getUpcomingTripsListInstance().remove(tripPositionAtList);
                        TripListData.getMyTripsListAdapterInstance(getApplicationContext(), TripListData.getUpcomingTripsListInstance()).notifyDataSetChanged();
                        if (doneCheckBox.isChecked()) {
                            trip.setDone(1);
                            trip.setStatus("done");
                            AlarmManager.deleteTask(trip.getAlarmId(), getApplicationContext());
                            TripListData.getHistoricalTripsListInstance().add(trip);
                            TripListData.getMyHistoricalTripsListAdapterInstance(getApplicationContext(), TripListData.getUpcomingTripsListInstance()).notifyDataSetChanged();
                        } else {
                            trip.setDone(0);
                            trip.setStatus("upcoming");
                            trip.setMilliSeconds(diff_in_ms);
                            AlarmManager.setTask(trip, AddOrEditTrip.this, diff_in_ms);
                            TripListData.getUpcomingTripsListInstance().add(tripPositionAtList, trip);
                            TripListData.getMyTripsListAdapterInstance(getApplicationContext(), TripListData.getUpcomingTripsListInstance()).notifyDataSetChanged();
                        }
                        if (!notesForUpdate.isEmpty()) {

                            for (Note note : notesForUpdate) {

                                System.out.println("note idddddddddddd" + new DBAdapter(AddOrEditTrip.this).addNote(note));
                            }
                            trip.setNotes(notesForUpdate);
                        }

                        new DBAdapter(AddOrEditTrip.this).updateTrip(trip);
                        finish();
                    }
                }


            }


        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (tripPositionAtList == -1) {
            doneLayout.setVisibility(View.INVISIBLE);
            trip = new Trip();
            notes = new ArrayList<>();
            tripId = UUID.randomUUID().toString().substring(10);
            trip.setId(tripId);
        } else {
            notesForUpdate = new ArrayList<>();
            trip = TripListData.getUpcomingTripsListInstance().get(tripPositionAtList);
            tripFrom.setHint(trip.getStart());
            tripTo.setHint(trip.getEnd());
            tripName.setText(trip.getName());
            tripDate.setText(trip.getDate());
            tripTime.setText(trip.getTime() + "");

            if (trip.getDone() == 1) {
                doneCheckBox.setChecked(true);
            }
            roundLayout.setVisibility(View.INVISIBLE);
            notes = new DBAdapter(getApplicationContext()).getTripNotes(trip.getId());
            System.out.println("notessss size " + notes.size());
            if (!notes.isEmpty()) {
                for (Note note : notes) {
                    TextView textView = new TextView(AddOrEditTrip.this);
                    textView.setText(note.getNote());
                    linearLayout.addView(textView);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static boolean checkTimeUpcoming(String date, String time) {

        long diff;
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date dateTovalidate = null;
        try {
            dateTovalidate = df.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar nextDay = new GregorianCalendar();
        nextDay.setTime(dateTovalidate);
        diff = nextDay.getTimeInMillis() - System.currentTimeMillis();
        ;
        System.out.println("diffffffffff " + diff);
        if (diff > 0) {
            return true;
        } else {
            return false;
        }
    }


    boolean checkText(String text) {
        if (text != null && !(text.trim().equals(""))) {
            return true;
        } else {
            return false;
        }
    }

}