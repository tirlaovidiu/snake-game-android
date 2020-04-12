package com.example.adrian.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.adrian.myapplication.engine.SnakeMultiPlayer;
import com.example.adrian.myapplication.socket.ConnectionStatus;
import com.example.adrian.myapplication.socket.SingletonClient;
import com.example.adrian.myapplication.socket.StompMessage;
import com.example.adrian.myapplication.socket.StompMessageListener;
import com.example.adrian.myapplication.socket.TopicHandler;
import com.example.adrian.myapplication.socket.WebSocketClient;
import com.example.adrian.myapplication.ui.SnakeMultiPlayerView;

import com.example.adrian.myapplication.util.common.EventType;
import com.example.adrian.myapplication.util.common.ClientMessage;
import com.example.adrian.myapplication.util.common.LobbyMessage;
import com.example.adrian.myapplication.util.snake.SnakeMessage;
import com.example.adrian.myapplication.util.snake.enums.Direction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static android.os.SystemClock.sleep;
import static com.example.adrian.myapplication.util.Constants.SERVER_ADDRESS;


/*
 ******************************
 # Created by Tirla Ovidiu #
 # 25.01.2018 #
 ******************************
*/
public class SnakeMultiPlayerActivity extends Activity {
    private static final String TAG = "SNAKE";

    private WebSocketClient client = SingletonClient.getClient();
    private Gson gson = new GsonBuilder().create();

    SnakeMultiPlayer snakeMultiPlayer = new SnakeMultiPlayer();
    private SnakeMultiPlayerView snakeMultiPlayerView;
    private Button moveUp;
    private Button moveR;
    private Button moveD;
    private Button moveL;
    private float previousX, previousY;

    private static String clientId = SingletonClient.getClient().getUsername();

    private EventType currentEvent = EventType.HELLO_EVENT;

