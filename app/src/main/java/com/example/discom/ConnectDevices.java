package com.example.discom;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ConnectDevices extends AppCompatActivity {

    ArrayList<BluetoothDevice> discoveredDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_devices);
        startServer();
        this.discoveredDevices = (ArrayList<BluetoothDevice>)getIntent().getSerializableExtra("DeviceList");
        TextView text = (TextView)findViewById(R.id.textView5);
        if(this.discoveredDevices == null)
            text.append("Null list received");
        else {
            text.append("\n");
            int len = this.discoveredDevices.size();
            BluetoothDevice device;
            for(int i = 0; i < len; i++) {
                device = this.discoveredDevices.get(i);
                text.append("Device: ");
                text.append(device.getName());
                text.append("\n");
                text.append("Address: ");
                text.append(device.getAddress());
                text.append("\n");
            }
        }
    }

    public void startClient(View view) {
        int len = this.discoveredDevices.size();
        for(int i = 0; i < len; i++) {
            BluetoothClient bluetoothClient = new BluetoothClient(this.discoveredDevices.get(i));
            bluetoothClient.start();
        }
    }

    public void startServer() {
        BluetoothServer bluetoothServer = new BluetoothServer();
        bluetoothServer.start();
    }
}
