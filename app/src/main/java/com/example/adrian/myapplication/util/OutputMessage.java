package com.example.adrian.myapplication.util;

import com.example.adrian.myapplication.util.common.EventType;
import com.example.adrian.myapplication.util.snake.Coordinate;
import com.example.adrian.myapplication.util.snake.enums.Direction;


import java.util.ArrayList;
import java.util.List;

/*
 ******************************
 # Created by Tirla Ovidiu #
 # 16.02.2018 #
 ******************************
*/
public class OutputMessage {
    private String from;
    private String message;
    private String topic;
    private EventType eventType;
    private List<Coordinate> walls = new ArrayList<>();
    private List<Coordinate> snakeP1 = new ArrayList<>();
    private List<Coordinate> snakeP2 = new ArrayList<>();
    private List<Coordinate> apples = new ArrayList<>();
    private List<Coordinate> obstacles = new ArrayList<>();

    private Direction p1NextDirection;
    private Direction p2NextDirection;
    private Direction p1CurrentDirection;
    private Direction p2CurrentDirection;
    private String winner;

    public OutputMessage() {
    }

    public OutputMessage(String from, String message, String topic) {
        this.from = from;
        this.message = message;
        this.topic = topic;
    }

    public OutputMessage(String from, String message, String topic, EventType eventType) {
        this.from = from;
        this.message = message;
        this.topic = topic;
        this.eventType = eventType;
    }

    public OutputMessage(String from, String message, String topic, EventType eventType, List<Coordinate> snakeP1, List<Coordinate> snakeP2, List<Coordinate> walls, List<Coordinate> apples, List<Coordinate> obstacles, Direction p1CurrentDirection, Direction p2CurrentDirection) {
        this.from = from;
        this.message = message;
        this.topic = topic;
        this.eventType = eventType;
        this.snakeP1 = snakeP1;
        this.snakeP2 = snakeP2;
        this.walls = walls;
        this.apples = apples;
        this.obstacles = obstacles;
        this.p1CurrentDirection = p1CurrentDirection;
        this.p2CurrentDirection = p2CurrentDirection;
    }

    public OutputMessage(String from, String message, String topic, Direction p1NextDirection, Direction p2NextDirection) {
        this.from = from;
        this.message = message;
        this.topic = topic;
        this.p1NextDirection = p1NextDirection;
        this.p2NextDirection = p2NextDirection;
    }

    public OutputMessage(String from, String message, String topic, String winner, EventType eventType) {
        this.from = from;
        this.message = message;
        this.topic = topic;
        this.winner = winner;
        this.eventType = eventType;
    }


    public void setFrom(String from) {
        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setWalls(List<Coordinate> walls) {
        this.walls = walls;
    }

    public void setSnakeP1(List<Coordinate> snakeP1) {
        this.snakeP1 = snakeP1;
    }

    public void setSnakeP2(List<Coordinate> snakeP2) {
        this.snakeP2 = snakeP2;
    }

    public void setApples(List<Coordinate> apples) {
        this.apples = apples;
    }

    public void setObstacles(List<Coordinate> obstacles) {
        this.obstacles = obstacles;
    }

    public void setP1NextDirection(Direction p1NextDirection) {
        this.p1NextDirection = p1NextDirection;
    }

    public void setP2NextDirection(Direction p2NextDirection) {
        this.p2NextDirection = p2NextDirection;
    }

    public void setP1CurrentDirection(Direction p1CurrentDirection) {
        this.p1CurrentDirection = p1CurrentDirection;
    }

    public void setP2CurrentDirection(Direction p2CurrentDirection) {
        this.p2CurrentDirection = p2CurrentDirection;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getTopic() {
        return topic;
    }

    public EventType getEventType() {
        return eventType;
    }

    public List<Coordinate> getWalls() {
        return walls;
    }

    public List<Coordinate> getSnakeP1() {
        return snakeP1;
    }

    public List<Coordinate> getSnakeP2() {
        return snakeP2;
    }

    public List<Coordinate> getApples() {
        return apples;
    }

    public List<Coordinate> getObstacles() {
        return obstacles;
    }

    public Direction getP1NextDirection() {
        return p1NextDirection;
    }

    public Direction getP2NextDirection() {
        return p2NextDirection;
    }

    public Direction getP1CurrentDirection() {
        return p1CurrentDirection;
    }

    public Direction getP2CurrentDirection() {
        return p2CurrentDirection;
    }

    public String getWinner() {
        return winner;
    }
}
