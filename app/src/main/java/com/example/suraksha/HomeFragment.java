package com.example.suraksha;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment implements View.OnClickListener, SensorEventListener {

    CardView locationCard, sirenCard, crimeCard, helplineCard, policeCard, closeCard;
    String userLocation = "";

    LocationManager mLocationManager; LocationListener mListener;
    SensorManager sensorManager; private long lastUpdate;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        locationCard = view.findViewById(R.id.locationCardView);
        sirenCard = view.findViewById(R.id.sirenCardView);
        crimeCard = view.findViewById(R.id.crimeCardView);
        helplineCard = view.findViewById(R.id.helplineCardView);
        policeCard = view.findViewById(R.id.policeCardView);
        closeCard = view.findViewById(R.id.closeCardView);

        locationCard.setOnClickListener(this);
        sirenCard.setOnClickListener(this);
        crimeCard.setOnClickListener(this);
        helplineCard.setOnClickListener(this);
        policeCard.setOnClickListener(this);
        closeCard.setOnClickListener(this);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mListener = (Location location) ->
        {
            userLocation = location.toString();
        };

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        else
        {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    mListener);
        }

        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType()== Sensor.TYPE_ACCELEROMETER)
        {
            getAccelerometer(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    private void getAccelerometer(SensorEvent event)
    {
        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x*x+y*y+z*z)/(SensorManager.GRAVITY_EARTH*SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();

        if (accelationSquareRoot>=2)
        {
            if (actualTime-lastUpdate<200)
            {return;}
            lastUpdate = actualTime;

            Toast.makeText(getContext(), "Detecting shaking", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), AlertActivity.class);
            intent.putExtra("location", userLocation);
            startActivity(intent);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onClick(View view)
    {
        int itemId = view.getId();

        if (itemId == R.id.locationCardView)
        {
            Intent intent = new Intent(getContext(), ContactsActivity.class);
            startActivity(intent);
        }
        else if (itemId == R.id.sirenCardView)
        {
            Intent intent = new Intent(getContext(), SirenActivity.class);
            startActivity(intent);
        }
        else if (itemId == R.id.crimeCardView)
        {
            Intent intent = new Intent(getContext(), ReportsActivity.class);
            startActivity(intent);
        }
        else if (itemId == R.id.helplineCardView)
        {
            Intent intent = new Intent(getContext(), HelplinesActivity.class);
            startActivity(intent);
        }
        else if (itemId == R.id.policeCardView)
        {
            Intent intent = new Intent(getContext(), AlertActivity.class);
            intent.putExtra("location", userLocation);
            startActivity(intent);
        }
        else if (itemId == R.id.closeCardView)
        {
            Intent intent = new Intent(getContext(), CloseFriendsActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, mListener);
        }

    }

}