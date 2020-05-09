package com.example.discom;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
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

public class TextInterface extends AppCompatActivity {

    ArrayList<BluetoothDevice> pairedDevices;
    ArrayList<Long> receivedMessageTimes = new ArrayList<Long>();
    ArrayList<JSONObject> receivedJSON = new ArrayList<>();
    JSONObject jsonObject;
    long selfNumber;
    boolean startClientBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_interface);
        this.pairedDevices = (ArrayList<BluetoothDevice>)getIntent().getSerializableExtra("DeviceList");
        SharedPreferences sharedPref = this.getSharedPreferences("MY_SETTINGS", Context.MODE_PRIVATE);
        this.selfNumber = sharedPref.getLong("PhoneNumber", 0);
        TextView text11 = findViewById(R.id.textView11);
        text11.setMovementMethod(new ScrollingMovementMethod());
        text11.append("Self Number: " + this.selfNumber + "\n");
        startServer();
    }

    public void makeMessage(View view) {
        TextView text11 = findViewById(R.id.textView11);
        text11.setText("");
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
        String selfNumberText = Long.toString(this.selfNumber);
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
        this.receivedMessageTimes.add(time);

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
            jsonObject.put("Sender", this.selfNumber);
            jsonObject.put("Message", message);
            jsonObject.put("CurrentHops", 0);
            //make list of bluetooth device for hops list in JSONArray

        } catch (JSONException e) {
            Log.e(Constants.TAG, "JSON object construction error");
        }
        String jsonText = jsonObject.toString();
        text11.append(jsonText);
        text11.append("\n");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            byte[] encodedJSON = Base64.getEncoder().encode(jsonText.getBytes());
            /*
            text11.append(new String(encodedJSON));
            text11.append("\n");
             */
            byte[] decodedJSON = Base64.getDecoder().decode(encodedJSON);
            /*
            text11.append(new String(decodedJSON));
            text11.append("\n");
             */
        }
        else {
            byte[] encodedJSON = android.util.Base64.encode(jsonText.getBytes(), android.util.Base64.DEFAULT);
            /*
            text11.append(new String(encodedJSON));
            text11.append("\n");
             */
            byte[] decodedJSON = android.util.Base64.decode(encodedJSON, android.util.Base64.DEFAULT);
            /*
            text11.append(new String(decodedJSON));
            text11.append("\n");
             */
        }

        //Send message out to devices
        text11.append("Sending message to available paired devices.\n");
        startClient(jsonObject, 0, 0, null);

    }

    public void startServer() {
        final TextView text = findViewById(R.id.textView11);
        BluetoothDevice device;
        Handler serverHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //handle cases
                switch(msg.what) {
                    case Constants.SERVER_CREATING_CHANNEL:
                        /*
                        text.append(Constants.SERVER_CREATING_CHANNEL_TEXT);
                        text.append("\n");
                        text.append("Server: Channel number is " + Integer.toString(serverChannel));
                        text.append("\n");

                         */
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
                        /*
                        text.append(Constants.SERVER_DEVICE_CONNECTED_TEXT);
                        text.append("\n");

                         */
                        startServer();
                        break;
                    case Constants.SERVER_SOCKET_CLOSED:
                        /*
                        text.append(Constants.SERVER_SOCKET_CLOSED_TEXT);
                        text.append("\n");
                         */
                        break;
                    case Constants.SERVER_SOCKET_CLOSE_FAIL:
                        text.append(Constants.SERVER_SOCKET_CLOSE_FAIL_TEXT);
                        text.append("\n");
                        break;
                    case Constants.SERVER_DEVICE_INFO:
                        BluetoothDevice tmp = (BluetoothDevice) msg.obj;
                        text.append("Server: Connected to " + tmp.getName());
                        text.append("\n");

                        break;
                    case Constants.SOCKET:
                        //text.append("Receiving message\n");
                        BluetoothSocket socket = (BluetoothSocket) msg.obj;
                        final BluetoothDevice receivedDevice = socket.getRemoteDevice();
                        Handler messageHandler = new Handler(Looper.getMainLooper()){
                            @Override
                            public void handleMessage(Message msg) {
                                if(msg.what == Constants.JSON_OBJECT_RECEIVE) {
                                    JSONObject jsonObject = (JSONObject) msg.obj;
                                    String displayText = "Message received from " + receivedDevice.toString() + "\n";
                                    text.append(displayText);
                                    try {
                                        messageResponse(jsonObject, receivedDevice);
                                    } catch (JSONException e) {
                                        text.append("JSON Exception " + e);
                                    }
                                    /*
                                    text.append(jsonObject.toString());
                                    text.append("\n");
                                     */
                                }
                                else if(msg.what == Constants.JSON_RECEIVE_FAIL) {
                                    text.append("Error receiving message.\n");
                                }
                            }
                        };
                        MessageServer messageServer = new MessageServer(socket, messageHandler);
                        messageServer.start();
                        break;
                    default:
                        break;
                }
            }
        };
        BluetoothServer bluetoothServer = new BluetoothServer(serverHandler, 0, Constants.UUID_2);
        bluetoothServer.start();
    }


    public void startClient(final JSONObject jsonObject, final int deviceNumber, final int attemptNumber, final BluetoothDevice ignoreDevice) {
        int len = this.pairedDevices.size();
        if(deviceNumber >= len)
            return;
        if(attemptNumber >= Constants.MAX_ATTEMPTS)
            return;
        if(ignoreDevice != null && this.pairedDevices.get(deviceNumber).getAddress().equals(ignoreDevice.getAddress())) {
            startClient(jsonObject, deviceNumber + 1, 0, ignoreDevice);
            return;
        }
        while(startClientBusy);
        startClientBusy = true;
        final TextView text = findViewById(R.id.textView11);
        String displayText = "Sending to " + this.pairedDevices.get(deviceNumber).toString() + "\n";
        text.append(displayText);
        Handler clientHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //handle cases
                switch(msg.what) {
                    case Constants.CLIENT_CREATING_CHANNEL:
                        /*
                        text.append(Constants.CLIENT_CREATING_CHANNEL_TEXT);
                        text.append("\n");
                         */
                        break;
                    case Constants.CLIENT_CREATING_CHANNEL_FAIL:
                        text.append(Constants.CLIENT_CREATING_CHANNEL_FAIL_TEXT);
                        text.append("\n");
                        break;
                    case Constants.CLIENT_ATTEMPTING_CONNECTION:
                        /*
                        text.append(Constants.CLIENT_ATTEMPTING_CONNECTION_TEXT);
                        text.append("\n");
                         */
                        break;
                    case Constants.CLIENT_CONNECTED:
                        /*
                        text.append(Constants.CLIENT_CONNECTED_TEXT);
                        text.append("\n");
                         */
                        break;
                    case Constants.CLIENT_CONNECTION_FAIL:
                        text.append(Constants.CLIENT_CONNECTION_FAIL_TEXT);
                        text.append("\n");
                        startClient(jsonObject, deviceNumber, attemptNumber + 1, ignoreDevice);
                        break;
                    case Constants.CLIENT_SOCKET_CLOSE_FAIL:
                        text.append(Constants.CLIENT_SOCKET_CLOSE_FAIL_TEXT);
                        text.append("\n");
                        break;
                    case Constants.CLIENT_CLOSING_SOCKET:
                        /*
                        text.append(Constants.CLIENT_CLOSING_SOCKET_TEXT);
                        text.append("\n");
                         */
                        break;
                    case Constants.CLIENT_DEVICE_INFO:
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        text.append("Client: Connected to " + device.getName());
                        text.append("\n");
                        break;
                    case Constants.SOCKET:
                        //text.append("Sending message\n");
                        BluetoothSocket socket = (BluetoothSocket) msg.obj;
                        Handler messageHandler = new Handler(Looper.getMainLooper()){
                            @Override
                            public void handleMessage(Message msg) {
                                if(msg.what == Constants.JSON_SEND_FAIL) {
                                    text.append("Message sending failed\n");
                                    startClient(jsonObject, deviceNumber, attemptNumber + 1, ignoreDevice);
                                }
                                else {
                                    text.append("Message sent successfully\n");
                                    startClient(jsonObject, deviceNumber + 1, 0, ignoreDevice);
                                }
                            }
                        };
                        MessageClient messageClient = new MessageClient(socket, jsonObject, messageHandler);
                        messageClient.start();
                        break;
                    default:
                        break;
                }
            }
        };
        BluetoothClient bluetoothClient = new BluetoothClient(this.pairedDevices.get(deviceNumber), clientHandler, Constants.UUID_2);
        bluetoothClient.start();
        startClientBusy = false;
    }

    void messageResponse(JSONObject jsonObject, BluetoothDevice receivedDevice) throws JSONException {

        final TextView text = findViewById(R.id.textView11);
        long number = (long) jsonObject.get("Recipient");

        //make list of timestamps, if msg already received, drop
        long time = (long) jsonObject.get("Timestamp");
        if(this.receivedMessageTimes.contains(time)) {
            text.append("Already received, dropping\n");
            return;
        }

        //otherwise, append to list
        this.receivedMessageTimes.add(time);

        //if you are intended recipient, do not broadcast further
        if(Long.toString(number).equals(Long.toString(this.selfNumber))) {
            text.append("You are the intended receipient\n");
            text.append((String) jsonObject.get("Message"));
            text.append("\n");
            this.receivedJSON.add(jsonObject);
            return;
        }

        //if max hops are reached, then drop packet
        int hops = (int) jsonObject.get("CurrentHops");
        if(Constants.MAX_HOPS == hops + 1)
            return;
        //otherwise increment current hops by 1
        jsonObject.put("CurrentHops", hops + 1);

        //braodcast message back to network
        //add check so that it does not broadcast back to original device
        //add lock so that only one copy of startClient is running at any time
        text.append("Broadcasting message back to network\n");
        startClient(jsonObject, 0, 0, receivedDevice);
    }

    public void showMessages(View view) throws JSONException {
        TextView text = findViewById(R.id.textView11);
        text.setText("");
        int len = this.receivedJSON.size();
        for(int i = 0; i < len; i++) {
            JSONObject jsonObject = this.receivedJSON.get(i);
            text.append("From: ");
            text.append(Long.toString((Long) jsonObject.get("Sender")));
            text.append("\n");
            text.append("Message: ");
            text.append((String) jsonObject.get("Message"));
            text.append("\n\n");
        }
    }
}
