package com.example.discom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BluetoothClient extends Thread {
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private Handler handler;

    BluetoothClient(BluetoothDevice device, Handler handler) {
        UUID uuid = UUID.fromString(Constants.MY_UUID_STRING);
        BluetoothSocket tmp = null;
        bluetoothDevice = device;
        this.handler = handler;

        try {
            tmp = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket = tmp;
            sendMessageUp(Constants.CLIENT_CREATING_CHANNEL);
        } catch (IOException e) {
            sendMessageUp(Constants.CLIENT_CREATING_CHANNEL_FAIL);
        }
    }

    public void run() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();

        try {
            sendMessageUp(Constants.CLIENT_ATTEMPTING_CONNECTION);
            bluetoothSocket.connect();
            sendMessageUp(Constants.CLIENT_CONNECTED);

            //send notification to handler about device connection
            Message msg = new Message();
            msg.what = Constants.DEVICE_INFO;
            msg.obj = this.bluetoothDevice;
            handler.sendMessage(msg);

            //manage socket
            Message msg2 = new Message();
            msg.what = Constants.SOCKET;
            msg.obj = bluetoothSocket;
            handler.sendMessage(msg2);

        } catch (IOException e) {
            sendMessageUp(Constants.CLIENT_CONNECTION_FAIL);
            try {
                bluetoothSocket.close();
            } catch (IOException ex) {
                sendMessageUp(Constants.CLIENT_SOCKET_CLOSE_FAIL);
            }
        }
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Log.e(Constants.TAG, "Error in sleeping thread");
        }
        cancel();
    }

    private void cancel() {
        try {
            bluetoothSocket.close();
            sendMessageUp(Constants.CLIENT_CLOSING_SOCKET);
        } catch (IOException e) {
            sendMessageUp(Constants.CLIENT_SOCKET_CLOSE_FAIL);
        }
    }

    private void sendMessageUp(int ACTION) {
        Message msg = new Message();
        msg.what = ACTION;
        handler.sendMessage(msg);
    }
}
