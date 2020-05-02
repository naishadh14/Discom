package com.example.discom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.skyfishjy.library.RippleBackground;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeviceDiscovery extends AppCompatActivity {

    List<BluetoothDevice> discoveredDevices;
    int REQUEST_CHECK_SETTINGS;
    int REQUEST_ENABLE_BT;
    boolean isRippleOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_discovery);
        this.discoveredDevices = new ArrayList<BluetoothDevice>();
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final RippleBackground rippleBackground = (RippleBackground)findViewById(R.id.content);
        ImageView imageView = (ImageView)findViewById(R.id.centerImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRippleOn) {
                    rippleBackground.stopRippleAnimation();
                    cancelDiscovery();
                }
                else {
                    rippleBackground.startRippleAnimation();
                    startDiscovery();
                }
                isRippleOn = !isRippleOn;
            }
        });

        //Starting experimental code
        IntentFilter bt_state_filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bt_state_receiver, bt_state_filter);

        IntentFilter discovery_state_filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(discovery_state_receiver, discovery_state_filter);

        IntentFilter discovery_status_start = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(discovery_starting, discovery_status_start);

        IntentFilter discovery_status_finish = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discovery_ending, discovery_status_finish);

        IntentFilter device_found = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(device_found_receiver, device_found);

        if(!isMyLocationOn()) {
            displayLocationSettingsRequest(getApplicationContext());
        }

        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void startDiscovery() {

        //if location is off, quit discovery and request again
        if(!isMyLocationOn()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please switch on location to continue", Toast.LENGTH_LONG);
            toast.show();
            displayLocationSettingsRequest(getApplicationContext());
            return;
        }

        //sleep for 1s for any necessary changes in BT state to reflect
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        //if bluetooth is off, quit discovery and request again
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        //create list of discovered devices from scratch
        this.discoveredDevices.clear();
        refreshList();

        //if already discovering, restart the process
        if(bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();
        boolean flag = bluetoothAdapter.startDiscovery();

        //if discovery failed to start, show toast message
        if(!flag) {
            Toast toast = Toast.makeText(getApplicationContext(), "Discovery failed: Please make sure Bluetooth and Location are on before retrying.", Toast.LENGTH_LONG);
            toast.show();
            displayLocationSettingsRequest(getApplicationContext());
        }
    }

    public void cancelDiscovery() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();
    }

    boolean isMyLocationOn() {
        Context context = getApplicationContext();
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(Constants.TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(Constants.TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(DeviceDiscovery.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(Constants.TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(Constants.TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    // Create a BroadcastReceiver for ACTION_STATE_CHANGED.
    private final BroadcastReceiver bt_state_receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                // State has changed
                TextView text = (TextView) findViewById(R.id.textView3);
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    == BluetoothAdapter.STATE_OFF)
                    text.append("Adapter is off.\n");
                else if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    == BluetoothAdapter.STATE_ON) {
                    //text.append("Adapter is on.\n");
                    int mode = checkScanMode();
                    if(mode == 0 || mode == 1)
                        requestDiscovery();
                }
            }
        }
    };

    //Create a BroadcastReceiver for ACTION_SCAN_MODE_CHANGED.
    private final BroadcastReceiver discovery_state_receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                // SCAN MODE HAS CHANGED. GET NEW SCAN MODE.
                TextView text = (TextView) findViewById(R.id.textView3);
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1);
                if(mode == BluetoothAdapter.SCAN_MODE_NONE)
                    text.append("Scan mode none.\n");
                else if(mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE)
                    text.append("Scan mode connectable.\n");
                else if(mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    text.append("Scan mode connectable and discoverable.\n");
                }
            }
        }
    };

    private final BroadcastReceiver discovery_starting = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // SCAN MODE HAS CHANGED. GET NEW SCAN MODE.
                TextView text = (TextView) findViewById(R.id.textView3);
                text.append("Discovery process has started.\n");
                makeButtonInvisible();
            }
        }
    };

    private final BroadcastReceiver discovery_ending = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // SCAN MODE HAS CHANGED. GET NEW SCAN MODE.
                TextView text = (TextView) findViewById(R.id.textView3);
                text.append("Discovery process has finished.\n");
                makeButtonVisible();
            }
        }
    };


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver device_found_receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                TextView text = (TextView) findViewById(R.id.textView3);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                createDiscoveryList(device);
            }
        }
    };

    //Make a onDestroy

    public void makeButtonVisible() {
        Button button = (Button) findViewById(R.id.button3);
        button.setVisibility(View.VISIBLE);
    }

    public void makeButtonInvisible() {
        Button button = (Button) findViewById(R.id.button3);
        button.setVisibility(View.GONE);
    }

    public void NavigateToConnectDevices(View view) {
        /*
        if(this.discoveredDevices.size() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(), "No devices found!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        */
        Intent intent = new Intent(this, ConnectDevices.class);
        intent.putExtra("DeviceList", (Serializable) this.discoveredDevices);
        startActivity(intent);
    }

    public void createDiscoveryList(BluetoothDevice device) {
        addDeviceToList(device);
        refreshList();
    }

    public boolean checkDuplicate(BluetoothDevice device) {
        int len = this.discoveredDevices.size();
        for(int i = 0; i < len; i++) {
            if(this.discoveredDevices.get(i).equals(device))
                return true;
        }
        return false;
    }

    public void addDeviceToList(BluetoothDevice device) {
        if(!checkDuplicate(device))
            this.discoveredDevices.add(device);
    }

    public void refreshList() {
        TextView text = (TextView) findViewById(R.id.textView4);
        text.setText("");
        int len = this.discoveredDevices.size();
        BluetoothDevice device;
        for(int i = 0; i < len; i++) {
            device = discoveredDevices.get(i);
            text.append("Device: ");
            text.append(device.getName());
            text.append("\n");
            text.append("Address: ");
            text.append(device.getAddress());
            text.append("\n\n");
        }
    }

    public void requestDiscovery() {
        Intent discover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discover.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
        startActivity(discover);
    }

    public int checkScanMode() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        TextView text = (TextView) findViewById(R.id.textView3);
        int mode = bluetoothAdapter.getScanMode();
        if(mode == BluetoothAdapter.SCAN_MODE_NONE) {
            text.append("Scan mode none.\n");
            return 0;
        }
        else if(mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
            text.append("Scan mode connectable.\n");
            return 1;
        }
        else if(mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            text.append("Scan mode connectable and discoverable.\n");
            return 2;
        }
        return -1;
    }

}
