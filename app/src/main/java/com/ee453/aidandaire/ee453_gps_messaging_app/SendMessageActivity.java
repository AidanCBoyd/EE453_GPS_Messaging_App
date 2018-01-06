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
        Intent intent = new Intent(this, MapsActivity.class); //intent object for navigation to maps page
        EditText editText = (EditText) findViewById(R.id.message_box); //edit text object allows user to input text into application
        String message = editText.getText().toString(); 
        intent.putExtra("MESSAGE", message); //intent object takes message as argument, as extra content within the object 
        startActivity(intent);
    }
}
