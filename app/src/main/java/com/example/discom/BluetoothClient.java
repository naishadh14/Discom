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
            Log.e(TAG, "RFCOMM Client channel created");
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
    }

    public void run() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();

        try {
            bluetoothSocket.connect();
        } catch (IOException e) {
            Log.e(TAG, "Could not connect", e);
            try {
                bluetoothSocket.close();
            } catch (IOException ex) {
                Log.e(TAG, "Could not close the client socket", e);
            }
            return;
        }
        //manage socket
    }

    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
