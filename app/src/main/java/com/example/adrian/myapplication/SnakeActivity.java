package com.example.adrian.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.AsyncLayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.adrian.myapplication.engine.SnakeEngine;
import com.example.adrian.myapplication.util.snake.enums.Direction;
import com.example.adrian.myapplication.util.snake.enums.GameState;
import com.example.adrian.myapplication.ui.SnakeView;

public class SnakeActivity extends Activity {

    private SnakeEngine snakeEngine;
    private SnakeView snakeView;
    private Button moveUp;
    private Button moveR;
    private Button moveD;
    private Button moveL;

    private final Handler handler = new Handler();

    private float previousX, previousY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.activity_snake);

        snakeEngine = new SnakeEngine();
        snakeEngine.initGame();

        snakeView = (SnakeView) findViewById(R.id.snakeView);
        moveUp = (Button) findViewById(R.id.moveU);
        moveR = (Button) findViewById(R.id.moveR);
        moveD = (Button) findViewById(R.id.moveD);
        moveL = (Button) findViewById(R.id.moveL);

        controlSnake();

        startUpdateHandler();
    }

    private void controlSnake() {
        snakeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        previousX = event.getX();
                        previousY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float newX = event.getX();
                        float newY = event.getY();
                        moveSnake(previousX, previousY, newX, newY);
                        break;
                }
                return true;
            }
        });

        moveUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    snakeEngine.UpdateDirection(Direction.NORTH);
                }
                return true;
            }
        });

        moveR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    snakeEngine.UpdateDirection(Direction.EAST);
                }
                return true;
            }
        });

        moveD.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    snakeEngine.UpdateDirection(Direction.SOUTH);
                }
                return true;
            }
        });

        moveL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    snakeEngine.UpdateDirection(Direction.WEST);
                }
                return true;
            }
        });

//        moveL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                snakeEngine.UpdateDirection(Direction.WEST);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        onGameLost();
        super.onBackPressed();
    }

    private void startUpdateHandler() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snakeEngine.Update();

                if (snakeEngine.getCurrentGameState() == GameState.Running) {
                    handler.postDelayed(this, snakeEngine.getSnakeSpeed());
                }

                if (snakeEngine.getCurrentGameState() == GameState.Lost) {
                    onGameLost();
                }

                snakeView.setSnakeViewMap(snakeEngine.getMap());
                snakeView.invalidate();

            }
        }, snakeEngine.getSnakeSpeed());
    }

    private void onGameLost() {
        Toast.makeText(this, getLoseMessage(), Toast.LENGTH_SHORT).show();
        Intent data = new Intent();
        data.putExtra("score", snakeEngine.getPoints());
        setResult(RESULT_OK, data);
        finish();
    }

    private String getLoseMessage() {
        return "You lost, " + snakeEngine.getPoints();
    }

    private void moveSnake(float previousX, float previousY, float newX, float newY) {
        if (Math.abs(newX - previousX) > Math.abs(newY - previousY)) {
            if (newX < previousX) {
                snakeEngine.UpdateDirection(Direction.WEST);
            } else {
                snakeEngine.UpdateDirection(Direction.EAST);
            }
        } else {
            if (newY < previousY) {
                snakeEngine.UpdateDirection(Direction.NORTH);
            } else {
                snakeEngine.UpdateDirection(Direction.SOUTH);
            }

        }
    }


}
