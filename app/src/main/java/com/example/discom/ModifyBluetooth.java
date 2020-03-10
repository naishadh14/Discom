package com.example.discom;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ModifyBluetooth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_bluetooth);
    }

    public void bluetoothSetup(View view) {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            //do error handling
        }

        TextView text = (TextView) findViewById(R.id.textView);
        text.setText("Starting Bluetooth");
        //switching on bluetooth, if disabled
        if(!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        while(!bluetoothAdapter.isEnabled());

        //Navigating to next page, which lists paired devices, and other available devices
        Intent available_devices = new Intent(this, PairedDevices.class);
        startActivity(available_devices);
    }

}
