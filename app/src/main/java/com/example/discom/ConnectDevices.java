package com.example.discom;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

public class ConnectDevices extends AppCompatActivity {

    ArrayList<BluetoothDevice> discoveredDevices, pairedDevices;
    BluetoothServer serverThread;
    ArrayList<BluetoothClient> clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_devices_1);
        //createUI();
        startServer(Constants.serverChannel++);
        this.discoveredDevices = (ArrayList<BluetoothDevice>)getIntent().getSerializableExtra("DeviceList");
        this.pairedDevices = new ArrayList<BluetoothDevice>();
//        TextView text6 = (TextView)findViewById(R.id.textView6);
//        text6.append("\n");
//        TextView text7 = (TextView)findViewById(R.id.textView7);
//        text7.append("\n");
//        TextView text = (TextView)findViewById(R.id.textView5);
        setDeviceCards();

        IntentFilter deviceConnected = new IntentFilter();
        registerReceiver(deviceConnectedReceiver, deviceConnected);
    }

    void setDeviceCards() {
        int len = this.discoveredDevices.size();
        CardView cardView;
        TextView text;
        for(int i = 0; i < len; i++) {
            BluetoothDevice device = this.discoveredDevices.get(i);
            int j = i + 1;
            switch (j) {
                case 1:
                    cardView = findViewById(R.id.cardDevice1);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice1);
                    text.setText(device.getName());
                    break;
                case 2:
                    cardView = findViewById(R.id.cardDevice2);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice2);
                    text.setText(device.getName());
                    break;
                case 3:
                    cardView = findViewById(R.id.cardDevice3);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice3);
                    text.setText(device.getName());
                    break;
                case 4:
                    cardView = findViewById(R.id.cardDevice4);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice4);
                    text.setText(device.getName());
                    break;
                case 5:
                    cardView = findViewById(R.id.cardDevice5);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice5);
                    text.setText(device.getName());
                    break;
                case 6:
                    cardView = findViewById(R.id.cardDevice6);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice6);
                    text.setText(device.getName());
                    break;
                case 7:
                    cardView = findViewById(R.id.cardDevice7);
                    cardView.setVisibility(View.VISIBLE);
                    text = findViewById(R.id.textDevice7);
                    text.setText(device.getName());
                    break;
                default:
                    break;
            }
        }
    }

    public void startClient(View view) {
        int len = this.discoveredDevices.size();
        //final TextView text = (TextView)findViewById(R.id.textView6);
        for(int i = 0; i < len; i++) {
            Handler clientHandler = new Handler(Looper.getMainLooper()) {
              @Override
              public void handleMessage(Message msg) {
                  //handle cases
                  switch(msg.what) {
//                      case Constants.CLIENT_CREATING_CHANNEL:
//                          text.append(Constants.CLIENT_CREATING_CHANNEL_TEXT);
//                          text.append("\n");
//                          break;
//                      case Constants.CLIENT_CREATING_CHANNEL_FAIL:
//                          text.append(Constants.CLIENT_CREATING_CHANNEL_FAIL_TEXT);
//                          text.append("\n");
//                          break;
//                      case Constants.CLIENT_ATTEMPTING_CONNECTION:
//                          text.append(Constants.CLIENT_ATTEMPTING_CONNECTION_TEXT);
//                          text.append("\n");
//                          break;
//                      case Constants.CLIENT_CONNECTED:
//                          text.append(Constants.CLIENT_CONNECTED_TEXT);
//                          text.append("\n");
//                          break;
//                      case Constants.CLIENT_CONNECTION_FAIL:
//                          text.append(Constants.CLIENT_CONNECTION_FAIL_TEXT);
//                          text.append("\n");
//                          break;
//                      case Constants.CLIENT_SOCKET_CLOSE_FAIL:
//                          text.append(Constants.CLIENT_SOCKET_CLOSE_FAIL_TEXT);
//                          text.append("\n");
//                          break;
//                      case Constants.CLIENT_CLOSING_SOCKET:
//                          text.append(Constants.CLIENT_CLOSING_SOCKET_TEXT);
//                          text.append("\n");
//                          break;
                      case Constants.CLIENT_DEVICE_INFO:
                          BluetoothDevice device = (BluetoothDevice) msg.obj;
                          //text.append("Client: Connected to " + device.getName());
                          //text.append("\n");
                          createPairedList(device);
                          break;
                      default:
                          break;
                  }
              }
            };
            BluetoothClient bluetoothClient = new BluetoothClient(this.discoveredDevices.get(i), clientHandler, Constants.UUID_1);
            bluetoothClient.start();
            //this.clientThread.add(bluetoothClient);
        }
    }

    public void startServer(final int serverChannel) {
        //final TextView text = (TextView)findViewById(R.id.textView6);
        Handler serverHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //handle cases
                switch(msg.what) {
//                    case Constants.SERVER_GETTING_ADAPTER:
//                        text.append(Constants.SERVER_GETTING_ADAPTER_TEXT);
//                        text.append("\n");
//                        break;
//                    case Constants.SERVER_CREATING_CHANNEL:
//                        text.append(Constants.SERVER_CREATING_CHANNEL_TEXT);
//                        text.append("\n");
//                        text.append("Server: Channel number is " + Integer.toString(serverChannel));
//                        text.append("\n");
//                        break;
//                    case Constants.SERVER_CREATING_CHANNEL_FAIL:
//                        text.append(Constants.SERVER_CREATING_CHANNEL_FAIL_TEXT);
//                        text.append("\n");
//                        break;
//                    case Constants.SERVER_WAITING_DEVICE:
//                        text.append(Constants.SERVER_WAITING_DEVICE_TEXT);
//                        text.append("\n");
//                        break;
//                    case Constants.SERVER_ACCEPT_FAIL:
//                        text.append(Constants.SERVER_ACCEPT_FAIL_TEXT);
//                        text.append("\n");
//                        break;
                    case Constants.SERVER_DEVICE_CONNECTED:
//                        text.append(Constants.SERVER_DEVICE_CONNECTED_TEXT);
//                        text.append("\n");
                        startServer(Constants.serverChannel++);
                        break;
//                    case Constants.SERVER_SOCKET_CLOSED:
//                        text.append(Constants.SERVER_SOCKET_CLOSED_TEXT);
//                        text.append("\n");
//                        break;
//                    case Constants.SERVER_SOCKET_CLOSE_FAIL:
//                        text.append(Constants.SERVER_SOCKET_CLOSE_FAIL_TEXT);
//                        text.append("\n");
//                        break;
                    case Constants.SERVER_DEVICE_INFO:
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        //text.append("Server: Connected to " + device.getName());
                        //text.append("\n");
                        createPairedList(device);
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
        /*
        int len = this.clientThread.size();
        for(int i = 0; i < len; i++)
            this.clientThread.get(i).interrupt();
         */
        Intent intent = new Intent(this, TextInterface.class);
        intent.putExtra("DeviceList", (Serializable) this.pairedDevices);
        startActivity(intent);
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
        //refreshList();
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

    void createUI() {
        Context context = getApplicationContext();
        ScrollView scrollView = findViewById(R.id.scrollViewDevices);
        LinearLayout linearLayout = findViewById(R.id.linearLayoutDevices);
        CardView cardView = new CardView(context);
        LayoutParams layoutparams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        cardView.setLayoutParams(layoutparams);
        cardView.setRadius(30);
        cardView.setContentPadding(8, 8, 8, 8);

        LinearLayout linearLayout1 = new LinearLayout(context);
        linearLayout1.setLayoutParams(layoutparams);
        linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout1.setBackgroundColor(Color.parseColor("#6002EE"));


        ImageView image = new ImageView(context);
        image.setImageResource(R.drawable.ic_tornado);
        LayoutParams layoutparams1 = new LayoutParams(100, 100);
        image.setLayoutParams(layoutparams1);
        linearLayout1.addView(image);

        TextView text =  new TextView(context);
        LayoutParams layoutparams2 = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT,
                0.5f
        );
        layoutparams2.gravity = Gravity.CENTER_HORIZONTAL;
        text.setLayoutParams(layoutparams2);
        text.setTextColor(Color.parseColor("#ffffff"));
        text.setText("OnePlus 3T");
        text.setTextSize(21);
        text.setPadding(0, 20, 0, 0);
        linearLayout1.addView(text);

        ImageView image2 = new ImageView(context);
        image2.setImageResource(R.drawable.ic_red_cancel);
        LayoutParams layoutParams3 = new LayoutParams(50, 50);
        layoutparams.gravity = Gravity.CENTER;
        image2.setLayoutParams(layoutParams3);
        image2.setPadding(10, 10, 10, 10);
        linearLayout1.addView(image2);

        cardView.addView(linearLayout1);
        linearLayout.addView(cardView);
    }
}
