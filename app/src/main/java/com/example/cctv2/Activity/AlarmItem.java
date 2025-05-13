package com.example.cctv2.Activity;

public class AlarmItem {
    private String message;
    private String date;

    public AlarmItem(String message, String date) {
        this.message = message;
        this.date = date;
    }

    public String getMessage() { return message; }
    public String getDate() { return date; }
}
