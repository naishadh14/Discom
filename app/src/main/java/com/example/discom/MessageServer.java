package com.example.discom;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MessageServer extends Thread {
    private BluetoothSocket socket;
    private Handler handler;

    MessageServer(BluetoothSocket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    public void run() {
        byte[] encodedJSON, decodedJSON;
        JSONObject jsonObject;
        InputStream inputStream;
        try {
            inputStream = this.socket.getInputStream();
        } catch(IOException e) {
            Log.e(Constants.TAG, "Server: Error getting InputStream");
            sendMessage(Constants.JSON_RECEIVE_FAIL);
            return;
        }
        try {
            encodedJSON = new byte[Constants.MAX_MESSAGE_SIZE];
            inputStream.read(encodedJSON);
            Log.e(Constants.TAG, "Server: Message successfully read");
        } catch (IOException e) {
            Log.e(Constants.TAG, "Error reading message");
            sendMessage(Constants.JSON_RECEIVE_FAIL);
            return;
        }
        try {
            decodedJSON = android.util.Base64.decode(encodedJSON, android.util.Base64.DEFAULT);
            String jsonText = new String(decodedJSON);
            jsonObject = new JSONObject(jsonText);
            Log.e(Constants.TAG, "Server: Message successfully decoded");
            Log.e(Constants.TAG, jsonText);
        } catch(JSONException e) {
            Log.e(Constants.TAG, "Error parsing JSON");
            sendMessage(Constants.JSON_RECEIVE_FAIL);
            return;
        }
        sendMessageWithObject(Constants.JSON_OBJECT_RECEIVE, jsonObject);
        Log.e(Constants.TAG, "Server: Message sent up to main thread");
    }

    private void sendMessage(int ACTION) {
        Message msg = new Message();
        msg.what = ACTION;
        this.handler.sendMessage(msg);
    }

    private void sendMessageWithObject(int ACTION, JSONObject jsonObject) {
        Message msg = new Message();
        msg.what = ACTION;
        msg.obj = jsonObject;
        handler.sendMessage(msg);
    }
}
