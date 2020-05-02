package com.example.discom;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

public class ConfirmNumber extends AppCompatActivity {

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_number);

        //try to get mobile number of user, if previously saved
        this.sharedPref = this.getSharedPreferences("MY_SETTINGS", Context.MODE_PRIVATE);
        if(this.sharedPref.contains("PhoneNumber")) {
            //sets value of textfield in case mobile number is found
            TextInputEditText text = findViewById(R.id.editText);
            text.setText(Long.toString(this.sharedPref.getLong("PhoneNumber", 0)));
        }

        //check if location permission enabled
        //if not, ask for permission
        Access.checkPermissions(getApplicationContext(), this);
    }

    public void confirm(View view) {

        Context context = getApplicationContext();

        //make sure app has location permissions, else do not proceed
        if(!Access.checkPermissions(context, this)) {
            Toast toast = Toast.makeText(context, "Please grant location access to continue", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        //get handle to textfield
        TextInputEditText text = findViewById(R.id.editText);
        String number_text = text.getText().toString();

        //show toast message if mobile number entered is not 10 digits
        if (number_text.length() != 10) {
            Toast toast = Toast.makeText(context, "Phone Number must be 10 digits", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        //save mobile number to SharedPreferences for future reference
        long number = Long.parseLong(number_text);
        SharedPreferences.Editor editor = this.sharedPref.edit();
        editor.putLong("PhoneNumber", number);
        editor.apply();

        //open next screen
        Intent intent = new Intent(this, DeviceDiscovery.class);
        startActivity(intent);


    }

    public void cancel(View view) {
        TextInputEditText text = findViewById(R.id.editText);
        text.setText("");
    }

    /*
    boolean checkPermissions() {
        //checks if permission already granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //permission not granted, ask user to allow
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        }
        else
            //already granted
            return true;
    }
     */
}