    private Integer playerId = 1;
    private String lobbyId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_snake_multiplayer);

        currentEvent = EventType.HELLO_EVENT;

        findLobby();

        snakeMultiPlayerView = (SnakeMultiPlayerView) findViewById(R.id.snakeMultiPlayerView);
        moveUp = (Button) findViewById(R.id.moveU);
        moveR = (Button) findViewById(R.id.moveR);
        moveD = (Button) findViewById(R.id.moveD);
        moveL = (Button) findViewById(R.id.moveL);

        controlSnake();

    }

    private void findLobby() {
        final TopicHandler errorHandler = client.subscribe("/user/queue/error");
        errorHandler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                Log.d("SERVER-ERROR", message.toString());
            }
        });

        final TopicHandler replayHandler = client.subscribe("/user/queue/reply");
        replayHandler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                Log.d("SERVER-REPLAY", message.toString());

            }
        });

        final TopicHandler lobbyHandler = client.subscribe("/topic/queue/" + clientId);
        lobbyHandler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                LobbyMessage lobbyMessage = gson.fromJson(message.getContent(), LobbyMessage.class);
                Log.d("LOBBY", "Received LOBBY ID: " + lobbyMessage.toString());
                client.unSubscribe(lobbyHandler);
                Log.d("LOBBY", lobbyMessage.getTopic());
                playerId = lobbyMessage.getPlayerId();
                Log.e("PLAYER-ID", String.valueOf(playerId));
                playOnLobby(lobbyMessage.getTopic());
            }
        });

        client.sendMessage("/app/snake/queue/" + clientId, new ClientMessage(clientId, "I want to play snake", clientId, EventType.HELLO_EVENT));
    }

    private void playOnLobby(String topic) {
        lobbyId = topic;
        TopicHandler snakeGameHandler = client.subscribe("/topic/snake/" + topic);
        snakeGameHandler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                handleMessage(message.getContent());
            }
        });
        ClientMessage clientMessage = new ClientMessage(clientId, "Play snake", lobbyId, currentEvent);
        sendGameMessage(clientMessage);
    }

    private void handleMessage(String message) {
        Log.d(TAG, "Received " + message);
        SnakeMessage serverMessage = gson.fromJson(message, SnakeMessage.class);
        if (serverMessage.getEvent() != null)
            currentEvent = serverMessage.getEvent();
        if (serverMessage.getEvent() == EventType.GAME_FIND_LOBBY) {
            findLobby();
        } else if (currentEvent == EventType.HELLO_EVENT) {
//            playerId = 1;
//            toast("Wait for other players to connect !");
        } else if (currentEvent == EventType.START_GAME_EVENT) {
            if (playerId == 0) {
                snakeMultiPlayer.init(serverMessage.getSnakeP1(), serverMessage.getP1CurrentDirection(), serverMessage.getWalls(), serverMessage.getObstacles(), serverMessage.getApples(), serverMessage.getSnakeP2());
            } else {
                snakeMultiPlayer.init(serverMessage.getSnakeP2(), serverMessage.getP2CurrentDirection(), serverMessage.getWalls(), serverMessage.getObstacles(), serverMessage.getApples(), serverMessage.getSnakeP1());
            }
            sendGameMessage(new ClientMessage(clientId, "Start", lobbyId, currentEvent));
        } else if (currentEvent == EventType.RUNNING_GAME_EVENT) {
            if (serverMessage.getMessage().equals("UPDATE")) {
                Log.d("SERVER", "Update");
                if (playerId == 0) {
                    snakeMultiPlayer.updateSnake(serverMessage.getP1NextCoordinate(), serverMessage.getGrowP1());
                    snakeMultiPlayer.updateEnemySnake(serverMessage.getP2NextCoordinate(), serverMessage.getGrowP2());
                } else {
                    snakeMultiPlayer.updateSnake(serverMessage.getP2NextCoordinate(), serverMessage.getGrowP2());
                    snakeMultiPlayer.updateEnemySnake(serverMessage.getP1NextCoordinate(), serverMessage.getGrowP1());
                }
                snakeMultiPlayer.updateApples(serverMessage.getApples());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateView();
                    }
                });
            }
        } else if (currentEvent == EventType.GAME_OVER_EVENT) {
            snakeMultiPlayer.gameLost();
        }

    }

    private void updateView() {
        snakeMultiPlayerView.setSnakeViewMap(snakeMultiPlayer.getMap());
        snakeMultiPlayerView.invalidate();
    }

    public void sendGameMessage(ClientMessage clientMessage) {
        client.sendMessage("/app/snake/lobby/" + lobbyId, clientMessage);
    }

    private void toast(String text) {
        Log.i(TAG, text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        disconnectStomp();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        disconnectStomp();
        super.onBackPressed();
    }

    public void disconnectStomp() {
        if (client.isConnected()) {
            client.disconnect();
            toast("Success disconnected");
        }
    }


    private void controlSnake() {
        snakeMultiPlayerView.setOnTouchListener(new View.OnTouchListener() {
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
                    UpdateDirection(Direction.NORTH);
                }
                return true;
            }
        });

        moveR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    UpdateDirection(Direction.EAST);
                }
                return true;
            }
        });

        moveD.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    UpdateDirection(Direction.SOUTH);
                }
                return true;
            }
        });

        moveL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    UpdateDirection(Direction.WEST);
                }
                return true;
            }

        });
    }


    private void moveSnake(float previousX, float previousY, float newX, float newY) {
        if (Math.abs(newX - previousX) > Math.abs(newY - previousY)) {
            if (newX < previousX) {
                UpdateDirection(Direction.WEST);
            } else {
                UpdateDirection(Direction.EAST);
            }
        } else {
            if (newY < previousY) {
                UpdateDirection(Direction.NORTH);
            } else {
                UpdateDirection(Direction.SOUTH);
            }

        }
    }

    private void UpdateDirection(Direction direction) {
        sendGameMessage(new ClientMessage(clientId, "Update", lobbyId, EventType.RUNNING_GAME_EVENT, direction));
    }

}