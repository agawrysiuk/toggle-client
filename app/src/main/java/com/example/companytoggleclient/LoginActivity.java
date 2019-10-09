package com.example.companytoggleclient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.DecimalFormat;

public class LoginActivity extends AppCompatActivity implements LocationListener {
    final int LOCATION_PERMISSION_REQUEST_CODE = 1252;

    private boolean online = false;
    private String email = "";
    private double latitude = 0.0;
    private double longitude = 0.0;
    private DecimalFormat df = new DecimalFormat("#.###");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = findViewById(R.id.textView);
        assert message != null;
        email = message.split(":")[0];
        textView.setText("Logged in as: "+email);

        Button button = findViewById(R.id.button_signin);
        online = message.split(":")[1].equals("online");
        button.setText(online? "Check out" : "Check in");

        //location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= 23) { // Marshmallow

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            return;

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        onLocationChanged(location);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        TextView textView = findViewById(R.id.locationText);

        textView.setText("Location: "+latitude + "," + longitude);
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

    @SuppressLint("SetTextI18n")
    public void checkInOut (View view) {
        String response = MainActivity.sendMessage((online? "CHECKOUT:"+email : "CHECKIN:"+email)+":"+df.format(latitude) + ":"+df.format(longitude));
        TextView errorCheckinText = findViewById(R.id.errorCheckinText);
        if(response==null) {
            errorCheckinText.setText("Network error while trying to log in.");
            return;
        }
        if(response.contains("Success")) {
            online = !online;
            Button button = findViewById(R.id.button_signin);
            button.setText(online? "Check out" : "Check in");
        }
        errorCheckinText.setText(response.split(":")[0]);
    }

}
