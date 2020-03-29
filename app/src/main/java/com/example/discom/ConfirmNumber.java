package com.example.discom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmNumber extends AppCompatActivity {

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_number);
        this.sharedPref = this.getSharedPreferences("MY_SETTINGS", Context.MODE_PRIVATE);
        if(this.sharedPref.contains("PhoneNumber")) {
            EditText text = findViewById(R.id.editText);
            text.setText(Long.toString(this.sharedPref.getLong("PhoneNumber", 0)));
        }
    }

    public void confirm(View view) {
        EditText text = findViewById(R.id.editText);
        String number_text = text.getText().toString();
        if(number_text.length() != 10) {
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
}
