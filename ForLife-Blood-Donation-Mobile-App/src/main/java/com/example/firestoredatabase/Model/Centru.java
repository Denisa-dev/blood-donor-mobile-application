package com.example.firestoredatabase.Model;

public class Centru {
   private String lat;
   private String longi;

   public Centru(String lat, String longi) {
      this.lat = lat;
      this.longi = longi;
   }

   public Centru(){}

   public String getLat() {
      return lat;
   }

   public void setLat(String lat) {
      this.lat = lat;
   }

   public String getLongi() {
      return longi;
   }

   public void setLongi(String longi) {
      this.longi = longi;
   }
}
