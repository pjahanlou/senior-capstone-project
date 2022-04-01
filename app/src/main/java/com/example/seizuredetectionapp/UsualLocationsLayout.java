package com.example.seizuredetectionapp;

public class UsualLocationsLayout {

    private String street;
    private String city;
    private String stateAndCountry;

    public UsualLocationsLayout(String street, String city, String stateAndCountry){

        this.street = street;
        this.city = city;
        this.stateAndCountry = stateAndCountry;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateAndCountry() {
        return stateAndCountry;
    }

    public void setStateAndCountry(String stateAndCountry) {
        this.stateAndCountry = stateAndCountry;
    }

    public String toString(){
        return this.street + " " + this.city + " " + this.stateAndCountry ;
    }

}
