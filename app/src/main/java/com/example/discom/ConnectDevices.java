package com.example.discom;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ConnectDevices extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_devices);
        ArrayList<DeviceInfo> discovered_devices = (ArrayList<DeviceInfo>)getIntent().getSerializableExtra("DeviceList");
        TextView text = (TextView)findViewById(R.id.textView5);
        if(discovered_devices == null)
            text.append("Null list received");
        else {
            text.append("\n");
            int len = discovered_devices.size();
            DeviceInfo device;
            for(int i = 0; i < len; i++) {
                device = discovered_devices.get(i);
                text.append("Device: ");
                text.append(device.name);
                text.append("\n");
                text.append("Address: ");
                text.append(device.address);
                text.append("\n");
            }
        }
    }

    public void startConnection(View view) {

    }
}
