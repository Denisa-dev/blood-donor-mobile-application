package com.example.firestoredatabase.Model;

public class HomeListCards {
    Integer Img;
    String category;

    public HomeListCards(Integer img, String category ) {

        Img = img;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public int getImg() {
        return Img;
    }
}
