package com.example.adrian.myapplication.engine;

import com.example.adrian.myapplication.util.snake.enums.Direction;
import com.example.adrian.myapplication.util.snake.enums.GameState;
import com.example.adrian.myapplication.util.snake.enums.TileType;
import com.example.adrian.myapplication.util.snake.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 ******************************
 # Created by Tirla Ovidiu #
 # 25.01.2018 #
 ******************************
*/
public class SnakeEngine {
    private static final int GameWidth = 30;
    private static final int GameHeight = 36;
    private int points = 0;
    private int snakeSpeed = 150;

    private Random random = new Random();
    private boolean increaseTail = false;

    private List<Coordinate> walls = new ArrayList<>();
    private List<Coordinate> snake = new ArrayList<>();
    private List<Coordinate> apples = new ArrayList<>();
    private List<Coordinate> obstacles = new ArrayList<>();

    private Direction currentDirection = Direction.SOUTH;

    private GameState currentGameState = GameState.Running;


    private boolean canChangeDirection = true;

    public SnakeEngine() {
    }

    public void initGame() {
        AddSnake();
        AddWalls();
        AddObstacles();
        AddApples();
    }


    public void UpdateDirection(Direction newDirection) {
        if (Math.abs(newDirection.ordinal() - currentDirection.ordinal()) % 2 == 1 && canChangeDirection) {
            currentDirection = newDirection;
            canChangeDirection = false;
        }

    }

    public void Update() {
        switch (currentDirection) {
            case WEST:
                UpdateSnake(-1, 0);
                break;
            case SOUTH:
                UpdateSnake(0, 1);
                break;
            case EAST:
                UpdateSnake(1, 0);
                break;
            case NORTH:
                UpdateSnake(0, -1);
                break;
        }


        Coordinate snakeHeadPosition = snake.get(0);

        for (Coordinate wall : walls) {
            if (snakeHeadPosition.equals(wall)) {
                if (wall.getX() == 0) {
                    snake.get(0).setX(GameWidth - 2);
                    break;
                } else if (wall.getX() == GameWidth - 1) {
                    snake.get(0).setX(1);
                    break;
                } else if (wall.getY() == 0) {
                    snake.get(0).setY(GameHeight - 2);
                    break;
                } else if (wall.getY() == GameHeight - 1) {
                    snake.get(0).setY(1);
                    break;
                }
            }

        }

        for (Coordinate obstacle : obstacles) {
            if (snakeHeadPosition.equals(obstacle)) {
                currentGameState = GameState.Lost;
                return;
            }
        }

        for (int i = 1; i < snake.size(); i++) {
            if (snakeHeadPosition.equals(snake.get(i))) {
                currentGameState = GameState.Lost;
                return;
            }
        }

        Coordinate appleToRemove = null;
        for (Coordinate apple : apples) {
            if (snakeHeadPosition.equals(apple)) {
                appleToRemove = apple;
                increaseTail = true;
                break;
            }
        }
        if (appleToRemove != null) {
            snakeSpeed -= 5;
            apples.remove(appleToRemove);
            AddApples();
        }
    }

    public int getPoints() {
        return points;
    }

    private void UpdateSnake(int x, int y) {
        snake.add(0, new Coordinate(snake.get(0).getX() + x, snake.get(0).getY() + y));
        if (!increaseTail) {
            snake.remove(snake.size() - 1);
        } else {
            points++;
            increaseTail = false;
        }

        canChangeDirection = true;
    }

    public TileType[][] getMap() {
        TileType[][] map = new TileType[GameWidth][GameHeight];

        for (int x = 0; x < GameWidth; x++) {
            for (int y = 0; y < GameHeight; y++) {
                map[x][y] = TileType.Nothing;
            }
        }

        for (Coordinate s : snake) {
            map[s.getX()][s.getY()] = TileType.SnakeTail;
        }

        for (Coordinate apple : apples) {
            map[apple.getX()][apple.getY()] = TileType.Apple;
        }

        map[snake.get(0).getX()][snake.get(0).getY()] = TileType.SnakeHead;

        for (Coordinate wall : walls) {
            map[wall.getX()][wall.getY()] = TileType.Wall;
        }

        for (Coordinate obstacle : obstacles) {
            map[obstacle.getX()][obstacle.getY()] = TileType.Obstacle;
        }

        return map;
    }

    private void AddSnake() {
        snake.clear();
        snake.add(new Coordinate(16, 7));
        snake.add(new Coordinate(15, 7));
//        snake.add(new Coordinate(14, 7));
//        snake.add(new Coordinate(13, 7));
//        snake.add(new Coordinate(12, 7));
//        snake.add(new Coordinate(11, 7));
//        snake.add(new Coordinate(10, 7));
//        snake.add(new Coordinate(9, 7));
//        snake.add(new Coordinate(8, 7));
//        snake.add(new Coordinate(7, 7));
//        snake.add(new Coordinate(6, 7));
//        snake.add(new Coordinate(5, 7));
//        snake.add(new Coordinate(4, 7));
//        snake.add(new Coordinate(3, 7));
//        snake.add(new Coordinate(2, 7));
//        snake.add(new Coordinate(1, 7));

//        apples.add(new Coordinate(5, 9));
    }

    private void AddWalls() {
        //Top and bot walls
        for (int x = 0; x < GameWidth; x++) {
            walls.add(new Coordinate(x, 0));
            walls.add(new Coordinate(x, GameHeight - 1));
        }
        //Left and right walls
        for (int y = 1; y < GameHeight; y++) {
            walls.add(new Coordinate(0, y));
            walls.add(new Coordinate(GameWidth - 1, y));
        }

    }


    private void AddObstacles() {
        obstacles.add(new Coordinate(5, 5));
        obstacles.add(new Coordinate(6, 5));
        obstacles.add(new Coordinate(7, 5));
        obstacles.add(new Coordinate(8, 5));
        obstacles.add(new Coordinate(9, 5));
        obstacles.add(new Coordinate(10, 5));
        obstacles.add(new Coordinate(11, 5));
        obstacles.add(new Coordinate(11, 6));
        obstacles.add(new Coordinate(11, 7));
        obstacles.add(new Coordinate(11, 8));
        obstacles.add(new Coordinate(11, 9));
        obstacles.add(new Coordinate(11, 10));
    }

    private void AddApples() {
        Coordinate coordinate = null;

        boolean added = false;

        while (!added) {
            int x = 1 + random.nextInt(GameWidth - 2);
            int y = 1 + random.nextInt(GameHeight - 2);

            coordinate = new Coordinate(x, y);
            boolean collision = false;

            for (Coordinate s : snake) {
                if (s.equals(coordinate)) {
                    collision = true;
                    break;
                }
            }

            for (Coordinate apple : apples) {
                if (apple.equals(coordinate)) {
                    collision = true;
                    break;
                }
            }

            for (Coordinate obstacle : obstacles) {
                if (obstacle.equals(coordinate)) {
                    collision = true;
                    break;
                }
            }

            added = !collision;
        }

        apples.add(coordinate);
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public int getSnakeSpeed() {
        return snakeSpeed;
    }


    public static int getGameWidth() {
        return GameWidth;
    }

    public static int getGameHeight() {
        return GameHeight;
    }
}
