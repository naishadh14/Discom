package com.example.discom;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MessageClient extends Thread {
    private JSONObject jsonObject;
    private BluetoothSocket socket;
    private int count;

    MessageClient(BluetoothSocket socket, JSONObject jsonObject, int count) {
        this.socket = socket;
        this.jsonObject = jsonObject;
        this.count = count;
    }

    public void run() {
        if(this.count == 0)
            return;
        OutputStream outputStream;
        final InputStream inputStream;
        final byte[] encodedJSON, receivedJSON, decodedJSON;
        receivedJSON = new byte[Constants.MAX_MESSAGE_SIZE];

        //try to send message to server
        try {
            outputStream = this.socket.getOutputStream();
            String jsonText = jsonObject.toString();
            encodedJSON = android.util.Base64.encode(jsonText.getBytes(), android.util.Base64.DEFAULT);
            outputStream.write(encodedJSON);
            Log.e(Constants.TAG, "Client: Message sent successfully");
        } catch (IOException e) {
            Log.e(Constants.TAG, "Error sending JSON to client");
            close();
            return;
        }

        /*
        //wait for ACK from server, or timeout and run thread again after 2 seconds
        //thread runs maximum of 3 times
        try {
            inputStream = this.socket.getInputStream();
            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<Integer> task = new Callable<Integer>() {
                public Integer call() throws IOException {
                    return inputStream.read(receivedJSON);
                }
            };
            Future<Integer> future = executor.submit(task);
            Integer numBytes = future.get(2, TimeUnit.SECONDS);
        } catch(TimeoutException e) {
            //retry message sending
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            Log.e(Constants.TAG, "MessageClient: Starting new thread due to ACK TIMEOUT");
            MessageClient messageClient =
                    new MessageClient(socket, jsonObject, this.count - 1);
            messageClient.start();
            return;
        } catch (IOException e) {
            Log.e(Constants.TAG, "MessageClient: Error getting InputStream or reading" + this.count);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(Constants.TAG, "MessageClient: Future Executor error");
        }


        //decode receivedJSON
        try {
            //for now, assuming that anything received was ACK
            decodedJSON = android.util.Base64.decode(encodedJSON, android.util.Base64.DEFAULT);
            String jsonText = new String(decodedJSON);
            jsonObject = new JSONObject(jsonText);
            Log.e(Constants.TAG, "MessageClient: ACK received");
            Log.e(Constants.TAG, jsonText);
        } catch (JSONException e) {
            Log.e(Constants.TAG, "MessageClient: Error parsing JSON");
        }
        close();
         */
        close();
    }

    private void close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            Log.e(Constants.TAG, "Error closing socket");
        }
    }
}
