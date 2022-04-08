package com.example.seizuredetectionapp;

import java.util.List;

public class Journal {
    public String dateAndTime, durationOfSeizure, description, postDescription, severity;
    public List<String> triggers,mood,typeOfSeizure;
    public Journal()
    {

    }

    public Journal(String dateAndTime,List<String> mood,List<String> typeOfSeizure,String durationOfSeizure,
                   List<String> triggers,String description,String postDescription, String severity)
        {
            this.dateAndTime = dateAndTime;
            this.mood = mood;
            this.typeOfSeizure = typeOfSeizure;
            this.durationOfSeizure = durationOfSeizure;
            this.triggers = triggers;
            this.description = description;
            this.postDescription = postDescription;
            this.severity = severity;
        }

    public String toString(){
        return "Date and Time: " + dateAndTime + " Duration: " + durationOfSeizure + " Description: " + description;
    }

}
