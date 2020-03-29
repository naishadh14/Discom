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

    ArrayList<BluetoothDevice> discoveredDevices, pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_devices);
        startServer(Constants.serverChannel++);
        this.discoveredDevices = (ArrayList<BluetoothDevice>)getIntent().getSerializableExtra("DeviceList");
        this.pairedDevices = new ArrayList<BluetoothDevice>();
        TextView text6 = (TextView)findViewById(R.id.textView6);
        text6.append("\n");
        TextView text7 = (TextView)findViewById(R.id.textView7);
        text7.append("\n");
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
        final TextView text = (TextView)findViewById(R.id.textView6);
        for(int i = 0; i < len; i++) {
            Handler clientHandler = new Handler(Looper.getMainLooper()) {
              @Override
              public void handleMessage(Message msg) {
                  //handle cases
                  switch(msg.what) {
                      case Constants.CLIENT_CREATING_CHANNEL:
                          text.append(Constants.CLIENT_CREATING_CHANNEL_TEXT);
                          text.append("\n");
                          break;
                      case Constants.CLIENT_CREATING_CHANNEL_FAIL:
                          text.append(Constants.CLIENT_CREATING_CHANNEL_FAIL_TEXT);
                          text.append("\n");
                          break;
                      case Constants.CLIENT_ATTEMPTING_CONNECTION:
                          text.append(Constants.CLIENT_ATTEMPTING_CONNECTION_TEXT);
                          text.append("\n");
                          break;
                      case Constants.CLIENT_CONNECTED:
                          text.append(Constants.CLIENT_CONNECTED_TEXT);
                          text.append("\n");
                          break;
                      case Constants.CLIENT_CONNECTION_FAIL:
                          text.append(Constants.CLIENT_CONNECTION_FAIL_TEXT);
                          text.append("\n");
                          break;
                      case Constants.CLIENT_SOCKET_CLOSE_FAIL:
                          text.append(Constants.CLIENT_SOCKET_CLOSE_FAIL_TEXT);
                          text.append("\n");
                          break;
                      case Constants.CLIENT_CLOSING_SOCKET:
                          text.append(Constants.CLIENT_CLOSING_SOCKET_TEXT);
                          text.append("\n");
                          break;
                      default:
                          BluetoothDevice device = (BluetoothDevice) msg.obj;
                          text.append("Client: Connected to " + device.getName());
                          text.append("\n");
                          createPairedList(device);
                          break;
                  }
              }
            };
            BluetoothClient bluetoothClient = new BluetoothClient(this.discoveredDevices.get(i), clientHandler);
            bluetoothClient.start();
        }
    }

    public void startServer(final int serverChannel) {
        final TextView text = (TextView)findViewById(R.id.textView6);
        Handler serverHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //handle cases
                switch(msg.what) {
                    case Constants.SERVER_GETTING_ADAPTER:
                        text.append(Constants.SERVER_GETTING_ADAPTER_TEXT);
                        text.append("\n");
                        break;
                    case Constants.SERVER_CREATING_CHANNEL:
                        text.append(Constants.SERVER_CREATING_CHANNEL_TEXT);
                        text.append("\n");
                        text.append("Server: Channel number is " + Integer.toString(serverChannel));
                        text.append("\n");
                        break;
                    case Constants.SERVER_CREATING_CHANNEL_FAIL:
                        text.append(Constants.SERVER_CREATING_CHANNEL_FAIL_TEXT);
                        text.append("\n");
                        break;
                    case Constants.SERVER_WAITING_DEVICE:
                        text.append(Constants.SERVER_WAITING_DEVICE_TEXT);
                        text.append("\n");
                        break;
                    case Constants.SERVER_ACCEPT_FAIL:
                        text.append(Constants.SERVER_ACCEPT_FAIL_TEXT);
                        text.append("\n");
                        break;
                    case Constants.SERVER_DEVICE_CONNECTED:
                        text.append(Constants.SERVER_DEVICE_CONNECTED_TEXT);
                        text.append("\n");
                        startServer(Constants.serverChannel++);
                        break;
                    case Constants.SERVER_SOCKET_CLOSED:
                        text.append(Constants.SERVER_SOCKET_CLOSED_TEXT);
                        text.append("\n");
                        break;
                    case Constants.SERVER_SOCKET_CLOSE_FAIL:
                        text.append(Constants.SERVER_SOCKET_CLOSE_FAIL_TEXT);
                        text.append("\n");
                        break;
                    default:
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        text.append("Server: Connected to " + device.getName());
                        text.append("\n");
                        createPairedList(device);
                        break;
                }
            }
        };
        BluetoothServer bluetoothServer = new BluetoothServer(serverHandler, serverChannel);
        bluetoothServer.start();
    }

    public void startTextingInterface(View view) {
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

    public void createPairedList(BluetoothDevice device) {
        addDeviceToList(device);
        refreshList();
    }

    public void addDeviceToList(BluetoothDevice device) {
        if(!checkDuplicate(device))
            this.pairedDevices.add(device);
    }

    public boolean checkDuplicate(BluetoothDevice device) {
        int len = this.pairedDevices.size();
        for(int i = 0; i < len; i++) {
            if(this.pairedDevices.get(i).equals(device))
                return true;
        }
        return false;
    }

    public void refreshList() {
        TextView text = (TextView) findViewById(R.id.textView7);
        text.setText("");
        int len = this.pairedDevices.size();
        BluetoothDevice device;
        for(int i = 0; i < len; i++) {
            device = pairedDevices.get(i);
            text.append("Device: ");
            text.append(device.getName());
            text.append("\n");
            text.append("Address: ");
            text.append(device.getAddress());
            text.append("\n\n");
        }
    }
}
