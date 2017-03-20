package com.jets.mytrips.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by rocke on 3/16/2017.
 */

public class Note implements Parcelable {
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

    protected Note(Parcel in) {
        id = in.readString();
        tripId = in.readString();
        note = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(tripId);
        dest.writeString(note);
    }
}
