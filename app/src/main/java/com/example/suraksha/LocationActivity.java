package com.example.suraksha;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; String userLocation = "";
    Button sendButton; String number; double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        sendButton = findViewById(R.id.sendButton);

        Intent intent = getIntent();
        number = intent.getStringExtra("number");
        userLocation = intent.getStringExtra("location");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLocation();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String[] locationList = userLocation.split(" ");

        String coordinates = locationList[1];
        String[] latLong = coordinates.split(",");
        lat = Double.parseDouble(latLong[0]);
        lon = Double.parseDouble(latLong[1]);

        // Add a marker in the user's location and move the camera
        LatLng userLoc = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(userLoc).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15));
    }

    public void sendLocation()
    {
        String message = "SOS!!! http://maps.google.com/?q=" + String.valueOf(lat) + "," + String.valueOf(lon);
        boolean waInstalled = appInstalledOrNot("com.whatsapp");

        if (waInstalled)
        {
            String uri = "http://api.whatsapp.com/send?phone=+91" + number + "&text=" + message;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }
        else
        {
            Toast.makeText(LocationActivity.this, "Whatsapp not installed", Toast.LENGTH_LONG).show();
        }

    }

    private boolean appInstalledOrNot(String url)
    {
        boolean foundApp;
        PackageManager packageManager = getPackageManager();
        try
        {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            foundApp = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            foundApp = false;
        }
        return foundApp;
    }

}