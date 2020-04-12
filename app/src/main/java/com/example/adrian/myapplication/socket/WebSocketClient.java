package com.example.adrian.myapplication.socket;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static com.example.adrian.myapplication.util.Constants.ACCEPT_VERSION;
import static com.example.adrian.myapplication.util.Constants.CONNECT;
import static com.example.adrian.myapplication.util.Constants.CONNECTED;
import static com.example.adrian.myapplication.util.Constants.DESTINATION;
import static com.example.adrian.myapplication.util.Constants.ERROR;
import static com.example.adrian.myapplication.util.Constants.HEART_BEAT;
import static com.example.adrian.myapplication.util.Constants.SEND;
import static com.example.adrian.myapplication.util.Constants.SERVER_ADDRESS;
import static com.example.adrian.myapplication.util.Constants.SERVER_LOGIN_ADDRESS;
import static com.example.adrian.myapplication.util.Constants.SUBSCRIBE;
import static com.example.adrian.myapplication.util.Constants.UNSUBSCRIBE;


public final class WebSocketClient extends WebSocketListener {

    private Map<String, TopicHandler> topics = new HashMap<>();
//    private CloseHandler closeHandler;

    private Integer nextSubId = 0;
    private String username = "guest";
    private String password = "guest";

    private WebSocket webSocket;
    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    //TODO find a better way
    private ChangeListener statusListener = new ChangeListener() {
        @Override
        public void onChange() {
            Log.d("SOCKET", connectionStatus.name());
        }
    };

    public WebSocketClient() {

    }

    public synchronized void connect() {
        if (connectionStatus == ConnectionStatus.ERROR || connectionStatus == ConnectionStatus.DISCONNECTED) {
            connectionStatus = ConnectionStatus.CONNECTING;

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .pingInterval(10, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(SERVER_ADDRESS)
                    .build();

            webSocket = client.newWebSocket(request, this);

            // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
            client.dispatcher().executorService().shutdown();

//            final OkHttpClient client = new OkHttpClient.Builder()
//                    .connectTimeout(10, TimeUnit.SECONDS)
//                    .build();
//
//            RequestBody body = new FormBody.Builder()
//                    .add("username", username)
//                    .add("password", password)
//                    .build();
//            final Request req = new Request.Builder()
//                    .url(SERVER_LOGIN_ADDRESS)
//                    .post(body)
//                    .build();
//
//            final String[] cookie = new String[1];
//
//            Call call = client.newCall(req);
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    connectionStatus = ConnectionStatus.ERROR;
//                    statusListener.onChange();
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) {
//                    if (response.code() != 200) {
//                        connectionStatus = ConnectionStatus.ERROR;
//                        Log.e("REQUEST-CONNECT", "Response: " + response.toString() + username);
//                    } else {
//                        connectionStatus = ConnectionStatus.CONNECTING;
//                        cookie[0] = response.headers("Set-Cookie").get(0);
//                        Log.e("RESPONSE", "Cookie: " + cookie[0]);
//                        connectToSocket(cookie[0]);
//                    }
//                    statusListener.onChange();
//                }
//            });
        }
    }

    private void connectToSocket(String cookies) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .pingInterval(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_ADDRESS)
                .addHeader("Cookie", cookies)
                .build();

        webSocket = client.newWebSocket(request, this);

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();

    }


    @Override
    public void onOpen(final WebSocket webSocket, final Response response) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                sendConnectMessage(webSocket);
//                closeHandler = new CloseHandler(webSocket);
            }
        });
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        StompMessage message = StompMessageSerializer.deserialize(text);
        String topic = message.getHeader(DESTINATION);
        if (topics.containsKey(topic)) {
            topics.get(topic).onMessage(message);
        }
        Log.e("MESSAGE", text);
        if (message.getCommand().equals(ERROR)) {
            connectionStatus = ConnectionStatus.ERROR;
            disconnect();
        }
        if (!connectionStatus.equals(ConnectionStatus.CONNECTED)) {
            if (message.getCommand().equals(CONNECTED)) {
                connectionStatus = ConnectionStatus.CONNECTED;
            }
            statusListener.onChange();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("MESSAGE: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, reason);
        connectionStatus = ConnectionStatus.DISCONNECTED;
        statusListener.onChange();
        Log.d("[CLIENT]", "CLOSE: " + code + " " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
        Log.e("[OnFailure]", "NOT NUll: " + t.getMessage());
        if (t.getClass() == SocketTimeoutException.class)
            connectionStatus = ConnectionStatus.TIMEOUT;
        else
            connectionStatus = ConnectionStatus.ERROR;
        statusListener.onChange();
        disconnect();
    }

    private void sendConnectMessage(WebSocket webSocket) {
        StompMessage message = new StompMessage(CONNECT);
        message.put(ACCEPT_VERSION, "1.1");
        message.put(HEART_BEAT, "10000,10000");
//        message.put("login", username);
//        message.put("passcode", password);
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    public TopicHandler subscribe(String topic) {
        TopicHandler handler = new TopicHandler(topic, nextSubId.toString());
        topics.put(handler.getTopic(), handler);
        if (webSocket != null) {
            sendSubscribeMessage(webSocket, topic, nextSubId.toString());
        }
        //TODO find a better way
        nextSubId++;
        return handler;
    }

    public TopicHandler getTopicHandler(String topic) {
        if (topics.containsKey(topic)) {
            return topics.get(topic);
        }
        //TODO investigate null
        return null;
    }

    public void unSubscribe(TopicHandler topicHandler) {
        sendUnSubscribeMessage(topicHandler.getTopic(), topicHandler.getSubId());
        topics.remove(topicHandler.getTopic());
    }

    private void sendUnSubscribeMessage(String topic, String id) {
        StompMessage message = new StompMessage(UNSUBSCRIBE);
        message.put("id", id);
        message.put(DESTINATION, topic);
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    private void sendSubscribeMessage(WebSocket webSocket, String topic, String id) {
        StompMessage message = new StompMessage(SUBSCRIBE);
        message.put("id", id);
        message.put(DESTINATION, topic);
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    public void sendMessage(String topic, Object object) {
        StompMessage message = new StompMessage(SEND);
        message.put(DESTINATION, topic);
        message.setContent(new Gson().toJson(object));
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "client request");
        }
        webSocket = null;
    }

    public boolean isConnected() {
        return connectionStatus.equals(ConnectionStatus.CONNECTED) && webSocket != null;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setStatusListener(ChangeListener statusListener) {
        this.statusListener = statusListener;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}