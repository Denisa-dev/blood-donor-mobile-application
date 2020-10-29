package com.example.firestoredatabase.Model;

public class Centre {
    private String name, centreId;

    public Centre(){}

    public Centre(String name, String centreId) {
        this.name = name;
        this.centreId = centreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCentreId() {
        return centreId;
    }

    public void setCentreId(String centreId) {
        this.centreId = centreId;
    }
}
