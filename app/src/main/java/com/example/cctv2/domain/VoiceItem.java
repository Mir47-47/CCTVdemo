package com.example.cctv2.domain;

import lombok.Getter;
import lombok.Setter;


public class VoiceItem {
    @Getter
    @Setter
    private String name;
    @Getter
    private String path;
    @Getter
    private String date;
    @Getter
    private int length;

    public VoiceItem(String name, String path, String date, int length) {
        this.name = name;
        this.path = path;
        this.date = date;
        this.length = length;
    }
}
