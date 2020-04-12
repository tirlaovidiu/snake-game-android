package com.example.adrian.myapplication.util.common;


import com.example.adrian.myapplication.util.snake.enums.Direction;

/*
 ******************************
 # Created by Tirla Ovidiu #
 # 16.02.2018 #
 ******************************
*/
public class ClientMessage {
    private String from;
    private String text;
    private String lobbyId;
    private EventType eventType;
    private Direction nextMove;

    public ClientMessage() {
    }

    public ClientMessage(String from, String text, String lobbyId, EventType eventType) {
        this.from = from;
        this.text = text;
        this.lobbyId = lobbyId;
        this.eventType = eventType;
    }

    public ClientMessage(String from, String text, String lobbyId, EventType eventType, Direction nextMove) {
        this.from = from;
        this.text = text;
        this.lobbyId = lobbyId;
        this.eventType = eventType;
        this.nextMove = nextMove;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Direction getNextMove() {
        return nextMove;
    }

    public void setNextMove(Direction nextMove) {
        this.nextMove = nextMove;
    }
}
