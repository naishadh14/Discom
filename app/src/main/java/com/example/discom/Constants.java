package com.example.discom;

public final class Constants {
    //Application's UUID
    static final String MY_UUID_STRING = "48ccdb6c-7bab-45f6-bd68-f7e5fb8d06fe";
    //TAG for log messages
    static final String TAG = "DJ@H#BD";
    //Channels for socket connections
    static final String CHANNEL_1 = "CHANNEL_1";
    static final String CHANNEL_2 = "CHANNEL_2";

    static int serverChannel = 0;

    //ints and Strings for server communication across threads
    static final int SERVER_GETTING_ADAPTER = 8500;
    static final String SERVER_GETTING_ADAPTER_TEXT = "Server: Getting adapter";
    static final int SERVER_CREATING_CHANNEL = 8501;
    static final String SERVER_CREATING_CHANNEL_TEXT = "Server: Created RFCOMM channel";
    static final int SERVER_CREATING_CHANNEL_FAIL = 8502;
    static final String SERVER_CREATING_CHANNEL_FAIL_TEXT = "Server: Could not create RFCOMM channel";
    static final int SERVER_WAITING_DEVICE = 8503;
    static final String SERVER_WAITING_DEVICE_TEXT = "Server: Waiting for device";
    static final int SERVER_ACCEPT_FAIL = 8504;
    static final String SERVER_ACCEPT_FAIL_TEXT = "Server: Socket's accept() method failed";
    static final int SERVER_DEVICE_CONNECTED = 8505;
    static final String SERVER_DEVICE_CONNECTED_TEXT = "Server: Device connected!";
    static final int SERVER_SOCKET_CLOSED = 8506;
    static final String SERVER_SOCKET_CLOSED_TEXT = "Server: Socket closed";
    static final int SERVER_SOCKET_CLOSE_FAIL = 8507;
    static final String SERVER_SOCKET_CLOSE_FAIL_TEXT = "Server: Could not close socket";

    //ints and Strings for client communication across threads
    static final int CLIENT_CREATING_CHANNEL = 8508;
    static final String CLIENT_CREATING_CHANNEL_TEXT = "Client: RFCOMM channel created";
    static final int CLIENT_CREATING_CHANNEL_FAIL = 8509;
    static final String CLIENT_CREATING_CHANNEL_FAIL_TEXT = "Client: Could not create RFCOMM channel";
    static final int CLIENT_ATTEMPTING_CONNECTION = 8510;
    static final String CLIENT_ATTEMPTING_CONNECTION_TEXT = "Client: Attempting to connect to device";
    static final int CLIENT_CONNECTED = 8511;
    static final String CLIENT_CONNECTED_TEXT = "Client: Connected to device!";
    static final int CLIENT_CONNECTION_FAIL = 8512;
    static final String CLIENT_CONNECTION_FAIL_TEXT = "Client: Could not connect";
    static final int CLIENT_SOCKET_CLOSE_FAIL = 8513;
    static final String CLIENT_SOCKET_CLOSE_FAIL_TEXT = "Client: Could not close the socket after connection fail";
    static final int CLIENT_CLOSING_SOCKET = 8514;
    static final String CLIENT_CLOSING_SOCKET_TEXT = "Client: Closing socket";

    static final int DEFAULT = 8515;


}
