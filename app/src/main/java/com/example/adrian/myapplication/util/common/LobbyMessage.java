package com.example.adrian.myapplication.util.common;

/*
 ******************************
 # Created by Tirla Ovidiu #
 # 27.02.2018 #
 ******************************
*/
public class LobbyMessage {
    private String from;
    private String message;
    private String topic;
    private EventType event;
    private int playerId;

    public LobbyMessage(String from, String message, String topic, int playerId, EventType event) {
        this.from = from;
        this.message = message;
        this.topic = topic;
        this.playerId = playerId;
        this.event = event;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public EventType getEvent() {
        return event;
    }

    public void setEvent(EventType event) {
        this.event = event;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
