package com.ee453.aidandaire.ee453_gps_messaging_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class SendMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
    }

    public void sentHandler(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        EditText editText = (EditText) findViewById(R.id.message_box);
        String message = editText.getText().toString();
        intent.putExtra("MESSAGE", message);
        startActivity(intent);
    }
}
