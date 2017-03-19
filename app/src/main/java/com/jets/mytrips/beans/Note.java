package com.jets.mytrips.beans;

/**
 * Created by rocke on 3/16/2017.
 */

public class Note {
    String id;
    String tripId;
    String note;

    public Note(String id, String tripId, String note) {
        this.id = id;
        this.tripId = tripId;
        this.note = note;
    }

    public Note() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
