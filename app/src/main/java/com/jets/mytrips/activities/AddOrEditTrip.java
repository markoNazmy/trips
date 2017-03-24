package com.jets.mytrips.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Calendar;
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
    Button pickDate;
    Button pickTime;
    Button addNote;
    Button save;
    LinearLayout linearLayout;
    ArrayList<Note>notes;
    ArrayList<Note>notesForUpdate;
    int year;
    int month;
    int day;
    int hours;
    int minutes;
    Trip trip;
    String tripId ;
    int tripPositionAtList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_trip);





        tripName = (EditText) findViewById(R.id.trip_name);
        tripNote = (EditText) findViewById(R.id.note_text);
        tripStatus =(TextView) findViewById(R.id.status);
        tripDate =(TextView) findViewById(R.id.date_view);
        tripTime =(TextView) findViewById(R.id.time_view);
        tripFrom = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.from_auto_complete);
        tripTo = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.to_auto_complete);
        pickDate =(Button)findViewById(R.id.pick_date) ;
        pickTime =(Button)findViewById(R.id.pick_time) ;
        addNote =(Button)findViewById(R.id.add_note) ;
        save =(Button)findViewById(R.id.save) ;
        linearLayout =(LinearLayout)findViewById(R.id.note_layout);

        Intent intent = getIntent();
        tripPositionAtList = intent.getIntExtra("tripPositionAtList",-1);
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
        tripTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("dsfdsf", "Place: " + place.getName());
                trip.setEnd(place.getName().toString());
                trip.setEnd(place.getName().toString());
                trip.setEndX(place.getLatLng().latitude);
                trip.setEndY(place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("dsfdsf", "An error occurred: " + status);
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
                                tripDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
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
                        System.out.println( selectedHour + ":" + selectedMinute);
                        tripTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hours, minutes, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }

        });






        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tripNote.getText().toString()!=null&&!tripNote.getText().toString().trim().equals("")) {
                    Note note = new Note();
                    note.setId(UUID.randomUUID().toString().substring(10));
                    note.setNote(tripNote.getText().toString());
                    note.setTripId(trip.getId());
                    System.out.println(note.getNote());
                    tripNote.setText("");
                    TextView textView = new TextView(AddOrEditTrip.this);
                    textView.setText(note.getNote());
                    linearLayout.addView(textView);
                    if (tripPositionAtList==-1) {
                        notes.add(note);
                    }
                    else{
                        notesForUpdate.add(note);
                    }
                }
                else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you did'nt entered a note to add !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });









        save.setOnClickListener(new View.OnClickListener() {
            boolean flag_save =false;

            @Override
            public void onClick(View v) {
                if(tripName.getText()!=null&&!tripName.getText().toString().trim().equals("")){
                    trip.setName(tripName.getText().toString());
                    flag_save=true;
                }
                else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter a name for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    flag_save=false;
                }


                if(trip.getStart()!=null&&!trip.getStart().equals("")){
                    flag_save=true;
                }
                else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter Start destination for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    flag_save=false;
                }




                if(trip.getEnd()!=null&&!trip.getEnd().equals("")){
                    flag_save=true;
                }
                else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter End destination for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    flag_save=false;
                }




                if(year!=0&&(month+1)!=0&&day!=0){
                    flag_save=true;
                }
                else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter a date for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    flag_save=false;
                }



                if(hours!=0){
                    flag_save=true;
                }
                else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter a time for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    flag_save=false;
                }




                if(flag_save){
                    TripController tripController = TripController.getInstance(AddOrEditTrip.this);
                    SharedPreferences sharedPreferences = getSharedPreferences("MyTrips", MODE_PRIVATE);
                    int user_id =sharedPreferences.getInt("id", -1);
                    if (user_id!=-1) {
                        trip.setUserId(user_id);
                    }
                    System.out.println("aaaaaaaaaaaaaaaaaaaaaaa"+user_id);

// for adding new trip
                    if (tripPositionAtList==-1) {
                        long diff_in_ms;
                        long currentDay= System.currentTimeMillis();
                        GregorianCalendar nextDay=new  GregorianCalendar (year,month+1,day,hours,minutes,0);
                        diff_in_ms=nextDay. getTimeInMillis()-currentDay;
                        //System.out.println("aaaaaaaaaaaaaaaaaaa start " +trip.getStart() );
                        //  noooooooo dateeeee
                        trip.setImage("aaa");
                        trip.setStatus("upcoming");
                        trip.setDate(day + "/" + (month + 1) + "/" + year);
                        trip.setTime( hours + ":" + minutes);
                        trip.setAlarmId( new Random().nextInt(1000 - 5) + 5);
                        AlarmManager.setTask(trip,AddOrEditTrip.this,diff_in_ms);
                        if(!notes.isEmpty()) {

                            for (Note note : notes) {

                                System.out.println("note idddddddddddd"+new DBAdapter(AddOrEditTrip.this).addNote(note));
                            }
                            trip.setNotes(notes);
                        }
                        new DBAdapter(AddOrEditTrip.this).addTrip(trip);
                        // User trips is now asynchronous
                        tripController.setUserTripsSynchronized(false);
                        TripListData.getUpcomingTripsListInstance().add(trip);
                        TripListData.getMyTripsListAdapterInstance(AddOrEditTrip.this, TripListData.getUpcomingTripsListInstance()).notifyDataSetChanged();
                    }


// for updating a trip
                    else{
                        if(!notesForUpdate.isEmpty()) {

                            for (Note note : notesForUpdate) {

                                System.out.println("note idddddddddddd"+new DBAdapter(AddOrEditTrip.this).addNote(note));
                            }
                            trip.setNotes(notesForUpdate);
                        }
                        new DBAdapter(AddOrEditTrip.this).updateTrip(trip);
                        // User trips is now asynchronous
                        tripController.setUserTripsSynchronized(false);
                        TripListData.getUpcomingTripsListInstance().remove(tripPositionAtList);
                        TripListData.getUpcomingTripsListInstance().add(tripPositionAtList,trip);
                        TripListData.getMyTripsListAdapterInstance(AddOrEditTrip.this, TripListData.getUpcomingTripsListInstance()).notifyDataSetChanged();

                    }
                    finish();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(tripPositionAtList==-1) {
            trip = new Trip();
            notes = new ArrayList<>();
            tripId = UUID.randomUUID().toString().substring(10);
            trip.setId(tripId);
        }
        else{
            notesForUpdate=new ArrayList<>();
            trip = TripListData.getUpcomingTripsListInstance().get(tripPositionAtList);
            tripFrom.setHint(trip.getStart());
            tripTo.setHint(trip.getEnd());
            tripName.setText(trip.getName());
            tripDate.setText(trip.getDate());
            tripTime.setText(trip.getTime()+"");
            notes = new DBAdapter(getApplicationContext()).getTripNotes(trip.getId());
            System.out.println("notessss size "+notes.size());
            if(!notes.isEmpty()) {
                for (Note note : notes) {
                    TextView textView = new TextView(AddOrEditTrip.this);
                    textView.setText(note.getNote());
                    linearLayout.addView(textView);
                }
            }
        }
    }
}
//    Calendar calendar = Calendar.getInstance();
//                calendar.set(year, month+1, day,
//                        hours, minutes, 0);
//                System.out.println("eeeeeeeeeeeeeeeee"+(calendar.getTimeInMillis()-System.currentTimeMillis()));
//                long diff_in_ms;
//                long currentDay= System.currentTimeMillis();
//                GregorianCalendar nextDay=new  GregorianCalendar (year,month+1,day,hours,minutes,0);
//
//                diff_in_ms=nextDay. getTimeInMillis()-currentDay;
//                System.out.println("nowwwwwwwwwwwwwwwwwwww"+diff_in_ms);