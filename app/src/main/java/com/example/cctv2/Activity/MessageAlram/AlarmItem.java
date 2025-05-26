package com.example.cctv2.Activity.MessageAlram;

import com.example.cctv2.R;

public class AlarmItem {
    private String message;
    private String date;
    private String imagePath;
    private int type;

    public AlarmItem(String message, String date, int type) {
        this.message = message;
        this.date = date;
        this.imagePath = "R.drawable.samplepicture"+Integer.toString(type); // 기본값으로 기본 사진 설정
        this.type = type;
    }

    public String getMessage() { return message; }
    public String getDate() { return date; }
    public String getImagePath() { return imagePath; }
    public int getType() { return type; }

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
