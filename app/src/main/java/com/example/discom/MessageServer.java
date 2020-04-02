package com.example.discom;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class MessageServer extends Thread {
    private BluetoothSocket socket;
    private Handler handler;
    private int count;

    MessageServer(BluetoothSocket socket, Handler handler, int count) {
        this.socket = socket;
        this.handler = handler;
        this.count = count;
    }

    public void run() {
        if(count == 0)
            return;
        byte[] encodedJSON, decodedJSON;
        JSONObject jsonObject;
        InputStream inputStream;
        OutputStream outputStream;

        //get InputStream or throw error
        try {
            inputStream = this.socket.getInputStream();
            encodedJSON = new byte[Constants.MAX_MESSAGE_SIZE];
        } catch(IOException e) {
            Log.e(Constants.TAG, "MessageServer: Error getting InputStream");
            return;
        }

        //try reading from stream
        try {
            inputStream.read(encodedJSON);
            Log.e(Constants.TAG, "MessageServer: Message successfully read");

            //send ACK back to client
            Log.e(Constants.TAG, "MessageServer: Sending ACK");
            outputStream = this.socket.getOutputStream();
            outputStream.write(encodedJSON);
            Log.e(Constants.TAG, "MessageServer: ACK Sent");
        } catch (IOException e) {
            Log.e(Constants.TAG, "MessageServer: Error reading message");
            Message msg = new Message();
            msg.what = Constants.MESSAGE_READ_RETRY;
            handler.sendMessage(msg);

            //sleep for 1 second before retrying
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            MessageServer messageServer = new MessageServer(socket, this.handler, this.count - 1);
            messageServer.start();
            return;
        }
        cancel();

        //try to parse JSON or throw error
        try {
            decodedJSON = android.util.Base64.decode(encodedJSON, android.util.Base64.DEFAULT);
            String jsonText = new String(decodedJSON);
            jsonObject = new JSONObject(jsonText);
            Log.e(Constants.TAG, "MessageServer: Message successfully decoded");
            Log.e(Constants.TAG, jsonText);
        } catch(JSONException e) {
            Log.e(Constants.TAG, "MessageServer: Error parsing JSON");
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
