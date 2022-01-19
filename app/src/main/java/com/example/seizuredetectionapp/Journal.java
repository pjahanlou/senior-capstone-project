package com.example.seizuredetectionapp;

public class Journal {
    public String dateAndTime, mood, typeOfSeizure, durationOfSeizure, trigger, description, postDescription;

    public Journal()
    {

    }

    public Journal(String dateAndTime,String mood,String typeOfSeizure,String durationOfSeizure,
                   String trigger,String description,String postDescription)
        {
            this.dateAndTime = dateAndTime;
            this.mood = mood;
            this.typeOfSeizure = typeOfSeizure;
            this.durationOfSeizure = durationOfSeizure;
            this.trigger = trigger;
            this.description = description;
            this.postDescription = postDescription;
        }

}
