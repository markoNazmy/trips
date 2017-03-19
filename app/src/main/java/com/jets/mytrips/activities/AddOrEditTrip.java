package com.jets.mytrips.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Calendar;
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
    ListView notesList;
    LinearLayout linearLayout;
    ArrayAdapter<String> tripListAdapter;
    ArrayList<String>notesText = new ArrayList<String>();
    ArrayList<Note>notes;
    int year;
    int month;
    int day;
    int hours;
    int minutes;
    Trip trip;
    String tripId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_trip);
        tripName = (EditText) findViewById(R.id.trip_name);
        tripNote = (EditText) findViewById(R.id.note_text);
        tripStatus =(TextView) findViewById(R.id.status);
        tripDate =(TextView) findViewById(R.id.date_view);
        tripTime =(TextView) findViewById(R.id.time_view);
//        notesList = (ListView) findViewById(R.id.notes_list);
        tripFrom = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.from_auto_complete);
        tripTo = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.to_auto_complete);
        pickDate =(Button)findViewById(R.id.pick_date) ;
        pickTime =(Button)findViewById(R.id.pick_time) ;
        addNote =(Button)findViewById(R.id.add_note) ;
        save =(Button)findViewById(R.id.save) ;
        linearLayout =(LinearLayout)findViewById(R.id.note_layout);
        trip = new Trip();
        notes = new ArrayList<>();
        tripId = UUID.randomUUID().toString().substring(10);
        trip.setId(tripId);
//        tripListAdapter = new ArrayAdapter<String>(AddOrEditTrip.this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, notesText);
//
//        notesList.setAdapter(tripListAdapter);
        tripFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("dsfdsf", "Place: " + place.getName());
                trip.setStart(place.getName().toString());

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

                Note note = new Note();
                note.setId(UUID.randomUUID().toString().substring(10));
                note.setNote(tripNote.getText().toString());
                note.setTripId(tripId);
                System.out.println(note.getNote());
                tripNote.setText("");
//                tripListAdapter.add(note.getNote());
//                tripListAdapter.notifyDataSetChanged();
                TextView textView = new TextView(AddOrEditTrip.this);
                textView.setText(note.getNote());
                linearLayout.addView(textView);
                notes.add(note);
//                ArrayList<String>notesText = new ArrayList<String>();
//                for (int i =0;i<notes.size();i++){
//                    notesText.add(notes.get(i).getNote());
//                }

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
