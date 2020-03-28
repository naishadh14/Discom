package com.example.discom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.UUID;

public class BluetoothServer extends Thread {
    private BluetoothServerSocket bluetoothServer = null;
    private Handler handler;

    public BluetoothServer(Handler handler, int channel_num) {
        UUID uuid = UUID.fromString(Constants.MY_UUID_STRING);
        BluetoothServerSocket tmp = null;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.handler = handler;
        String channel = "CHANNEL_" + Integer.toString(channel_num);
        sendMessageUp(Constants.SERVER_GETTING_ADAPTER);
        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(channel, uuid);
            sendMessageUp(Constants.SERVER_CREATING_CHANNEL);
            bluetoothServer = tmp;
        } catch (IOException e) {
            sendMessageUp(Constants.SERVER_CREATING_CHANNEL_FAIL);
        }
    }

    public void run() {
        BluetoothSocket socket = null;
        while(true) {
            try {
                sendMessageUp(Constants.SERVER_WAITING_DEVICE);
                socket = bluetoothServer.accept();
            } catch (IOException e) {
                sendMessageUp(Constants.SERVER_ACCEPT_FAIL);
                break;
            }
            if(socket != null) {
                //manage socket in separate thread
                sendMessageUp(Constants.SERVER_DEVICE_CONNECTED);
                //close the socket, since only one connection per socket
                try {
                    bluetoothServer.close();
                    sendMessageUp(Constants.SERVER_SOCKET_CLOSED);
                } catch (IOException e) {
                    sendMessageUp(Constants.SERVER_SOCKET_CLOSE_FAIL);
                }
                break;
            }
        }
    }

    public void cancel() {
        try {
            bluetoothServer.close();
        } catch (IOException e) {
            sendMessageUp(Constants.SERVER_SOCKET_CLOSE_FAIL);
        }
    }

    private void sendMessageUp(int ACTION) {
        Message msg = new Message();
        msg.what = ACTION;
        handler.sendMessage(msg);
    }
}
