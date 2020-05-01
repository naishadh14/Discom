package com.example.discom;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ConfirmNumber extends AppCompatActivity {

    SharedPreferences sharedPref;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_number);
        this.sharedPref = this.getSharedPreferences("MY_SETTINGS", Context.MODE_PRIVATE);
        if (this.sharedPref.contains("PhoneNumber")) {
            EditText text = findViewById(R.id.editText);
            text.setText(Long.toString(this.sharedPref.getLong("PhoneNumber", 0)));
        }
        checkPermissions();
    }

    public void confirm(View view) {

        //make sure app has location permissions, else return
        if(!checkPermissions()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please grant location access to continue", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        EditText text = findViewById(R.id.editText);
        String number_text = text.getText().toString();
        if (number_text.length() != 10) {
            Toast toast = Toast.makeText(getApplicationContext(), "Phone Number must be 10 digits", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        long number = Long.parseLong(number_text);
        SharedPreferences.Editor editor = this.sharedPref.edit();
        editor.putLong("PhoneNumber", number);
        editor.apply();
        Intent intent = new Intent(this, DeviceDiscovery.class);
        startActivity(intent);
    }

    boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        }
        else
            return true;
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission
                }
                return;
            }

        }
    }
     */
}