package com.example.discom;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class TextInterface extends AppCompatActivity {

    ArrayList<BluetoothDevice> pairedDevices;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_interface);
        this.pairedDevices = (ArrayList<BluetoothDevice>)getIntent().getSerializableExtra("DeviceList");
        startServer(Constants.serverChannel++);
    }

    public void makeMessage(View view) {

        //get values from user input
        EditText text = findViewById(R.id.editText2);
        String number_text = text.getText().toString();
        //check for 10 digits
        if(number_text.length() != 10) {
            Toast toast = Toast.makeText(getApplicationContext(), "Phone Number must be 10 digits", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        //check that recipient number is not self number
        SharedPreferences sharedPref = this.getSharedPreferences("MY_SETTINGS", Context.MODE_PRIVATE);
        long selfNumber = sharedPref.getLong("PhoneNumber", 0);
        String selfNumberText = Long.toString(selfNumber);
        if(selfNumberText.equals(number_text)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Recipient Number cannot be your own number", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        long number = Long.parseLong(number_text);
        text = findViewById(R.id.editText3);
        String message = text.getText().toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long time = timestamp.getTime();

        /*
        Make JSONObject consisting of
        1. Timestamp
        2. Recipient Number
        3. Sender Number
        4. Message
        5. List of BluetoothDevices msg has hopped to (JSONArray)
        6. Max number of Hops
        */
        JSONObject jsonObject = new JSONObject();
        this.jsonObject = jsonObject;
        try {
            jsonObject.put("Timestamp", time); //serves as ID for message
            jsonObject.put("Recipient", number);
            jsonObject.put("Sender", selfNumber);
            jsonObject.put("Message", message);
            jsonObject.put("MaxHops", Constants.MAX_HOPS);
            //make list of bluetooth device for hops list in JSONArray

        } catch (JSONException e) {
            Log.e(Constants.TAG, "JSON object construction error");
        }
        String jsonText = jsonObject.toString();
        TextView text2 = findViewById(R.id.textView11);
        text2.setText(jsonText);
        text2.append("\n");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            byte[] encodedJSON = Base64.getEncoder().encode(jsonText.getBytes());
            text2.append(new String(encodedJSON));
            text2.append("\n");
            byte[] decodedJSON = Base64.getDecoder().decode(encodedJSON);
            text2.append(new String(decodedJSON));
            text2.append("\n");
        }
        else {
            byte[] encodedJSON = android.util.Base64.encode(jsonText.getBytes(), android.util.Base64.DEFAULT);
            text2.append(new String(encodedJSON));
            text2.append("\n");
            byte[] decodedJSON = android.util.Base64.decode(encodedJSON, android.util.Base64.DEFAULT);
            text2.append(new String(decodedJSON));
            text2.append("\n");
        }

        //Send message out to devices
        text2.append("Sending message to available paired devices.\n");
        startClient();

    }

    private void startServer(final int serverChannel) {
        //any socket that comes through server is to be read from
        final TextView text = findViewById(R.id.textView11);
        Handler serverHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                //handle cases
                if (msg.what == Constants.SERVER_DEVICE_CONNECTED) {
                    text.append(Constants.SERVER_DEVICE_CONNECTED_TEXT);
                    text.append("\n");
                    startServer(Constants.serverChannel++);
                } else if (msg.what == Constants.DEVICE_INFO){
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    text.append("Server: Connected to " + device.getName());
                    text.append("\n");
                } else if(msg.what == Constants.SOCKET) {
                    //receive message from socket
                    text.append("Receiving message");
                    BluetoothSocket socket = (BluetoothSocket) msg.obj;
                    Handler messageHandler = new Handler(Looper.getMainLooper()){
                      @Override
                      public void handleMessage(Message msg) {
                          if(msg.what == Constants.JSON_OBJECT_RECEIVE) {
                              JSONObject jsonObject = (JSONObject) msg.obj;
                              text.append("Message received");
                              text.append(jsonObject.toString());
                          }
                      }
                    };
                    MessageServer messageServer = new MessageServer(socket, messageHandler);
                    messageServer.start();

                }
            }
        };
        BluetoothServer bluetoothServer = new BluetoothServer(serverHandler, serverChannel);
        bluetoothServer.start();
        text.append("Server: Starting");
    }

    public void startClient() {
        //any socket that comes through client is to be sent to
        int len = this.pairedDevices.size();
        final TextView text = (TextView)findViewById(R.id.textView11);
        for(int i = 0; i < len; i++) {
            Handler clientHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    //handle cases
                    if (msg.what == Constants.CLIENT_CONNECTED) {
                        text.append(Constants.CLIENT_CONNECTED_TEXT);
                        text.append("\n");
                    } else if (msg.what == Constants.DEVICE_INFO) {
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        text.append("Client: Connected to " + device.getName());
                        text.append("\n");
                    } else if(msg.what == Constants.SOCKET) {
                        //send msg to socket
                        text.append("Sending message\n");
                        BluetoothSocket socket = (BluetoothSocket) msg.obj;
                        MessageClient messageClient = new MessageClient(socket, jsonObject);
                        messageClient.start();
                    }
                }
            };
            BluetoothClient bluetoothClient = new BluetoothClient(this.pairedDevices.get(i), clientHandler);
            bluetoothClient.start();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                Log.e(Constants.TAG, "Thread could not sleep");
            }
        }
    }
}
