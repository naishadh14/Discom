package com.example.discom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.skyfishjy.library.RippleBackground;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeviceDiscovery extends AppCompatActivity {

    List<BluetoothDevice> discoveredDevices;
    int REQUEST_ENABLE_BT;
    boolean isRippleOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_discovery);

        //set Bluetooth Action Button to start discovery mode
        setBluetoothIconEnable();

        //set OnClickListener for Bluetooth Connection button
        final FloatingActionButton button = findViewById(R.id.action_b);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToConnectDevices();
            }
        });

        //initializing variables
        this.discoveredDevices = new ArrayList<>();
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Context context = getApplicationContext();

        //set Center Phone Image's OnClick function
        final ImageView imageView = findViewById(R.id.centerImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRippleOn) {
                    stopDiscoveryAndAnimation();
                }
                else {
                    startDiscoveryAndAnimation();
                }
            }
        });

        //Registering BroadCast receivers (BCR)
        //BCR for Changing state of Bluetooth
        IntentFilter bt_state_filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bt_state_receiver, bt_state_filter);
        //BCR for changing scan mode of Bluetooth
        IntentFilter discovery_state_filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(discovery_state_receiver, discovery_state_filter);
        //BCR for when discovery finishes
        IntentFilter discovery_status_finish = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discovery_ending, discovery_status_finish);
        //BCR for when new device is found
        IntentFilter device_found = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(device_found_receiver, device_found);
        //BCR for changing status of location services
        IntentFilter location_change = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(gpsReceiver, location_change);

        //If location is not on, request
        if(!Access.isMyLocationOn(context)) {
            Access.displayLocationSettingsRequest(context, this);
        }

        //If Bluetooth is not enabled, request
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void startLocalDiscovery() {

        Context context = getApplicationContext();

        //if location is off, quit discovery and request again
        if(!Access.isMyLocationOn(context)) {
            stopDiscoveryAndAnimation();
            Toast toast = Toast.makeText(context, "Please switch on Location and retry", Toast.LENGTH_SHORT);
            toast.show();
            Access.displayLocationSettingsRequest(context, this);
            return;
        }

        //if bluetooth is off, quit discovery and request again
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()) {
            stopDiscoveryAndAnimation();
            Toast toast = Toast.makeText(context, "Please switch on Bluetooth and retry", Toast.LENGTH_SHORT);
            toast.show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        //if not discoverable, request for discovery
        if(checkScanMode() != 2) {
            stopDiscoveryAndAnimation();
            Toast toast = Toast.makeText(context, "Please switch on Discovery and retry", Toast.LENGTH_SHORT);
            toast.show();
            requestDiscovery();
            return;
        }

        //create list of discovered devices from scratch
        this.discoveredDevices.clear();

        //if already discovering, restart the process
        if(bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();
        boolean flag = bluetoothAdapter.startDiscovery();

        //if discovery failed to start, show toast message
        if(!flag) {
            Toast toast = Toast.makeText(getApplicationContext(), "Discovery failed: Please make sure Bluetooth and Location are on before retrying.", Toast.LENGTH_LONG);
            toast.show();
            Access.displayLocationSettingsRequest(getApplicationContext(), this);
        }
    }

    //method to stop discovering if discovery is on
    public void cancelDiscovery() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();
    }


    //method to make central device image reappear
    //to be used when discovery process is off
    void makeCenterImageVisible() {
        ImageView image = findViewById(R.id.centerImage);
        image.setVisibility(View.VISIBLE);
    }

    //method to make central device image disappear
    //to be used when discovery process is on
    void makeCenterImageInvisible() {
        ImageView image = findViewById(R.id.centerImage);
        image.setVisibility(View.INVISIBLE);
    }

    //method to stop discovery process, related animation, and switch icon image
    void stopDiscoveryAndAnimation() {
        final RippleBackground rippleBackground = findViewById(R.id.content);
        rippleBackground.stopRippleAnimation();
        makeCenterImageVisible();
        isRippleOn = false;
        setBluetoothIconEnable();
        cancelDiscovery();
    }

    //method to start discovery process, related animation, and switch icon image
    void startDiscoveryAndAnimation() {
        final RippleBackground rippleBackground = findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        makeCenterImageInvisible();
        makeSideImageInvisible();
        this.discoveredDevices.clear();
        isRippleOn = true;
        setBluetoothIconDisable();
        startLocalDiscovery();
    }

    //change icon image to disabled, and switch OnClickListener to stop discovery
    void setBluetoothIconDisable() {
        final FloatingActionButton button = findViewById(R.id.action_a);
        button.setIcon(R.drawable.ic_bluetooth_disable);
        button.setOnClickListener(null);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopDiscoveryAndAnimation();
            }
        });
    }

    //change icon image to searching, and switch OnClickListener to start discovery
    void setBluetoothIconEnable() {
        final FloatingActionButton button = findViewById(R.id.action_a);
        button.setIcon(R.drawable.ic_bluetooth_search);
        button.setOnClickListener(null);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDiscoveryAndAnimation();
            }
        });
    }

    //method to make side phones visible if new devices are found
    //during discovery process
    void setSidePhoneVisibility(String name) {
        int n = this.discoveredDevices.size();
        ImageView image;
        TextView text;
        CardView card;
        switch (n) {
            case 1:
                image = findViewById(R.id.sideImage1);
                text = findViewById(R.id.sideImageText1);
                card = findViewById(R.id.card1);
                card.setVisibility(View.VISIBLE);
                break;
            case 2:
                image = findViewById(R.id.sideImage2);
                text = findViewById(R.id.sideImageText2);
                card = findViewById(R.id.card2);
                card.setVisibility(View.VISIBLE);
                break;
            case 3:
                image = findViewById(R.id.sideImage3);
                text = findViewById(R.id.sideImageText3);
                card = findViewById(R.id.card3);
                card.setVisibility(View.VISIBLE);
                break;
            case 4:
                image = findViewById(R.id.sideImage4);
                text = findViewById(R.id.sideImageText4);
                card = findViewById(R.id.card4);
                card.setVisibility(View.VISIBLE);
                break;
            default:
                return;
        }
        image.setVisibility(View.VISIBLE);
        text.setText(name);
    }

    //method to make all side phones invisible
    void makeSideImageInvisible() {
        ImageView image;
        CardView card;
        image = findViewById(R.id.sideImage1);
        image.setVisibility(View.INVISIBLE);
        card = findViewById(R.id.card1);
        card.setVisibility(View.INVISIBLE);
        image = findViewById(R.id.sideImage2);
        image.setVisibility(View.INVISIBLE);
        card = findViewById(R.id.card2);
        card.setVisibility(View.INVISIBLE);
        image = findViewById(R.id.sideImage3);
        image.setVisibility(View.INVISIBLE);
        card = findViewById(R.id.card3);
        card.setVisibility(View.INVISIBLE);
        image = findViewById(R.id.sideImage4);
        image.setVisibility(View.INVISIBLE);
        card = findViewById(R.id.card4);
        card.setVisibility(View.INVISIBLE);
    }

    //Method to switch to ConnectDevices Activity, and passing the list of discovered devices
    public void navigateToConnectDevices() {
        Intent intent = new Intent(this, ConnectDevices.class);
        intent.putExtra("DeviceList", (Serializable) this.discoveredDevices);
        startActivity(intent);
    }

    //return true if new device already exists in list of found devices
    public boolean checkDuplicate(BluetoothDevice device) {
        int len = this.discoveredDevices.size();
        for(int i = 0; i < len; i++) {
            if(this.discoveredDevices.get(i).equals(device))
                return true;
        }
        return false;
    }

    //add new device to list if it is not duplicate
    public void addDeviceToList(BluetoothDevice device) {
        if(!checkDuplicate(device))
            this.discoveredDevices.add(device);
    }

    //method to ask user for making device discoverable
    public void requestDiscovery() {
        Intent discover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discover.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1800);
        startActivity(discover);
    }

    //method to check current scan mode of device
    public int checkScanMode() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        int mode = bluetoothAdapter.getScanMode();
        if(mode == BluetoothAdapter.SCAN_MODE_NONE)
            return 0;
        else if(mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE)
            return 1;
        else if(mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
            return 2;
        return -1;
    }

    //check if user switches off location, and stop discovery process
    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                stopDiscoveryAndAnimation();
            }
        }
    };

    //If state of bluetooth adapter is changed, stop discovery process
    private final BroadcastReceiver bt_state_receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

                //stop discovery on change, if ongoing
                stopDiscoveryAndAnimation();

                //if scan mode is connectable, but not discoverable
                //request permission for discovery
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_ON) {
                    int mode = checkScanMode();
                    if(mode == 0 || mode == 1)
                        requestDiscovery();
                }
            }
        }
    };

    //If scan mode of bluetooth is changed, stop discovery process
    private final BroadcastReceiver discovery_state_receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                stopDiscoveryAndAnimation();
            }
        }
    };

    //Once discovery process is complete, switch off animations
    private final BroadcastReceiver discovery_ending = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                stopDiscoveryAndAnimation();
            }
        }
    };


    //Once new device is found during discovery process, add it to the list
    //Also, make side phone visible
    private final BroadcastReceiver device_found_receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                addDeviceToList(device);
                setSidePhoneVisibility(device.getName());
            }
        }
    };
}
