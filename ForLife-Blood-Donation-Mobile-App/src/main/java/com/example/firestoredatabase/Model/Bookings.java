package com.example.firestoredatabase.Model;

public class Bookings {
    private String date;
    private String centru;
    private String time;

    public Bookings(){};

    public Bookings(String date, String centru, String time) {
        this.date = date;
        this.centru = centru;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCentru() {
        return centru;
    }

    public void setCentru(String centru) {
        this.centru = centru;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
