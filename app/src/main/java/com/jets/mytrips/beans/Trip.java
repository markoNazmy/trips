package com.jets.mytrips.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rocke on 3/14/2017.
 */

public class Trip  implements Serializable{

    String id;
    int userId;
    String start;
    String startCoord;
    String end;
    String endCoord;
    String date;
    String time;
    String status;
    int done;       //represented as int in db
    ArrayList<Note> notes;

    public Trip(String id, int userId, String start, String startCoord, String end, String endCoord,
                String date, String time, String status, int done, ArrayList<Note> notes) {
        this.id = id;
        this.userId = userId;
        this.start = start;
        this.startCoord = startCoord;
        this.end = end;
        this.endCoord = endCoord;
        this.date = date;
        this.time = time;
        this.status = status;
        this.done = done;
        this.notes = notes;
    }

    public Trip() {

    }

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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int isDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public String getStartCoord() {
        return startCoord;
    }

    public void setStartCoord(String startCoord) {
        this.startCoord = startCoord;
    }

    public String getEndCoord() {
        return endCoord;
    }

    public void setEndCoord(String endCoord) {
        this.endCoord = endCoord;
    }
}
