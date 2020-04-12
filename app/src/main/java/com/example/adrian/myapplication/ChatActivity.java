package com.example.adrian.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrian.myapplication.engine.SnakeMultiPlayer;
import com.example.adrian.myapplication.socket.ChangeListener;
import com.example.adrian.myapplication.socket.ConnectionStatus;
import com.example.adrian.myapplication.socket.SingletonClient;
import com.example.adrian.myapplication.socket.WebSocketClient;
import com.example.adrian.myapplication.util.Constants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;


import java.util.Arrays;

import static com.example.adrian.myapplication.util.Constants.SERVER_ADDRESS;

/*
 ******************************
 # Created by Tirla Ovidiu #
 # 25.01.2018 #
 ******************************
*/
public class ChatActivity extends Activity {

    private final String TAG = "PUBNUB";
    PNConfiguration pnConfiguration = new PNConfiguration();
    PubNub pubnub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        pnConfiguration.setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(Constants.PUBNUB_PUBLISH_KEY);
        pnConfiguration.setSecure(false);
        pnConfiguration.setUuid("user1");

        pubnub = new PubNub(pnConfiguration);

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    Log.w(TAG, String.valueOf(status.getCategory()));
                    // This event happens when radio / connectivity is lost
                } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    // Connect event. You can do stuff like publish, and know you'll get it.
                    // Or just use the connected event to confirm you are subscribed for
                    // UI / internal notifications, etc
                    if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                        pubnub.publish().channel("channel1").message("hello!!").async(new PNCallback<PNPublishResult>() {
                            @Override
                            public void onResponse(PNPublishResult result, PNStatus status) {
                                // Check whether request successfully completed or not.
                                if (!status.isError()) {
                                    Log.w(TAG, "MESSAGE PUBLISHED");
                                    // ClientMessage successfully published to specified channel.
                                }
                                // Request processing failed.
                                else {
                                    Log.w(TAG, "ERROR PUBLISHING MESSAGE");
                                    // Handle message publish error. Check 'category' property to find out possible issue
                                    // because of which request did fail.
                                    //
                                    // Request can be resent using: [status retry];
                                }
                            }
                        });
                    }
                } else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
                    Log.w(TAG, "CONNECTION LOST(2)");
                    // Happens as part of our regular operation. This event happens when
                    // radio / connectivity is lost, then regained.
                } else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
                    Log.w(TAG, "CONNECTION LOST(3)");
                    // Handle messsage decryption error. Probably client configured to
                    // encrypt messages and on live data feed it received plain text.
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                // Handle new message stored in message.message
                if (message.getChannel() != null) {
                    Log.w(TAG, "CHANNEL " + String.valueOf(message.getChannel()));
                    // ClientMessage has been received on channel group stored in
                    // message.getChannel()
                } else {
                    Log.w(TAG, "SUBSCRIPTION " + String.valueOf(message.getSubscription()));
                    // ClientMessage has been received on channel stored in
                    // message.getSubscription()
                }

                Log.w(TAG, message.getTimetoken() + " " + message.getMessage());
                Log.w(TAG, String.valueOf(message.getUserMetadata()));
                showMessage(message.getMessage());
            /*
                log the following items with your favorite logger
                    - message.getMessage()
                    - message.getSubscription()
                    - message.getTimetoken()
            */
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });


        pubnub.subscribe().channels(Arrays.asList("channel1")).execute();

        Button sendMsg = (Button) findViewById(R.id.sendDataBtn);
        final EditText sendTextMessageText = (EditText) findViewById(R.id.sendMessageText);
        final EditText senderName = (EditText) findViewById(R.id.senderName);
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = String.valueOf(senderName.getText());
                if (name.isEmpty() || name.equals(null))
                    name = "NoName";
                sendMessage(pubnub, String.valueOf(sendTextMessageText.getText()), name);
            }
        });
    }


    private void sendMessage(PubNub pubNub, String message, String name) {
        JsonObject data = new JsonObject();
        data.addProperty("name", name);
        data.addProperty("msg", message);

        pubNub.publish()
                .message(data)
                .channel("channel1")
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {

                    }
                });
    }

    private void showMessage(final JsonElement message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView chatHistoryView = (TextView) findViewById(R.id.chatHistory);
                JsonObject jsonObject = new JsonObject();
                if (message.isJsonObject())
                    jsonObject = message.getAsJsonObject();
                String content = "";
                if (jsonObject.has("name") && jsonObject.has("msg"))
                    content = jsonObject.get("name").getAsString() + ": " + jsonObject.get("msg").getAsString() + '\n' + chatHistoryView.getText();
//                String content = message + "\n" + chatHistoryView.getText();
                chatHistoryView.setText(content);
            }
        });


    }

    @Override
    public void onBackPressed() {
        pubnub.disconnect();
        super.onBackPressed();
    }
}
