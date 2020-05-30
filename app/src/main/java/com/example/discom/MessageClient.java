package com.example.discom;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
        final byte[] receivedJSON = new byte[Constants.MAX_MESSAGE_SIZE];

        //try to send message to server or throw error
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

        //wait for ACK from server or throw error
        try {
            final InputStream inputStream = this.socket.getInputStream();
            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<Integer> task = new Callable<Integer>() {
                public Integer call() throws IOException {
                    return inputStream.read(receivedJSON);
                }
            };
            Future<Integer> future = executor.submit(task);
            Integer numBytes = future.get(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Error getting ACK from server");
            sendMessage(Constants.JSON_SEND_FAIL);
            return;
        }

        //try to close socket
        try {
            this.socket.close();
        } catch (IOException e) {
            Log.e(Constants.TAG, "Error closing socket");
        }

        //inform up that message was sent successfully
        sendMessage(Constants.JSON_SENT);
    }

    private void sendMessage(int ACTION) {
        Message msg = new Message();
        msg.what = ACTION;
        this.handler.sendMessage(msg);
    }
}
