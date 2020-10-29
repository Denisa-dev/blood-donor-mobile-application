package com.example.firestoredatabase.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Judet implements Parcelable {
    private String name, judetId;

    public Judet(){}

    public static Creator<Judet> getCREATOR() {
        return CREATOR;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJudetId() {
        return judetId;
    }

    public void setJudetId(String judetId) {
        this.judetId = judetId;
    }

    public Judet(String name, String judetId) {
        this.name = name;
        this.judetId = judetId;
    }

    protected Judet(Parcel in) {
        name = in.readString();
        judetId = in.readString();
    }

    public static final Creator<Judet> CREATOR = new Creator<Judet>() {
        @Override
        public Judet createFromParcel(Parcel in) {
            return new Judet(in);
        }

        @Override
        public Judet[] newArray(int size) {
            return new Judet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(judetId);
    }
}
