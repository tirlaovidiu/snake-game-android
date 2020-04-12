package com.example.adrian.myapplication.socket;

import java.util.HashSet;
import java.util.Set;

/*
 ******************************
 # Created by Tirla Ovidiu #
 # 25.01.2018 #
 ******************************
*/

public class TopicHandler {
    private String topic;
    private String subId;
    private Set<StompMessageListener> listeners = new HashSet<>();

    public TopicHandler(String topic, String subId) {
        this.topic = topic;
        this.subId = subId;
    }

    public TopicHandler() {

    }

    public String getTopic() {
        return topic;
    }

    public void addListener(StompMessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(StompMessageListener listener) {
        listeners.remove(listener);
    }

    public void onMessage(StompMessage message) {
        for (StompMessageListener listener : listeners) {
            listener.onMessage(message);
        }
    }

    public String getSubId() {
        return subId;
    }
}
