package com.example.cctv2.Activity.MessageAlram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageStorage {
    private static final List<String> messageList = new ArrayList<>();

    public static void addMessage(String message) {
        synchronized (messageList) {
            messageList.add(message);
        }
    }

    public static List<String> getMessages() {
        synchronized (messageList) {
            return new ArrayList<>(messageList);
        }
    }
}