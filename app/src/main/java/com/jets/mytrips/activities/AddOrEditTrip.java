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
import android.widget.CheckBox;
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
    LinearLayout doneLayout;
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
        doneTextView=(TextView) findViewById(R.id.doneText) ;
        tripFrom = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.from_auto_complete);
        tripTo = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.to_auto_complete);
        pickDate =(Button)findViewById(R.id.pick_date) ;
        pickTime =(Button)findViewById(R.id.pick_time) ;
        addNote =(Button)findViewById(R.id.add_note) ;
        save =(Button)findViewById(R.id.save) ;
        doneCheckBox =(CheckBox) findViewById(R.id.done_checkBox) ;
        linearLayout =(LinearLayout)findViewById(R.id.note_layout);
        doneLayout = (LinearLayout) findViewById(R.id.done_layout) ;

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


            @Override
            public void onClick(View v) {

                long diff_in_ms=0;
                long currentDay= System.currentTimeMillis();
                if(checkText(tripName.getText().toString())){
                    trip.setName(tripName.getText().toString());
                }
                else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter a name for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }


                if(!checkText(trip.getStart())){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter Start destination for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


                if(!checkText(trip.getEnd())){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter End destination for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }





                if(!checkText(tripDate.getText().toString())){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter a date for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }




                if(!checkText(tripTime.getText().toString())){

                    Snackbar.make(getWindow().getDecorView().getRootView(), "you should enter a time for your trip", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                if(checkText(tripDate.getText().toString())&&checkText(tripTime.getText().toString())){
                    DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    Date date= null;
                    try {
                        date = df.parse(tripDate.getText().toString()+" "+tripTime.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    GregorianCalendar nextDay=new  GregorianCalendar ();
                    nextDay.setTime(date);
                    diff_in_ms=nextDay. getTimeInMillis()-currentDay;
                    System.out.println("diffffffffff "+diff_in_ms);
//                    GregorianCalendar nextDayy=new  GregorianCalendar (year,month,day,hours,minutes,0);
//                    diff_in_ms=nextDayy. getTimeInMillis()-currentDay;
//                    System.out.println("diffffffffff "+diff_in_ms);
                    trip.setDate(tripDate.getText().toString());
                    trip.setTime( tripTime.getText().toString());
                }

                SharedPreferences sharedPreferences = getSharedPreferences("MyTrips", MODE_PRIVATE);
                int user_id = sharedPreferences.getInt("id", -1);
                if (user_id != -1) {
                    trip.setUserId(user_id);
                }

                if (tripPositionAtList==-1) {
                    if (checkText(tripName.getText().toString()) && checkText(tripDate.getText().toString())&&checkText(tripTime.getText().toString())&& checkText(trip.getStart()) && checkText(trip.getEnd())) {
                        trip.setImage("aaa");
                        trip.setStatus("upcoming");
                        trip.setAlarmId( new Random().nextInt(1000 - 5) + 5);

                        trip.setMilliSeconds(diff_in_ms);

                        AlarmManager.setTask(trip,AddOrEditTrip.this,diff_in_ms);
                        if(!notes.isEmpty()) {

                            for (Note note : notes) {

                                System.out.println("note idddddddddddd"+new DBAdapter(AddOrEditTrip.this).addNote(note));
                            }
                            trip.setNotes(notes);
                        }
                        new DBAdapter(AddOrEditTrip.this).addTrip(trip);
                        TripListData.getTripsListInstance().add(trip);
                        TripListData.getMyTripsListAdapterInstance(AddOrEditTrip.this, TripListData.getTripsListInstance()).notifyDataSetChanged();
                        Intent intent = new Intent(AddOrEditTrip.this,CurrentTripsActivity.class);
                        startActivity(intent);
                    }
                }

                else{
                    if (checkText(tripName.getText().toString()) && checkText(trip.getStart()) && checkText(trip.getEnd())) {
                        if(doneCheckBox.isChecked()){
                            trip.setDone(1);
                        }
                        else {
                            trip.setDone(0);
                        }
                        if (!notesForUpdate.isEmpty()) {

                            for (Note note : notesForUpdate) {

                                System.out.println("note idddddddddddd" + new DBAdapter(AddOrEditTrip.this).addNote(note));
                            }
                            trip.setNotes(notesForUpdate);
                        }
                        trip.setMilliSeconds(diff_in_ms);
                        AlarmManager.setTask(trip,AddOrEditTrip.this,diff_in_ms);
                        new DBAdapter(AddOrEditTrip.this).updateTrip(trip);
                        TripListData.getTripsListInstance().remove(tripPositionAtList);
                        TripListData.getTripsListInstance().add(tripPositionAtList, trip);
                        TripListData.getMyTripsListAdapterInstance(AddOrEditTrip.this, TripListData.getTripsListInstance()).notifyDataSetChanged();
                        Intent intent = new Intent(AddOrEditTrip.this,CurrentTripsActivity.class);
                        startActivity(intent);
                    }
                }





                }


        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(tripPositionAtList==-1) {
            doneLayout.setVisibility(View.INVISIBLE);
            trip = new Trip();
            notes = new ArrayList<>();
            tripId = UUID.randomUUID().toString().substring(10);
            trip.setId(tripId);
        }
        else{
            notesForUpdate=new ArrayList<>();
            trip = TripListData.getTripsListInstance().get(tripPositionAtList);
            tripFrom.setHint(trip.getStart());
            tripTo.setHint(trip.getEnd());
            tripName.setText(trip.getName());
            tripDate.setText(trip.getDate());
            tripTime.setText(trip.getTime()+"");
            if(trip.getDone()==1){
                doneCheckBox.setChecked(true);
            }
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


















    boolean checkText(String text){
        if(text!=null&&!(text.trim().equals(""))){
            return true;
        }
        else{
            return false;
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