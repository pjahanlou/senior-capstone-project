package com.example.seizuredetectionapp;

public class UsualLocationsLayout {

    private String address;

    public UsualLocationsLayout(String address){
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String toString(){
        return this.address;
    }

}
