package com.example.seizuredetectionapp;

public class UsualLocationsLayout {

    private String street;

    public UsualLocationsLayout(String street){

        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String toString(){
        return this.street + " ";
    }

}
