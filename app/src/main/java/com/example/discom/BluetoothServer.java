package com.example.discom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothServer extends Thread {
    private BluetoothServerSocket bluetoothServer = null;
    private static final String MY_UUID_STRING = "48ccdb6c-7bab-45f6-bd68-f7e5fb8d06fe";
    private static final String CHANNEL_NAME = "CHANNEL_1";
    private static final String TAG = "DJ@H#BD";

    public BluetoothServer() {
        UUID uuid = UUID.fromString(MY_UUID_STRING);
        BluetoothServerSocket tmp = null;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(CHANNEL_NAME, uuid);
            Log.e(TAG, "RFCOMM server channnel created");
            bluetoothServer = tmp;
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
    }

    public void run() {
        BluetoothSocket socket = null;
        while(true) {
            try {
                socket = bluetoothServer.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }
            if(socket != null) {
                //manage socket
                try {
                    bluetoothServer.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close the connect socket", e);
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
}
