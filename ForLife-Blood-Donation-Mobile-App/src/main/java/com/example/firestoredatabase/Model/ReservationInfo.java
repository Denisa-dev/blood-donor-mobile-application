package com.example.firestoredatabase.Model;

public class ReservationInfo {
    private String customeName, centreId, time, telefon, name, email, grupa, sex, date, calendar;
    Long slot;

    public ReservationInfo(){}

    public ReservationInfo(String customeName, String centreId, String time, String telefon, String name, String email, String grupa, String sex, String date, String calendar, Long slot) {
        this.customeName = customeName;
        this.centreId = centreId;
        this.time = time;
        this.telefon = telefon;
        this.name = name;
        this.email = email;
        this.grupa = grupa;
        this.sex = sex;
        this.date = date;
        this.calendar = calendar;
        this.slot = slot;
    }

    public String getCustomeName() {
        return customeName;
    }

    public void setCustomeName(String customeName) {
        this.customeName = customeName;
    }

    public String getCentreId() {
        return centreId;
    }

    public void setCentreId(String centreId) {
        this.centreId = centreId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGrupa() {
        return grupa;
    }

    public void setGrupa(String grupa) {
        this.grupa = grupa;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }
}
