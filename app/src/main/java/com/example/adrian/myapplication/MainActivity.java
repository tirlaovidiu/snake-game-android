package com.example.adrian.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrian.myapplication.socket.ChangeListener;
import com.example.adrian.myapplication.socket.ConnectionStatus;
import com.example.adrian.myapplication.socket.SingletonClient;
import com.example.adrian.myapplication.socket.WebSocketClient;
import com.example.adrian.myapplication.util.common.ClientMessage;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

public class MainActivity extends Activity {
    Integer highScore = 0;
    String fileName = "score.data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView scoreTextView = (TextView) findViewById(R.id.lastScoreTextView);
        final TextView connectionStatusTextView = (TextView) findViewById(R.id.connectionStatusTextView);
        final Button connectBtn = (Button) findViewById(R.id.connectBtn);
        final Button disconnectBtn = (Button) findViewById(R.id.disconnectBtn);
        final EditText usernameText = (EditText) findViewById(R.id.usernameInput);
        final EditText passwordText = (EditText) findViewById(R.id.passwordInput);

        String dataFromFile = "";
        File file = new File(getFilesDir(), fileName);
        if (file.exists()) {
            dataFromFile = readFromFile(fileName);
            String message = getResources().getString(R.string.highScore) + " " + dataFromFile;
            scoreTextView.setText(message);
            scoreTextView.setVisibility(View.VISIBLE);
            highScore = Integer.valueOf(dataFromFile);
        }

        final WebSocketClient client = SingletonClient.getClient();

        client.setStatusListener(new ChangeListener() {
            @Override
            public void onChange() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectionStatusTextView.setText(client.getConnectionStatus().name());
                    }
                });
            }
        });

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (client.getConnectionStatus() != ConnectionStatus.CONNECTED) {
                    client.setUsername(usernameText.getText().toString());
                    client.setPassword(passwordText.getText().toString());
                    client.connect();
                } else {
                    client.sendMessage("/app/hello", "hi there");
                    Toast.makeText(getApplicationContext(), "Already connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.disconnect();
            }
        });

        Button testBtn = (Button) findViewById(R.id.testBtn);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView scoreTextView = (TextView) findViewById(R.id.lastScoreTextView);

        Integer score = data.getIntExtra("score", 0);
        highScore = Math.max(score, highScore);
        String message = getResources().getString(R.string.lastScore) + " " + score;
        scoreTextView.setText(message);
        scoreTextView.setVisibility(View.VISIBLE);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        try {
            writeToFile(fileName, String.valueOf(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    private void writeToFile(String fileName, String data) throws IOException {
        File outFile = new File(getFilesDir(), fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(outFile, false);
        byte[] contents = data.getBytes();
        fileOutputStream.write(contents);
        fileOutputStream.flush();
        fileOutputStream.close();

    }


    public void startSnake(View view) {
        Intent intent = new Intent(this, SnakeActivity.class);
        startActivityForResult(intent, 2);

    }

    public void startMultiplayer(View view) {
        if (SingletonClient.getClient().getConnectionStatus() == ConnectionStatus.CONNECTED) {
            Intent intent = new Intent(this, SnakeMultiPlayerActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Connect to server first !", Toast.LENGTH_SHORT).show();
        }
    }


    private String readFromFile(String fileName) {
        String ret = "";
        try {
            InputStream inputStream = openFileInput(fileName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void startChat(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}
