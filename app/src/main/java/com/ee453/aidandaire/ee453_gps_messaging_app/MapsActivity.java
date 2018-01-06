package com.ee453.aidandaire.ee453_gps_messaging_app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private FirebaseDatabase database = FirebaseDatabase.getInstance(); //calls firebase server
    private DatabaseReference myRef = database.getReference("locations"); //refers to 'locations'table within database
    private HashMap<String, DatabaseEntry> markers = new HashMap<String, DatabaseEntry>();
    private LocationData currentLocation = new LocationData(53.283681, -30.063978); //initalize current locationdata object
    private String message = null; 
    private boolean notEntered = true; //used to stop repeating entries 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.startGettingLocations(); 



        // Read from the database
        // Attach a listener to read the data at our posts reference
        ValueEventListener valueEventListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) { //retrieves each entry from database table reference
                    Object o = dsp.getValue(); //gets values from entry
                    String key = dsp.getKey(); //gets key from entry - in this case, date and time of entry
                    HashMap<String, Double> hm = (HashMap<String, Double>) o; //for lat and lng
                    HashMap<String, String> hm2 = (HashMap<String, String>) o; //for messages 
                    double lat_marker = hm.get("lat");
                    double long_marker = hm.get("lng");
                    String text_marker = hm2.get("message");

                    DatabaseEntry db = new DatabaseEntry(lat_marker, long_marker, text_marker);
                    if (!markers.containsKey(key)) {
                        markers.put(key, db); //adds key and database entry object to markers hashmap
                    }
                }
                if (notEntered) { //if just opened page
                    if (message != null && !message.isEmpty()) {
                        addMessageMarker(); //adds new purple marker (when uploading new message)
                    } else {
                        addDatabaseMarkers(); //adds only orange markers (for just viewing page)
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void addDatabaseMarkers() { //adds all coordinates and correspoding messages from database to map as orange markers
        mMap.clear();
        for (String key : markers.keySet()) {
            DatabaseEntry dbe = markers.get(key);
            LocationData loc = new LocationData(dbe.getLat(),dbe.getLng());
            LatLng oldLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.addMarker(new MarkerOptions().position(oldLocation).title(dbe.getMessage()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))); //creating new marker object for google map
        }
        LatLng newLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()); //used for camera
        float zoomLevel = 5.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, zoomLevel)); //moves center of map (camera position) to current location
        notEntered = false; 
    }

    private void addMessageMarker() { //for uploading new entries

        // Write a message to the database
        Date now = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String value = dt.format(now);
        LatLng newLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()); //converts current location to LatLng
        String prev_message = "";
        String marker_message = message;
        mMap.clear();
        for (String key : markers.keySet()) { //iterates through each value in markers

            DatabaseEntry dbe = markers.get(key);
            LocationData loc = new LocationData(dbe.getLat(),dbe.getLng());
            LatLng oldLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
            if(CalculationByDistance(newLocation,oldLocation) > 10) { //checks if current location is more than 10 metres from each marker
                mMap.addMarker(new MarkerOptions().position(oldLocation).title(dbe.getMessage()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))); //if this is case, orange marker created
            } else {
                prev_message = dbe.getMessage();
                myRef.child(key).setValue(null); //deletes old entry
            }
        }
        if (!prev_message.equals("")) {
            marker_message = prev_message + ", " + message; //appends old message to previous message
        }
        mMap.addMarker(new MarkerOptions().position(newLocation).title(marker_message).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))); //adds current location marker as purple
        float zoomLevel = 5.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, zoomLevel));
        DatabaseEntry db = new DatabaseEntry(currentLocation.getLatitude(), currentLocation.getLongitude(), marker_message);
        myRef.child(value).setValue(db); //adds new entry to database
        message = null;
        notEntered = false;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker on Eng Building and move the camera
        LatLng engBuilding = new LatLng(53.283681, -9.063978);
        float zoomLevel = 5.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(engBuilding, zoomLevel));


        Intent intent = getIntent();
        message = intent.getStringExtra("MESSAGE"); //retrieves message from intent object

    }

    @Override
    public void onLocationChanged(Location location) { //method called when user location changed
        mMap.clear();
        for (String key : markers.keySet()) {
            DatabaseEntry dbe = markers.get(key);
            LocationData loc = new LocationData(dbe.getLat(),dbe.getLng());
            LatLng oldLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.addMarker(new MarkerOptions().position(oldLocation).title(dbe.getMessage()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))); //adds oranges markers from previous entries as before
        }

        // Write a message to the database
        LocationData locationDatabase = new LocationData(Math.round(location.getLatitude() * 1000000.0) / 1000000.0, Math.round(location.getLongitude() * 1000000.0) / 1000000.0); //rounds coordinates to 6 decimal places
        currentLocation = locationDatabase;
        LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
        float zoomLevel = 5.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation,zoomLevel));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void startGettingLocations() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean canGetLocation = true;
        int ALL_PERMISSIONS_RESULT = 101;
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;// Distance in meters
        long MIN_TIME_BW_UPDATES = 10000;// Time in milliseconds

        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsToRequest;

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);


        //Check if GPS and Network are on, if not asks the user to turn on
        if (!isGPS && !isNetwork) {
            showSettingsAlert();
        } else {
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();


            return;
        }


        //Starts requesting location updates
        if (canGetLocation) {
            if (isGPS) {
                lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Location loc = lm.getLastKnownLocation(lm.getBestProvider(new Criteria(), false)); //retrieving current location from gps
                currentLocation = new LocationData(Math.round(loc.getLatitude() * 1000000.0) / 1000000.0, Math.round(loc.getLongitude() * 1000000.0) / 1000000.0);
            } else if (isNetwork) {
                // from Network Provider

                lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Location loc = lm.getLastKnownLocation(lm.getBestProvider(new Criteria(), false));//retrieving current location from gps
                currentLocation = new LocationData(Math.round(loc.getLatitude() * 1000000.0) / 1000000.0, Math.round(loc.getLongitude() * 1000000.0) / 1000000.0); 
            }
        } else {
            Toast.makeText(this, "Can't get location", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

	 //calculates distance in metres between two coordinates(based off implementation from stackoverflow.com)
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1); //based on trigonometry, and the relationship between latitude, longitude and earth radius
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double meter = valueResult % 1000;

        return meter;
    }
}
