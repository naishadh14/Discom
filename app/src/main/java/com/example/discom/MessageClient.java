package com.example.discom;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class MessageClient extends Thread {
    private JSONObject jsonObject;
    private BluetoothSocket socket;

    MessageClient(BluetoothSocket socket, JSONObject jsonObject) {
        this.socket = socket;
        this.jsonObject = jsonObject;
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
            return;
        }
        try {
            this.socket.close();
        } catch (IOException e) {
            Log.e(Constants.TAG, "Error closing socket");
        }
    }
}
