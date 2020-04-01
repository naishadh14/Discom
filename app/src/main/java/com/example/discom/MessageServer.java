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
        try {
            InputStream inputStream = this.socket.getInputStream();
            encodedJSON = new byte[Constants.MAX_MESSAGE_SIZE];
            inputStream.read(encodedJSON);
        } catch (IOException e) {
            Log.e(Constants.TAG, "Error receiving message");
            return;
        }
        try {
            decodedJSON = android.util.Base64.decode(encodedJSON, android.util.Base64.DEFAULT);
            String jsonText = new String(decodedJSON);
            jsonObject = new JSONObject(jsonText);
        } catch(JSONException e) {
            Log.e(Constants.TAG, "Error parsing JSON");
            return;
        }
        Message msg = new Message();
        msg.what = Constants.JSON_OBJECT_RECEIVE;
        msg.obj = jsonObject;
        handler.sendMessage(msg);
    }
}
