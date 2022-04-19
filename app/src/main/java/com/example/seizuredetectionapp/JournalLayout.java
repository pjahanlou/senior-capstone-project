package com.example.seizuredetectionapp;

public class JournalLayout {
    private String dateAndTime;
    private String duration;
    private String description;
    private String severity;

    public JournalLayout(String dateAndTime, String duration, String description, String severity){
        this.dateAndTime = dateAndTime;
        this.duration = duration;
        this.description = description;
        this.severity = severity;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() { return description; }

    public String getSeverity() { return severity; }

    public void setSeverity() {this.severity = severity; }

    public void setDescription(String description) {
        this.description = description;
    }
}
