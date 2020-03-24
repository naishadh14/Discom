package com.example.discom;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ConnectDevices extends AppCompatActivity {

    private static final int GETTING_ADAPTER = 2487;
    ArrayList<BluetoothDevice> discoveredDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_devices);
        startServer();
        this.discoveredDevices = (ArrayList<BluetoothDevice>)getIntent().getSerializableExtra("DeviceList");
        TextView text6 = (TextView)findViewById(R.id.textView6);
        text6.append("\n");
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
                text.append("\n\n");
            }
        }

        IntentFilter deviceConnected = new IntentFilter();
        registerReceiver(deviceConnectedReceiver, deviceConnected);
    }

    public void startClient(View view) {
        int len = this.discoveredDevices.size();
        for(int i = 0; i < len; i++) {
            BluetoothClient bluetoothClient = new BluetoothClient(this.discoveredDevices.get(i));
            bluetoothClient.start();
        }
    }

    public void startServer() {
        final TextView text = (TextView)findViewById(R.id.textView6);
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //handle cases
                if(msg.what == GETTING_ADAPTER) {
                    text.append("Server: Getting adapter");
                }
            }
        };
        BluetoothServer bluetoothServer = new BluetoothServer(handler);
        bluetoothServer.start();
    }

    private final BroadcastReceiver deviceConnectedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device =  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            TextView text = (TextView)findViewById(R.id.textView6);
            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                text.append(device.getName());
                text.append("\n");
            }
        }
    };
}
