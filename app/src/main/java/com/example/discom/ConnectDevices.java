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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ConnectDevices extends AppCompatActivity {

    ArrayList<BluetoothDevice> discoveredDevices, pairedDevices;
    BluetoothServer serverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_devices_1);

        //set Icon and OnClickListener on BT Connect button
        setButtonAvailable();

        //start server for listening to incoming connections
        startServer(Constants.serverChannel++);

        //get ArrayList of discovered devices from previous activity
        this.discoveredDevices = (ArrayList<BluetoothDevice>)getIntent().getSerializableExtra("DeviceList");
        this.pairedDevices = new ArrayList<>();

        //display cards of discovered devices
        setDeviceCards();

        //register Broadcast Receiver for new device found
        IntentFilter deviceConnected = new IntentFilter();
        registerReceiver(deviceConnectedReceiver, deviceConnected);
    }


    //method to set cards visible & set text
    void setDeviceCards() {
        int len = this.discoveredDevices.size();
        CardView cardView;
        TextView text;

        //Set count for discovered devices
        text = findViewById(R.id.discoveredDeviceCount);
        String displayText = "Discovered Devices: " + len;
        text.setText(displayText);

        //loop through cards and make correct number of cards visible
        for(int i = 0; i < len; i++) {
            BluetoothDevice device = this.discoveredDevices.get(i);
            int j = i + 1;
            switch (j) {
                //make card j visible and set Device Name and Address
                case 1:
                    cardView = findViewById(R.id.cardDevice1);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.deviceName1);
                    text.setText(device.getName());
                    text = findViewById(R.id.deviceAddress1);
                    text.setText(device.getAddress());
                    break;
                case 2:
                    cardView = findViewById(R.id.cardDevice2);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.deviceName2);
                    text.setText(device.getName());
                    text = findViewById(R.id.deviceAddress2);
                    text.setText(device.getAddress());
                    break;
                case 3:
                    cardView = findViewById(R.id.cardDevice3);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.deviceName3);
                    text.setText(device.getName());
                    text = findViewById(R.id.deviceAddress3);
                    text.setText(device.getAddress());
                    break;
                case 4:
                    cardView = findViewById(R.id.cardDevice4);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.deviceName4);
                    text.setText(device.getName());
                    text = findViewById(R.id.deviceAddress4);
                    text.setText(device.getAddress());
                    break;
                case 5:
                    cardView = findViewById(R.id.cardDevice5);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.deviceName5);
                    text.setText(device.getName());
                    text = findViewById(R.id.deviceAddress5);
                    text.setText(device.getAddress());
                    break;
                case 6:
                    cardView = findViewById(R.id.cardDevice6);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.deviceName6);
                    text.setText(device.getName());
                    text = findViewById(R.id.deviceAddress6);
                    text.setText(device.getAddress());
                    break;
                case 7:
                    cardView = findViewById(R.id.cardDevice7);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.deviceName7);
                    text.setText(device.getName());
                    text = findViewById(R.id.deviceAddress7);
                    text.setText(device.getAddress());
                    break;
                default:
                    break;
            }
        }
    }

    //method to make icons show loading status for unpaired devices
    void setConnectionStatusLoading() {
        int len = this.discoveredDevices.size();
        ImageView image;
        BluetoothDevice device;

        //loop through cards and set connection status
        for(int i = 0; i < len; i++) {
            int j = i + 1;
            device = this.discoveredDevices.get(i);
            switch (j) {
                case 1:
                    image = findViewById(R.id.iconDevice1);
                    //if device is not paired, set status to loading
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_loading);
                    break;
                case 2:
                    image = findViewById(R.id.iconDevice2);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_loading);
                    break;
                case 3:
                    image = findViewById(R.id.iconDevice3);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_loading);
                    break;
                case 4:
                    image = findViewById(R.id.iconDevice4);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_loading);
                    break;
                case 5:
                    image = findViewById(R.id.iconDevice5);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_loading);
                    break;
                case 6:
                    image = findViewById(R.id.iconDevice6);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_loading);
                    break;
                case 7:
                    image = findViewById(R.id.iconDevice7);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_loading);
                    break;
                default:
                    break;
            }
        }

        //After 5 seconds of keeping cards at loading, set their status to not connected
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new   Runnable() {
                            public void run() {
                                setConnectionStatusFail();
                                setButtonAvailable();
                            }
                        });
                    }
                },
                5000
        );
    }

    //method to set paired devices to connected status
    void setConnectionStatusSuccess() {
        ImageView image;
        BluetoothDevice device;
        int len = this.discoveredDevices.size();

        //set count of paired devices
        TextView text = findViewById(R.id.pairedDeviceCount);
        String displayText = "Paired Devices: " + this.pairedDevices.size();
        text.setText(displayText);

        //loop through cards and set connection status
        for(int i = 0; i < len; i++) {
            int j = i + 1;
            device = this.discoveredDevices.get(i);
            switch (j) {
                case 1:
                    image = findViewById(R.id.iconDevice1);
                    //if device is in list of paired devices, set status to connected
                    if(this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_green_tick);
                    break;
                case 2:
                    image = findViewById(R.id.iconDevice2);
                    if(this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_green_tick);
                    break;
                case 3:
                    image = findViewById(R.id.iconDevice3);
                    if(this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_green_tick);
                    break;
                case 4:
                    image = findViewById(R.id.iconDevice4);
                    if(this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_green_tick);
                    break;
                case 5:
                    image = findViewById(R.id.iconDevice5);
                    if(this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_green_tick);
                    break;
                case 6:
                    image = findViewById(R.id.iconDevice6);
                    if(this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_green_tick);
                    break;
                case 7:
                    image = findViewById(R.id.iconDevice7);
                    if(this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_green_tick);
                    break;
                default:
                    break;
            }
        }
    }

    //method to set connection status of unpaired devices
    void setConnectionStatusFail() {
        int len = this.discoveredDevices.size();
        ImageView image;
        BluetoothDevice device;

        //loop through cards and set connectio status
        for(int i = 0; i < len; i++) {
            int j = i + 1;
            device = this.discoveredDevices.get(i);
            switch (j) {
                case 1:
                    image = findViewById(R.id.iconDevice1);
                    //if device is not in list of paired devices, set status to not connected
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_red_cancel);
                    break;
                case 2:
                    image = findViewById(R.id.iconDevice2);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_red_cancel);
                    break;
                case 3:
                    image = findViewById(R.id.iconDevice3);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_red_cancel);
                    break;
                case 4:
                    image = findViewById(R.id.iconDevice4);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_red_cancel);
                    break;
                case 5:
                    image = findViewById(R.id.iconDevice5);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_red_cancel);
                    break;
                case 6:
                    image = findViewById(R.id.iconDevice6);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_red_cancel);
                    break;
                case 7:
                    image = findViewById(R.id.iconDevice7);
                    if(!this.pairedDevices.contains(device))
                        image.setImageResource(R.drawable.ic_red_cancel);
                    break;
                default:
                    break;
            }
        }
    }

    //method to prevent user from reclicking while connection is already in progress
    void setButtonBusy() {
        FloatingActionButton button = findViewById(R.id.bluetoothConnect);

        //remove onClickListener until connection attempts are complete
        button.setOnClickListener(null);

        //remove icon to indicate that button is not clickable
        button.setImageDrawable(null);
    }

    //method to add functionality back to button after connection attempt is complete
    void setButtonAvailable() {
        FloatingActionButton button = findViewById(R.id.bluetoothConnect);

        //add onClickListener to allow connection attempt to start
        button.setOnClickListener(null);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClient(view);
            }
        });

        //add icon back to button to indicate that button is now clickable
        button.setImageResource(R.drawable.ic_bluetooth_connect);
    }

    //method to start connection to devices in DiscoveredDevices list
    public void startClient(View view) {

        //prevent user from reclicking button & show loading status
        setButtonBusy();
        setConnectionStatusLoading();

        int len = this.discoveredDevices.size();

        //loop through ArrayList of discovered devices and attempt to establish paired status
        for(int i = 0; i < len; i++) {
            final BluetoothDevice currentDevice = this.discoveredDevices.get(i);

            //if device has already been paired, move on to next device
            if(this.pairedDevices.contains(currentDevice))
                continue;

            //register handler to set connection status in case of successful connection
            Handler clientHandler = new Handler(Looper.getMainLooper()) {
              @Override
              public void handleMessage(Message msg) {
                  if (msg.what == Constants.CLIENT_DEVICE_INFO) {
                      BluetoothDevice device = (BluetoothDevice) msg.obj;
                      addDeviceToList(device);
                      setConnectionStatusSuccess();
                  }
              }
            };

            //start thread to establish connection
            BluetoothClient bluetoothClient = new BluetoothClient(currentDevice, clientHandler, Constants.UUID_1);
            bluetoothClient.start();
        }
    }

    //method to start server in background to listen for incoming connections
    public void startServer(final int serverChannel) {

        //register handler to take action
        Handler serverHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {

                    //in case of successful connection, restart server to allow new connections
                    case Constants.SERVER_DEVICE_CONNECTED:
                        startServer(Constants.serverChannel++);
                        break;

                    //set connection status of successfully connected device, and add it to list of paired devices
                    case Constants.SERVER_DEVICE_INFO:
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        addDeviceToList(device);
                        setConnectionStatusSuccess();
                        break;
                }
            }
        };

        //start thread to listen for connections
        BluetoothServer bluetoothServer = new BluetoothServer(serverHandler, serverChannel, Constants.UUID_1);
        bluetoothServer.start();
        this.serverThread = bluetoothServer;
    }

    //open TextInterface.java
    public void startTextingInterface(View view) {

        //close server thread to prevent accepting new connections
        this.serverThread.interrupt();

        //navigate to TextInterface and send list of paired devices
        Intent intent = new Intent(this, TextInterface.class);
        intent.putExtra("DeviceList", this.pairedDevices);
        startActivity(intent);
    }

    //register receiver for new device connection and set connection status to successful
    private final BroadcastReceiver deviceConnectedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                setConnectionStatusSuccess();
            }
        }
    };

    //method to add device to list of paired devices if it does not already exist
    public void addDeviceToList(BluetoothDevice device) {
        if(!this.pairedDevices.contains(device))
            this.pairedDevices.add(device);
    }
}
