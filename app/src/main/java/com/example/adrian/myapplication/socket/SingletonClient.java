package com.example.adrian.myapplication.socket;

/*
 ******************************
 # Created by Tirla Ovidiu #
 # 25.01.2018 #
 ******************************
*/


public class SingletonClient {
    private static WebSocketClient client;

    private SingletonClient() {
    }

    public static synchronized WebSocketClient getClient() {
        if (client == null) {
            client = new WebSocketClient();
            client.connect();
        }
        return client;
    }

}
