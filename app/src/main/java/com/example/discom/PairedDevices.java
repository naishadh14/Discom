package com.example.discom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class PairedDevices extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paired_devices);

        TextView text = (TextView) findViewById(R.id.textView2);
        text.setText("List of Paired Devices -\n");

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        text.append("Number of Paired Devices is ");
        text.append(Integer.toString(pairedDevices.size()));
        text.append("\n\n");
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                text.append("Device Name: ");
                text.append(deviceName);
                text.append("\n");
                text.append("Device Address: ");
                text.append(deviceHardwareAddress);
                text.append("\n\n");
            }
        }
    }

    public void discover(View view) {

        //Starting an intent to make device discoverable to other mobile devices via bluetooth
        //It will remain discoverable for 3600s(1 hour)
        Intent discover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discover.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
        startActivity(discover);
        //ADD ERROR CHECKING IF USER SAYS NO

        //If yes, navigate to MainActivity page
        Intent navigate = new Intent(this, MainActivity.class);
        startActivity(navigate);
    }
}
