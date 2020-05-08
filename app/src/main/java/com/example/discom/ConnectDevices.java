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
        setButtonAvailable();
        startServer(Constants.serverChannel++);
        this.discoveredDevices = (ArrayList<BluetoothDevice>)getIntent().getSerializableExtra("DeviceList");
        this.pairedDevices = new ArrayList<>();
        setDeviceCards();

        IntentFilter deviceConnected = new IntentFilter();
        registerReceiver(deviceConnectedReceiver, deviceConnected);
    }

    void setDeviceCards() {
        int len = this.discoveredDevices.size();
        CardView cardView;
        TextView text;
        String displayText;
        for(int i = 0; i < len; i++) {
            BluetoothDevice device = this.discoveredDevices.get(i);
            int j = i + 1;
            switch (j) {
                case 1:
                    cardView = findViewById(R.id.cardDevice1);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice1);
                    displayText = device.getName() + "\n" + device.getAddress();
                    text.setText(displayText);
                    break;
                case 2:
                    cardView = findViewById(R.id.cardDevice2);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice2);
                    displayText = device.getName() + "\n" + device.getAddress();
                    text.setText(displayText);
                    break;
                case 3:
                    cardView = findViewById(R.id.cardDevice3);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice3);
                    displayText = device.getName() + "\n" + device.getAddress();
                    text.setText(displayText);
                    break;
                case 4:
                    cardView = findViewById(R.id.cardDevice4);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice4);
                    displayText = device.getName() + "\n" + device.getAddress();
                    text.setText(displayText);
                    break;
                case 5:
                    cardView = findViewById(R.id.cardDevice5);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice5);
                    displayText = device.getName() + "\n" + device.getAddress();
                    text.setText(displayText);
                    break;
                case 6:
                    cardView = findViewById(R.id.cardDevice6);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice6);
                    displayText = device.getName() + "\n" + device.getAddress();
                    text.setText(displayText);
                    break;
                case 7:
                    cardView = findViewById(R.id.cardDevice7);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice7);
                    displayText = device.getName() + "\n" + device.getAddress();
                    text.setText(displayText);
                    break;
                default:
                    break;
            }
        }
    }

    void setConnectionStatusLoading() {
        int len = this.discoveredDevices.size();
        ImageView image;
        BluetoothDevice device;
        for(int i = 0; i < len; i++) {
            int j = i + 1;
            device = this.discoveredDevices.get(i);
            switch (j) {
                case 1:
                    image = findViewById(R.id.iconDevice1);
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

    void setConnectionStatusSuccess() {
        ImageView image;
        BluetoothDevice device;
        int len = this.discoveredDevices.size();
        for(int i = 0; i < len; i++) {
            int j = i + 1;
            device = this.discoveredDevices.get(i);
            switch (j) {
                case 1:
                    image = findViewById(R.id.iconDevice1);
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

    void setConnectionStatusFail() {
        int len = this.discoveredDevices.size();
        ImageView image;
        BluetoothDevice device;
        for(int i = 0; i < len; i++) {
            int j = i + 1;
            device = this.discoveredDevices.get(i);
            switch (j) {
                case 1:
                    image = findViewById(R.id.iconDevice1);
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

    void setButtonBusy() {
        FloatingActionButton button = findViewById(R.id.bluetoothConnect);
        button.setOnClickListener(null);
        button.setImageDrawable(null);
        button.setImageResource(R.drawable.ic_loading);
    }

    void setButtonAvailable() {
        FloatingActionButton button = findViewById(R.id.bluetoothConnect);
        button.setOnClickListener(null);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClient(view);
            }
        });
        button.setImageResource(R.drawable.ic_bluetooth_connect);
    }

    public void startClient(View view) {
        setButtonBusy();
        setConnectionStatusLoading();
        int len = this.discoveredDevices.size();
        for(int i = 0; i < len; i++) {
            final BluetoothDevice currentDevice = this.discoveredDevices.get(i);
            if(this.pairedDevices.contains(currentDevice))
                continue;
            Handler clientHandler = new Handler(Looper.getMainLooper()) {
              @Override
              public void handleMessage(Message msg) {
                  //handle cases
                  switch(msg.what) {
                      case Constants.CLIENT_DEVICE_INFO:
                          BluetoothDevice device = (BluetoothDevice) msg.obj;
                          addDeviceToList(device);
                          setConnectionStatusSuccess();
                          break;
                      default:
                          break;
                  }
              }
            };
            BluetoothClient bluetoothClient = new BluetoothClient(currentDevice, clientHandler, Constants.UUID_1);
            bluetoothClient.start();
        }
    }

    public void startServer(final int serverChannel) {
        Handler serverHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //handle cases
                switch(msg.what) {
                    case Constants.SERVER_CREATING_CHANNEL_FAIL:
                    case Constants.SERVER_ACCEPT_FAIL:
                        Toast toast = Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    case Constants.SERVER_DEVICE_CONNECTED:
                        startServer(Constants.serverChannel++);
                        break;
                    case Constants.SERVER_DEVICE_INFO:
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        addDeviceToList(device);
                        setConnectionStatusSuccess();
                        break;
                    default:
                        break;
                }
            }
        };
        BluetoothServer bluetoothServer = new BluetoothServer(serverHandler, serverChannel, Constants.UUID_1);
        bluetoothServer.start();
        this.serverThread = bluetoothServer;
    }

    public void startTextingInterface(View view) {
        Toast toast = Toast.makeText(getApplicationContext(), "Closing Threads", Toast.LENGTH_SHORT);
        toast.show();
        Log.e(Constants.TAG, "About to close server thread");
        this.serverThread.interrupt();
        Log.e(Constants.TAG, "Server thread closed");
        Intent intent = new Intent(this, TextInterface.class);
        intent.putExtra("DeviceList", this.pairedDevices);
        startActivity(intent);
    }

    private final BroadcastReceiver deviceConnectedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                setConnectionStatusSuccess();
            }
        }
    };

    public void addDeviceToList(BluetoothDevice device) {
        if(!this.pairedDevices.contains(device))
            this.pairedDevices.add(device);
    }
}
