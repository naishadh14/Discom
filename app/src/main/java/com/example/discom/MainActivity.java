package com.example.discom;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Starts with ConfirmNumber
        Intent intent = new Intent(this, ConfirmNumber.class);
        startActivity(intent);
    }
}
