package com.example.firestoredatabase.Model;

import java.util.Date;

public class ImagesAdapter {
  private String date, judet, centreId, time;
  private Date data;

  public ImagesAdapter(){}

    public ImagesAdapter(String date, String judet, String centreId, String time, Date data) {
        this.date = date;
        this.judet = judet;
        this.centreId = centreId;
        this.time = time;
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJudet() {
        return judet;
    }

    public void setJudet(String judet) {
        this.judet = judet;
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
