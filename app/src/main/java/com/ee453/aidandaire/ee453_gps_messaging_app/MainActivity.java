package com.ee453.aidandaire.ee453_gps_messaging_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void viewMap (View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void sendMessage (View view) {
        Intent intent = new Intent(this, SendMessageActivity.class);
        startActivity(intent);
    }
    public void viewMessages (View view) {
        Intent intent = new Intent(this, ReadMessagesActivity.class);
       startActivity(intent);
    }
}
