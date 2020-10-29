package com.example.firestoredatabase.Model;

import java.util.Date;

public class UserInfo {
    private String centreId, judet, slot, date, time;
    private Date data;
    public UserInfo(){}

    public UserInfo(String centreId, String judet, String slot, String date, String time, Date data) {
        this.centreId = centreId;
        this.judet = judet;
        this.slot = slot;
        this.date = date;
        this.time = time;
        this.data = data;
    }

    public String getCentreId() {
        return centreId;
    }

    public void setCentreId(String centreId) {
        this.centreId = centreId;
    }

    public String getJudet() {
        return judet;
    }

    public void setJudet(String judet) {
        this.judet = judet;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
