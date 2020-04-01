package com.example.discom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BluetoothServer extends Thread {
    private BluetoothServerSocket bluetoothServer = null;
    private Handler handler;
    private BluetoothSocket socket;

    BluetoothServer(Handler handler, int channel_num, String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
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
                this.socket = socket;
            } catch (IOException e) {
                sendMessageUp(Constants.SERVER_ACCEPT_FAIL);
                break;
            }
            if(socket != null) {
                //send notification to handler about device connection
                sendMessageUp(Constants.SERVER_DEVICE_CONNECTED);
                Message msg = new Message();
                msg.what = Constants.SERVER_DEVICE_INFO;
                msg.obj = socket.getRemoteDevice();
                handler.sendMessage(msg);

                //manage socket
                Message msg2 = new Message();
                msg2.what = Constants.SOCKET;
                msg2.obj = socket;
                handler.sendMessage(msg2);

                //close the socket, since only one connection per socket
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Log.e(Constants.TAG, "Error in sleeping thread");
                }
                cancel();
                break;
            }
        }
    }

    private void cancel() {
        try {
            this.socket.close();
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
