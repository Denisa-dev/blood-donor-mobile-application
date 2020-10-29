package com.example.firestoredatabase.Model;

public class EmailContactList {
    private String centru;
    private String judet;

    public EmailContactList(String centru, String judet) {
        this.centru = centru;
        this.judet = judet;
    }
    public EmailContactList(){}

    public String getCentru() {
        return centru;
    }

    public void setCentru(String centru) {
        this.centru = centru;
    }

    public String getJudet() {
        return judet;
    }

    public void setJudet(String judet) {
        this.judet = judet;
    }
}
