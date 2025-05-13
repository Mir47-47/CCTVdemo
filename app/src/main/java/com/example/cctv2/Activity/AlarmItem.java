package com.example.cctv2.Activity;

public class AlarmItem {
    private String message;
    private String date;
    private String imagePath;

    public AlarmItem(String message, String date) {
        this.message = message;
        this.date = date;
        this.imagePath = null; // 기본값으로 기본 사진 설정
    }

    public String getMessage() { return message; }
    public String getDate() { return date; }
    public String getImagePath() { return imagePath; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AlarmItem)) return false;
        AlarmItem other = (AlarmItem) obj;
        return this.message.equals(other.message) && this.date.equals(other.date);
    }

    @Override
    public int hashCode() {
        return (message + date).hashCode();
    }
}
