package com.example.discom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothClient extends Thread {
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private static final String MY_UUID_STRING = "48ccdb6c-7bab-45f6-bd68-f7e5fb8d06fe";
    private static final String TAG = "DJ@H#BD";

    public BluetoothClient(BluetoothDevice device) {
        UUID uuid = UUID.fromString(MY_UUID_STRING);
        BluetoothSocket tmp = null;
        bluetoothDevice = device;

        try {
            tmp = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket = tmp;
            Log.e(TAG, "Client: RFCOMM channel created");
        } catch (IOException e) {
            Log.e(TAG, "Client: Could not create RFCOMM channel", e);
        }
    }

    public void run() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();

        try {
            Log.e(TAG, "Client: Attempting to connect to device");
            bluetoothSocket.connect();
            Log.e(TAG, "Client: Connected to device!");
        } catch (IOException e) {
            Log.e(TAG, "Client: Could not connect", e);
            try {
                bluetoothSocket.close();
            } catch (IOException ex) {
                Log.e(TAG, "Client: Could not close the socket after connection fail", e);
            }
            return;
        }
        //manage socket
    }

    public void cancel() {
        try {
            bluetoothSocket.close();
            Log.e(TAG, "Closing socket after successful connection");
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
