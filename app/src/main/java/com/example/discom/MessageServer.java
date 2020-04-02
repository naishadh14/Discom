package com.example.discom;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

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

        //get InputStream or throw error
        try {
            inputStream = this.socket.getInputStream();
            encodedJSON = new byte[Constants.MAX_MESSAGE_SIZE];
        } catch(IOException e) {
            Log.e(Constants.TAG, "Server: Error getting InputStream");
            return;
        }

        //try reading from stream thrice before quitting
        try {
            //first try
            inputStream.read(encodedJSON);
            Log.e(Constants.TAG, "Server: Message successfully read");
            cancel();
        } catch (IOException e) {
            Log.e(Constants.TAG, "Error reading message #1");
            Message msg = new Message();
            msg.what = Constants.MESSAGE_READ_RETRY;
            handler.sendMessage(msg);

            //second try
            try {
                pause();
                inputStream.read(encodedJSON);
                Log.e(Constants.TAG, "Server: Message successfully read");
                cancel();
            } catch (IOException e2) {
                Log.e(Constants.TAG, "Error reading message #2");
                msg = new Message();
                msg.what = Constants.MESSAGE_READ_RETRY;
                handler.sendMessage(msg);

                //third try
                try {
                    pause();
                    inputStream.read(encodedJSON);
                    Log.e(Constants.TAG, "Server: Message successfully read");
                    cancel();
                } catch (IOException e3) {
                    Log.e(Constants.TAG, "Error reading message #3");
                    msg = new Message();
                    msg.what = Constants.MESSAGE_READ_FAIL;
                    handler.sendMessage(msg);
                }
            }
        }
        cancel();

        //try to parse JSON or throw error
        try {
            decodedJSON = android.util.Base64.decode(encodedJSON, android.util.Base64.DEFAULT);
            String jsonText = new String(decodedJSON);
            jsonObject = new JSONObject(jsonText);
            Log.e(Constants.TAG, "Server: Message successfully decoded");
            Log.e(Constants.TAG, jsonText);
        } catch(JSONException e) {
            Log.e(Constants.TAG, "Error parsing JSON");
            return;
        }

        //send JSONObject to main thread
        Message msg = new Message();
        msg.what = Constants.JSON_OBJECT_RECEIVE;
        msg.obj = jsonObject;
        handler.sendMessage(msg);
        Log.e(Constants.TAG, "Server: Message sent up to main thread");
    }

    private void cancel() {
        try {
            this.socket.close();
        } catch (IOException e) {
            Log.e(Constants.TAG, "Error closing socket");
        }
    }

    private void pause() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Log.e(Constants.TAG, "Error sleeping thread");
        }
    }
}
