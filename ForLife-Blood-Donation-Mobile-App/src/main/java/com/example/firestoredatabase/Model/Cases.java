package com.example.firestoredatabase.Model;

public class Cases {
    private String pacient;
    private String caz;
    private String opinie;
    private String centru;

    public Cases(){}

    public Cases(String pacient, String caz, String opinie, String centru) {
        this.pacient = pacient;
        this.caz = caz;
        this.opinie = opinie;
        this.centru = centru;
    }

    public String getPacient() {
        return pacient;
    }

    public void setPacient(String pacient) {
        this.pacient = pacient;
    }

    public String getCaz() {
        return caz;
    }

    public void setCaz(String caz) {
        this.caz = caz;
    }

    public String getOpinie() {
        return opinie;
    }

    public void setOpinie(String opinie) {
        this.opinie = opinie;
    }

    public String getCentru() {
        return centru;
    }

    public void setCentru(String centru) {
        this.centru = centru;
    }
}
