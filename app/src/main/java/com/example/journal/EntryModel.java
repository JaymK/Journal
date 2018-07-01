package com.example.journal;

public class EntryModel {

    public String entryTitle;
    public String entryTime;
    public EntryModel() {

    }

    public EntryModel(String entryTitle, String entryTime) {
        this.entryTitle = entryTitle;
        this.entryTime = entryTime;
    }

    public String getEntryTitle() {
        return entryTitle;
    }

    public void setEntryTitle(String entryTitle) {
        this.entryTitle = entryTitle;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }
}