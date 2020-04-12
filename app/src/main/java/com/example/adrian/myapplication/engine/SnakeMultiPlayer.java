package com.example.adrian.myapplication.engine;

import com.example.adrian.myapplication.util.snake.Coordinate;
import com.example.adrian.myapplication.util.snake.enums.Direction;
import com.example.adrian.myapplication.util.snake.enums.TileType;

import java.util.ArrayList;
import java.util.List;

/*
 ******************************
 # Created by Tirla Ovidiu #
 # 25.01.2018 #
 ******************************
*/
public class SnakeMultiPlayer {
    private static final int GameWidth = 30;
    private static final int GameHeight = 36;

    private List<Coordinate> walls = new ArrayList<>();
    private List<Coordinate> snakeP1 = new ArrayList<>(); //Your snake
    private List<Coordinate> snakeP2 = new ArrayList<>(); //Enemy snake
    private List<Coordinate> apples = new ArrayList<>();
    private List<Coordinate> obstacles = new ArrayList<>();

    private String winner;

    public void init(List<Coordinate> snakeP1, Direction currentDirection, List<Coordinate> walls, List<Coordinate> obstacles, List<Coordinate> apples, List<Coordinate> snakeP2) {
        this.snakeP1 = snakeP1;
        this.walls = walls;
        this.obstacles = obstacles;
        this.apples = apples;
        this.snakeP2 = snakeP2;
    }

    public void updateSnake(Coordinate nextCoordinate, Boolean growP1) {
        snakeP1.add(0, nextCoordinate);
        if (!growP1)
            snakeP1.remove(snakeP1.size() - 1);
    }

    public void updateEnemySnake(Coordinate nextCoordinate, Boolean growP2) {
        snakeP2.add(0, nextCoordinate);
        if (!growP2)
            snakeP2.remove(snakeP2.size() - 1);
    }


    public TileType[][] getMap() {
        TileType[][] map = new TileType[GameWidth][GameHeight];

        for (int x = 0; x < GameWidth; x++) {
            for (int y = 0; y < GameHeight; y++) {
                map[x][y] = TileType.Nothing;
            }
        }

        for (Coordinate s : snakeP1) {
            map[s.getX()][s.getY()] = TileType.Player1;
        }
        for (Coordinate s : snakeP2) {
            map[s.getX()][s.getY()] = TileType.Player2;
        }

        for (Coordinate apple : apples) {
            map[apple.getX()][apple.getY()] = TileType.Apple;
        }

        map[snakeP1.get(0).getX()][snakeP1.get(0).getY()] = TileType.SnakeHead;
        map[snakeP2.get(0).getX()][snakeP2.get(0).getY()] = TileType.SnakeHead;

        for (Coordinate wall : walls) {
            map[wall.getX()][wall.getY()] = TileType.Wall;
        }

        for (Coordinate obstacle : obstacles) {
            map[obstacle.getX()][obstacle.getY()] = TileType.Obstacle;
        }

        return map;
    }

    public void gameLost() {
        //TODO implement event on gameLost
    }

    public void updateApples(List<Coordinate> apples) {
        if (apples != this.apples) {
            this.apples = apples;
        }
    }
}
