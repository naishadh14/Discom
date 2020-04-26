package com.example.discom;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class MessageClient extends Thread {
    private JSONObject jsonObject;
    private BluetoothSocket socket;
    private Handler handler;

    MessageClient(BluetoothSocket socket, JSONObject jsonObject, Handler handler) {
        this.socket = socket;
        this.jsonObject = jsonObject;
        this.handler = handler;
    }

    public void run() {
        OutputStream outputStream = null;
        try {
            outputStream = this.socket.getOutputStream();
            String jsonText = jsonObject.toString();
            byte[] encodedJSON = android.util.Base64.encode(jsonText.getBytes(), android.util.Base64.DEFAULT);
            outputStream.write(encodedJSON);
            Log.e(Constants.TAG, "Client: Message sent successfully");
        } catch (IOException e) {
            Log.e(Constants.TAG, "Error sending JSON to client");
            sendMessage(Constants.JSON_SEND_FAIL);
            return;
        }
        try {
            this.socket.close();
        } catch (IOException e) {
            Log.e(Constants.TAG, "Error closing socket");
        }
        sendMessage(Constants.JSON_SENT);
    }

    private void sendMessage(int ACTION) {
        Message msg = new Message();
        msg.what = ACTION;
        this.handler.sendMessage(msg);
    }
}
