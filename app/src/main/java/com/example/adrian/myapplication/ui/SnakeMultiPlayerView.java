package com.example.adrian.myapplication.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.adrian.myapplication.engine.SnakeEngine;
import com.example.adrian.myapplication.util.snake.enums.TileType;

/*
 ******************************
 # Created by Tirla Ovidiu #
 # 25.01.2018 #
 ******************************
*/
public class SnakeMultiPlayerView extends View {
    private Paint paint = new Paint();
    private TileType snakeViewMap[][];

    public SnakeMultiPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSnakeViewMap(TileType[][] map) {
        this.snakeViewMap = map;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (snakeViewMap != null) {
            float tileSizeX = (canvas.getWidth() * 1.0f) / SnakeEngine.getGameWidth();
            float tileSizeY = (canvas.getHeight() * 1.0f) / SnakeEngine.getGameHeight();

            float circleSize = Math.min(tileSizeX, tileSizeY);

            for (int x = 0; x < SnakeEngine.getGameWidth(); x++) {
                for (int y = 0; y < SnakeEngine.getGameHeight(); y++) {
                    switch (snakeViewMap[x][y]) {
                        case Nothing:
                            paint.setColor(Color.WHITE);
                            break;
                        case Wall:
                            paint.setColor(Color.GRAY);
                            break;
                        case SnakeHead:
                            paint.setColor(Color.MAGENTA);
                            break;
//                        case SnakeTail:
//                            paint.setColor(Color.GREEN);
//                            break;
                        case Apple:
                            paint.setColor(Color.RED);
                            break;
                        case Obstacle:
                            paint.setColor(Color.GREEN);
                            break;
                        case Player1:
                            paint.setColor(Color.BLUE);
                            break;
                        case Player2:
                            paint.setColor(Color.YELLOW);
                            break;
                    }

//                    canvas.drawCircle(x * tileSizeX + tileSizeX / 2f + circleSize / 2, y * tileSizeY + tileSizeY / 2f + circleSize / 2, circleSize / 2, paint);
                    canvas.drawRect(x * tileSizeX, y * tileSizeY, (x + 1) * tileSizeX, (y + 1) * tileSizeY, paint);
                }
            }
        }
    }
}
