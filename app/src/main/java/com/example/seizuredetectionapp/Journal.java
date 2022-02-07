package com.example.seizuredetectionapp;

public class Journal {
    public String dateAndTime, mood, typeOfSeizure, durationOfSeizure, triggers, description, postDescription;

    public Journal()
    {

    }

    public Journal(String dateAndTime,String mood,String typeOfSeizure,String durationOfSeizure,
                   String triggers,String description,String postDescription)
        {
            this.dateAndTime = dateAndTime;
            this.mood = mood;
            this.typeOfSeizure = typeOfSeizure;
            this.durationOfSeizure = durationOfSeizure;
            this.triggers = triggers;
            this.description = description;
            this.postDescription = postDescription;
        }

    public String toString(){
        return dateAndTime + " " + mood + " " + typeOfSeizure;
    }

}
