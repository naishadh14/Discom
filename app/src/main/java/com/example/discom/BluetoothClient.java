package com.example.discom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothClient extends Thread {
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private Handler handler;

    public BluetoothClient(BluetoothDevice device, Handler handler) {
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
            Message msg = new Message();
            msg.what = Constants.DEFAULT;
            msg.obj = this.bluetoothDevice;
            handler.sendMessage(msg);
        } catch (IOException e) {
            sendMessageUp(Constants.CLIENT_CONNECTION_FAIL);
            try {
                bluetoothSocket.close();
            } catch (IOException ex) {
                sendMessageUp(Constants.CLIENT_SOCKET_CLOSE_FAIL);
            }
            return;
        }
        //manage socket
    }

    public void cancel() {
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
