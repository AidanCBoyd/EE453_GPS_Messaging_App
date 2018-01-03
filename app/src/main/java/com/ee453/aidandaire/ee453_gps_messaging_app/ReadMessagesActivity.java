package com.ee453.aidandaire.ee453_gps_messaging_app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class ReadMessagesActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("locations");
    private ListView list;
    private ArrayAdapter<String> stringArrayAdapter;
    private ArrayList<String> messages= new ArrayList<String>();

    public ReadMessagesActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
       setSupportActionBar(toolbar);
        toolbar.setTitle("Read Messages");
      //  ArrayList<TextView> textViews = new ArrayList<TextView>();
        // final TextView textView = (TextView)findViewById(R.id.textView7);
       list = (ListView) findViewById(R.id.listView);


        Query query = myRef.orderByKey();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String viewText = "";


                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Object o = dsp.getValue();
                    String key = dsp.getKey();
                    viewText += "Sent: "+key + " ";
                    HashMap<String, Double> hm = (HashMap<String, Double>) o;
                    HashMap<String, String> hm2 = (HashMap<String, String>) o;
                    double lat = hm.get("lat");
                    viewText += "\nLatitude:"+lat + ", ";
                    double lng = hm.get("lng");
                    viewText += "Longitude:"+lng + "\n";
                    String text = hm2.get("message");
                    viewText += "Message: "+text + "\n\n";
                    messages.add(viewText);
                    viewText = "";

                }


                stringArrayAdapter =new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,messages){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent){
                        // Get the Item from ListView
                        View view = super.getView(position, convertView, parent);

                        // Initialize a TextView for ListView each Item
                        TextView tv = (TextView) view.findViewById(android.R.id.text1);

                        // Set the text color of TextView (ListView Item)
                        tv.setTextColor(Color.BLACK);

                        // Generate ListView Item using TextView
                        return view;
                    }
                };
                list.setAdapter(stringArrayAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
            //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View view) {
              //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
          //  }
        //});

    });


    }

}
