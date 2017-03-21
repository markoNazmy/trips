package com.jets.mytrips.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rocke on 3/14/2017.
 */

public class Trip implements Parcelable{

    String id;
    int userId;
    String name;
    String start;
    double startX;
    double startY;
    String end;
    double endX;
    double endY;
    String date;
    String time;
    String status;
    int done;
    ArrayList<Note> notes;
    String image;
    int alarmId;

    public Trip(String id, int userId, String start, double startX, double startY, String end,
                double endX, double endY, String date, String time, String status, int done,
                ArrayList<Note> notes, String image, int alarmId) {
        this.id = id;
        this.userId = userId;
        this.start = start;
        this.startX = startX;
        this.startY = startY;
        this.end = end;
        this.endX = endX;
        this.endY = endY;
        this.date = date;
        this.time = time;
        this.status = status;
        this.done = done;
        this.notes = notes;
        this.image = image;
        this.alarmId = alarmId;
    }

    public Trip(){

    }

    protected Trip(Parcel in) {
        id = in.readString();
        userId = in.readInt();
        name = in.readString();
        start = in.readString();
        startX = in.readDouble();
        startY = in.readDouble();
        end = in.readString();
        endX = in.readDouble();
        endY = in.readDouble();
        date = in.readString();
        time = in.readString();
        status = in.readString();
        done = in.readInt();
        image = in.readString();
        alarmId = in.readInt();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(userId);
        dest.writeString(name);
        dest.writeString(start);
        dest.writeDouble(startX);
        dest.writeDouble(startY);
        dest.writeString(end);
        dest.writeDouble(endX);
        dest.writeDouble(endY);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(status);
        dest.writeInt(done);
        dest.writeString(image);
        dest.writeInt(alarmId);
    }
}
