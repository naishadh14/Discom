package com.example.discom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothServer extends Thread {
    private BluetoothServerSocket bluetoothServer = null;
    private static final String MY_UUID_STRING = Constants.MY_UUID_STRING;
    private static final String CHANNEL_NAME = Constants.CHANNEL_1;
    private static final String TAG = Constants.TAG;
    private static final int GETTING_ADAPTER = Constants.GETTING_ADAPTER;
    Handler handler;

    public BluetoothServer(Handler handler) {
        UUID uuid = UUID.fromString(MY_UUID_STRING);
        BluetoothServerSocket tmp = null;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.handler = handler;
        sendMessageUp(GETTING_ADAPTER);
        Log.e(TAG, "Server: Getting adapter");
        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(CHANNEL_NAME, uuid);
            Log.e(TAG, "Server: Created RFCOMM channel");
            bluetoothServer = tmp;
        } catch (IOException e) {
            Log.e(TAG, "Server: Could not create RFCOMM channel", e);
        }
    }

    public void run() {
        BluetoothSocket socket = null;
        while(true) {
            try {
                Log.e(TAG, "Server: Waiting for device");
                socket = bluetoothServer.accept();
            } catch (IOException e) {
                Log.e(TAG, "Server: Socket's accept() method failed", e);
                break;
            }
            if(socket != null) {
                //manage socket in separate thread
                Log.e(TAG, "Server: Device connected");
                //close the socket, since only one connection per socket
                try {
                    Log.e(TAG, "Server: Attempting to close socket, since connection already found");
                    bluetoothServer.close();
                } catch (IOException e) {
                    Log.e(TAG, "Server: Could not close socket, but connection found", e);
                }
                break;
            }
        }
    }

    public void cancel() {
        try {
            bluetoothServer.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

    private void sendMessageUp(int ACTION) {
        Message msg = new Message();
        msg.what = ACTION;
        handler.sendMessage(msg);
    }
}
