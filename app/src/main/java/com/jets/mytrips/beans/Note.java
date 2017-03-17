package com.jets.mytrips.beans;

/**
 * Created by rocke on 3/16/2017.
 */

public class Note {
    int id;
    int tripId;
    String note;

    public Note(int id, int tripId, String note) {
        this.id = id;
        this.tripId = tripId;
        this.note = note;
    }

    public Note() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
