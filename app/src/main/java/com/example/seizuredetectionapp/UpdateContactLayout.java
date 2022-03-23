package com.example.seizuredetectionapp;

public class UpdateContactLayout {

    private String number;
    private String name;

    public UpdateContactLayout(String name, String number){
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String toString(){
        return this.name + " " + this.number;
    }

}
